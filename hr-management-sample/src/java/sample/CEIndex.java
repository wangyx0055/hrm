/*
 * Copyright (C) 2016 davis
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
package sample;

import hrm.controller.CalleeContext;
import hrm.controller.ReturnValue;
import hrm.utils.Attribute;
import hrm.utils.Element;
import hrm.controller.BasicJSPResolver;
import hrm.controller.JSPResolver;
import java.awt.event.ActionEvent;
import java.util.Set;

/**
 *
 * @author davis
 */
public class CEIndex extends CalleeContext {

        
        
        private class Retrieval extends ReturnValue {

                @Override
                public String get_redirected_page_uri() {
                        return null;
                }

                @Override
                public Set<Attribute> get_session_attribute() {
                        return null;
                }

                @Override
                public Set<Attribute> get_requst_attribute() {
                        return null;
                }

                @Override
                public JSPResolver get_resolver() {
                        return new BasicJSPResolver("login");
                }
                
        }

        @Override
        public ReturnValue process(String action) {
                return new Retrieval();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
        }
        
}
