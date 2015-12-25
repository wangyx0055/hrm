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
import hrm.utils.AsciiStream;
import hrm.utils.Prompt;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

/**
 * List of all configurable resources.
 * @author davis
 */
public class HRMResource {
        public static final String      DBFORM_MODULE_PRESET = "DEFAULTSYSTEMDBFORMMODULEPRESET";
        
        HRMResource() {
                // Cannot be constructed
        }
        
        private ServletContext    m_context;
        
        private List<HRMBusinessImporter> load_plugin(String plugin_conf) {
                ArrayList<HRMBusinessImporter> importers = new ArrayList();
                String entrance = "";
                try {
                        InputStream in = m_context.getResourceAsStream(plugin_conf);
                        String content = AsciiStream.extract(in);
                        String[] entrances = content.split(System.lineSeparator());
                        for (String e : entrances) {
                                entrance = e;
                                Class<?> clazz = Class.forName("hrm.business." + e);
                                Constructor<?> ctor = clazz.getConstructor();
                                HRMBusinessImporter importer = 
                                        (HRMBusinessImporter) ctor.newInstance();
                                importers.add(importer);
                        }
                } catch (FileNotFoundException ex) {
                        Prompt.log(Prompt.ERROR, getClass().toString(), 
                                "Cannot find plugin configuration file: " + plugin_conf +
                                        ", Details: " + ex.getMessage());
                } catch (IOException ex) {
                        Prompt.log(Prompt.ERROR, getClass().toString(), 
                                "Cannot read plugin configuration file: " + plugin_conf +
                                        ", Details: " + ex.getMessage());
                } catch (ClassNotFoundException | 
                         NoSuchMethodException | 
                        SecurityException | 
                        InstantiationException | 
                        IllegalAccessException | 
                        IllegalArgumentException | 
                        InvocationTargetException ex) {
                        Prompt.log(Prompt.ERROR, getClass().toString(), 
                                "Cannot find/initialize class: " + entrance + 
                                        " specified in the plugin configuration file: " + plugin_conf +
                                           ", Details: " + ex.getMessage());
                }
                return importers;
        }
        
        public void init(ServletContext context) {
                m_context = context;
                
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Allocating resources...");
                
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Initializing DBFormModulePreset...");
                hrm.model.DBFormModulePreset preset = 
                        new hrm.model.DBFormModulePreset(
                                hrm.system.HRMResource.DBFORM_MODULE_PRESET);
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
                
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Loading plugins...");
                List<HRMBusinessImporter> plugins = load_plugin("CONF/hrm-plugin");
                
                // Initialize the plugins through importer
                for (HRMBusinessImporter importer : plugins) {
                        if (importer != null) {
                                try {
                                        importer.init();
                                } catch (HRMBusinessImporterException ex) {
                                        Prompt.log(Prompt.ERROR, getClass().toString(),
                                                "Error found while initializing plugin: " + 
                                                        importer.get_name());
                                }
                        }
                }
        }
        
        public void free() {
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Deallocating resources...");
                
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Destroying DBFormModulePreset...");
                try {
                        SystemConfDatabase.free();
                } catch (SQLException ex) {
                        Prompt.log(Prompt.ERROR, getClass().toString(), 
                                "cannot close resource associated with default SystemConfDatabase; Details: " + 
                                        ex.getMessage());
                }
        }
}
