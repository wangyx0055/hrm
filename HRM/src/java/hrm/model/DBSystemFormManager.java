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
import hrm.utils.Prompt;
import hrm.utils.Serializable;
import hrm.utils.Serializer;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Define a key set that is
 *
 * @author davis
 */
class Keys implements Serializable {

        private String m_name;
        private TreeSet<Element> m_keys;

        public Keys(Set<Element> keys, String name) {
                m_keys = new TreeSet<>(keys);
                m_name = name;
        }

        public Keys() {
                m_keys = new TreeSet<>();
                m_name = "";
        }

        public String get_name() {
                return m_name;
        }

        @Override
        public boolean equals(Object o) {
                if (!(o instanceof Keys)) {
                        return false;
                }
                return m_keys.equals(((Keys) o).m_keys);
        }

        @Override
        public int hashCode() {
                return m_keys.hashCode();
        }

        @Override
        public byte[] serialize() {
                Serializer s = new Serializer();
                s.write_array_header(m_keys.size());
                for (Element e : m_keys) {
                        s.write_serialized_stream(e.serialize());
                }
                s.write_string(m_name);
                return s.to_byte_stream();
        }

        @Override
        public void deserialize(byte[] stream) {
                m_keys.clear();

                Serializer s = new Serializer();
                s.from_byte_stream(stream);
                int l = s.read_array_header();
                for (int i = 0; i < l; i++) {
                        Element e = new Element();
                        e.deserialize(s.read_serialized_stream());
                        m_keys.add(e);
                }
                m_name = s.read_string();
        }

}

class TableMapper implements Serializable {

        private final Map<Keys, String> m_table_map;
        private static long m_uuid = 0;
        private boolean m_has_changed = false;

        public TableMapper() {
                m_table_map = new HashMap<>();
        }

        public String get_table(Keys key_def) {
                return m_table_map.get(key_def);
        }

        public String add_table(Keys key_def) {
                String new_table = key_def.get_name() + m_uuid++;
                m_table_map.put(key_def, new_table);
                m_has_changed = true;
                return new_table;
        }

        public boolean checkout_dirty_flag() {
                boolean to_return = m_has_changed;
                m_has_changed = false;
                return to_return;
        }

        public Collection<String> get_all_tables() {
                return m_table_map.values();
        }

        @Override
        public byte[] serialize() {
                Serializer s = new Serializer();
                s.write_array_header(m_table_map.size());
                for (Keys key : m_table_map.keySet()) {
                        s.write_serialized_stream(key.serialize());
                        s.write_string(m_table_map.get(key));
                }
                s.write_long(m_uuid);
                return s.to_byte_stream();
        }

        @Override
        public void deserialize(byte[] stream) {
                Serializer s = new Serializer();
                s.from_byte_stream(stream);

                int l = s.read_array_header();
                m_table_map.clear();
                for (int i = 0; i < l; i++) {
                        Keys key = new Keys();
                        key.deserialize(s.read_serialized_stream());
                }
                long id = s.read_long();
                m_uuid = Math.max(m_uuid, id);
        }

}

/**
 * A database implementation of the SystemFormManager
 *
 * @author davis
 */
public class DBSystemFormManager implements SystemFormManager {

        private static boolean m_is_first_time = true;

        public DBSystemFormManager(boolean with_mock, boolean to_reset) throws SQLException {
                if (m_is_first_time) {
                        if (with_mock) {
                                Database.init_with_mock_database();
                        } else {
                                Database.init();
                        }
                        if (to_reset) {
                                Database.clear();
                        }
                        m_is_first_time = false;
                }
        }

        @Override
        public void update(DBFormModule module, DBFormData data) throws SystemFormException {
        }

        @Override
        public DBFormData query(DBFormModule module, DBFormQuery query, DBFormData info) throws SystemFormException {
                return null;
        }

        @Override
        public void remove(DBFormModule module, DBFormQuery query, DBFormData info) throws SystemFormException {
        }

        private static class Database {

                private Database() {
                        /* can not be constructed */
                }

                private static final String SQL_DRIVER = "org.apache.derby.jdbc.ClientDriver";
                private static String m_database_url;
                private static String m_user;
                private static String m_password;
                private static Connection m_dbconn;

                private static final String MAPPING_TABLE = "SYSTEMFORMTABLEMAPPER";
                private static final String FORM_TABLE_PREFIX = "SYSTEMFORMDATA";

                private static TableMapper m_table_mapper = new TableMapper();

