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
package hrm.system;

import hrm.model.SystemConfDatabase;
import hrm.utils.Prompt;
import java.sql.SQLException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Initialization and shutdown of the HRM system.
 * @author davis
 */
public class HRMMain implements ServletContextListener {

        @Override
        public void contextInitialized(ServletContextEvent sce) {
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Initializing HRM...");
                
                hrm.model.DBFormModulePreset preset = 
                        new hrm.model.DBFormModulePreset(
                                hrm.system.ResourceInjection.DEFAULT_DBFORM_MODULE_PRESET);
                preset.add_modules_from_directory("Conf/");
                try {
                        SystemConfDatabase.init();
                        SystemConfDatabase.clear();
                        SystemConfDatabase.add_preset(preset);
                } catch (ClassNotFoundException | SQLException ex) {
                        Prompt.log(Prompt.ERROR, getClass().toString(), 
                                "cannot load in default SystemConfDatabase; Details: " + 
                                        ex.getMessage());
                }
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce) {
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Shutting down HRM...");
                try {
                        SystemConfDatabase.free();
                } catch (SQLException ex) {
                        Prompt.log(Prompt.ERROR, getClass().toString(), 
                                "cannot close resource associated with default SystemConfDatabase; Details: " + 
                                        ex.getMessage());
                }
        }
        
}
