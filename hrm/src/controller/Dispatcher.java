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
import hrm.view.JSPResolver;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This will dispatch the call with caller information to the correct callee.
 * @author davis
 */
public class Dispatcher {
        
        private final Map<String, CalleeContext>[]      m_call_map;
        
        public enum PageCategory {
                JspPage,
                NUM_PAGE_CATEGORIES
        }
        
        public Dispatcher() {
                int num_page_cate = PageCategory.NUM_PAGE_CATEGORIES.ordinal();
                m_call_map = new Map [num_page_cate];
                for (int i = 0; i < num_page_cate; i ++) {
                        m_call_map[i] = new HashMap<> ();
                }
        }

        /**
         * Helper method to store returned information from a controller.
         *
         * @author davis
         */
        public interface ReturnValue {

                public String get_redirected_page_uri();

                public Set<Attribute> get_session_attribute();

                public Set<Attribute> get_requst_attribute();

                public JSPResolver get_resolver();
        }

        /**
         * Helper method to generate a call with caller name and parameters.
         *
         * @author davis
         */
        public class CallerContext {
                private final String            m_caller;
                private final Set<Attribute>    m_attri = new HashSet();

                public CallerContext(String caller) {
                        m_caller = caller;
                }

                public void add_parameter(Attribute attri) {
                        m_attri.add(attri);
                }
        }

        /**
         * Helper method to generate a call with callee name and parameters.
         * @author davis
         */
        public interface CalleeContext {

                public void add_params(Set<Attribute> attri);
                
                public ReturnValue get_return_value();
        }

        public ReturnValue dispatch_jsp(CallerContext call) {
                CalleeContext callee = m_call_map[PageCategory.JspPage.ordinal()].get(call.m_caller);
                if (callee == null) return null;
                callee.add_params(call.m_attri);
                return callee.get_return_value();
        }

        public void register_controller_call(CalleeContext context, String mapped_call, PageCategory cate) {
                m_call_map[cate.ordinal()].put(mapped_call, context);
        }
        
        public CallerContext get_caller_context(String caller) {
                return new CallerContext(caller);
        }
}
