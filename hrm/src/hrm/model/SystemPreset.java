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

import java.io.InputStream;

/**
 * Define a system preset.
 * @author davis
 */
public abstract class SystemPreset implements hrm.utils.Serializable {
        private final String    m_name;
        private final int       m_type;
        
        public abstract void load_from_file(InputStream in) throws SystemPresetException;
        
        /**
         * Construct the system preset with a name constant.
         * @param name Name of this system preset.
         */
        SystemPreset(String name, int type) {
                m_name = name;
                m_type = type;
        }
        
        /**
         * Compare the object against a name.
         * @param o the name to be compared.
         * @return true if the name equals to this object's.
         */
        @Override
        public boolean equals(Object o) {
                if (!(o instanceof String)) return false;
                return m_name.equals((String) o);
        }

        @Override
        public int hashCode() {
                return m_name.hashCode();
        }

        public String get_name() {
                return m_name;
        }
        
        public int get_type() {
                return m_type;
        }
}
