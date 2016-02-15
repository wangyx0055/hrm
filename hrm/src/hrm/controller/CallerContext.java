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

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Helper method to define a call request with caller name and parameters.
 *
 * @author davis
 */
public class CallerContext {

        private final String m_callee;
        private final String m_caller_uri;
        private Map<String, String[]> m_attris;
        private List<DataPart> m_parts;
        
        public CallerContext(String callee, String caller_uri) {
                m_callee = callee;
                m_caller_uri = caller_uri;
        }

        public void set_parameters(Map<String, String[]> attris) {
                m_attris = attris;
        }
        
        public Map<String, String[]> get_parameters() {
                return m_attris;
        }
        
        public String whos_the_callee() {
                return m_callee;
        }
        
        public String caller_uri() {
                return m_caller_uri;
        }

        public void set_data_streams(List<DataPart> data_parts) {
                m_parts = data_parts;
        }
        
        public List<DataPart> get_data_streams() {
                return m_parts;
        }
}
