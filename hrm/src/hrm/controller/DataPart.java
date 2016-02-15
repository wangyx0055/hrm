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
package hrm.controller;

import hrm.utils.AsciiStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author davis
 */
public class DataPart {
        private String          m_filename;
        private InputStream     m_datastream;
        
        public DataPart() {
                m_filename = null;
                m_datastream = null;
        }
        
        public DataPart(String filename, InputStream datastream) {
                m_filename = filename;
                m_datastream = datastream;
        }
        
        public void set_file_name(String filename) {
                m_filename = filename;
        }
        
        public void set_data_stream(InputStream s) {
                m_datastream = s;
        }
        
        public String file_name() {
                return m_filename;
        }
        
        public InputStream data_stream() {
                return m_datastream;
        }
        
        @Override
        public String toString() {
                StringBuilder s = new StringBuilder();
                s.append("DataPart=").append(m_filename).append(":\n");
                try {
                        s.append(AsciiStream.extract(m_datastream));
                } catch (IOException ex) {
                        s.append("exception: ").append(ex);
                }
                return s.toString();
        }
}
