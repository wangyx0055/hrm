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

import hrm.view.HtmlUIBuilder;

/**
 * Simple implementation of JSPResolver which takes in the HtmlUIBuilder and output
 all the UI contained in it.
 *
 * @author davis
 */
public class BasicJSPResolver implements JSPResolver {

        private final String m_name;
        private final HtmlUIBuilder m_ui;

        public BasicJSPResolver(String name, HtmlUIBuilder ui) {
                m_name = name;
                m_ui = ui;
        }

        @Override
        public String get_name() {
                return m_name;
        }

        @Override
        public String toString() {
                return m_ui.get_root_node().generate_ui();
        }
}
