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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 * Test the SystemPresetManager class.
 * @author davis
 */
public class TestDBSystemPresetManager {
        @Rule public final TestName m_test_name = new TestName();
        
        public TestDBSystemPresetManager() {
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
        public void add_and_fetch_page_preset() throws ClassNotFoundException, SQLException {
                hrm.model.DBFormModulePreset preset = new hrm.model.DBFormModulePreset("Test HRM System Preset");
                // Add presets to database
                hrm.model.DBSystemPresetManager dbmgr = new hrm.model.DBSystemPresetManager(true, true);
                dbmgr.init_with_mock_database();
                dbmgr.add_system_preset(preset);
                // Fetch the preset back
                hrm.model.SystemPreset preset_fetched = dbmgr.get_system_preset("Test HRM System Preset");
                assertTrue(preset_fetched != null);
                assertTrue(preset_fetched instanceof hrm.model.DBFormModulePreset);
                hrm.model.DBFormModulePreset preset2 = (hrm.model.DBFormModulePreset) preset_fetched;
                System.out.println("System page preset: " + preset);
                System.out.println("System page preset2:" + preset2);
                assertEquals(preset, preset2);
        }
}
