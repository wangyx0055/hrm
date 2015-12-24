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
package hrm.deployment.utils;

import hrm.model.SystemConfDatabase;
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
 * A utility to deploy page presets to the database.
 * @author davis
 */
public class DeploySystemPresetsToDatabase {
        
        @Rule public final TestName     m_deployment_name = new TestName();
        
        public boolean                  m_is_mocked = false;
        
        public DeploySystemPresetsToDatabase() {
        }
        
        @BeforeClass
        public static void setUpClass() {
        }
        
        @AfterClass
        public static void tearDownClass() {
        }
        
        @Before
        public void setUp() {
                System.out.println("==========" + "Deploying " + m_deployment_name.getMethodName() + "...==========");
        }
        
        @After
        public void tearDown() {
                System.out.println("==========" + "Done      " + m_deployment_name.getMethodName() + "...==========");
        }

        @Test
        public void system_page_presets() throws ClassNotFoundException, SQLException {
                hrm.model.DBFormModulePreset preset = 
                        new hrm.model.DBFormModulePreset(hrm.system.ResourceInjection.DEFAULT_DBFORM_MODULE_PRESET);
                preset.add_modules_from_directory("Conf/");
                if (!m_is_mocked) SystemConfDatabase.init();
                else SystemConfDatabase.init_with_mock_database();
                SystemConfDatabase.clear();
                SystemConfDatabase.add_preset(preset);
        }
}
