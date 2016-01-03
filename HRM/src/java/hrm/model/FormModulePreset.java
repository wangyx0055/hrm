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
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;

/**
 * Preset that contains FormModule.
 * @author davis
 */
public class FormModulePreset extends SystemPreset {
        private HashMap<String, FormModule> m_modules = new HashMap<>();

        public FormModulePreset(String name) {
                super(name, SystemPresetFactory.DBFORM_MODULE_PRESET);
        }

        public FormModule add_module(String module_name) {
                if (m_modules.containsKey(module_name)) return null;
                FormModule module = new FormModule(module_name);
                m_modules.put(module_name, module);
                return module;
        }
        
        public FormModule add_module_from_file(InputStream in) throws SystemPresetException {
                FormModule module = new FormModule(in);
                String module_name = module.get_module_name();
                if (m_modules.containsKey(module_name)) return null;
                m_modules.put(module_name, module);
                return module;
        }
       
        public FormModule get_module(String module_name) {
                return m_modules.get(module_name);
        }
        
        public Collection<FormModule> export_all_modules() {
                return m_modules.values();
        }
        
        @Override
        public byte[] serialize() {
                Serializer s = new Serializer();
                s.write_array_header(m_modules.size());
                for (String module_name : m_modules.keySet()) {
                        FormModule module = m_modules.get(module_name);
                        s.write_serialized_stream(module.serialize());
                }
                return s.to_byte_stream();
        }

        @Override
        public void deserialize(byte[] stream) {
                Serializer s = new Serializer();
                s.from_byte_stream(stream);
                
                int l = s.read_array_header();
                m_modules = new HashMap<>(l);
                for (int i = 0; i < l; i ++) {
                        byte[] obj = s.read_serialized_stream();
                        FormModule module = new FormModule("");
                        module.deserialize(obj);
                        m_modules.put(module.get_module_name(), module);
                }
        }
        
        @Override
        public boolean equals(Object o) {
                if (!(o instanceof FormModulePreset))
                        return false;
                return m_modules.equals(((FormModulePreset) o).m_modules);
        }

        @Override
        public int hashCode() {
                return m_modules.hashCode();
        }
        
        @Override
        public String toString() {
                return m_modules.toString();
        }

        @Override
        public void load_from_file(InputStream in) throws SystemPresetException {
                add_module_from_file(in);
        }
}
