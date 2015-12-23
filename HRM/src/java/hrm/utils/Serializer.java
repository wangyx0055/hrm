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

import java.util.LinkedList;
import java.util.Queue;

/**
 * Utility to serialize and de-serialize an object.
 * @author davis
 */
public class Serializer {
        private final Queue<Byte>       m_stream = new LinkedList<>();
        
        public void write_string(String s) {
        }
        
        public String read_string() {
                return null;
        }
        
        public byte[] to_byte_stream() {
                byte[] bytes = new byte [m_stream.size()];
                int i = 0;
                while (!m_stream.isEmpty()) {
                        bytes[i ++] = m_stream.poll();
                }
                return bytes;
        }
        
        public void from_byte_stream(byte[] bytes) {
                for (byte b : bytes) {
                        m_stream.add(b);
                }
        }
}
