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

import hrm.utils.Attribute;
import java.util.Set;

/**
 *
 * @author davis
 */
public class Dispatcher {

        public enum PageCategory {
                JspPage
        }

        /**
         * Helper method to store returned information from a controller.
         *
         * @author davis
         */
        public class ReturnValue {

                public String get_redirected_page_uri() {
                        return null;
                }

                public Set<Attribute> get_session_attribute() {
                        return null;
                }

                public Set<Attribute> get_requst_attribute() {
                        return null;
                }

                public Class<?> get_controller_class() {
                        return null;
                }
        }

        /**
         * Helper method to generate a call with caller name and parameters.
         *
         * @author davis
         */
        public class CallerContext {

                public CallerContext(String caller) {
                }

                public void add_parameter(String param, Object value) {
                }

                public Object get_paramter(String param) {
                        return null;
                }
        }

        /**
         * Helper method to generate a call with callee name and parameters.
         * @author davis
         */
        public class CalleeContext {

                public CalleeContext(String callee) {
                }

                public void add_param_name(String name, Class<?> type) {
                }

                public Object get_param(String name) {
                        return null;
                }
        }

        public ReturnValue dispatch_jsp(CallerContext call) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public void register_controller_call(CalleeContext context, String mapped_page, PageCategory cate) {
        }
        
        public CallerContext get_caller_context(String caller) {
                return new CallerContext(caller);
        }
        
        public CalleeContext get_callee_context(String callee) {
                return new CalleeContext(callee);
        }
}
