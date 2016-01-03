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

import hrm.model.FormQuery;
import hrm.utils.Attribute;
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
        public void store_and_fetch() {
        }
}
