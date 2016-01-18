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

import hrm.controller.Dispatcher;
import hrm.controller.DispatcherManager;
import hrm.system.HRMMain;
import hrm.utils.Attribute;
import hrm.utils.Prompt;
import hrm.view.JSPResolver;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Handling a form request. This servlet takes in request from JSP and dispatch
 * it to the appropriate controller.
 *
 * @author davis
 */
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
//                Prompt.log(Prompt.NORMAL, getClass().toString(), "HTTP request caught: " + request);
//                
//                // Block recursive request
//                String indicator = (String) request.getAttribute("is_self_forward");
//                if (indicator != null && indicator.equals("true")) {
////                        throw new ServletException(
////                                "Self forward request will lead to infinite recursive call!");
//                        return ;
//                }
//                DispatcherManager mgr = HRMMain.get_system_context().get_dispatcher_manager();
//                Set<Dispatcher> dispatchers = mgr.get_all_dispatchers();
//
//                // construct a controller call
//                Map<String, String[]> params = request.getParameterMap();
//                Dispatcher.CallerContext context
//                        = new Dispatcher().get_caller_context(request.getParameter("call"));
//                for (String param : params.keySet()) {
//                        context.add_parameter(new Attribute(param, params.get(param)));
//                }
//
//                for (Dispatcher dp : dispatchers) {
//                        // call the controller
//                        Dispatcher.ReturnValue returned_value = dp.dispatch_jsp(context);
//                        if (returned_value == null) continue;
//                        
//                        // return the value back to the view
//                        Set<Attribute> attris = returned_value.get_session_attribute();
//                        if (attris != null) {
//                                HttpSession session = request.getSession();
//                                for (Attribute attri : attris) {
//                                        session.setAttribute(attri.get_name(), attri.get_object());
//                                }
//                        }
//                        attris = returned_value.get_requst_attribute();
//                        if (attris != null) {
//                                for (Attribute attri : attris) {
//                                        request.setAttribute(attri.get_name(), attri.get_object());
//                                }
//                        }
//                        JSPResolver resolver = returned_value.get_resolver();
//                        if (resolver != null) {
//                                request.setAttribute("jsp-resolver", resolver);
//                        }
//                        try {
//                                ServletContext ctx = getServletContext();
//                                if (ctx != null) {
//                                        String uri = returned_value.get_redirected_page_uri();
//                                        if (uri != null) {
//                                                request.setAttribute("is_self_forward", "true");
//                                                ctx.getRequestDispatcher(uri).forward(request, response);
//                                        }
//                                }
//                        } catch (ServletException | IOException e) {
//                                Prompt.log(Prompt.ERROR, getClass().toString(), 
//                                        "Caught by dispatcher servlet that "
//                                        + e.getMessage() + ", requested by: " + request.getRequestURI()
//                                        + ", processed by: " + returned_value.getClass().toString());
//                                e.printStackTrace();
//                        }
//                }

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
