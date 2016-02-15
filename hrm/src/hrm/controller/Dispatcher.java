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

import hrm.utils.FA;
import hrm.utils.Prompt;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This will dispatch the call with caller information to the correct callee.
 * @author davis
 */
public class Dispatcher {
        
        private final Map<String, CalleeContext>[]      m_call_map;
        private final FA                                m_pageflow = new FA();
        
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

        public ReturnValue dispatch_jsp(CallerContext call) {
                CalleeContext callee = 
                        m_call_map[PageCategory.JspPage.ordinal()].get(call.whos_the_callee());
                if (callee == null) return null;
                try {
                        return callee.get_return_value(call.get_parameters(),
                                                       call.get_data_streams(),
                                                       call.caller_uri());
                } catch (Exception ex) {
                        Prompt.log(Prompt.WARNING, getClass().toString(), 
                                   "Exception encountered when processing the callee: " + callee +
                                   ", Details: " + ex.getMessage());
                        return null;
                }
        }

        public void register_controller_call(CalleeContext context, String mapped_call, PageCategory cate) {
                m_call_map[cate.ordinal()].put(mapped_call, context);
        }
        
        public void register_controller_pageflow(CalleeContext from, CalleeContext to, PageFlow flow) {
        }
}
