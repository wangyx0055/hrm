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
package hrm.view;

import java.util.Map;

/**
 * Resolve page element to JSP page.
 * @author davis
 */
public interface JSPResolver {

        public enum PageElement {
                Label,
                Entry,
                LargeEntry,
                DropDownList,
                LabeledEntry,
                LargeLabeledEntry,
                LabeledDropDownList,
                LineBreak
        }
        
        public void set_name_mapping(Map<String, String> name_mapping);
        
        public void add_resolvable(JSPResolver.PageElement type, String name);
        
        public void add_non_resolvable(JSPResolver.PageElement type);
        
        public String resolve_page_as_string();
}
