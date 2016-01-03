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
package hrm.model;

import hrm.utils.Attribute;
import hrm.utils.Element;
import hrm.utils.NaryTree;
import hrm.utils.Prompt;
import hrm.utils.Serializer;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * Containing elements and hierarchical structure of a database form module.
 * @author davis
 */
public final class FormModule extends NaryTree<Element> implements hrm.utils.Serializable {
        private String            m_module_name;
        
        @Override
        public byte[] serialize() {
                // serialize the structural path
                Serializer s = new Serializer();
                Path<Element>[] paths = super.get_all_paths();
                
                // the module name
                s.write_string(m_module_name);
                
                // the tree structure
                s.write_array_header(paths.length);
                for (Path<Element> path : paths) {
                        // encode a path to the stream
                        boolean is_root = true;
                        while (path.next != null) {
                                if (!is_root) {
                                        s.write_int(1);         // enable continue
                                        s.write_string(path.name);
                                        s.write_serialized_stream(path.value.serialize());
                                } else {
                                        is_root = false;
                                }
                                path = path.next;
                        }
                        if (!is_root) {
                                s.write_int(1);         // enable continue
                                s.write_string(path.name);
                                s.write_serialized_stream(path.value.serialize());
                        }
                        s.write_int(0);                 // disable continue
                }
                return s.to_byte_stream();
        }

        @Override
        public void deserialize(byte[] stream) {
                Serializer s = new Serializer();
                s.from_byte_stream(stream);
                
                // dserialize the module name
                m_module_name = s.read_string();
                
                // deserialize all paths                
                int l = s.read_array_header();
                for (int i = 0; i < l; i ++) {
                        // extract a path from the stream
                        NaryTree<Element> current = this;
                        while (s.read_int() != 0) {
                                // there is a path then
                                String  child_name = s.read_string();
                                Element value = new Element();
                                value.deserialize(s.read_serialized_stream());
                                
                                current = current.add_child(child_name, value);
                        }
                }
        }
        
        /**
         * Construct a database form module.
         * @param module_name name of this module.
         */
        public FormModule(String module_name) {
                m_module_name = module_name;
        }
        
        /**
         * Construct a database form module from input stream.
         * @param in the input stream that contains the formatted data.
         * @throws hrm.model.SystemPresetException
         */
        public FormModule(InputStream in) throws SystemPresetException {
                m_module_name = build_from_file(in);
        }
        
        /**
         * For internal use.
         * @param as_root root level.
         */
        private FormModule(NaryTree<Element> as_root, String module_name) {
                super(as_root);
                m_module_name = module_name;
        }
        
        /**
         * Add a key to this form module.
         * @param child human-readable identifier for this key.
         * @param elm the key element.
         * @return module that contains the key.
         */
        public FormModule add_key(String child, Element elm) {
                return new FormModule(super.add_child(child, elm), m_module_name);
        }
        
        /**
         * All keys that are in this database form module.
         * @return set of keys. The key set is guaranteed to have fixed traversal order if
         * the set of elements are the same.
         */
        public TreeSet<Element> get_keys() {
                Map<String, NaryTree<Element>> children = super.get_all_children();
                TreeSet<Element> elm_set = new TreeSet<>();
                children.values().stream().forEach((node) -> {
                        elm_set.add(node.get_value());
                });
                return elm_set;
        }
        
        public List<Element> get_ordered_keys(List<String> key_names) {
                List<Element> elms = new LinkedList<>();
                Map<String, NaryTree<Element>> children = super.get_all_children();
                for (String name : key_names) {
                        NaryTree<Element> elm = children.get(name);
                        if (elm == null) return null;
                        elms.add(elm.get_value());
                }
                return elms;
        }
        
        /**
         * Get the structure of the FormModule.
         * @return the structural path of the form.
         */
        public NaryTree<Element>.Path<Element>[] get_structure() {
                return super.get_all_paths();
        }
        
