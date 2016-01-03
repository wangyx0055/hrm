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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Construct a query.
 *
 * @author davis
 */
public class FormQuery {

        private final String m_cond_clause;
        private final String m_sql_clause;
        private final List<String> m_keys;
        private final List<String> m_attri_names;
        private final Map<String, Attribute> m_attributes;

        /**
         * Construct a query through a boolean condition clause.
         * @param cond_clause 
         */
        public FormQuery(String cond_clause) {
                m_cond_clause = cond_clause;
                List<String> keys = new LinkedList<>();
                List<String> attri_names = new LinkedList<>();
                m_sql_clause = parse_tokens(cond_clause, keys, attri_names);
                m_keys = keys;
                m_attri_names = attri_names;
                m_attributes = new HashMap<>();
                for (String attri_name : m_attri_names) {
                        m_attributes.put(attri_name, null);
                }
        }
        
        /**
         * @return an effective SQL where clause representing the query.
         */
        public String sql_where_clause() {
                return m_sql_clause;
        }

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
        
        /**
         * @return Keys that are involved in the condition clause.
         */
        public List<String> get_involved_key_names() {
                return m_keys;
        }
        
        /**
         * Get the attributes associated with the query conditional clause.
         * @return Attributes in sequence order paralleled to the conditional clause.
         * @throws Exception when some attributes in not set(initialized).
         */
        public List<Attribute> get_attributes() throws Exception {
                List<Attribute> attris = new LinkedList<>();
                for (String attri_name : m_attri_names) {
                        Attribute attri = m_attributes.get(attri_name);
                        if (attri == null) {
                                throw new Exception("DBQueryForm attribute: " + attri_name + " is not "
                                        + "set(initialized), with the condition clause " + m_cond_clause);
                        } else {
                                attris.add(attri);
                        }
                }
                return attris;
        }
        
        @Override
        public String toString() {
                return "DBFormQuery = \ncond_clause:" + m_cond_clause + "\n" 
                        + "sql_clause: " + m_sql_clause + "\n" 
                        + "attri_names: " + m_attri_names + "\n"
                        + "attri_value: " + m_attributes + "\n"
                        + "key_names: " + m_keys;
        }
}
