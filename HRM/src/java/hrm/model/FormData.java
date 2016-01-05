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
package hrm.model;

import hrm.utils.Attribute;
import hrm.utils.Element;
import hrm.utils.Pair;
import hrm.utils.Prompt;
import hrm.utils.RMIObj;
import hrm.utils.Serializable;
import hrm.utils.Serializer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * To store form data.
 *
 * @author davis
 */
public class FormData implements Serializable {

        private final Map<String, RMIObj> m_attris = new HashMap();

        public FormData() {
        }

        public FormData(Map<String, RMIObj> attris) {
                m_attris.putAll(attris);
        }

        public void add_attribute(String s, RMIObj obj) {
                m_attris.put(s, obj);
        }

        public void update_attribute(String name, RMIObj obj) {
                m_attris.put(name, obj);
        }

        public void remove_attribute(String name) {
                m_attris.remove(name);
        }

        public RMIObj get_attribute(String name) {
                return m_attris.get(name);
        }

        public Map<String, RMIObj> get_attributes() {
                return new HashMap<>(m_attris);
        }

        public List<Pair<String, RMIObj>> get_ordered_attribute(List<Element> elms) {
                List<Pair<String, RMIObj>> attris = new LinkedList<>();
                for (Element elm : elms) {
                        RMIObj obj = m_attris.get(elm.get_name());
                        if (obj == null) {
                                continue;
                        }
                        attris.add(new Pair(elm.get_name(), obj));
                }
                return attris;
        }
        
        public boolean equals(Object o) {
                if (!(o instanceof FormData)) return false;
                FormData other = (FormData) o;
                return m_attris.equals(other.m_attris);
        }
        
        @Override
        public String toString() {
                return "FormData = [" + m_attris + "]";
        }

        @Override
        public byte[] serialize() {
                Serializer s = new Serializer();
                s.write_array_header(m_attris.size());

                for (String attri_name : m_attris.keySet()) {
                        s.write_string(attri_name);
                        RMIObj obj = m_attris.get(attri_name);
                        s.write_string(obj.get_class_name());
                        s.write_serialized_stream(obj.serialize());
                }
                return s.to_byte_stream();
        }

        @Override
        public void deserialize(byte[] stream) {
                m_attris.clear();

                Serializer s = new Serializer();
                s.from_byte_stream(stream);
                int l = s.read_array_header();
                for (int i = 0; i < l; i++) {
                        String attri_name = s.read_string();
                        String class_name = s.read_string();
                        try {
                                Class<?> clazz = Class.forName(class_name);
                                Constructor<?> ctor = clazz.getConstructor();
                                RMIObj obj = (RMIObj) ctor.newInstance();
                                obj.deserialize(s.read_serialized_stream());
                                m_attris.put(attri_name, obj);
                        } catch (ClassNotFoundException |
                                NoSuchMethodException |
                                SecurityException |
                                InstantiationException |
                                IllegalAccessException |
                                IllegalArgumentException |
                                InvocationTargetException ex) {
                                Prompt.log(Prompt.ERROR, getClass().toString(),
                                        "Failed to reflexively construct the class: "
                                        + class_name + ", Details: " + ex.getMessage());
                        }
                }
        }
}
