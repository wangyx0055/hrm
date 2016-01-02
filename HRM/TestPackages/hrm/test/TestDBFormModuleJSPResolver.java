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

import hrm.model.SystemPresetException;
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
 * Test the Page Module to JSP resolver.
 * @author davis
 */
public class TestDBFormModuleJSPResolver {
        @Rule public final TestName m_test_name = new TestName();
        
        public TestDBFormModuleJSPResolver() {
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
        public void page_module_to_jsp() throws SystemPresetException, FileNotFoundException {
                // Make the preset
                hrm.model.DBFormModulePreset preset = new hrm.model.DBFormModulePreset("Test HRM System Preset");
                hrm.model.DBFormModule module = preset.add_module("Test HR Archive Registration");
                
                module.build_from_file(new FileInputStream("web/CONF/hr-archive-module.xml"));
                System.out.println("Loaded module: " + module.toString());
                
                hrm.view.DBFormModuleJSPResolver jsp_res = new hrm.view.DBFormModuleJSPResolver(module);
                jsp_res.add_resolvable(hrm.view.JSPResolver.PageElement.DropDownList, "Level I Facility");
                jsp_res.add_resolvable(hrm.view.JSPResolver.PageElement.DropDownList, "Level II Facility");
                jsp_res.add_non_resolvable(hrm.view.JSPResolver.PageElement.LineBreak);
//                jsp_res.add_resolvable(hrm.view.JSPResolver.PageElement.LabeledEntry, "Name");
//                jsp_res.add_resolvable(hrm.view.JSPResolver.PageElement.LargeLabeledEntry, "Resume");
                
                String str_page = jsp_res.resolve_page_as_string();
                String expected_page_str = "";
                System.out.println("Resolved page string: \n" + str_page);
                assertEquals(str_page, expected_page_str);
        }
}