                /**
                 * Fetch the table mapper from database if there is any.
                 *
                 * @throws SQLException
                 */
                private static void fetch_table_mapper() throws SQLException {
                        // check if the mapping table is there
                        DatabaseMetaData dbm = m_dbconn.getMetaData();
                        ResultSet tables = dbm.getTables(null, null, MAPPING_TABLE, null);
                        if (tables.next()) {
                                // Table exists, fetch the table mapper
                                String sql = "SELECT * FROM " + MAPPING_TABLE;
                                PreparedStatement pstmt = m_dbconn.prepareStatement(sql);
                                ResultSet rs = pstmt.executeQuery();
                                if (rs.next()) {
                                        byte[] stream = rs.getBytes(2);
                                        m_table_mapper.deserialize(stream);
                                } else {
                                        // Do nothing.
                                }
                        } else {
                                // Create a table mapper as it doesn't exist yet, the mapper will be empty
                                Statement stmt = m_dbconn.createStatement();
                                stmt.executeUpdate("CREATE TABLE " + MAPPING_TABLE + " ("
                                        + "OBJECT BLOB(1M))");
                        }
                }

                /**
                 * Store the table mapper to the database as long as there is
                 * any change to that.
                 *
                 * @throws SQLException
                 */
                private static void store_table_mapper() throws SQLException {
                        if (m_table_mapper.checkout_dirty_flag()) {
                                // the table is dirty. should probably update it.
                                Statement stmt = m_dbconn.createStatement();
                                stmt.executeUpdate("TRUNCATE TABLE " + MAPPING_TABLE);
                                // store the entire table mapper
                                String sql = "UPDATE " + MAPPING_TABLE + " SET OBJECT=?";
                                PreparedStatement pstmt = m_dbconn.prepareStatement(sql);
                                pstmt.setBlob(2, new ByteArrayInputStream(m_table_mapper.serialize()));
                                pstmt.executeUpdate();
                        }
                }

                /**
                 * Connect to the database and fetch the table mapper.
                 */
                private static void connect_to_database() throws SQLException {
                        try {
                                Class.forName(SQL_DRIVER);
                        } catch (ClassNotFoundException ex) {
                                Prompt.log(Prompt.ERROR, "", "Cannot load in SQL Driver: " + SQL_DRIVER);
                        }
                        m_dbconn = DriverManager.getConnection(m_database_url, m_user, m_password);
                        fetch_table_mapper();
                }

                /**
                 * Initialize with the system form database.
                 *
                 * @throws java.sql.SQLException
                 */
                public static void init() throws SQLException {
                        m_database_url = "jdbc:derby://localhost:1527/HRMSystemFormData";
                        m_user = "hrm";
                        m_password = "hrm_password";
                        connect_to_database();
                }

                /**
                 * Initialize with a mock database which may be useful for
                 * testing.
                 *
                 * @throws java.sql.SQLException
                 */
                public static void init_with_mock_database() throws SQLException {
                        m_database_url = "jdbc:derby://localhost:1527/HRMTestDatabase";
                        m_user = "hrm_test";
                        m_password = "hrm_test_password";
                        connect_to_database();
                }

                /**
                 * Clear database connection, and all related resources.
                 *
                 * @throws java.sql.SQLException
                 */
                public static void free() throws SQLException {
                        if (m_dbconn != null) {
                                m_dbconn.close();
                        }
                }

                /**
                 * Store a .
                 *
                 * @param preset preset that are to be added.
                 * @throws java.sql.SQLException
                 */
                public static void store(DBFormModule module, DBFormData data) throws SQLException {
                }

                /**
                 * Fetch the system preset using its name.
                 *
                 * @param entry the name of the preset that is to be searched.
                 * @return the SystemPreset, if the entry exists, or null if the
                 * otherwise.
                 */
                public static DBFormData fetch(DBFormModule module, DBFormQuery query, DBFormData info) {
                        throw new UnsupportedOperationException();
                }
                
                /**
                 * 
                 * @param module
                 * @param query
                 * @param info 
                 */
                public static void remove(DBFormModule module, DBFormQuery query, DBFormData info) {
                }

                /**
                 * Clear all tables in the database and truncate the table
                 * mapper.
                 *
                 * @throws java.sql.SQLException
                 */
                public static void clear() throws SQLException {
                        // check if "SYSTEMCONF" table is there
                        DatabaseMetaData dbm = m_dbconn.getMetaData();
                        for (String table : m_table_mapper.get_all_tables()) {
                                ResultSet tables = dbm.getTables(null, null, table, null);
                                if (tables.next()) {
                                        // Table exists, drop it
                                        Statement stmt = m_dbconn.createStatement();
                                        stmt.executeUpdate("DROP TABLE " + table);
                                }
                        }
                        Statement stmt = m_dbconn.createStatement();
                        stmt.executeUpdate("TRUNCATE TABLE " + MAPPING_TABLE);
                }
        }
}
