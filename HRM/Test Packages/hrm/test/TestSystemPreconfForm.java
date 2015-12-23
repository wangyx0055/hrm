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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 * Test Form Data which is pre-configured in the system.
 * @author davis
 */
public class TestSystemPreconfForm {
        @Rule public final TestName m_test_name = new TestName();
        
        public TestSystemPreconfForm() {
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
        public void set_and_draw_form_data_from_system() throws ClassNotFoundException, SQLException {
                hrm.model.SystemPagePreset preset = new hrm.model.SystemPagePreset("Test HRM System Preset");
                hrm.model.SystemPageModule module = preset.add_module("Test HR Archive Registration");
                assertTrue(module != null);
                
                // Hand setting the form data
                List<String> dropdownlist = new ArrayList<>();
                dropdownlist.add("facility I A");
                dropdownlist.add("facility I B");
                dropdownlist.add("facility I C");
                module.add_element("Level I Facility", dropdownlist);
                
                List<String> dropdownlist2 = new ArrayList<>();
                dropdownlist2.add("facility II A");
                dropdownlist2.add("facility II B");
                dropdownlist2.add("facility II C");
                dropdownlist2.add("facility II D");
                module.add_element("Level II Facility", dropdownlist2);
                
                List<String> name_hint = new ArrayList<>();
                name_hint.add("Last Name, First Name");
                List<String> empty_string_list = new ArrayList<>();
                empty_string_list.add("");
                module.add_element("Name", name_hint);
                module.add_element("Name2", empty_string_list);
                
                module.add_element("Test Label", empty_string_list);
                
                System.out.println("Hand coded page module contains: " + module);
                
                // Load from .conf file
                hrm.model.SystemPageModule module2 = preset.add_module("HR Archive Registration(Loading from file)");
                try {
                        module2.build_from_file("Conf/Test.pageconf");
                } catch(hrm.model.SystemPageModuleException e) {
                        System.out.println("Load-module-from-file test failed. Error: " + e);
                        fail();
                }
                System.out.println("Page module loaded from Test.pageconf: " + module2);
                // The one loaded from file should be the same as the one specified above
                assertEquals(module, module2);
                
                // Add presets to database
                hrm.model.SystemConfDatabase.init_with_mock_database();
                hrm.model.SystemConfDatabase.add_preset(preset);
                // Fetch the preset back
                hrm.model.SystemPreset preset_fetched = hrm.model.SystemConfDatabase.fetch("Test HRM System Preset");
                assertTrue(preset_fetched != null);
                assertTrue(preset_fetched instanceof hrm.model.SystemPagePreset);
                hrm.model.SystemPagePreset preset2 = (hrm.model.SystemPagePreset) preset_fetched;
                System.out.println("System page preset: " + preset);
                System.out.println("System page preset2:" + preset2);
                assertEquals(preset, preset2);
        }
}
