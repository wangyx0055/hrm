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
import hrm.utils.Prompt;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
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

        // The filter configuration object we are associated with.  If
        // this value is null, this filter instance is not currently
        // configured. 
        private FilterConfig m_filter_config = null;

        public HRMRequestFilter() {
        }

        private boolean preprocess(ServletRequest request, ServletResponse response)
                throws IOException, ServletException {
                if (HRMMain.DEBUG) {
                        log("HRMRequestFilter:DoBeforeProcessing");
                        Prompt.log(Prompt.NORMAL, getClass().toString(), "Servlet request caught: " + request);
                }
                return true;
        }

        private void postprocess(ServletRequest request, ServletResponse response)
                throws IOException, ServletException {
                if (HRMMain.DEBUG) {
                        log("HRMRequestFilter:DoAfterProcessing");

                        for (Enumeration en = request.getAttributeNames(); en.hasMoreElements();) {
                                String name = (String) en.nextElement();
                                Object value = request.getAttribute(name);
                                log("attribute: " + name + "=" + value.toString());

                        }
                }
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
        @Override
        public void doFilter(ServletRequest request,
                             ServletResponse response,
                             FilterChain chain)
                throws IOException, ServletException {

                if (HRMMain.DEBUG) {
                        log("HRMRequestFilter:doFilter()");
                }

                if (!preprocess(request, response)) {
                        return;
                }

                Throwable problem = null;
                try {
                        chain.doFilter(request, response);
                } catch (IOException | ServletException t) {
                        // If an exception is thrown somewhere down the filter chain,
                        // we still want to execute our after processing, and then
                        // rethrow the problem after that.
                        System.out.println(t.getMessage());
                        problem = t;
                }
                
                postprocess(request, response);

                // If there was a problem, we want to rethrow it if it is
                // a known type, otherwise log it.
                if (problem != null) {
                        if (problem instanceof ServletException) {
                                throw (ServletException) problem;
                        }
                        if (problem instanceof IOException) {
                                throw (IOException) problem;
                        }
                        report_processing_error(problem, response);
                }
        }

        /**
         * Return the filter configuration object for this filter.
         *
         * @return
         */
        public FilterConfig get_filter_config() {
                return m_filter_config;
        }

        public ServletContext get_servlet_context() {
                return m_filter_config.getServletContext();
        }

        /**
         * Set the filter configuration object for this filter.
         *
         * @param filter_config The filter configuration object
         */
        public void set_filter_config(FilterConfig filter_config) {
                this.m_filter_config = filter_config;
        }

        /**
         * Destroy method for this filter
         */
        @Override
        public void destroy() {
        }

        /**
         * Init method for this filter
         *
         * @param filter_config
         */
        @Override
        public void init(FilterConfig filter_config) {
                this.m_filter_config = filter_config;
                if (filter_config != null) {
                        if (HRMMain.DEBUG) {
                                log("HRMRequestFilter:Initializing filter");
                        }
                }
        }

        /**
         * Return a String representation of this object.
         */
        @Override
        public String toString() {
                if (m_filter_config == null) {
                        return ("HRMRequestFilter()");
                }
                StringBuilder sb = new StringBuilder("HRMRequestFilter(");
                sb.append(m_filter_config);
                sb.append(")");
                return (sb.toString());
        }

        private void report_processing_error(Throwable t, ServletResponse response) {
                String stackTrace = get_stack_trace(t);

                if (stackTrace != null && !stackTrace.equals("")) {
                        try {
                                response.setContentType("text/html");
                                try (PrintStream ps = new PrintStream(response.getOutputStream());
                                     PrintWriter pw = new PrintWriter(ps)) {
                                        pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                                        // PENDING! Localize this for next official release
                                        pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                                        pw.print(stackTrace);
                                        pw.print("</pre></body>\n</html>"); //NOI18N
                                }
                                response.getOutputStream().close();
                        } catch (Exception ex) {
                                Prompt.log(Prompt.ERROR, getClass().toString(), ex.getMessage());
                        }
                } else {
                        try {
                                try (PrintStream ps = new PrintStream(response.getOutputStream())) {
                                        t.printStackTrace(ps);
                                }
                                response.getOutputStream().close();
                        } catch (Exception ex) {
                                Prompt.log(Prompt.ERROR, getClass().toString(), ex.getMessage());
                        }
                }
        }

        public static String get_stack_trace(Throwable t) {
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
                Prompt.log(Prompt.NORMAL, getClass().toString(), msg);
        }

}
