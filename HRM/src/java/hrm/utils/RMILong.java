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

/**
 * A decorator to add RMI characteristics to a Java Long.
 * @author davis
 */
public class RMILong implements RMIObj {
        private Long    m_long;
        
        public RMILong(Long l) {
                m_long = l;
        }

        @Override
        public String get_class_name() {
                return this.getClass().getName();
        }

        @Override
        public Object get_object() {
                return m_long;
        }

        @Override
        public byte[] serialize() {
                Serializer s = new Serializer();
                s.write_long(m_long);
                return s.to_byte_stream();
        }

        @Override
        public void deserialize(byte[] stream) {
                Serializer s = new Serializer();
                m_long = s.read_long();
        }
}
