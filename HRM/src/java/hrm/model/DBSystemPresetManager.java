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

import hrm.utils.Prompt;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database Implementation of the SystemPresetManger by using a dedicated database for system configurations. 
 * The inner class is singleton. Object cannot be constructed customarily.
 *
 * @author davis
 */
public final class DBSystemPresetManager implements SystemPresetManager {
        private static boolean  m_is_first_time = true;
        
        /**
         * This will construct the class by initializing the database at its first instantiation.
         * @param with_mock whether to use the mock database.
         * @param to_reset whether to reset the database.
         */
        public DBSystemPresetManager(boolean with_mock, boolean to_reset) {
                if (m_is_first_time) {
                        if (with_mock)  init_with_mock_database();
                        else            init_database();
                        if (to_reset)   reset_database();
                        m_is_first_time = false;
                }
        }

        @Override
        public boolean add_system_preset(SystemPreset preset) {
                try {
                        Database.add_preset(preset);
                } catch (SQLException ex) {
                        Prompt.log(Prompt.ERROR, getClass().toString(), ex.getMessage());
                        return false;
                }
                return true;
        }

        @Override
        public boolean add_system_preset_from_file(InputStream in) {
                SystemPreset preset = SystemPresetFactory.create_by_file(in);
                if (preset == null) return false;
                return add_system_preset(preset);
        }

        @Override
        public SystemPreset get_system_preset(String name) {
                return Database.fetch(name);
        }

        @Override
        public Set<SystemPreset> get_all_system_presets() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
        public void init_database() {
                try {
                        Database.init();
                } catch (ClassNotFoundException | SQLException ex) {
                        Prompt.log(Prompt.ERROR, getClass().toString(), 
                                "Failed to initialize the database, Details: " + ex.getMessage());
                }
        }
        
        public void init_with_mock_database() {
                try {
                        Database.init_with_mock_database();
                } catch (ClassNotFoundException | SQLException ex) {
                        Prompt.log(Prompt.ERROR, getClass().toString(), 
                                "Failed to initialize the mock database, Details: " + ex.getMessage());
                }
        }
        
        public void reset_database() {
                try {
                        Database.clear();
                } catch (SQLException ex) {
                        Prompt.log(Prompt.ERROR, getClass().toString(), 
                                "Failed to reset the database, Details: " + ex.getMessage());
                }
        }

        public void free() {
                try {
                        Database.free();
                } catch (SQLException ex) {
                        Prompt.log(Prompt.ERROR, getClass().toString(), 
                                "Failed to close database resource, Details: " + ex.getMessage());
                }
        }

        private static class Database {

                private Database() {
                        /* can not be constructed */
                }

                private static String m_database_url;
                private static String m_user;
                private static String m_password;
                private static Connection m_dbconn;

                private static final String TABLE = "SYSTEMCONF";

                /**
                 * Table goes as: name:String(primary key), binary_blob:byte[],
                 * type:int
                 *
                 * @throws ClassNotFoundException
                 * @throws SQLException
                 */
                private static void connect_to_database() throws ClassNotFoundException, SQLException {
                        Class.forName("org.apache.derby.jdbc.ClientDriver");
                        m_dbconn = DriverManager.getConnection(m_database_url, m_user, m_password);

                        // check if "SYSTEMCONF" table is there
                        DatabaseMetaData dbm = m_dbconn.getMetaData();
                        ResultSet tables = dbm.getTables(null, null, TABLE, null);
                        if (tables.next()) {
                                // Table exists
                        } else {
                                // Create a table as it doesn't exist yet
                                Statement stmt = m_dbconn.createStatement();
                                stmt.executeUpdate("CREATE TABLE SYSTEMCONF ("
                                        + "PRESETNAME VARCHAR(255) not NULL, "
                                        + "OBJECT BLOB(1M), "
                                        + "OBJECTTYPE INTEGER, "
                                        + "PRIMARY KEY(PRESETNAME))");
                        }
                }

