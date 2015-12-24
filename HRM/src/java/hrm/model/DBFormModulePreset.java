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

import hrm.utils.Serializer;
import java.util.HashMap;

/**
 * Preset that contains DBFormModule.
 * @author davis
 */
public class DBFormModulePreset extends SystemPreset {
        private final HashMap<String, DBFormModule> m_modules = new HashMap<>();

        public DBFormModulePreset(String name) {
                super(name, SystemPresetFactory.DBFORM_MODULE_PRESET);
        }

        public DBFormModule add_module(String module_name) {
                if (m_modules.containsKey(module_name)) return null;
                DBFormModule module = new DBFormModule();
                m_modules.put(module_name, module);
                return module;
        }
       
        public DBFormModule get_module(String module_name) {
                return m_modules.get(module_name);
        }

        public void add_module_from_directory(String dir) {
        }

        @Override
        public byte[] serialize() {
                Serializer s = new Serializer();
                return s.to_byte_stream();
        }

        @Override
        public void deserialize(byte[] stream) {
                Serializer s = new Serializer();
                s.from_byte_stream(stream);
        }
        
        @Override
        public boolean equals(Object o) {
                if (!(o instanceof DBFormModulePreset))
                        return false;
                return m_modules.equals(((DBFormModulePreset) o).m_modules);
        }

        @Override
        public int hashCode() {
                return m_modules.hashCode();
        }
        
        @Override
        public String toString() {
                return m_modules.toString();
        }
}
