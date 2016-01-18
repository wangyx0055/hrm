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
package test;

import hrm.model.DBDataComponentManager;
import hrm.model.DataComponent;
import hrm.model.DataComponentException;
import hrm.model.DataComponentFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TestName;
import hrm.model.DataComponentManager;

/**
 * A utility to deploy page presets to the database.
 * @author davis
 */
public class DeployDataComponentToDatabase {
        
        @Rule public final TestName     m_deployment_name = new TestName();
        
        public boolean                  m_is_mocked = false;
        
        public DeployDataComponentToDatabase() {
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
        public void system_page_presets() 
                throws ClassNotFoundException, SQLException, DataComponentException, FileNotFoundException {
                DataComponent form = DataComponentFactory.create_from_file(
                        DataComponentFactory.FORM_MODULE_COMPONENT, new FileInputStream("hr-archive.xml"));
                DataComponentManager mgr = new DBDataComponentManager(false, true);
                mgr.add_system_component(form);
        }
}
