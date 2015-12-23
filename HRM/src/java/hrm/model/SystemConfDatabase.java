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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A database that is dedicated for system configurations. This class is
 * singleton. Object cannot be constructed customarily.
 *
 * @author davis
 */
public final class SystemConfDatabase {

        private SystemConfDatabase() {
                /* can not be constructed */
        }
        
        private static String           m_database_url;
        private static String           m_user;
        private static String           m_password;
        private static Connection       m_dbconn;
        
        private static final String     TABLE = "SystemConf"; 
        
        private static void connect_to_database() throws ClassNotFoundException, SQLException {
                        Class.forName("org.apache.derby.jdbc.ClientDriver");
                        m_dbconn = DriverManager.getConnection(m_database_url, m_user, m_password);
                        // Create a table if it doesn't exist yet
                        Statement stmt = m_dbconn.createStatement();
                        String sql =    "IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES \n" +
                                        "           WHERE TABLE_NAME = N'" + TABLE + "')\n" +
                                        "BEGIN\n" +
                                        "       CREATE TABLE " + TABLE + " " +
                                        "       (preset_name VARCHAR(255) not NULL, " +
                                        "       object VARBINARY()" + 
                                        "       PRIMARY KEY (preset_name))" +
                                        "END";
                        stmt.executeUpdate(sql);
        }

        /**
         * Initialize with the dedicated system configuration database.
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
         * Initialize with a mock database which may be useful for testing.
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
         */
        public static void add_preset(SystemPreset preset) {
        }
        
        /**
         * Fetch the system preset using its name.
         *
         * @param entry the name of the preset that is to be searched.
         * @return the SystemPreset, if the entry exists, or null if the
         * otherwise.
         */
        public static SystemPreset fetch(String entry) {
                return null;
        }

        /**
         * Clear entries in the database and the tracking list.
         */
        public static void clear() {
        }
}
