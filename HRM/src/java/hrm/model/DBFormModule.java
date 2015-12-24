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

/**
 * Containing elements of a database form module.
 * @author davis
 */
public class DBFormModule implements hrm.utils.Serializable {
        
        public DBFormModule() {
        }

        @Override
        public byte[] serialize() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void deserialize(byte[] stream) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
        public class Hierarchy {
        }
        
        public Hierarchy get_root() {
                throw new UnsupportedOperationException("Not supported yet.");
        }
        
        public Hierarchy step_into(Hierarchy level) {
                throw new UnsupportedOperationException("Not supported yet.");
        }
        
        public Hierarchy step_out(Hierarchy level) {
                throw new UnsupportedOperationException("Not supported yet.");
        }
        
        public Hierarchy add_element(Hierarchy level, String element) {
                throw new UnsupportedOperationException("Not supported yet.");
        }
        
        public String[] get_all_elements(Hierarchy level) {
                throw new UnsupportedOperationException("Not supported yet.");
        }
        
        public void build_from_file(String hrArchiveRegistrationconf) throws DBFormModuleException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
}
