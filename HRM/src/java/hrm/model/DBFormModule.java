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

import hrm.utils.Element;
import hrm.utils.NaryTree;
import hrm.utils.Serializer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Containing elements and hierarchical structure of a database form module.
 * @author davis
 */
public class DBFormModule extends NaryTree<Element> implements hrm.utils.Serializable {
        @Override
        public byte[] serialize() {
                // serialize the structural path
                Serializer s = new Serializer();
                Path<Element>[] paths = super.get_all_paths();
                
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
                        }
                        s.write_int(0);                 // disable continue
                }
                return s.to_byte_stream();
        }

        @Override
        public void deserialize(byte[] stream) {
                // deserialize all paths
                Serializer s = new Serializer();
                s.from_byte_stream(stream);
                
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
         */
        public DBFormModule() {
        }
        
        /**
         * For internal use.
         * @param as_root root level.
         */
        private DBFormModule(NaryTree<Element> as_root) {
                super(as_root);
        }
        
        /**
         * Add a key to this form module.
         * @param child human-readable identifier for this key.
         * @param elm the key element.
         * @return module that contains the key.
         */
        public DBFormModule add_key(String child, Element elm) {
                return new DBFormModule(super.add_child(child, elm));
        }
        
        /**
         * All keys that are in this database form module.
         * @return set of keys
         */
        public Set<Element> get_keys() {
                Map<String, NaryTree<Element>> children = super.get_all_children();
                HashSet<Element> elm_set = new HashSet<>();
                children.values().stream().forEach((node) -> {
                        elm_set.add(node.get_value());
                });
                return elm_set;
        }
        
        public NaryTree<Element>.Path<Element>[] get_structure() {
                return super.get_all_paths();
        }

        public void build_from_file(String hrArchiveRegistrationconf) throws DBFormModuleException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
}
