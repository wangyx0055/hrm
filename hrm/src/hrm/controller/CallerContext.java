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
package hrm.controller;

import hrm.utils.Attribute;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper method to define a call request with caller name and parameters.
 *
 * @author davis
 */
public class CallerContext {

        private final String m_caller;
        private final Set<Attribute> m_attri = new HashSet();

        public CallerContext(String caller) {
                m_caller = caller;
        }

        public void add_parameter(Attribute attri) {
                m_attri.add(attri);
        }
        
        public Set<Attribute> get_attributes() {
                return m_attri;
        }
        
        public String whos_the_caller() {
                return m_caller;
        } 
}
