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
import hrm.utils.Element;
import hrm.utils.NaryTree;
import static hrm.view.UIBuilder.UIElement.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Resolve Page Module data to JSP page.
 *
 * @author davis
 */
public class FormModuleJSPResolver extends JSPResolver {
        private class PageObject {

//                private final JSPResolver.PageElement m_type;
//                private final String m_name;
//
//                private PageObject(JSPResolver.PageElement type, String name) {
//                        m_type = type;
//                        m_name = name;
//                }
//
//                private PageObject(JSPResolver.PageElement type) {
//                        m_type = type;
//                        m_name = null;
//                }
        }

        private final Map<String, List<NaryTree<Element>.Path<Element>>> m_data = new HashMap<>();
        private final List<PageObject> m_pageobjs = new LinkedList<>();

        /**
         * Construct a resolver using FormModule. Page object data will be
         * obtained from this module.
         *
         * @param m the FormModule.
         */
        public FormModuleJSPResolver(FormModule m) {
                // extract the structure as equivalence classes of the same secondary root
//                NaryTree.Path[] paths = m.get_structure();
//                for (NaryTree<Element>.Path<Element> path : paths) {
//                        path = path.next;               // skip the first root
//                        if (path.value == null) {
//                                continue;
//                        }
//                        Element elm = path.value;
//                        List<NaryTree<Element>.Path<Element>> lpaths = m_data.get(elm.get_name());
//                        if (lpaths != null) {
//                                // this path is of the same equivalence class
//                                lpaths.add(path);
//                        } else {
//                                // not yet seen this path
//                                lpaths = new LinkedList<>();
//                                lpaths.add(path);
//                                m_data.put(elm.get_name(), lpaths);
//                        }
//                }
        }
        
        @Override
        public String get_name() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        
        /**
         * Object that is resolvable from the FormModule.
         *
         * @param type type of page element.
         * @param name name of the resolvable.
         */
//        public void add_resolvable(JSPResolver.PageElement type, String name) {
//                m_pageobjs.add(new PageObject(type, name));
//        }
//
//        /**
//         * Object that is not resolvable from the FormModule but is still
// displayable.
//         *
//         * @param type type of page element.
//         */
//        public void add_non_resolvable(JSPResolver.PageElement type) {
//                m_pageobjs.add(new PageObject(type, null));
//        }

        public void set_name_mapping(Map<String, String> name_mapping) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        /**
         * Generate a JSP page from added elements.
         *
         * @return the JSP page.
         */
        public String resolve_page_as_string() {
                String page = "";
//                for (PageObject pageobj : m_pageobjs) {
//                        List<NaryTree<Element>.Path<Element>> lpaths = null;
//                        if (pageobj.m_name != null) {
//                                // resolvable
//                                lpaths = m_data.get(pageobj.m_name);
//                        }        
//                        switch (pageobj.m_type) {
//                                case Label: {
//                                        page += "<span id='label'>" + pageobj.m_name + "</span>\n";
//                                        break;
//                                }
//                                case Entry:
//                                case LabeledEntry: {
//                                        String initializer = "";
//                                        String label = "";
//                                        if (lpaths != null) {
//                                                // resolvable
//                                                // an entry should have 1 and only 1 path.
//                                                if (lpaths.isEmpty()) break;
//                                                NaryTree<Element>.Path<Element> path = lpaths.get(0);
//                                                if (path.next != null) initializer = path.next.name;
//                                                if (pageobj.m_type == LabeledEntry) label = pageobj.m_name;
//                                                page += label
//                                                        + "<input type=\"text\" name=\""
//                                                        + path.name + "\">"
//                                                        + initializer + "</input>\n";
//                                        } else {
//                                                // non-resolvable
//                                                if (pageobj.m_type == LabeledEntry) label = pageobj.m_name;
//                                                page += label
//                                                        + "<input type=\"text\" name=\""
//                                                        + pageobj.m_name + "\"/>\n";
//                                        }
//                                        break;
//                                }
//                                case LargeEntry:
//                                case LargeLabeledEntry: {
//                                        String initializer = "";
//                                        String label = "";
//                                        if (lpaths != null) {
//                                                // resolvable
//                                                // an entry should have 1 and only 1 path.
//                                                if (lpaths.isEmpty()) break;
//                                                NaryTree<Element>.Path<Element> path = lpaths.get(0);
//                                                if (path.next != null) initializer = path.next.name;
//                                                if (pageobj.m_type == LargeLabeledEntry) label = pageobj.m_name;
//                                                page += label
//                                                        + "<textarea name=\""
//                                                        + path.name + "\" cols=\"50\" rows=\"10\">"
//                                                        + initializer + "</textarea>\n";
//                                        } else {
//                                                // non-resolvable
//                                                if (pageobj.m_type == LargeLabeledEntry) label = pageobj.m_name;
//                                                page += label
//                                                        + "<textarea name=\""
//                                                        + pageobj.m_name + "\" cols=\"50\" rows=\"10\"/>\n";
//                                        }
//                                        break;
//                                }
//                                case DropDownList:
//                                case LabeledDropDownList: {
//                                        String label = ""; 
//                                        if (lpaths != null) {
//                                                // resolvable
//                                                // an entry should have at least 1 path
//                                                String root_name = null;
//                                                if (lpaths.isEmpty()) break;
//                                                String[] initializers = new String [lpaths.size()];
//                                                int i = 0;
//                                                for (NaryTree<Element>.Path<Element> path : lpaths) {
//                                                        root_name = path.name;
//                                                        if (path.next != null) 
//                                                                initializers[i ++] = path.next.name;
//                                                        else
//                                                                initializers[i ++] = null;
//                                                }
//                                                if (pageobj.m_type == LabeledDropDownList) label = pageobj.m_name;
//                                                page += label
//                                                        + "<select name=\""
//                                                        + root_name + "\">\n";
//                                                for (String initializer : initializers) {
//                                                        if (initializer == null) initializer = "";
//                                                        page += "<option value=\"" + initializer + "\">" + 
//                                                                initializer + "</option>\n";
//                                                }
//                                                page += "</select>\n";
//                                        } else {
//                                                // non-resolvable
//                                                if (pageobj.m_type == LabeledDropDownList) label = pageobj.m_name;
//                                                page += label
//                                                        + "<select name=\""
//                                                        + pageobj.m_name + "\"/>\n";
//                                        }
//                                        break;
//                                }
//                                case LineBreak: {
//                                        page += "<br>\n";
//                                        break;
//                                }
//                        }
//                }
                return page;
        }
}
