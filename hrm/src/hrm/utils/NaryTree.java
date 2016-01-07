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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

/**
 * Generic N-ary Tree data type.
 *
 * @author davis
 * @param <T> type of the node value.
 */
public class NaryTree<T>  {
        private NaryTree<T>                     m_parent;
        private HashMap<String, NaryTree<T>>    m_children;
        private String                          m_name;
        private T                               m_value;

        /**
         * Construct the n-ary tree as the root.
         */
        public NaryTree() {
                m_parent        = null;
                m_name          = "root";
                m_children      = new HashMap<>();
                m_value         = null;
        }
        
        /**
         * Use predefined root for construction.
         * @param as_root the predefined root.
         */
        public NaryTree(NaryTree as_root) {
                m_parent = as_root.m_parent;
                m_children = as_root.m_children;
                if (as_root.m_value != null)    m_value = (T) as_root.m_value;
                else                            m_value = null;
                m_name = as_root.m_name;
        }

        public class Path<T> {
                public T value;
                public String name;
                public Path next;
                public Path previous;
                
                public Path() {
                        name = null;
                        value = null;
                        next = null;
                        previous = null;
                }
                
                public Queue<T> to_value_sequence() {
                        Queue<T> seq = new LinkedList<>();
                        Path<T> p = this;
                        while (p.next != null) {
                                if (p.value != null) seq.add(p.value);
                                p = p.next;
                        }
                        if (p.value != null) seq.add(p.value);
                        return seq;
                }
                
                @Override
                public String toString() {
                        String s = "";
                        Path<T> p = this;
                        while (p.next != null) {
                                if (p.value != null) s += "(" + p.name + "," + p.value + "),";
                                p = p.next;
                        }
                        if (p.value != null) s += "(" + p.name + "," + p.value + ")";
                        return s;
                }
                
                @Override
                public Path clone() {
                        Path p = this;
                        Path clone = new Path();
                        Path curr = clone;
                        Path prev = null;
                        while (p.next != null) {
                                curr.name = p.name;
                                curr.value = p.value;
                                curr.previous = prev;
                                
                                curr.next = new Path();
                                curr = curr.next;
                                p = p.next;
                        }
                        curr.name = p.name;
                        curr.value = p.value;
                        curr.next = null;
                        curr.previous = prev;
                        return clone;
                }
        }
        
        /**
         * add_child a child under the current node.
         * Note that if the name of the child specified has already been existed, the value will be overridden.
         * @param child name of the child to be inserted.
         * @param value value of the child.
         * @return the child node inserted.
         */
        public NaryTree<T> add_child(String child, T value) {
                if (m_children.containsKey(child)) {
                        NaryTree<T> child_node = m_children.get(child);
                        child_node.m_value = value;
                        return child_node;
                } else {
                        NaryTree<T> child_node = new NaryTree<>();
                        child_node.m_value = value;
                        child_node.m_parent = this;
                        child_node.m_name = child;

                        m_children.put(child, child_node);
                        return child_node;
                }
        }
        
        /**
         * @return parent of the current node.
         */
        public NaryTree<T> parent() {
                return m_parent;
        }
        
        private void __dfs(NaryTree<T> root, Path dummymem, LinkedList<Path<T>> paths) {
                dummymem.name = root.m_name;
                dummymem.value = root.m_value;
                
                if (root.m_children.isEmpty()) {
                        dummymem.next = null;
                        // go all the way back to the beginning
                        while (dummymem.previous != null) dummymem = dummymem.previous;
                        paths.add(dummymem.clone());
                } else {
                        Path dummmy2            = new Path();
                        dummymem.next           = dummmy2;
                        dummmy2.previous        = dummymem;

                        for (NaryTree<T> child : root.m_children.values()) {
                                __dfs(child, dummmy2, paths);
                        }
                }
        }
        
        /**
         * @return All the paths in starting from current node.
         */
        public Path<T>[] get_all_paths() {
                LinkedList<Path<T>> paths = new LinkedList<>();
                __dfs(this, new Path(), paths);
                
                Path[] array = new Path[paths.size()];
                int i = 0;
                for (Path path : paths) {
                        array[i ++] = path;
                }
                return array;
        }
        
        /**
         * @return Children that are under the current node.
         */
        public Map<String, NaryTree<T>> get_all_children() {
                return m_children;
        }
        
        /**
         * @return value of the current node.
         */
        public T get_value() {
                return m_value;
        }
        
        /**
         * Compare the equality of two trees recursively.
         * Would an O(N) operation.
         * @param o the tree to be compared with. it has to be an n-ary tree.
         * @return true if equals.
         */
        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof NaryTree)) return false;
                NaryTree<T> other = (NaryTree<T>) o;
                return  (m_parent == null && other.m_parent == null || 
                        this.m_parent.equals(other.m_parent)) && 
                        (m_value == null && other.m_value == null || 
                        this.m_value.equals(other.m_value)) &&
                        this.m_children.equals(other.m_children);
        }
        
        
        @Override
        public int hashCode() {
                int hash = 3;
                hash = 11 * hash + Objects.hashCode(this.m_parent);
                hash = 11 * hash + Objects.hashCode(this.m_children);
                hash = 11 * hash + Objects.hashCode(this.m_value);
                return hash;
        }
        
        @Override
        public String toString() {
                String s = "n-ary tree = [\n";
                Path<T>[] paths = get_all_paths();
                for (Path<T> path : paths) {
                        s += "[" + path + "],\n";
                }
                s += "]";
                return s;
        }
}
