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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 * Test the DBFormModule class.
 * @author davis
 */
public class TestDBFormModule {
        @Rule public final TestName m_test_name = new TestName();
        
        private static hrm.model.DBFormModule module;
        private static hrm.model.DBFormModule module2;
        
        public TestDBFormModule() {
        }
        
        @BeforeClass
        public static void setUpClass() {
                hrm.model.DBFormModulePreset preset = new hrm.model.DBFormModulePreset("Test HRM System Preset");
                module = preset.add_module("Test HR Archive Registration");
                module2 = preset.add_module("HR Archive Registration(Loading from file)");
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
        public void add_info() {
                
                assertTrue(module != null);
                // Hand setting the form data;
                hrm.model.DBFormModule key = module.add_key("Level I Facility", 
                        new hrm.utils.Element("Level I Facility", String.class));
                key.add_child("facility I A", new hrm.utils.Element("facility I A"));
                key.add_child("facility I B", new hrm.utils.Element("facility I B"));
                key.add_child("facility I C", new hrm.utils.Element("facility I C"));
                assertEquals(key.parent(), module);
                
                hrm.model.DBFormModule key2 = module.add_key("Level II Facility", 
                        new hrm.utils.Element("Level II Facility", String.class));
                // add_child level III facility first
                hrm.model.DBFormModule key3 = module.add_key("Level III Facility", 
                                new hrm.utils.Element("Level III Facility", String.class));
                key3.add_child("facility III A", new hrm.utils.Element("facility III A"));
                key3.add_child("facility III B", new hrm.utils.Element("facility III B"));
                key3.add_child("facility III C", new hrm.utils.Element("facility III C"));
                assertEquals(key3.parent(), module);
                // add_child level II facility back
                key2.add_child("facility II A", new hrm.utils.Element("facility II A"));
                key2.add_child("facility II B", new hrm.utils.Element("facility II B"));
                key2.add_child("facility II C", new hrm.utils.Element("facility II C"));
                key2.add_child("facility II D", new hrm.utils.Element("facility II D"));
                assertEquals(key2.parent(), module);
                // add_child Name
                hrm.model.DBFormModule key4 = module.add_key("Name", new hrm.utils.Element("Name", String.class));
                assertNotEquals(key4, key);
                key4.add_child("Name hint", new hrm.utils.Element("Last Name, First Name"));
                assertEquals(key4.parent(), module);
                
                System.out.println("Hand coded page module contains: " + module);
                
                // Load from .conf file
                try {
                        module2.build_from_file(new FileInputStream("Conf/Test/test.xml"));
                } catch(FileNotFoundException | hrm.model.DBFormModuleException e) {
                        System.out.println("Load-module-from-file test failed. Error: " + e);
                        fail();
                }
                System.out.println("Page module loaded from test.xml: " + module2);
                // The one loaded from file should be the same as the one specified above
//                assertEquals(module, module2);
        }
        
        @Test
        public void get_structural_info() {
                System.out.println("Structure of the module: " + module);
        }
        
        @Test
        public void serialization() {
                module2.deserialize(module.serialize());
                System.out.println("Module(original):      " + module);
                System.out.println("Module2(deserialized): " + module2);
                assertEquals(module, module2);
        }
}
