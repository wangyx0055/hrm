/*
 * Copyright (C) 2015 davis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package hrm.controller;

import hrm.system.HRMMain;
import hrm.utils.Attribute;
import hrm.utils.Prompt;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/**
 * Handling a form request. This servlet takes in request from JSP and dispatch
 * it to the appropriate controller.
 *
 * @author davis
 */
@MultipartConfig
public class HRMDispatcherServlet extends HttpServlet {

        public HRMDispatcherServlet() {
        }

        /**
         * Processes requests for both HTTP <code>GET</code> and
         * <code>POST</code> methods.
         *
         * @param request servlet request
         * @param response servlet response
         * @throws ServletException if a servlet-specific error occurs
         * @throws IOException if an I/O error occurs
         */
        protected void processRequest(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
                if (HRMMain.DEBUG) {
                        System.out.println("Request for internal dispatcher servlet");
                        Prompt.log(Prompt.NORMAL, getClass().toString(), "HTTP request caught: " + request);
                }

                // Block recursive request
                String indicator = (String) request.getAttribute("is_self_forward");
                if (indicator != null && indicator.equals("true")) {
                        throw new ServletException(
                                "Self forward request will lead to infinite recursive call!");
                }
                // gather parameters/attributes
                Map<String, String[]> params = request.getParameterMap();
                HttpSession session = request.getSession();
                Enumeration<String> session_attris = session.getAttributeNames();
                while (session_attris.hasMoreElements()) {
                        String attri_name = session_attris.nextElement();
                        params.put(attri_name, new String[]{(String) session.getAttribute(attri_name)});
                }
                // gather multipart data
                List<DataPart> data_parts = new ArrayList<>();
                try {
                        Collection<Part> file_parts = request.getParts();
                        for (Part file_part : file_parts) {
                                String file_name = file_part.getSubmittedFileName();
                                InputStream data_stream = file_part.getInputStream();
                                data_parts.add(new DataPart(file_name, data_stream));
                        }
                } catch (IOException | ServletException ex) {
                        if (HRMMain.DEBUG)
                                Prompt.log(Prompt.NORMAL, getClass().toString(), 
                                           "Data file not supplied: " + ex.getMessage());
                }
                
                // construct a controller call
                String callee = request.getParameter("call");
                CallerContext context = new CallerContext(callee == null ? "" : callee,
                                                          request.getRequestURI().substring(
                                                                  request.getContextPath().length()));
                context.set_parameters(params);
                context.set_data_streams(data_parts);
                
                // dispatch the call
                DispatcherManager mgr = HRMMain.get_system_context().get_dispatcher_manager();
                Set<Dispatcher> dispatchers = mgr.get_all_dispatchers();
                for (Dispatcher dp : dispatchers) {
                        // call the controller
                        ReturnValue returned_value = dp.dispatch_jsp(context);
                        if (returned_value == null) {
                                continue;
                        }

                        // return the value back to the view
                        Set<Attribute> attris = returned_value.get_session_attribute();
                        if (attris != null) {
                                for (Attribute attri : attris) {
                                        session.setAttribute(attri.get_name(), attri.get_object());
                                }
                        }
                        attris = returned_value.get_requst_attribute();
                        if (attris != null) {
                                for (Attribute attri : attris) {
                                        request.setAttribute(attri.get_name(), attri.get_object());
                                }
                        }
                        JSPResolver resolver = returned_value.get_resolver();
                        if (resolver != null) {
                                String[] var_name = params.get("return");
                                String s = resolver.toString();
                                if (var_name != null) {
                                        request.setAttribute(var_name[0], s);
                                } else {
                                        request.setAttribute("ret", s);
                                        Prompt.log(Prompt.WARNING, getClass().toString(),
                                                   "This call: " + callee + " has return value but ignored");
                                }
                        }
                        try {
                                ServletContext ctx = request.getServletContext();
                                if (ctx != null) {
                                        String uri = returned_value.get_redirected_page_uri();
                                        if (uri != null) {
                                                request.setAttribute("is_self_forward", "true");
                                                ctx.getRequestDispatcher(uri).forward(request, response);
                                        }
                                }
                        } catch (ServletException | IOException e) {
                                Prompt.log(Prompt.ERROR, getClass().toString(),
                                           "Caught by dispatcher servlet that "
                                           + e.getMessage() + ", requested by: " + request.toString()
                                           + ", processed by: " + returned_value.getClass().toString());
                        }
                }
        }

        // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
        /**
         * Handles the HTTP <code>GET</code> method.
         *
         * @param request servlet request
         * @param response servlet response
         * @throws ServletException if a servlet-specific error occurs
         * @throws IOException if an I/O error occurs
         */
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
                processRequest(request, response);
        }

        /**
         * Handles the HTTP <code>POST</code> method.
         *
         * @param request servlet request
         * @param response servlet response
         * @throws ServletException if a servlet-specific error occurs
         * @throws IOException if an I/O error occurs
         */
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
                processRequest(request, response);
        }

        /**
         * Returns a short description of the servlet.
         *
         * @return a String containing servlet description
         */
        @Override
        public String getServletInfo() {
                return "HRMDispatcherServlet - Internal Servlet to dispatch request to appropiate callee";
        }// </editor-fold>

}
