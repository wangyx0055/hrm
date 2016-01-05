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

import hrm.utils.Serializer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 *
 * @author davis
 */
public class TestSerializer {
        
        @Rule public final TestName m_test_name = new TestName();
        
        public TestSerializer() {
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
        public void serialize_and_deserialize() {
                Serializer s = new Serializer();
                s.write_int(new Integer(1000100));
                s.write_string("This is a String to be serialized");
                s.write_long(new Long(101010100010L));
                
                Serializer d = new Serializer();
                d.from_byte_stream(s.to_byte_stream());
                assertEquals(1000100, d.read_int());
                assertEquals("This is a String to be serialized", d.read_string());
                assertEquals(101010100010L, d.read_long());
        }
}
