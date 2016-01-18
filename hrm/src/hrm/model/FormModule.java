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

import hrm.utils.Prompt;
import hrm.utils.RMIConstructor;
import hrm.utils.RMIObj;
import hrm.utils.Serializable;
import hrm.utils.Serializer;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Container to store groups of serializable data.
 * @author davis
 */
public class FormModule extends DataComponent implements Serializable {
        
        private final Map<String, List<RMIObj>>        m_groups = new HashMap<>();

        public FormModule(String name) {
                super(name, DataComponentFactory.FORM_MODULE_COMPONENT);
        }
        
        /**
         * Add content to a group specified by the group id.
         * @param groupid the group to add to.
         * @param obj the content to be added.
         */
        public void add_content_to(String groupid, RMIObj obj) {
                List<RMIObj> group = m_groups.get(groupid);
                if (group != null) {
                        group.add(obj);
                } else {
                        group = new LinkedList<>();
                        group.add(obj);
                        m_groups.put(groupid, group);
                }
        }
        
        /**
         * Get the group of the content by id.
         * @param groupid the group where it contains the list of contents.
         * @return contents inside the group.
         */
        public List<RMIObj> get_group_content(String groupid) {
                return m_groups.get(groupid);
        }
        
        @Override
        public String toString() {
                return "FormModule=[\n" + m_groups + "]";
        }
        
        @Override
        public boolean equals(Object o) {
               if (!(o instanceof FormModule)) return false;
               FormModule other = (FormModule) o;
               return m_groups.equals(other.m_groups);
        }
        
        @Override
        public byte[] serialize() {
                Serializer s = new Serializer();
                s.write_array_header(m_groups.size());
                for (String groupid : m_groups.keySet()) {
                        s.write_string(groupid);
                        List<RMIObj> group = m_groups.get(groupid);
                        s.write_array_header(group.size());
                        for (RMIObj obj : group) {
                                RMIConstructor.serialize_rmi_obj(obj, s);
                        }
                }
                return s.to_byte_stream();
        }

        @Override
        public void deserialize(byte[] stream) {
                Serializer s = new Serializer();
                s.from_byte_stream(stream);
                m_groups.clear();
                int n_groups = s.read_array_header();
                for (int i = 0; i < n_groups; i ++) {
                        String groupid = s.read_string();
                        int n_objs = s.read_array_header();
                        List<RMIObj> group = new LinkedList<>();
                        for (int j = 0; j < n_objs; j ++) {
                                String class_name = s.read_string();
                                try {
                                        RMIObj obj = RMIConstructor.deserialze_rmi_obj(class_name, s);
                                        group.add(obj);
                                } catch (Exception ex) {
                                        Prompt.log(Prompt.ERROR, getClass().toString(), 
                                                "Failed to deserialize RMIObj: " + class_name);
                                }
                        }
                        m_groups.put(groupid, group);
                }
        }
}
