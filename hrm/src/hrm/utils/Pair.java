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
package hrm.utils;

import java.util.Objects;

/**
 * Represent an ordered pair.
 * @author davis
 * @param <T> first parameter
 * @param <V> second parameter
 */
public class Pair<T, V> {
        public T        first;
        public V        second;
        
        public Pair(T first, V second) {
                this.first = first;
                this.second = second;
        }
        
        public Pair() {
                first = null;
                second = null;
        }
        
        @Override
        public boolean equals(Object o) {
                if (!(o instanceof Pair)) return false;
                return ((Pair) o).first.equals(first) && ((Pair) o).second.equals(second);
        }

        @Override
        public int hashCode() {
                int hash = 7;
                hash = 79 * hash + Objects.hashCode(this.first);
                hash = 79 * hash + Objects.hashCode(this.second);
                return hash;
        }
        
        @Override
        public String toString() {
                return "<" + first + "," + second + ">";
        }
}
