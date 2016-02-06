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
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

/**
 * Database Implementation of the SystemPresetManger by using a dedicated
 * database for system configurations. The inner class is singleton. Object
 * cannot be constructed customarily.
 *
 * @author davis
 */
public final class DBDataComponentManager implements DataComponentManager {

        private static boolean m_is_first_time = true;

        /**
         * This will construct the class by initializing the database at its
         * first instantiation.
         *
         * @param db_url URL of the database. It may or may not exits.
         * @param db_user User name of the database.
         * @param db_password Password of the database.
         * @param to_reset whether to reset the database.
         * @throws java.sql.SQLException
         * @throws java.lang.ClassNotFoundException
         */
        public DBDataComponentManager(String db_url, String db_user, String db_password,
                                      boolean to_reset) throws SQLException, ClassNotFoundException {
                if (m_is_first_time) {
                        try {
                                Database.init(db_url, db_user, db_password);
                        } catch (SQLException ex) {
                                Prompt.log(Prompt.ERROR, getClass().toString(),
                                           "Failed to initialize the database");
                                Prompt.log_sql_ex(getClass().toString(), ex);
                                throw ex;
                        }
                        if (to_reset) {
                                try {
                                        Database.clear();
                                } catch (SQLException ex) {
                                        Prompt.log(Prompt.WARNING, getClass().toString(),
                                                   "Failed to clear the database");
                                        Prompt.log_sql_ex(getClass().toString(), ex);
                                }
                        }
                        m_is_first_time = false;
                }
        }

        @Override
        public boolean add_system_component(DataComponent preset) {
                try {
                        Database.add_component(preset);
                } catch (SQLException ex) {
                        Prompt.log(Prompt.ERROR, getClass().toString(), ex.getMessage());
                        return false;
                }
                return true;
        }

        @Override
        public DataComponent get_system_component(String name) {
                return Database.fetch(name);
        }

        @Override
        public Set<DataComponent> get_all_system_components() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void free() {
                try {
                        Database.free();
                } catch (SQLException ex) {
                        Prompt.log(Prompt.ERROR, getClass().toString(),
                                   "Failed to close database resource, Details: " + ex.getMessage());
                        Prompt.log_sql_ex(getClass().toString(), ex);
                }
        }

        private static class Database {

                private Database() {
                        /* can not be constructed */
                }

                private static Connection m_dbconn;

                private static final String TABLE = "SYSTEMCONF";
                private static final String SQL_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
                private static final String SQL_PROTOCOL = "jdbc:derby:";

