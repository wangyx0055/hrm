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

import hrm.utils.Attribute;
import hrm.utils.Element;
import hrm.utils.Serializable;
import hrm.utils.Serializer;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * To construct a query. A query is used to fetch, store and remove pieces of FormData 
 * in FormDataManager. It specifies the relational information of the keys which can 
 * confine the locations where the FormData can be extracted from/stored in.
 * It also store the full set of keys. With these keys and the name of the query,
 * a relational table in FormDataManager can be uniquely associated.
 *
 * @author davis
 */
public class FormQuery extends DataComponent {
        
        /**
         * Parse the boolean clause and extract the attributes, keys being used and return
         * an effective SQL conditional clause.
         * @param statement the boolean clause.
         * @param keys keys involved in the boolean clause.
         * @param attri_names attribute names referenced in the boolean clause.
         * @return an effective SQL conditional clause.
         */
        private String parse_tokens(String statement, List<String> keys, List<String> attri_names) {
                int state = 0;
                int front = 0;
                int i = 0;
                while (i < statement.length()) {
                        char c = statement.charAt(i) ;
                        switch (state) {
                                case 0:         // space
                                        if ((c >= 'a' && c <= 'z')
                                                || (c >= 'A' && c <= 'Z')) {
                                                front = i;
                                                state = 1;
                                        } else if (c == '\'') {
                                                state = 3;
                                                i ++;
                                        } else if (c == '#') {
                                                front = i;
                                                state = 4;
                                                i ++;
                                        } else {
                                                i++;
                                        }
                                        break;
                                case 1:         // alphabet
                                        if (!((c >= 'a' && c <= 'z')
                                                || (c >= 'A' && c <= 'Z')
                                                || (c == '_')
                                                || (c >= '0' && c <= '9'))) {
                                                state = 2;
                                        } else {
                                                i++;
                                        }
                                        break;
                                case 2:         // record
                                        String tmp = new String(statement.toCharArray(), front, i - front);
                                        if (tmp.compareToIgnoreCase("AND") == 0 ||
                                                tmp.compareToIgnoreCase("OR") == 0) {
                                                // ignore key words
                                        } else {
                                                keys.add(tmp);
                                        }
                                        state = 0;
                                        i++;
                                        break;
                                case 3:         // quotation mark
                                        if (c == '\'') {
                                                state = 0;
                                                i++;
                                        } else {
                                                i++;
                                        }
                                        break;
                                case 4:         // attribute mark
                                        if (c == '#') {
                                                String tmp2 = new String(statement.toCharArray(), 
                                                        front + 1, i - front - 1);
                                                attri_names.add(tmp2);
                                                state = 0;
                                                i ++;
                                        } else {
                                                i ++;
                                        }
                                        break;
                        }
                }
                // generate sql statement
                char[] csql = new char [statement.length()];
                int c = 0;
                boolean is_attri = false;
                for (int j = 0; j < statement.length(); j ++) {
                        if (is_attri || statement.charAt(j) == '#') {
                                if (is_attri == false) {
                                        csql[c++] = '?';
                                        is_attri = true;
                                } else if (statement.charAt(j) == '#') {
                                        csql[c++] = ' ';
                                        is_attri = false;
                                }
                        } else {
                                csql[c++] = statement.charAt(j);
                        }
                }
                String sql = new String(csql, 0, c);
                return sql;
        }
        
        public class QueryMode implements Serializable {
                private String m_name;
                private String m_cond_clause;
                private String m_sql_clause;
                private List<String> m_keys_involved;
                private List<String> m_attri_order;
                private Map<String, Attribute> m_attributes;
                
                private QueryMode() {}
                
                public QueryMode(String name, String cond_clause) {
                        m_name = name;
                        m_cond_clause = cond_clause;
                        
                        List<String> keys = new LinkedList<>();
                        m_attri_order = new LinkedList<>();
                        m_sql_clause = parse_tokens(cond_clause, keys, m_attri_order);
                        m_keys_involved = keys;
                        m_attributes = new HashMap<>();
                        for (String attri_name : m_attri_order) {
                                m_attributes.put(attri_name, null);
                        }
                }
                
                public String get_sql_clause() {
                        return m_sql_clause;
                }
                
                public List<String> get_key_names_involved () {
                        return m_keys_involved;
                }
                
                public Map<String, Attribute> get_attributes() {
                        return m_attributes;
                }

                public String get_conditional_clause() {
                        return m_cond_clause;
                }
                
                public boolean verify(StringBuilder errors) {
                        // to ensure that every attribute is initialized.
                        for (String attri_name : m_attributes.keySet()) {
                                if (m_attributes.get(attri_name) == null) {
                                        errors.append("QueryMode: ").
                                                append(this.toString()).
                                                append(", has uninitializerd attribute: ").
                                                append(attri_name);
                                        return false;
                                }
                        }
                        return true;
                }
                
                /**
                * Set the attribute associated with the attribute name.
                * @param attri_name the attribute name associated.
                * @param attri the attribute value.
                * @throws Exception when the attribute name doesn't exists in the condition clause specified
                * through class construction.
                */
               public void set_attribute(String attri_name, Attribute attri) throws Exception {
                       if (!m_attributes.containsKey(attri_name)) {
                               throw new Exception("No such attribute: " + attri_name + " injectible to the clause " 
                                       + m_cond_clause);
                       } else {
                               m_attributes.put(attri_name, attri);
                       }
               }
               
               public List<Attribute> get_ordered_attributes() {
                       List<Attribute> attris = new LinkedList<>();
                       for (String attri_name : m_attri_order) {
                               attris.add(m_attributes.get(attri_name));
                       }
                       return attris;
               }
                
