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

import hrm.model.FormData;
import hrm.view.HtmlUIBuilder;
import java.util.Collection;

/**
 * A JSPResolver implementation to resolve query results. 
 * It takes in a collection of FormData query results and reproduce the same amount of UIs '
 * such that each of which is linked with the query result. This is useful when the set 
 * of UI content is dependent of some query results.
 * 
 * @author davis
 */
public class FormQueryJSPResolver implements JSPResolver {
        
        public FormQueryJSPResolver(Collection<FormData> formdatas, HtmlUIBuilder ui) {
        }
                
        @Override
        public String get_name() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
        @Override
        public String toString() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
}
