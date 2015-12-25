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
package hrm.servlets;

import hrm.controller.ControllerCallContext;
import hrm.controller.ControllerDispatcher;
import hrm.controller.ControllerReturnValue;
import hrm.utils.Attribute;
import hrm.utils.Prompt;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Handling a form request.
 * This servlet takes in request from JSP and dispatch it to the appropriate controller.
 * @author davis
 */
public class HRMDispatcherServlet extends HttpServlet {
        private final ControllerDispatcher      m_dispatcher = new ControllerDispatcher();
        
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
                // construct a controller call
                Map<String, String[]> params = request.getParameterMap();
                ControllerCallContext context = new ControllerCallContext(request.getRequestURI());
                for (String param : params.keySet()) {
                        context.add_parameter(param, params.get(param));
                }
                // call the controller
                ControllerReturnValue returned_value = m_dispatcher.dispatch_jsp(context);
                // return the value back to the view
                Set<Attribute> attris = returned_value.get_session_attribute();
                if (attris != null) {
                        HttpSession session = request.getSession();
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
                try {
                        ServletContext ctx = getServletContext();
                        if (ctx != null) ctx.getRequestDispatcher(returned_value.get_redirected_page_uri()).
                                forward(request, response);
                } catch (Exception e) {
                        Prompt.log(Prompt.ERROR, getClass().toString(), "Caught by dispatcher servlet that " + 
                                e.getMessage() + ", requested by: " + request.getRequestURI() +
                                ", processed by: " + returned_value.getClass().toString());
                        e.printStackTrace();
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
                return "Short description";
        }// </editor-fold>

}
