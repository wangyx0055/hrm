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

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Utility to serialize and de-serialize an object.
 * @author davis
 */
public class Serializer {
        private final Queue<Byte>       m_stream = new LinkedList<>();
        
        public void write_int(int n) {
                byte n3 = (byte) (n >>> 24);
                byte n2 = (byte) ((n >>> 16) & 0XFF);
                byte n1 = (byte) ((n >>> 8) & 0XFF);
                byte n0 = (byte) (n & 0XFF);
                m_stream.add(n0);
                m_stream.add(n1);
                m_stream.add(n2);
                m_stream.add(n3);
        }
        
        public int read_int() {
                byte n0 = m_stream.poll();
                byte n1 = m_stream.poll();
                byte n2 = m_stream.poll();
                byte n3 = m_stream.poll();
                return (n3 << 24) | (n2 << 16) | (n1 << 8) | (n0);
        }
        
        public void write_string(String s) {
                byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
                write_array_header(bytes.length);
                for (byte b : bytes) {
                        m_stream.add(b);
                }
        }
        
        public String read_string() {
                int l = read_array_header();
                byte[] bytes = new byte [l];
                for (int i = 0; i < bytes.length; i ++) {
                        bytes[i] = m_stream.poll();
                }
                return new String(bytes, StandardCharsets.UTF_8);
        }
        
        public void write_array_header(int size) {
                write_int(size);
        }
        
        public int read_array_header() {
                return read_int();
        }
        
        public void write_serialized_stream(byte[] stream) {
                write_array_header(stream.length);
                for (byte b : stream) {
                        m_stream.add(b);
                }
        }
        
        public byte[] read_serialized_stream() {
                byte[] stream = new byte[read_array_header()];
                for (int i = 0; i < stream.length; i ++) {
                        stream[i] = m_stream.poll();
                }
                return stream;
        }
        
        public byte[] to_byte_stream() {
                // added an extra byte to ensure that it always has something
                byte[] bytes = new byte [m_stream.size() + 1];
                int i = 1;
                while (!m_stream.isEmpty()) {
                        bytes[i ++] = m_stream.poll();
                }
                return bytes;
        }
        
        public void from_byte_stream(byte[] bytes) {
                // construct the byte stream, ps: should poll off the marker byte
                for (int i = 1; i < bytes.length; i ++) {
                        m_stream.add(bytes[i]);
                }
        }
}