                /**
                 * Initialize with the dedicated system configuration database.
                 *
                 * @throws java.lang.ClassNotFoundException
                 * @throws java.sql.SQLException
                 */
                public static void init() throws ClassNotFoundException, SQLException {
                        m_database_url = "jdbc:derby://localhost:1527/HRMSystemConfiguration";
                        m_user = "hrm";
                        m_password = "hrm_password";
                        connect_to_database();
                }

                /**
                 * Initialize with a mock database which may be useful for
                 * testing.
                 *
                 * @throws java.lang.ClassNotFoundException
                 * @throws java.sql.SQLException
                 */
                public static void init_with_mock_database() throws ClassNotFoundException, SQLException {
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
                 * Add a system preset to the database.
                 *
                 * @param preset preset that are to be added.
                 * @throws java.sql.SQLException
                 */
                public static void add_preset(SystemPreset preset) throws SQLException {
                        try {
                                String sql = "SELECT * FROM " + TABLE + " WHERE PRESETNAME=?";
                                PreparedStatement pstmt = m_dbconn.prepareStatement(sql);
                                pstmt.setString(1, preset.get_name());
                                ResultSet rs = pstmt.executeQuery();
                                // update the record if it already exists
                                sql = "UPDATE " + TABLE + " \n"
                                        + "SET PRESETNAME=?,OBJECT=?,OBJECTTYPE=? \n"
                                        + "WHERE PRESETNAME=?";
                                pstmt = m_dbconn.prepareStatement(sql);
                                pstmt.setString(1, preset.get_name());
                                pstmt.setBlob(2, new ByteArrayInputStream(preset.serialize()));
                                pstmt.setInt(3, preset.get_type());
                                pstmt.setString(4, preset.get_name());
                                pstmt.executeUpdate();
                        } catch (SQLException e) {
                                // Never seen this record before, then insert it
                                PreparedStatement pstmt = m_dbconn.prepareStatement(
                                        "INSERT INTO " + TABLE + " VALUES (?,?,?)");
                                pstmt.setString(1, preset.get_name());
                                pstmt.setBlob(2, new ByteArrayInputStream(preset.serialize()));
                                pstmt.setInt(3, preset.get_type());
                                pstmt.executeUpdate();
                        }

                }

                /**
                 * Fetch the system preset using its name.
                 *
                 * @param entry the name of the preset that is to be searched.
                 * @return the SystemPreset, if the entry exists, or null if the
                 * otherwise.
                 */
                public static SystemPreset fetch(String entry) {
                        try {
                                String sql = "SELECT * FROM " + TABLE + " WHERE PRESETNAME=?";
                                PreparedStatement pstmt = m_dbconn.prepareStatement(sql);
                                pstmt.setString(1, entry);
                                ResultSet rs = pstmt.executeQuery();
                                if (rs.next()) {
                                        String name = rs.getString(1);
                                        byte[] stream = rs.getBytes(2);
                                        int type = rs.getInt(3);

                                        SystemPreset preset = SystemPresetFactory.create_by_type_and_name(type, name);
                                        preset.deserialize(stream);
                                        return preset;
                                } else {
                                        return null;
                                }
                        } catch (SQLException ex) {
                                ex.printStackTrace();
                                return null;
                        }
                }

                /**
                 * Clear entries in the database and the tracking list.
                 *
                 * @throws java.sql.SQLException
                 */
                public static void clear() throws SQLException {
                        // check if "SYSTEMCONF" table is there
                        DatabaseMetaData dbm = m_dbconn.getMetaData();
                        ResultSet tables = dbm.getTables(null, null, TABLE, null);
                        if (tables.next()) {
                                // Table exists, drop it
                                Statement stmt = m_dbconn.createStatement();
                                stmt.executeUpdate("DROP TABLE " + TABLE);
                        }
                }
        }
}
