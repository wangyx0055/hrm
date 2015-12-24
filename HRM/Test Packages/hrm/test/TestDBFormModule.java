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

import hrm.model.DBFormModule;
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
        
        public TestDBFormModule() {
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
        public void add_element_to_page_module() {
                hrm.model.DBFormModulePreset preset = new hrm.model.DBFormModulePreset("Test HRM System Preset");
                hrm.model.DBFormModule module = preset.add_module("Test HR Archive Registration");
                assertTrue(module != null);
                
                // Hand setting the form data
                DBFormModule.Hierarchy h = module.get_root();
                h = module.add_element(h, "Level I Facility");
                DBFormModule.Hierarchy h_i = module.step_into(h);
                h_i = module.add_element(h_i, "facility I A");
                h_i = module.add_element(h_i, "facility I B");
                h_i = module.add_element(h_i, "facility I C");
                
                assertEquals(h, module.step_out(h_i));
                
                h = module.add_element(h, "Level II Facility");
                // add level III facility first
                DBFormModule.Hierarchy h3 = module.add_element(h, "Level III Facility");
                h_i = module.step_into(h3);
                h_i = module.add_element(h_i, "facility III A");
                h_i = module.add_element(h_i, "facility III B");
                h_i = module.add_element(h_i, "facility III C");
                assertNotEquals(h, module.step_out(h_i));
                // add level II facility back
                DBFormModule.Hierarchy h_ii = module.step_into(h);
                h_ii = module.add_element(h_ii, "facility II A");
                h_ii = module.add_element(h_ii, "facility II B");
                h_ii = module.add_element(h_ii, "facility II C");
                h_ii = module.add_element(h_ii, "facility II D");
                assertEquals(h, module.step_out(h_ii));
                // add Name
                h = module.add_element(h, "Name");
                assertNotEquals(h, h3);
                h_i = module.step_into(h);
                h_i = module.add_element(h_i, "Last Name, First Name");
                assertEquals(module.step_out(h_i), h);
                
                System.out.println("Hand coded page module contains: " + module);
                
                // Load from .conf file
                hrm.model.DBFormModule module2 = preset.add_module("HR Archive Registration(Loading from file)");
                try {
                        module2.build_from_file("Conf/Test/Test.pageconf");
                } catch(hrm.model.DBFormModuleException e) {
                        System.out.println("Load-module-from-file test failed. Error: " + e);
                        fail();
                }
                System.out.println("Page module loaded from Test.pageconf: " + module2);
                // The one loaded from file should be the same as the one specified above
                assertEquals(module, module2);
        }
        
        @Test
        public void get_element_from_page_module() {
        }
}
