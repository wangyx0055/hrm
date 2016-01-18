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
import hrm.view.JSPResolver;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 *
 * @author davis
 */
public class HRMRequestFilter implements Filter {
        
        private static final boolean debug = true;

        // The filter configuration object we are associated with.  If
        // this value is null, this filter instance is not currently
        // configured. 
        private FilterConfig    filterConfig = null;
        private ServletContext  m_servlet_ctx;
        
        public HRMRequestFilter() {
        }        
        
        private void doBeforeProcessing(ServletRequest request, ServletResponse response)
                throws IOException, ServletException {
//                if (debug) {
                        log("HRMRequestFilter:DoBeforeProcessing");
                        Prompt.log(Prompt.NORMAL, getClass().toString(), "Servlet request caught: " + request);
//                }
                
                // Block recursive request
                String indicator = (String) request.getAttribute("is_self_forward");
                if (indicator != null && indicator.equals("true")) {
                        throw new ServletException(
                                "Self forward request will lead to infinite recursive call!");
                }
                DispatcherManager mgr = HRMMain.get_system_context().get_dispatcher_manager();
                Set<Dispatcher> dispatchers = mgr.get_all_dispatchers();

                // construct a controller call
                Map<String, String[]> params = request.getParameterMap();
                CallerContext context = new CallerContext(request.getParameter("call"));
                for (String param : params.keySet()) {
                        context.add_parameter(new Attribute(param, params.get(param)));
                }

                for (Dispatcher dp : dispatchers) {
                        // call the controller
                        ReturnValue returned_value = dp.dispatch_jsp(context);
                        if (returned_value == null) continue;
                        
                        // return the value back to the view
                        Set<Attribute> attris = returned_value.get_session_attribute();
//                        if (attris != null) {
//                                HttpSession session = getSession();
//                                for (Attribute attri : attris) {
//                                        session.setAttribute(attri.get_name(), attri.get_object());
//                                }
//                        }
                        attris = returned_value.get_requst_attribute();
                        if (attris != null) {
                                for (Attribute attri : attris) {
                                        request.setAttribute(attri.get_name(), attri.get_object());
                                }
                        }
                        JSPResolver resolver = returned_value.get_resolver();
                        if (resolver != null) {
                                request.setAttribute("jsp-resolver", resolver);
                        }
                        try {
                                ServletContext ctx = m_servlet_ctx;
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
                                e.printStackTrace();
                        }
                }
        }        
        
        private void doAfterProcessing(ServletRequest request, ServletResponse response)
                throws IOException, ServletException {
                if (debug) {
                        log("HRMRequestFilter:DoAfterProcessing");
                }

                // Write code here to process the request and/or response after
                // the rest of the filter chain is invoked.
                // For example, a logging filter might log the attributes on the
                // request object after the request has been processed. 
                /*
                for (Enumeration en = request.getAttributeNames(); en.hasMoreElements(); ) {
                    String name = (String)en.nextElement();
                    Object value = request.getAttribute(name);
                    log("attribute: " + name + "=" + value.toString());

                }
                 */
                // For example, a filter might append something to the response.
                /*
	PrintWriter respOut = new PrintWriter(response.getWriter());
	respOut.println("<P><B>This has been appended by an intrusive filter.</B>");
                 */
        }

        /**
         *
         * @param request The servlet request we are processing
         * @param response The servlet response we are creating
         * @param chain The filter chain we are processing
         *
         * @exception IOException if an input/output error occurs
         * @exception ServletException if a servlet error occurs
         */
        public void doFilter(ServletRequest request, ServletResponse response,
                FilterChain chain)
                throws IOException, ServletException {
                
                if (debug) {
                        log("HRMRequestFilter:doFilter()");
                }
                
                doBeforeProcessing(request, response);
                
                Throwable problem = null;
                try {
                        chain.doFilter(request, response);
                } catch (Throwable t) {
                        // If an exception is thrown somewhere down the filter chain,
                        // we still want to execute our after processing, and then
                        // rethrow the problem after that.
                        problem = t;
                        t.printStackTrace();
                }
                
                doAfterProcessing(request, response);

                // If there was a problem, we want to rethrow it if it is
                // a known type, otherwise log it.
                if (problem != null) {
                        if (problem instanceof ServletException) {
                                throw (ServletException) problem;
                        }
                        if (problem instanceof IOException) {
                                throw (IOException) problem;
                        }
                        sendProcessingError(problem, response);
                }
        }

        /**
         * Return the filter configuration object for this filter.
         */
        public FilterConfig getFilterConfig() {
                return (this.filterConfig);
        }

        /**
         * Set the filter configuration object for this filter.
         *
         * @param filterConfig The filter configuration object
         */
        public void setFilterConfig(FilterConfig filterConfig) {
                this.filterConfig = filterConfig;
        }

        /**
         * Destroy method for this filter
         */
        public void destroy() {                
        }

        /**
         * Init method for this filter
         */
        public void init(FilterConfig filterConfig) {                
                this.filterConfig = filterConfig;
                if (filterConfig != null) {
                        if (debug) {                                
                                log("HRMRequestFilter:Initializing filter");
                        }
                }
        }

        /**
         * Return a String representation of this object.
         */
        @Override
        public String toString() {
                if (filterConfig == null) {
                        return ("HRMRequestFilter()");
                }
                StringBuffer sb = new StringBuffer("HRMRequestFilter(");
                sb.append(filterConfig);
                sb.append(")");
                return (sb.toString());
        }
        
        private void sendProcessingError(Throwable t, ServletResponse response) {
                String stackTrace = getStackTrace(t);                
                
                if (stackTrace != null && !stackTrace.equals("")) {
                        try {
                                response.setContentType("text/html");
                                PrintStream ps = new PrintStream(response.getOutputStream());
                                PrintWriter pw = new PrintWriter(ps);                                
                                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                                // PENDING! Localize this for next official release
                                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");                                
                                pw.print(stackTrace);                                
                                pw.print("</pre></body>\n</html>"); //NOI18N
                                pw.close();
                                ps.close();
                                response.getOutputStream().close();
                        } catch (Exception ex) {
                        }
                } else {
                        try {
                                PrintStream ps = new PrintStream(response.getOutputStream());
                                t.printStackTrace(ps);
                                ps.close();
                                response.getOutputStream().close();
                        } catch (Exception ex) {
                        }
                }
        }
        
        public static String getStackTrace(Throwable t) {
                String stackTrace = null;
                try {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        t.printStackTrace(pw);
                        pw.close();
                        sw.close();
                        stackTrace = sw.getBuffer().toString();
                } catch (Exception ex) {
                }
                return stackTrace;
        }
        
        public void log(String msg) {
                filterConfig.getServletContext().log(msg);                
        }
        
}
