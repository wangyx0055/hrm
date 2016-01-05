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
package hrm.utils;

import java.util.Objects;

/**
 * Represent an generic element.
 *
 * @author davis
 */
public class Element implements Serializable, Comparable {

        private String    m_name;
        private Class<?>  m_type;

        /**
         * Construct an element.
         *
         * @param name name of the element to be added.
         * @param type type of the content associated with the element.
         */
        public Element(String name, Class<?> type) {
                m_name = name;
                m_type = type;
        }

        /**
         * Construct an DBFormModule.Element.
         *
         * @param name name of the element to be added. type of this element is
         * unspecified.
         */
        public Element(String name) {
                m_name = name;
                m_type = null;
        }

        public Element() {
                m_name = null;
                m_type = null;
        }

        /**
         * @return name of the element.
         */
        public String get_name() {
                return m_name;
        }

        /**
         * @return type of the element. null if the type of the element is
         * unspecified.
         */
        public Class<?> get_type() {
                return m_type;
        }
        
        @Override
        public String toString() {
                String s = "";
                if (m_name != null) s += m_name;
                if (m_type != null) s += "<" + m_type.toString() + ">";
                else                s += "<?>";
                return s;
        }
        
        @Override
        public boolean equals(Object o) {
                if (!(o instanceof Element)) return false;
                Element other = (Element) o;
                return m_name.equals(other.m_name) && m_type.equals(other.m_type);
        }

        @Override
        public int hashCode() {
                int hash = 7;
                hash = 53 * hash + Objects.hashCode(this.m_name);
                hash = 53 * hash + Objects.hashCode(this.m_type);
                return hash;
        }

        @Override
        public byte[] serialize() {
                Serializer s = new Serializer();
                s.write_string(m_name);
                if (m_type != null)     s.write_string(m_type.getName());
                else                    s.write_string("null");
                return s.to_byte_stream();
        }

        @Override
        public void deserialize(byte[] stream) {
                Serializer s = new Serializer();
                s.from_byte_stream(stream);
                m_name = s.read_string();
                String class_name = s.read_string();
                try {
                        if (!class_name.equals("null")) m_type = Class.forName(class_name);
                        else                            m_type = null;
                } catch (ClassNotFoundException ex) {
                        Prompt.log(Prompt.ERROR, getClass().toString(), 
                                "Class not found when deserializing element: (" + 
                                        m_name + "," + class_name + ")");
                }
        }

        @Override
        public int compareTo(Object o) {
                return m_name.compareTo(((Element) o).m_name);
        }
}
