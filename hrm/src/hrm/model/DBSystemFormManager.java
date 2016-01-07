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
import hrm.utils.RMIObj;
import hrm.utils.Serializable;
import hrm.utils.Serializer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

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
                KeyDefn other = (KeyDefn) o;
                return m_keys.equals(other.m_keys) && m_name.equals(other.m_name);
        }

        @Override
        public int hashCode() {
                return m_keys.hashCode() * m_name.hashCode();
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
                String new_table = key_def.get_name() + ++m_uuid;
                m_table_map.put(key_def, new_table);
                m_has_changed = true;
                return new_table;
        }

        public void remove_table(KeyDefn key_def) {
                m_has_changed = true;
                m_table_map.remove(key_def);
        }
        
        public void clear() {
                m_has_changed = true;
                m_table_map.clear();
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
                        String table = s.read_string();
                        m_table_map.put(key, table);
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
                                Database.init_with_mock_database(to_reset);
                        } else {
                                Database.init(to_reset);
                        }
                        m_is_first_time = false;
                }
        }

        @Override
        public void update(FormModule module, FormQuery query, FormData info) throws SystemFormException {
                Database.store(module, query, info);

        }

        @Override
        public List<FormData> query(FormModule module, FormQuery query) throws SystemFormException {
                return Database.fetch(module, query);
        }

        @Override
        public void safe_remove(FormModule module, FormQuery query) throws SystemFormException {
        }

        @Override
        public void remove(FormModule module, FormQuery query) throws SystemFormException {
                Database.remove(module, query);
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
                                        Blob blob = rs.getBlob(1);
                                        try {
                                                m_table_mapper.deserialize(IOUtils.toByteArray(
                                                        blob.getBinaryStream()));
                                        } catch (IOException ex) {
                                                Prompt.log(Prompt.ERROR, "fetch_table_mapper",
                                                        "Failed to read the binary blob stream, Details: "
                                                        + ex.getMessage());
                                        }
                                } else {
                                        // Do nothing.
                                }
                                rs.close();
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
                                        String sql = "INSERT INTO " + MAPPING_TABLE + " VALUES (?)";
                                        PreparedStatement pstmt = m_dbconn.prepareStatement(sql);
                                        pstmt.setBlob(1, new ByteArrayInputStream(m_table_mapper.serialize()));
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
                private static void connect_to_database(boolean to_clear) throws SQLException {
                        try {
                                Class.forName(SQL_DRIVER);
                        } catch (ClassNotFoundException ex) {
                                Prompt.log(Prompt.ERROR, "", "Cannot load in SQL Driver: " + SQL_DRIVER);
                        }
                        m_dbconn = DriverManager.getConnection(m_database_url, m_user, m_password);
                        fetch_table_mapper();
                        if (to_clear) clear();
                        m_table_mapper.clear();
                }

                /**
                 * Initialize with the system form database.
                 *
                 * @throws java.sql.SQLException
                 */
                public static void init(boolean to_clear) throws SQLException {
                        m_database_url = "jdbc:derby://localhost:1527/HRMSystemFormData";
                        m_user = "hrm";
                        m_password = "hrm_password";
                        connect_to_database(to_clear);
                }

                /**
                 * Initialize with a mock database which may be useful for
                 * testing.
                 *
                 * @throws java.sql.SQLException
                 */
                public static void init_with_mock_database(boolean to_clear) throws SQLException {
                        m_database_url = "jdbc:derby://localhost:1527/HRMTestDatabase";
                        m_user = "hrm_test";
                        m_password = "hrm_test_password";
                        connect_to_database(to_clear);
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
                        FormQuery query,
                        StringBuilder sql) throws SystemFormException {
                        try {
                                sql.setLength(0);
                                sql.append("SELECT * FROM ").append(table).append(" WHERE ").
                                        append(query.sql_where_clause());
                                PreparedStatement pstmt = m_dbconn.prepareStatement(sql.toString());
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
                        FormModule module, FormData info,
                        StringBuilder sql) throws SystemFormException, SQLException {
                        // Form the sql template
                        sql.setLength(0);
                        sql.append("INSERT INTO ").append(table).append(" VALUES (?");
                        TreeSet<Element> keys = module.get_keys();
                        for (int i = 0; i < keys.size(); i++) {
                                sql.append(",?");
                        }
                        sql.append(")");
                        // Inject the data
                        PreparedStatement pstmt = m_dbconn.prepareStatement(sql.toString());
                        pstmt.setBlob(1, new ByteArrayInputStream(info.serialize()));
                        int i = 2;      // key sequence starts at the second column
                        for (Element key : keys) {
                                RMIObj obj = info.get_attribute(key.get_name());
                                if (obj == null) {
                                        // key data not supplied
                                        throw new SystemFormException(
                                                SystemFormException.Error.StoringError).
                                                add_extra_info("Failed as the data associated with the key: " + key
                                                        + " is not supplied by the DBFormData parameter");
                                }
                                if (key.get_type() == String.class) {
                                        pstmt.setString(i++, (String) obj.get_object());
                                } else if (key.get_type() == Integer.class) {
                                        pstmt.setInt(i++, (Integer) obj.get_object());
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
                        FormData info,
                        StringBuilder sql) throws SystemFormException {
                        sql.setLength(0);
                        sql.append("UPDATE ").append(table).append(" SET FORMDATAOBJECT=?");
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
                                sql.append(",").append(elm.get_name()).append("=?");
                        }
                        // Generate the where clause
                        sql.append(" WHERE ").append(query.sql_where_clause());
                        // Configure the prepared statement
                        PreparedStatement pstmt;
                        try {
                                pstmt = m_dbconn.prepareStatement(sql.toString());
                                // Store the binary data form
                                pstmt.setBlob(1, new ByteArrayInputStream(info.serialize()));
                                // Store the keys, key sequence starts at the second column
                                int i = 2;
                                for (Element key : data_elms) {
                                        RMIObj obj = info.get_attribute(key.get_name());
                                        if (obj == null) {
                                                // key data not supplied
                                                throw new SystemFormException(
                                                        SystemFormException.Error.StoringError).
                                                        add_extra_info("Failed as the data associated with the key: " + key
                                                                + " is not supplied by the DBFormData parameter");
                                        }
                                        if (key.get_type() == String.class) {
                                                pstmt.setString(i++, (String) obj.get_object());
                                        } else if (key.get_type() == Integer.class) {
                                                pstmt.setInt(i++, (Integer) obj.get_object());
                                        } else {
                                                // Failed as the key is not sql typed.
                                                throw new SystemFormException(
                                                        SystemFormException.Error.StoringError).
                                                        add_extra_info("Failed as the key is not sql typed: " + key);
                                        }
                                }
                                // Configure the where clause
                                for (int j = 0; j < query_elms.size(); j++) {
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
                        } catch (SQLException ex) {
                                throw new SystemFormException(SystemFormException.Error.StoringError).
                                        add_extra_info("Unknown SQL error: " + ex.getMessage());
                        }
                        return pstmt;
                }

                private static PreparedStatement generate_table_creation_statement(String table,
                        FormModule module, StringBuilder sql) throws SystemFormException, SQLException {
                        sql.setLength(0);
                        sql.append("CREATE TABLE ").append(table).append("(");
                        // first column has to be the form data itself.
                        sql.append("FORMDATAOBJECT BLOB(1M)");
                        TreeSet<Element> keys = module.get_keys();
                        for (Element key : keys) {
                                if (key.get_type() == String.class) {
                                        sql.append(",").append(key.get_name()).append(" VARCHAR(255)");
                                } else if (key.get_type() == Integer.class) {
                                        sql.append(",").append(key.get_name()).append(" INTEGER");
                                } else {
                                        // Failed as the key is not sql typed.
                                        throw new SystemFormException(
                                                SystemFormException.Error.InvalidParameterError).
                                                add_extra_info("Failed as the key is not sql typed: "
                                                        + key);
                                }
                        }
                        sql.append(")");

                        PreparedStatement pstmt = m_dbconn.prepareStatement(sql.toString());
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

                                PreparedStatement pstmt;
                                StringBuilder sql = new StringBuilder();
                                try {
                                        pstmt = generate_table_creation_statement(table, module, sql);
                                        pstmt.executeUpdate();
                                } catch (SQLException ex) {
                                        m_table_mapper.remove_table(key_defn);
                                        throw new SystemFormException(SystemFormException.Error.StoringError).
                                                add_extra_info("Failed to create table via: <"
                                                        + sql + ">, " + ex.getMessage());
                                }
                        }
                        StringBuilder sql = new StringBuilder();
                        try {
                                PreparedStatement pstmt = generate_select_statement(
                                        table, module, query, sql);
                                // query
                                ResultSet rs = pstmt.executeQuery();
                                if (rs.next()) {
                                        // the record exists
                                        pstmt = generate_update_statement(table, module, query, info, sql);
                                        pstmt.executeUpdate();
                                } else {
                                        // update the record as it has already existed
                                        pstmt = generate_insert_statement(table, module, info, sql);
                                        pstmt.executeUpdate();
                                }
                        } catch (SQLException ex) {
                                m_table_mapper.remove_table(key_defn);
                                throw new SystemFormException(SystemFormException.Error.UnknownError).
                                        add_extra_info("Unknown SQL Error: " + ex.getMessage()
                                                + "\nSQL Statement: " + sql);
                        }
                        store_table_mapper();
                }

                /**
                 * Fetch the system preset using its name.
                 *
                 * @param entry the name of the preset that is to be searched.
                 * @return the SystemPreset, if the entry exists, or null if the
                 * otherwise.
                 */
                public static List<FormData> fetch(FormModule module,
                        FormQuery query) throws SystemFormException {
                        KeyDefn key_defn = new KeyDefn(module.get_keys(), module.get_module_name());
                        String table = m_table_mapper.get_table(key_defn);
                        if (table == null) {
                                return null;    // no matched result since such table which stores the form is absent.
                        }
                        StringBuilder sql = new StringBuilder();
                        try {
                                PreparedStatement pstmt = generate_select_statement(
                                        table, module, query, sql);
                                // query
                                ResultSet rs = pstmt.executeQuery();
                                List<FormData> queried_form = new LinkedList<>();
                                while (rs.next()) {
                                        // FormData is guaranteed to be at the first column
                                        Blob blob = rs.getBlob(1);
                                        FormData dbform = new FormData();
                                        try {
                                                dbform.deserialize(IOUtils.toByteArray(blob.getBinaryStream()));
                                                queried_form.add(dbform);
                                        } catch (IOException ex) {
                                                Prompt.log(Prompt.ERROR, "fetch",
                                                        "Failed to read the binary blob stream, Details: "
                                                        + ex.getMessage());
                                        }
                                }
                                rs.close();
                                return queried_form;
                        } catch (SQLException ex) {
                                throw new SystemFormException(SystemFormException.Error.UnknownError).
                                        add_extra_info("Unknown SQL Error: " + ex.getMessage()
                                                + "\nSQL Statement: " + sql);
                        }
                }

                /**
                 *
                 * @param module
                 * @param query
                 * @param info
                 */
                public static void remove(FormModule module, FormQuery query) {
                        throw new UnsupportedOperationException();
                }

                /**
                 * Clear all tables in the database and truncate the table
                 * mapper.
                 *
                 * @throws java.sql.SQLException
                 */
                public static void clear() throws SQLException {
                        // check if those subjected table is there
                        DatabaseMetaData dbm = m_dbconn.getMetaData();
                        for (String table : m_table_mapper.get_all_tables()) {
                                try {
                                        Statement stmt = m_dbconn.createStatement();
                                        stmt.executeUpdate("DROP TABLE " + table);
                                } catch (SQLException ex) {
                                        // ignore the problem
                                }
                        }
                        Statement stmt = m_dbconn.createStatement();
                        stmt.executeUpdate("TRUNCATE TABLE " + MAPPING_TABLE);
                }
        }
}
