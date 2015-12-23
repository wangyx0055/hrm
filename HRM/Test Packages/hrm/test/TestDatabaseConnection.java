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
package hrm.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 * Test the connection with the HRMTestDatabase
 *
 * @author davis
 */
public class TestDatabaseConnection {

        @Rule
        public final TestName m_test_name = new TestName();
        
        private final String    TEST_DATABASE_URL = "jdbc:derby://localhost:1527/HRMTestDatabase";
        private final String    TEST_DATABASE_USER = "hrm_test";
        private final String    TEST_DATABASE_PASS = "hrm_test_password";

        public TestDatabaseConnection() {
        }

        @BeforeClass
        public static void setUpClass() {
        }

        @AfterClass
        public static void tearDownClass() {
        }

        @Before
        public void setUp() {
                System.out.println("===================" + "Running Test Case: " + m_test_name.getMethodName() + "===================");
        }

        @After
        public void tearDown() {
                System.out.println("===================" + "Finished Test case:" + m_test_name.getMethodName() + "===================");
        }

        @Test
        public void connect_to_database() {
                Connection conn = null;
                try {
                        //STEP 2: Register JDBC driver
                        Class.forName("org.apache.derby.jdbc.ClientDriver");

                        //STEP 3: Open a connection
                        System.out.println("Connecting to a selected database...");
                        conn = DriverManager.getConnection(TEST_DATABASE_URL, TEST_DATABASE_USER, TEST_DATABASE_PASS);
                        System.out.println("Connected database successfully...");
                } catch (SQLException | ClassNotFoundException se) {
                        System.out.println(se.getMessage());
                        fail();
                } finally {
                        //finally block used to close resources
                        try {
                                if (conn != null) {
                                        conn.close();
                                }
                        } catch (SQLException se) {
                        }//end finally try
                }//end try
                System.out.println("Goodbye!");
        }
}
