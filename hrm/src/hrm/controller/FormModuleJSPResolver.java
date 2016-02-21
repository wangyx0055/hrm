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

import hrm.model.FormModule;
import hrm.view.UIBuilder;

/**
 * A JSPResolver implementation to resolve internal FormModule correspondence. 
 * It takes in a FormModule and UIBuilder then link the elements in the UIBuilder 
 * with the associated data in the FormModule. This is particularly useful if 
 * you want to deal with elements with preset information lie within the DataCompoenentManager.
 *
 * @author davis
 */
public class FormModuleJSPResolver implements JSPResolver {

        /**
         * Constructor.
         *
         * @param m the FormModule.
         * @param ui UIBuilder which contains UIs that have already configured.
         */
        public FormModuleJSPResolver(FormModule m, UIBuilder ui) {
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
