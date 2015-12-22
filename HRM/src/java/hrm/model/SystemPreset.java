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
 *
 * @author davis
 */
public class SystemPreset {
        private final String    m_name;
        
        SystemPreset(String name) {
                m_name = name;
        }
        
        @Override
        public boolean equals(Object o) {
                if (!(o instanceof String)) return false;
                return m_name.equals((String) o);
        }

        @Override
        public int hashCode() {
                return m_name.hashCode();
        }
}