                @Override
                public String toString() {
                        return  "[query_mode: " + m_name + "\n"
                                + "\tcond_clause:" + m_cond_clause + "\n"
                                + "\tsql_clause: " + m_sql_clause + "\n"
                                + "\tattri_value: " + m_attributes + "\n"
                                + "\tkey_names: " + m_keys + "\n"
                                + "\t],";
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
        
        private final Map<String, QueryMode>    m_query_modes = new HashMap<>();
        private final TreeMap<String, Element>  m_keys = new TreeMap<>();
        private QueryMode                       m_active_qm = null;
        
        /**
         * Construct a query through a boolean condition clause.
         * @param name name of the query. 
         */
        public FormQuery(String name) {
                super(name, DataComponentFactory.FORM_QUERY_COMPONENT);
        }
        
        /**
         * Add a query mode to the query and set it as the currently active query.
         * @param query_name name of this query mode.
         * @param cond_clause query clause.
         * @return the query mode created.
         */
        public QueryMode create_query_mode(String query_name, String cond_clause) {
                QueryMode qm = new QueryMode(query_name, cond_clause);
                m_query_modes.put(query_name, qm);
                set_active_query_mode(query_name);
                return qm;
        }
        
        /**
         * Add a key to this form query.
         * @param elm the key.
         */
        public void add_key(Element elm) {
                m_keys.put(elm.get_name(), elm);
        }
        
        /**
         * Remove a key from this form query.
         * @param key_name the key to be removed.
         */
        public void remove_key(String key_name) {
                m_keys.remove(key_name);
        }
        
        /**
         * @return get all key names in current form query, with fixed ordering.
         */
        public Set<String> get_key_names() {
                return m_keys.keySet();
        }
        
        /**
         * @return get all the keys in current form query, with fixed ordering.
         */
        public Collection<Element> get_keys() {
                return m_keys.values();
        }
        
        /**
         * Get a key by name.
         * @param key_name name of the key.
         * @return the key that corresponds to the name.
         */
        public Element get_key(String key_name) {
                return m_keys.get(key_name);
        }
        
        /**
         * Set the active query mode to be the one specified.
         * @param query_name the query mode wanted to be active.
         */
        public void set_active_query_mode(String query_name) {
                m_active_qm = m_query_modes.get(query_name);
        }
        
        /**
         * @param errors Any error detected is saved as string message.
         * @return whether current state of the query is valid.
         */
        public boolean verify(StringBuilder errors) {
                if (m_active_qm == null) {
                        errors.append("Current active QueryMode is empty, which it shouldn't be");
                        return false;
                }
                if (m_active_qm.verify(errors)) {
                        return false;
                }
                if (!m_keys.isEmpty()) {
                        errors.append("The key set of the FormQuery is empty, which it shouldn't be");
                        return false;
                }
                // to ensure no attribute is outside of all keys available.
                for (String key_name : m_active_qm.get_key_names_involved()) {
                        if (m_keys.get(key_name) == null) {
                                errors.append("The QueryMode: ").
                                        append(m_active_qm).
                                        append(" contains key names involved: ").
                                        append(key_name).
                                        append(" that doesn't appear in the key set: ").
                                        append(m_keys);
                                return false;
                        }
                }
                errors.append("The form query passes sanity check");
                return true;
        }
        
        /**
         * @return an effective SQL where clause representing the query.
         */
        public String sql_where_clause() {
                return m_active_qm.m_sql_clause;
        }

        /**
         * @return Return keys, with ordering, that are involved in the condition clause.
         */
        public List<String> get_ordered_query_key_names() {
                return m_active_qm.get_key_names_involved();
        }
        
        /**
         * Get the attributes associated with current active query mode.
         * @return Attributes in sequential order paralleled to the conditional clause.
         */
        public List<Attribute> get_ordered_query_attributes() {
                return m_active_qm.get_ordered_attributes();
        }
        
        @Override
        public String toString() {
                String s = "FormQuery = [\n";
                for (String qmid : m_query_modes.keySet()) {
                        s += "\t" + m_query_modes.get(qmid).toString();
                }
                s += "], sanity check: ";
                StringBuilder sb = new StringBuilder();
                verify(sb);
                s += sb;
                return s;
        }

        @Override
        public byte[] serialize() {
                Serializer s = new Serializer();
                s.write_array_header(m_query_modes.size());
                for (String qmid : m_query_modes.keySet()) {
                        s.write_string(qmid);
                        s.write_serialized_stream(m_query_modes.get(qmid).serialize());
                }
                s.write_array_header(m_keys.size());
                for (String key_name : m_keys.keySet()) {
                        s.write_string(key_name);
                        s.write_serialized_stream(m_keys.get(key_name).serialize());
                }
                return s.to_byte_stream();
        }

        @Override
        public void deserialize(byte[] stream) {
                Serializer s = new Serializer();
                s.from_byte_stream(stream);
                int l = s.read_array_header();
                m_query_modes.clear();
                for (int i = 0; i < l; i ++){
                        String qmid = s.read_string();
                        QueryMode qm = new QueryMode();
                        qm.deserialize(s.read_serialized_stream());
                        m_query_modes.put(qmid, qm);
                }
                l = s.read_array_header();
                m_keys.clear();
                for (int i = 0; i < l; i ++) {
                        String key_name = s.read_string();
                        Element elm = new Element();
                        elm.deserialize(s.read_serialized_stream());
                        m_keys.put(key_name, elm);
                }
        }
}
