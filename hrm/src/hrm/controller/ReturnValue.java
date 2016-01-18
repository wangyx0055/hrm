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
import hrm.view.JSPResolver;
import java.util.Set;

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
