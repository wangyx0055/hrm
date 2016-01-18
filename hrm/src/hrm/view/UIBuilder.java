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

import hrm.utils.NaryTree;

/**
 * To build a generic UI which can be later resolved by any page resolvers.
 * @author davis
 */
public class UIBuilder {
        
        public enum UIElement {
                Label,
                Entry,
                LargeEntry,
                DropDownList,
                LabeledEntry,
                LargeLabeledEntry,
                LabeledDropDownList,
                LineBreak
        }
        
        public class UINode extends NaryTree<UINode> {
                private final String    m_id;
                private final String    m_name;
                
                private UINode(String id, String name) {
                        m_id = id;
                        m_name = name;
                }
                
                private String get_id() {
                        return m_id;
                }
                
                public String get_name() { 
                        return m_name;
                }
                
                public void add_label() {}
                
                public void add_large_entry() {}
                
                public void add_drop_down_list() {}
                
                public void add_line_break() {}
                
                public void add_node_hierachy(UINode child) {
                        throw new UnsupportedOperationException();
                }

                public void set_node_hierachy(UINode child, boolean to_link) {
                        throw new UnsupportedOperationException();
                }
        
                public UINode get_parent(UINode node) {
                        throw new UnsupportedOperationException();
                }

                public UINode get_child(UINode node, String name) {
                        throw new UnsupportedOperationException();
                }
        }
        
        public interface Visitor {
                public void node_visit(UINode node);
        }

        public UINode create_node(boolean as_root, String name) {
                throw new UnsupportedOperationException();
        }
        
        public UINode get_node_by_id(String id) {
                throw new UnsupportedOperationException();
        }
        
        public void visit(Visitor visitor) {
                throw new UnsupportedOperationException();
        }
}
