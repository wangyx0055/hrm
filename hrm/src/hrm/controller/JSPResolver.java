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

/**
 * Represents one type of ResponseData that can be visualized in the form of JSP page.
 * A JSPResolver will return JSP code as string through the toString method.
 * 
 * @author davis
 */
public interface JSPResolver extends ResponseData {
        
        /**
         * @return unique name of the resolver.
         */
        public String get_name();
        
        /**
         * Resolve the entire page.
         * @return resolved JSP code.
         */
        @Override
        public String toString();
}
