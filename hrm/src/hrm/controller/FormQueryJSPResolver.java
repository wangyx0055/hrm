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
import hrm.utils.Prompt;
import hrm.utils.RMIObj;
import hrm.view.HtmlUIBuilder;
import hrm.view.HtmlUIBuilder.UIElement;
import hrm.view.HtmlUIBuilder.UINode;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jdom2.Element;

/**
 * A JSPResolver implementation to resolve query results. 
 * It takes in a collection of FormData query results and reproduce the same amount of UIs '
 * such that each of which is linked with the query result. This is useful when the set 
 * of UI content is dependent of some query results.
 * 
 * @author davis
 */
public class FormQueryJSPResolver implements JSPResolver {
        
        public class QueryResultGroup {
                public List<FormData> query_result;

                public QueryResultGroup(List<FormData> result) {
                        query_result = result;
                }
        }
        
        final private String m_name;
        final private List<QueryResultGroup> m_groups;
        final private Map<Callback, QueryResultGroup> m_callback2query = new HashMap<>();
        final private UINode m_node;
        
        private class QueryHeadVisitor implements HtmlUIBuilder.ElementVisitor {
                
                private UIElement m_query_elm;
                
                @Override
                public void element_visit(UIElement elm, UINode node) {
                        if (elm.has_category("queryhead")) {
                                m_query_elm = elm;
                        }
                }
                
                public UIElement get_query_element() {
                        return m_query_elm;
                }
        }
        
        private class QueryElementVisitor implements HtmlUIBuilder.ElementVisitor {

                private final Set<UIElement> m_query_elms = new HashSet<>();
                
                @Override
                public void element_visit(UIElement elm, UINode node) {
                        if (elm.has_category("query")) {
                                m_query_elms.add(elm);
                        }
                }
                
                public Set<UIElement> get_query_elements() {
                        return m_query_elms;
                }
                
                public void reset() {
                        m_query_elms.clear();
                }
        }
        
        private void plaintext_filler(List<FormData> formdatas, UIElement elm, String[] tokens) {
        }
        
        private void barchart_filler(List<FormData> formdatas, UIElement elm, String[] tokens) {
        }
                
        private void fill_with_form_datas(List<QueryResultGroup> groups, UINode node) {
                QueryHeadVisitor visitor = new QueryHeadVisitor();
                node.visit_element(visitor);
                UIElement query_head = visitor.get_query_element();
                // duplicate the query section and search for 
                List<UIElement> elms = node.duplicate(query_head, groups.size());
                QueryElementVisitor elm_visitor = new QueryElementVisitor();
                int i = 0;
                for (UIElement elm : elms) {
                        elm_visitor.reset();
                        node.visit_element(elm, elm_visitor);
                        Set<UIElement> query_elms = elm_visitor.get_query_elements();
                        List<FormData> formdatas = groups.get(i ++).query_result;
                        for (UIElement query_elm : query_elms) {
                                String[] tokens = query_elm.tokenize_id();
                                // determine the type of the query element and find appropiate method
                                // to fill the element with form data
                                switch (tokens[1]) {
                                case "plaintext": {
                                        plaintext_filler(formdatas, query_elm, tokens);
                                        break;
                                }
                                case "barchart": {
                                        barchart_filler(formdatas, query_elm, tokens);
                                        break;
                                }
                                default:
                                        Prompt.log(Prompt.WARNING, getClass().toString(),
                                                   "No such type as: " + tokens[1] + " in " + 
                                                   query_elm.get_id());
                                        break;
                                }
                        }
                }
        }
        
        public FormQueryJSPResolver(String name, List<QueryResultGroup> groups, HtmlUIBuilder ui) {
                m_name = name;
                m_groups = groups;
                m_node = ui.get_root_node();
                
                fill_with_form_datas(groups, m_node);
        }
        
        public FormQueryJSPResolver(String name, List<QueryResultGroup> groups, UINode node) {
                m_name = name;
                m_groups = groups;
                m_node = node;
                
                fill_with_form_datas(groups, m_node);
        }
        
        public QueryResultGroup get_query_group_from(Callback callback) {
                return m_callback2query.get(callback);
        }
        
        @Override
        public String get_name() {
                return m_name;
        }
        
        @Override
        public String toString() {
                return m_node.generate_ui();
        }
        
}
