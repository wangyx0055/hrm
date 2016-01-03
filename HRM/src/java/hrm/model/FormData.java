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
package hrm.model;

import hrm.utils.Attribute;
import hrm.utils.Element;
import hrm.utils.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * To store form data.
 * @author davis
 */
public class FormData implements Serializable {
        private final Map<String, Attribute>  m_attris = new HashMap();
        
        public FormData() {
        }
        
        public FormData(Set<Attribute> attris) {
                for (Attribute attri : attris) {
                        m_attris.put(attri.get_name(), attri);
                }
        }
        
        public void add_attribute(Attribute attri) {
                m_attris.put(attri.get_name(), attri);
        }
        
        public void update_attribute(String name, Attribute attri) {
                m_attris.put(name, attri);
        }
        
        public void remove_attribute(String name) {
                m_attris.remove(name);
        }
        
        public Attribute get_attribute(String name) {
                return m_attris.get(name);
        }
        
        public Set<Attribute> get_attributes() {
                HashSet<Attribute> attris = new HashSet<>();
                for (Attribute attri : m_attris.values()) {
                        attris.add(attri);
                }
                return attris;
        }
        
        public List<Attribute> get_ordered_attribute(List<Element> elms) {
                List<Attribute> attris = new LinkedList<>();
                for (Element elm : elms) {
                        Attribute attri = m_attris.get(elm.get_name());
                        if (attri == null) continue;
                        attris.add(attri);
                }
                return attris;
        }

        @Override
        public byte[] serialize() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void deserialize(byte[] stream) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
}
