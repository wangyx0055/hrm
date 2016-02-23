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

import hrm.utils.Prompt;
import hrm.utils.UIDGenerator;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.io.IOUtils;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

/**
 * To build a generic UI which can be later resolved by any page resolvers.
 *
 * @author davis
 */
public class HtmlUIBuilder {

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

        public class InsertionPoint {

                private final String m_id;
                private final Element m_elm;
                private UINode m_node;

                public InsertionPoint(String id, Element elm) {
                        m_id = id;
                        m_elm = elm;
                        m_node = null;
                }

                public void link_ui_node(UINode node) {
                        m_node = node;
                }

                public UINode node_2b_inserted() {
                        return m_node;
                }

                public Element insertion_spot() {
                        return m_elm;
                }

                @Override
                public boolean equals(Object o) {
                        if (o instanceof String) {
                                return m_id.equals(o);
                        } else {
                                return false;
                        }
                }

                @Override
                public int hashCode() {
                        int hash = 5;
                        hash = 37 * hash + Objects.hashCode(this.m_id);
                        return hash;
                }
        }

        public class UINode {

                private final String m_id;
                private final String m_name;
                private final Document m_doc = new Document();
                private final Element m_head = new Element("head");
                private final Element m_body = new Element("body");
                Map<String, InsertionPoint> m_ins_points = new HashMap<>();
                private boolean is_dirty = true;
                private String m_cached_html = "";

                private void touch() {
                        is_dirty = true;
                }

                private boolean is_touched() {
                        return is_dirty;
                }

                private void save(String html) {
                        m_cached_html = html;
                        is_dirty = false;
                }

                private UINode(String id, String name) {
                        m_id = id;
                        m_name = name;
                        Element root = new Element("html");
                        root.addContent(m_head);
                        root.addContent(m_body);
                        m_doc.setRootElement(root);
                }

                private String get_id() {
                        return m_id;
                }

                public String get_name() {
                        return m_name;
                }

                public void add_label() {
                }

                public void add_large_entry() {
                }

                public void add_drop_down_list() {
                }

                public void add_line_break() {
                }

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

                @Override
                public boolean equals(Object o) {
                        if (!(o instanceof UINode)) {
                                return false;
                        }
                        return get_id().equals(((UINode) o).get_id());
                }

                @Override
                public int hashCode() {
                        int hash = 3;
                        hash = 79 * hash + Objects.hashCode(this.m_id);
                        return hash;
                }

                private Element finalize_body(UINode node) {
                        if (node == null) {
                                return null;
                        }
                        for (String inspid : node.m_ins_points.keySet()) {
                                InsertionPoint insp = node.m_ins_points.get(inspid);
                                Element elm = insp.insertion_spot();
                                Element to_be_inserted = finalize_body(insp.node_2b_inserted());
                                if (to_be_inserted != null) {
                                        elm.removeContent();
                                        append_html_element(elm, to_be_inserted.getChildren());
                                }
                        }
                        return node.m_body;
                }

                /**
                 *
                 * @return
                 */
                public String generate_ui() {
//                        if (!is_touched()) {
//                                return m_cached_html;
//                        } else {
                        finalize_body(this);
                        XMLOutputter xout = new XMLOutputter();
                        StringWriter sw = new StringWriter();
                        try {
                                xout.output(m_body, sw);
                        } catch (IOException ex) {
                                Prompt.log(Prompt.ERROR, getClass().toString(),
                                           "Failed to generate html: " + ex.getMessage());
                        }
                        //save(sw.toString());
                        return sw.toString();
//                        }
                }
                
                public String generate_page() {
//                        if (!is_touched()) {
//                                return m_cached_html;
//                        } else {
                        finalize_body(this);
                        XMLOutputter xout = new XMLOutputter();
                        StringWriter sw = new StringWriter();
                        try {
                                xout.output(m_doc, sw);
                        } catch (IOException ex) {
                                Prompt.log(Prompt.ERROR, getClass().toString(),
                                           "Failed to generate html: " + ex.getMessage());
                        }
                        //save(sw.toString());
                        return sw.toString();
//                        }
                }

                private void append_html_element(Element parent, List<Element> html1) {
                        List<Element> backup = new LinkedList<>(html1);
                        for (Element elm : backup) {
                                parent.addContent(elm.detach());
                        }
                }

                private static final String MONITORED_TAG = "div";

                private void generate_insertion_points(Element elm, Map<String, InsertionPoint> insert) {
                        List<Element> children = elm.getChildren();
                        for (Element node : children) {
                                if (node.getName().equals(MONITORED_TAG)) {
                                        Attribute id = node.getAttribute("id");
                                        if (id == null || !id.isSpecified()) {
                                                continue;
                                        }
                                        InsertionPoint p = new InsertionPoint(id.getValue(), node);
                                        insert.put(id.getValue(), p);
                                }
                                generate_insertion_points(node, insert);
                        }
                }

                public Map<String, InsertionPoint> insert_html(String html_frag, boolean insert_header) {
                        touch();

                        SAXBuilder sax = new SAXBuilder();
                        Document doc;
                        try {
                                doc = sax.build(new InputStreamReader(IOUtils.toInputStream(html_frag)));
                        } catch (JDOMException | IOException ex) {
                                Prompt.log(Prompt.ERROR, getClass().toString(),
                                           "Failed to compile the input html: " + ex.getMessage());
                                return m_ins_points;
                        }
                        Element html_root = doc.getRootElement();
                        Element head = html_root.getChild("head");
                        Element body = html_root.getChild("body");
                        if (body == null) {
                                Prompt.log(Prompt.ERROR, getClass().toString(),
                                           "Failed to compile the input html: missing body");
                                return m_ins_points;
                        }
                        if (insert_header && head != null) {
                                append_html_element(m_head, head.getChildren());
                        }
                        append_html_element(m_body, body.getChildren());
                        generate_insertion_points(m_body, m_ins_points);
                        return m_ins_points;
                }
        }

        private UINode m_root = null;
        private final Map<String, UINode> m_nodes = new HashMap<>();

        public interface NodeVisitor {

                public void node_visit(UINode node);
        }
        
        public interface ElementVisitor {
                
                public void element_visit(Element elm);
        }

        public UINode create_node(boolean as_root, String name) {
                UINode node = new UINode(name, UIDGenerator.get_uid().toString());
                m_nodes.put(node.get_id(), node);
                if (as_root) {
                        m_root = node;
                }
                return node;
        }

        public UINode get_node_by_id(String id) {
                return m_nodes.get(id);
        }

        public UINode get_root_node() {
                return m_root;
        }
        
        private void visit_node_dfs(UINode node, NodeVisitor visitor) {
                if (node == null) return;
                visitor.node_visit(node);
                for (InsertionPoint insp : node.m_ins_points.values()) {
                        visit_node_dfs(insp.m_node, visitor);
                }
        }

        public void visit_nodes(NodeVisitor visitor) {
                visit_node_dfs(get_root_node(), visitor);
        }
        
        private void visit_element_dfs(Element element, ElementVisitor visitor) {
                if (element == null) return;
                visitor.element_visit(element);
                for (Element elm : element.getChildren()) {
                        visit_element_dfs(elm, visitor);
                }
        }
        
        public void visit_element(UINode node, ElementVisitor visitor) {
                visit_element_dfs(node.m_body, visitor);
        }
}