                /**
                 * Table goes as: name:String(primary key), binary_blob:byte[],
                 * type:int
                 *
                 * @throws ClassNotFoundException
                 * @throws SQLException
                 */
                private static void connect_to_database(String url, String user, String password)
                        throws ClassNotFoundException, SQLException {
                        try {
                                Class.forName(SQL_DRIVER);
                        } catch (ClassNotFoundException ex) {
                                Prompt.log(Prompt.ERROR, "DBDataComponentManager.connect_to_database",
                                           "Cannot load in such sql driver as: " + ex.getMessage());
                                throw ex;
                        }
                        try {
                                m_dbconn = DriverManager.getConnection(SQL_PROTOCOL + url, user, password);
                        } catch (SQLException ex) {
                                // It's possible that the database doesn't exists yet, try creating one
                                Prompt.log(Prompt.WARNING, "DBDataComponentManager.connect_to_database",
                                           "Database doesn't not exists, trying to create one");
                                Prompt.log_sql_ex(null, ex);
                                m_dbconn = DriverManager.getConnection(
                                        SQL_PROTOCOL + url + ";create=true", user, password);
                                Prompt.log(Prompt.NORMAL, "DBDataComponentManager.connect_to_database",
                                           "Database has been created successfully");
                        }
                        // check if "SYSTEMCONF" table is there
                        DatabaseMetaData dbm = m_dbconn.getMetaData();
                        ResultSet tables = dbm.getTables(null, null, TABLE, null);
                        if (tables.next()) {
                                // Table exists
                        } else {
                                // Create a table as it doesn't exist yet
                                Statement stmt = m_dbconn.createStatement();
                                stmt.executeUpdate("CREATE TABLE " + TABLE + " ("
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
                public static void init(String db_url, String db_user, String db_password)
                        throws ClassNotFoundException, SQLException {
                        connect_to_database(db_url, db_user, db_password);
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
                 * Add a system comp to the database.
                 *
                 * @param comp comp that are to be added.
                 * @throws java.sql.SQLException
                 */
                public static void add_component(DataComponent comp) throws SQLException {
                        try {
                                String sql = "SELECT * FROM " + TABLE + " WHERE PRESETNAME=?";
                                PreparedStatement pstmt = m_dbconn.prepareStatement(sql);
                                pstmt.setString(1, comp.get_name());
                                ResultSet rs = pstmt.executeQuery();
                                if (!rs.next()) {
                                        // Never seen this record before, then insert it
                                        pstmt = m_dbconn.prepareStatement(
                                                "INSERT INTO " + TABLE + " VALUES (?,?,?)");
                                        pstmt.setString(1, comp.get_name());
                                        pstmt.setBlob(2, new ByteArrayInputStream(comp.serialize()));
                                        pstmt.setInt(3, comp.get_type());
                                        pstmt.executeUpdate();
                                        return;
                                }
                                // update the record if it already exists
                                sql = "UPDATE " + TABLE + " \n"
                                      + "SET PRESETNAME=?,OBJECT=?,OBJECTTYPE=? \n"
                                      + "WHERE PRESETNAME=?";
                                pstmt = m_dbconn.prepareStatement(sql);
                                pstmt.setString(1, comp.get_name());
                                pstmt.setBlob(2, new ByteArrayInputStream(comp.serialize()));
                                pstmt.setInt(3, comp.get_type());
                                pstmt.setString(4, comp.get_name());
                                pstmt.executeUpdate();
                        } catch (SQLException e) {
                                Prompt.log(Prompt.ERROR, "preset: " + comp.get_name(),
                                           "Failed to add preset to the database, Details: " + e.getMessage());
                        }

                }

                /**
                 * Fetch the system preset using its name.
                 *
                 * @param entry the name of the preset that is to be searched.
                 * @return the DataComponent, if the entry exists, or null if
                 * the otherwise.
                 */
                public static DataComponent fetch(String entry) {
                        try {
                                String sql = "SELECT * FROM " + TABLE + " WHERE PRESETNAME=?";
                                PreparedStatement pstmt = m_dbconn.prepareStatement(sql);
                                pstmt.setString(1, entry);
                                ResultSet rs = pstmt.executeQuery();
                                if (rs.next()) {
                                        String name = rs.getString(1);
                                        byte[] stream = rs.getBytes(2);
                                        int type = rs.getInt(3);

                                        DataComponent comp;
                                        try {
                                                comp = DataComponentFactory.
                                                        create_by_type_and_name(type, name);
                                                comp.deserialize(stream);
                                                return comp;
                                        } catch (DataComponentException ex) {
                                                Prompt.log(Prompt.ERROR, entry,
                                                           "Failed to fetch this component: " + entry
                                                           + ", Details: " + ex.getMessage());
                                                return null;
                                        }
                                } else {
                                        return null;
                                }
                        } catch (SQLException ex) {
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
                        if (m_dbconn == null) {
                                throw new SQLException("connection hasn't been established");
                        }
                        DatabaseMetaData dbm = m_dbconn.getMetaData();
                        ResultSet tables = dbm.getTables(null, null, TABLE, null);
                        if (tables.next()) {
                                // Table exists, truncate the content of it
                                Statement stmt = m_dbconn.createStatement();
                                stmt.executeUpdate("TRUNCATE TABLE " + TABLE);
                        }
                }
        }
}
