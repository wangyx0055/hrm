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
package hrm.utils;

import java.util.Objects;

/**
 * A decorator to add RMI characteristics to a Java String.
 * @author davis
 */
public class RMIInteger implements RMIObj {
        private Integer  m_integer;
        
        public RMIInteger() {
        }
        
        public RMIInteger(Integer integer) {
                m_integer = integer;
        }

        @Override
        public String get_class_name() {
                return this.getClass().getName();
        }

        @Override
        public Object get_object() {
                return m_integer;
        }

        @Override
        public byte[] serialize() {
                Serializer s = new Serializer();
                s.write_int(m_integer);
                return s.to_byte_stream();
        }

        @Override
        public void deserialize(byte[] stream) {
                Serializer s = new Serializer();
                s.from_byte_stream(stream);
                m_integer = s.read_int();
        }

        @Override
        public String toString() {
                return "RMIInteger=" + m_integer.toString();
        }
        
        @Override
        public boolean equals(Object o) {
                if (!(o instanceof RMIInteger)) return false;
                RMIInteger other = (RMIInteger) o;
                return m_integer.equals(other.m_integer);
        }

        @Override
        public int hashCode() {
                int hash = 7;
                hash = 83 * hash + Objects.hashCode(this.m_integer);
                return hash;
        }
}
