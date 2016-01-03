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
import java.util.List;
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
class KeyDefn implements Serializable {

        private String m_name;
        private TreeSet<Element> m_keys;

        public KeyDefn(Set<Element> keys, String name) {
                m_keys = new TreeSet<>(keys);
                m_name = name;
        }

        public KeyDefn() {
                m_keys = new TreeSet<>();
                m_name = "";
        }

        public String get_name() {
                return m_name;
        }

        @Override
        public boolean equals(Object o) {
                if (!(o instanceof KeyDefn)) {
                        return false;
                }
                return m_keys.equals(((KeyDefn) o).m_keys);
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

        private final Map<KeyDefn, String> m_table_map;
        private static long m_uuid = 0;
        private boolean m_has_changed = false;

        public TableMapper() {
                m_table_map = new HashMap<>();
        }

        public String get_table(KeyDefn key_def) {
                return m_table_map.get(key_def);
        }

        public String add_table(KeyDefn key_def) {
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
                for (KeyDefn key : m_table_map.keySet()) {
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
                        KeyDefn key = new KeyDefn();
                        key.deserialize(s.read_serialized_stream());
                }
                long id = s.read_long();
                m_uuid = Math.max(m_uuid, id);
        }

}

/**
 * A database implementation of the SystemFormManager.
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
        public void update(FormModule module, FormQuery query, FormData info) throws SystemFormException {
                Database.store(module, query, info);

        }

        @Override
        public FormData query(FormModule module, FormQuery query, FormData info) throws SystemFormException {
                return Database.fetch(module, query, info);
        }

        @Override
        public void remove(FormModule module, FormQuery query, FormData info) throws SystemFormException {
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

                private static final TableMapper m_table_mapper = new TableMapper();

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
                 */
                private static void store_table_mapper() {
                        if (m_table_mapper.checkout_dirty_flag()) {
                                try {
                                        // the table is dirty. should probably update it.
                                        Statement stmt = m_dbconn.createStatement();
                                        stmt.executeUpdate("TRUNCATE TABLE " + MAPPING_TABLE);
                                        // store the entire table mapper
                                        String sql = "UPDATE " + MAPPING_TABLE + " SET OBJECT=?";
                                        PreparedStatement pstmt = m_dbconn.prepareStatement(sql);
                                        pstmt.setBlob(2, new ByteArrayInputStream(m_table_mapper.serialize()));
                                        pstmt.executeUpdate();
                                } catch (SQLException ex) {
                                        Prompt.log(Prompt.ERROR, "DBSystemFromManager",
                                                "Cannot store table mapper, Details: " + ex.getMessage());
                                }
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

                private static PreparedStatement generate_select_statement(String table,
                        FormModule module,
                        FormQuery query) throws SystemFormException {
                        try {
                                String sql = "SELECT * FROM " + table + " WHERE " + query.sql_where_clause();
                                PreparedStatement pstmt = m_dbconn.prepareStatement(sql);
                                // configure the prepared statement
                                List<String> key_names = query.get_involved_key_names();
                                List<Attribute> query_attris;
                                try {
                                        query_attris = query.get_attributes();
                                } catch (Exception ex) {
                                        throw new SystemFormException(SystemFormException.Error.InvalidParameterError).
                                                add_extra_info(ex.getMessage());
                                }
                                List<Element> query_elms = module.get_ordered_keys(key_names);
                                if (query_attris == null || key_names == null || query_elms == null) {
                                        throw new SystemFormException(SystemFormException.Error.InvalidParameterError).
                                                add_extra_info("Query attributes, info attributes and key elements are "
                                                        + "incompatible: " + query + ", " + key_names + ", " + query_elms);
                                }
                                for (int i = 0; i < query_attris.size(); i++) {
                                        Attribute attri = query_attris.get(i);
                                        Element elm = query_elms.get(i);
                                        Object obj = attri.get_object();
                                        if (elm.get_type() == String.class) {
                                                pstmt.setString(i + 1, (String) obj);
                                        } else if (elm.get_type() == Integer.class) {
                                                pstmt.setInt(i + 1, (Integer) obj);
                                        } else {
                                                // Failed as the key is not sql typed.
                                                throw new SystemFormException(
                                                        SystemFormException.Error.QueryError).
                                                        add_extra_info("Failed as the key is not sql typed: " + elm);
                                        }
                                }
                                return pstmt;
                        } catch (SQLException ex) {
                                throw new SystemFormException(SystemFormException.Error.QueryError).
                                        add_extra_info("Unknown SQL error: " + ex.getMessage());
                        }
                }

                private static PreparedStatement generate_insert_statement(String table,
                        FormModule module, FormData info) throws SystemFormException, SQLException {
                        // Form the sql template
                        String sql = "INSERT INTO " + table + " VALUES (?";
                        TreeSet<Element> keys = module.get_keys();
                        for (int i = 0; i < keys.size(); i++) {
                                sql += ",?";
                        }
                        sql += ")";
                        // Inject the data
                        PreparedStatement pstmt = m_dbconn.prepareStatement(sql);
                        pstmt.setBlob(1, new ByteArrayInputStream(info.serialize()));
                        int i = 2;      // key sequence starts at the second column
                        for (Element key : keys) {
                                Attribute attri = info.get_attribute(key.get_name());
                                if (attri == null) {
                                        // key data not supplied
                                        throw new SystemFormException(
                                                SystemFormException.Error.StoringError).
                                                add_extra_info("Failed as the data associated with the key: " + key
                                                        + " is not supplied by the DBFormData parameter");
                                }
                                if (key.get_type() == String.class) {
                                        pstmt.setString(i++, (String) attri.get_object());
                                } else if (key.get_type() == Integer.class) {
                                        pstmt.setInt(i++, (Integer) attri.get_object());
                                } else {
                                        // Failed as the key is not sql typed.
                                        throw new SystemFormException(
                                                SystemFormException.Error.StoringError).
                                                add_extra_info("Failed as the key is not sql typed: " + key);
                                }
                        }
                        return pstmt;
                }

                private static PreparedStatement generate_update_statement(String table,
                        FormModule module,
                        FormQuery query,
                        FormData info) throws SystemFormException {
                        String sql = "UPDATE " + table + " SET FORMDATAOBJECT=?";
                        // Check the parameters
                        List<String> query_key_names = query.get_involved_key_names();
                        List<Attribute> query_attris;
                        try {
                                query_attris = query.get_attributes();
                        } catch (Exception ex) {
                                throw new SystemFormException(SystemFormException.Error.InvalidParameterError).
                                        add_extra_info(ex.getMessage());
                        }
                        List<Element> query_elms = module.get_ordered_keys(query_key_names);
                        if (query_key_names == null || query_attris == null || query_elms == null) {
                                throw new SystemFormException(SystemFormException.Error.InvalidParameterError).
                                        add_extra_info("Query attributes, info attributes and key elements are "
                                                + "incompatible: " + query + ", " + query_attris + ", " + query_elms);
                        }
                        // Generate the value clause
                        TreeSet<Element> data_elms = module.get_keys();
                        for (Element elm : data_elms) {
                                sql += "," + elm.get_name() + "=?";
                        }
                        // Generate the where clause
                        sql += " WHERE " + query.sql_where_clause();
                        // Configure the prepared statement
                        PreparedStatement pstmt;
                        try {
                                pstmt = m_dbconn.prepareStatement(sql);
                                // Store the binary data form
                                pstmt.setBlob(1, new ByteArrayInputStream(info.serialize()));
                                // Store the keys, key sequence starts at the second column
                                int i = 2;
                                for (Element key : data_elms) {
                                        Attribute attri = info.get_attribute(key.get_name());
                                        if (attri == null) {
                                                // key data not supplied
                                                throw new SystemFormException(
                                                        SystemFormException.Error.StoringError).
                                                        add_extra_info("Failed as the data associated with the key: " + key
                                                                + " is not supplied by the DBFormData parameter");
                                        }
                                        if (key.get_type() == String.class) {
                                                pstmt.setString(i++, (String) attri.get_object());
                                        } else if (key.get_type() == Integer.class) {
                                                pstmt.setInt(i++, (Integer) attri.get_object());
                                        } else {
                                                // Failed as the key is not sql typed.
                                                throw new SystemFormException(
                                                        SystemFormException.Error.StoringError).
                                                        add_extra_info("Failed as the key is not sql typed: " + key);
                                        }
                                }
                                // Configure the where clause
                                for (int j = 0; j < query_elms.size(); j ++) {
                                        Attribute attri = query_attris.get(j);
                                        Element key = query_elms.get(j);
                                        if (attri == null) {
                                                // key data not supplied
                                                throw new SystemFormException(
                                                        SystemFormException.Error.StoringError).
                                                        add_extra_info("Failed as the data associated with the key: " + key
                                                                + " is not supplied by the DBFormData parameter");
                                        }
                                        if (key.get_type() == String.class) {
                                                pstmt.setString(i++, (String) attri.get_object());
                                        } else if (key.get_type() == Integer.class) {
                                                pstmt.setInt(i++, (Integer) attri.get_object());
                                        } else {
                                                // Failed as the key is not sql typed.
                                                throw new SystemFormException(
                                                        SystemFormException.Error.StoringError).
                                                        add_extra_info("Failed as the key is not sql typed: " + key);
                                        }
                                }
                        } catch(SQLException ex) {
                                throw new SystemFormException(SystemFormException.Error.StoringError).
                                        add_extra_info("Unknown SQL error: " + ex.getMessage());
                        }
                        return pstmt;
                }

                private static PreparedStatement generate_table_creation_statement(String table,
                        FormModule module) throws SystemFormException, SQLException {
                        String sql = "CREATE TABLE " + table + "(";
                        // first column has to be the form data itself.
                        sql += "FORMDATAOBJECT BLOB(1M)";
                        TreeSet<Element> keys = module.get_keys();
                        for (Element key : keys) {
                                if (key.get_type() == String.class) {
                                        sql += ",";
                                        sql += key.get_name() + " VARCHAR(255)";
                                } else if (key.get_type() == Integer.class) {
                                        sql += ",";
                                        sql += key.get_name() + "INTEGER";
                                } else {
                                        // Failed as the key is not sql typed.
                                        throw new SystemFormException(
                                                SystemFormException.Error.FetchingError).
                                                add_extra_info("Failed as the key is not sql typed: "
                                                        + key);
                                }
                        }
                        sql += ")";

                        PreparedStatement pstmt = m_dbconn.prepareStatement(sql);
                        return pstmt;
                }

                /**
                 * Store the form data.
                 *
                 * @param preset preset that are to be added.
                 * @throws java.sql.SQLException
                 */
                public static void store(FormModule module,
                        FormQuery query,
                        FormData info) throws SystemFormException {
                        KeyDefn key_defn = new KeyDefn(module.get_keys(), module.get_module_name());
                        String table = m_table_mapper.get_table(key_defn);
                        if (table == null) {
                                // generate new table name, store the table mapper and create the new table.
                                table = m_table_mapper.add_table(key_defn);
                                store_table_mapper();

                                PreparedStatement pstmt = null;
                                try {
                                        pstmt = generate_table_creation_statement(table, module);
                                        pstmt.executeUpdate();
                                } catch (SQLException ex) {
                                        throw new SystemFormException(SystemFormException.Error.StoringError).
                                                add_extra_info("Failed to create table via: <"
                                                        + pstmt + ">, " + ex.getMessage());
                                }
                        }
                        try {
                                PreparedStatement pstmt = generate_select_statement(
                                        table, module, query);
                                // query
                                ResultSet rs = pstmt.executeQuery();
                                if (rs.next()) {
                                        // the record exists
                                        pstmt = generate_insert_statement(table, module, info);
                                        pstmt.executeUpdate();
                                } else {
                                        // update the record as it has already existed
                                        pstmt = generate_update_statement(table, module, query, info);
                                        pstmt.executeUpdate();
                                }
                        } catch (SQLException ex) {
                                throw new SystemFormException(SystemFormException.Error.UnknownError).
                                        add_extra_info("Unknown SQL Error: " + ex.getMessage());
                        }

                }

                /**
                 * Fetch the system preset using its name.
                 *
                 * @param entry the name of the preset that is to be searched.
                 * @return the SystemPreset, if the entry exists, or null if the
                 * otherwise.
                 */
                public static FormData fetch(FormModule module,
                        FormQuery query,
                        FormData info) throws SystemFormException {
                        KeyDefn key_defn = new KeyDefn(module.get_keys(), module.get_module_name());
                        String table = m_table_mapper.get_table(key_defn);
                        if (table == null) {
                                return null;    // no matched result since such table which stores the form is absent.
                        }
                        try {
                                PreparedStatement pstmt = generate_select_statement(
                                        table, module, query);
                                // query
                                ResultSet rs = pstmt.executeQuery();
                                if (rs.next()) {
                                        byte[] stream = rs.getBytes(1); // FormData is guaranteed to be at the first column
                                        FormData dbform = new FormData();
                                        dbform.deserialize(stream);
                                        return dbform;
                                } else {
                                        return null;
                                }
                        } catch (SQLException ex) {
                                throw new SystemFormException(SystemFormException.Error.UnknownError).
                                        add_extra_info("Unknown SQL Error: " + ex.getMessage());
                        }
                }

                /**
                 *
                 * @param module
                 * @param query
                 * @param info
                 */
                public static void remove(FormModule module, FormQuery query, FormData info) {
                        throw new UnsupportedOperationException();
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
