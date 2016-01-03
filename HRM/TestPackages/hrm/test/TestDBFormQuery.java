/*
 * Copyright (C) 2016 davis
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

import hrm.model.DBSystemFormManager;
import hrm.model.FormData;
import hrm.model.FormModule;
import hrm.model.FormQuery;
import hrm.model.SystemFormException;
import hrm.utils.Attribute;
import hrm.utils.RMIInteger;
import hrm.utils.RMIString;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 * Test the FormQuery and its helper classes.
 * @author davis
 */
public class TestDBFormQuery {
        @Rule public final TestName m_test_name = new TestName();
        
        public TestDBFormQuery() {
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
        public void dbform_query() throws Exception {
                FormQuery query = new FormQuery("(city = #CityName# AND name = #CompanyName#)\n" +
"OR (ranking >= #RankingNumber#)");
                query.set_attribute("CompanyName", new Attribute("ATTRI2", "Pixar"));
                query.set_attribute("CityName", new Attribute("ATT1", "Irvine"));
                query.set_attribute("RankingNumber", new Attribute("ATTR3", 10));
                System.out.println(query);
        }
        
        @Test
        public void store_and_fetch() throws SQLException, Exception {
                FormModule module = new FormModule("testconf/test-preset.xml");
                FormData data = new FormData();
                data.add_attribute("Document", new RMIInteger(100024));
                data.add_attribute("Name", new RMIString("davis"));
                FormData data2 = new FormData();
                data.add_attribute("Document", new RMIInteger(100025));
                data.add_attribute("Name", new RMIString("ozh"));
                FormQuery query = new FormQuery("1 = 1");
                FormData data3 = new FormData();
                data.add_attribute("Document", new RMIInteger(100024));
                data.add_attribute("Name", new RMIString("someone"));
                FormQuery query2 = new FormQuery("Document=#Document-No#");
                query2.set_attribute("Document-No", new Attribute("dfdka", 100024));
                
                DBSystemFormManager formmgr = new DBSystemFormManager(true, true);
                formmgr.update(module, query, data);
                formmgr.update(module, query, data2);
                formmgr.update(module, query2, data3);
                
                FormQuery query3 = new FormQuery("Name=#persons-name# AND Document=#Document-No#");
                query3.set_attribute("Document-No", new Attribute("dfdka", 100024));
                query3.set_attribute("persons-name", new Attribute("adsf", "davis"));
                try {
                        FormData result = formmgr.query(module, query3);
                        fail();
                } catch(SystemFormException ex) {
                        System.out.println("System form not found exception test pass: "
                                + ex.getMessage());
                }
                
                FormQuery query4 = new FormQuery("Name=#persons-name# AND Document=#Document-No#");
                query4.set_attribute("Document-No", new Attribute("dfdka", 100025));
                query4.set_attribute("persons-name", new Attribute("adsf", "ozh"));
                FormData result = formmgr.query(module, query4);
                assertEquals(result, data2);
        }
}