        /**
         * Construct a FormModule from file.
         * @param stream the stream that contains the configuration..
         * @return the module name specified by the file.
         * @throws SystemPresetException 
         */
        public String build_from_file(InputStream stream) throws SystemPresetException {
                String module_name = null;
                SAXBuilder builder = new SAXBuilder();
                try {
                        Document document = (Document) builder.build(stream);
                        org.jdom2.Element root_node = document.getRootElement();
                        if (!root_node.getName().equals("module")) {
                                throw new JDOMException("Invalid DBFormModule file: <module> block not found");
                        }
                        module_name = root_node.getAttributeValue("name");
                        if (module_name == null) {
                                throw new JDOMException("Invalid DBFormModule file: <name> attribute for "
                                                + "module tag is mandatory");
                        }
                        m_module_name = module_name;
                        
                        // extract keys
                        List<org.jdom2.Element> keys = root_node.getChildren("key");
                        if (keys == null || keys.isEmpty()) {
                                throw new JDOMException("Invalid DBFormModule file: <module> block not found");
                        }
                        for (org.jdom2.Element key : keys) {
                                String name = key.getAttributeValue("name");
                                String type = key.getAttributeValue("type");
                                if (name == null) {
                                        throw new JDOMException("Invalid DBFormModule file: <name> attribute for "
                                                + "key tag is mandatory");
                                }
                                Class<?> class_type;
                                try {
                                        class_type = Class.forName(type);
                                        add_key(name, new Element(name, class_type));
                                } catch (ClassNotFoundException ex) {
                                        throw new JDOMException("type:" + type + " for key:" + name + 
                                                " couldn't be found");
                                }
                        }
                        // extract all attributes
                        Map<List<org.jdom2.Element>, Integer> attris = new HashMap<>();
                        attris.put(root_node.getChildren("attribute"), 0);
                        attris.put(root_node.getChildren("key"), 0);
                        
                        Map<List<org.jdom2.Element>, Integer> tmp = new HashMap<>();
                        
                        List<NaryTree<Element>> parent  = new LinkedList<>();
                        parent.add(this);
                        List<NaryTree<Element>> children  = new LinkedList<>();
                        while (!attris.isEmpty()) {
                                int i = 0;
                                int iparent;
                                for (List<org.jdom2.Element> nodes : attris.keySet()) {
                                        iparent = attris.get(nodes);
                                        for (org.jdom2.Element node : nodes) {
                                                String name = node.getAttributeValue("name");
                                                String type = node.getAttributeValue("type");
                                                if (name == null) {
                                                        throw new JDOMException("Invalid DBFormModule file: <name> attribute for "
                                                                + "attribute tag is mandatory");
                                                }
                                                Class<?> class_type;
                                                NaryTree<Element> child;
                                                try {
                                                        class_type = Class.forName(type);
                                                        child = parent.get(iparent).
                                                                add_child(name, new Element(name, class_type));
                                                } catch (ClassNotFoundException ex) {
                                                        child = parent.get(iparent).
                                                                add_child(name, new Element(name));
                                                }
                                                children.add(child);
                                                List<org.jdom2.Element> child_attris = node.getChildren("attribute");
                                                tmp.put(child_attris, i ++);
                                        }
                                        
                                }
                                attris.clear();
                                attris = new HashMap<>(tmp);
                                tmp.clear();
                                        
                                parent.clear();
                                parent.addAll(children);
                                children.clear();
                        }
                } catch (JDOMException | IOException ex) {
                        throw new SystemPresetException(SystemPresetException.Error.LoadingError).
                                        add_extra_info(ex.getMessage());
                }
                return module_name;
        }
        
        /**
         * @return name of the module.
         */
        public String get_module_name() {
                return m_module_name;
        }
        
        @Override
        public String toString() {
                return "DatabaseFormModule:" + m_module_name + " = [\n" + super.toString() + "\n]";
        }
}
