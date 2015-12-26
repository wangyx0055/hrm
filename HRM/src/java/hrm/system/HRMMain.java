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

import hrm.model.SystemPresetException;
import hrm.model.SystemPresetManager;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Initialization and shutdown of the HRM system.
 * @author davis
 */
public class HRMMain implements ServletContextListener {
        
        private ServletContext          m_servlet_ctx;
        private HRMSystemContext        m_ctrl_ctx;
        private List<HRMBusinessPlugin> m_plugins;
        
        @Override
        public void contextInitialized(ServletContextEvent sce) {
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Initializing HRM...");
                // iniialize resources
                m_servlet_ctx = sce.getServletContext();
                m_ctrl_ctx = new HRMSystemContext();
                init();
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce) {
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Shutting down HRM...");
                // destroying resources
                free();
        }
        
        private List<HRMBusinessPlugin> load_plugin(String plugin_conf) {
                ArrayList<HRMBusinessPlugin> importers = new ArrayList();
                String entrance = "";
                try {
                        InputStream in = m_servlet_ctx.getResourceAsStream(plugin_conf);
                        String content = AsciiStream.extract(in);
                        String[] entrances = content.split(System.lineSeparator());
                        for (String e : entrances) {
                                entrance = e;
                                Class<?> clazz = Class.forName("hrm.business." + e);
                                Constructor<?> ctor = clazz.getConstructor();
                                HRMBusinessPlugin importer = 
                                        (HRMBusinessPlugin) ctor.newInstance();
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
        
        public void init() {
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Allocating resources...");
                
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Initializing System preset...");
                // constructing DBFormPreset
                hrm.model.DBFormModulePreset dbform_preset = 
                        new hrm.model.DBFormModulePreset(HRMDefaultName.dbformmodule());
                try {
                        dbform_preset.add_module_from_file(
                                m_servlet_ctx.getResourceAsStream("hr-archive.xml"));
                } catch (SystemPresetException ex) {
                        Prompt.log(Prompt.ERROR, getClass().toString(), 
                                "Failed to load in DBFormModulePreset, Details: " + ex.getMessage());
                }
                // add presets to manager
                SystemPresetManager mgr = m_ctrl_ctx.get_preset_manager();
                mgr.add_system_preset(dbform_preset);
                
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Loading plugins...");
                m_plugins = load_plugin("CONF/hrm-plugin");
                
                // Initialize the plugins through importer
                for (HRMBusinessPlugin plugin : m_plugins) {
                        if (plugin != null) {
                                try {
                                        plugin.init(m_ctrl_ctx);
                                } catch (HRMBusinessPluginException ex) {
                                        Prompt.log(Prompt.ERROR, getClass().toString(),
                                                "Error found while initializing plugin: " + 
                                                        plugin.get_name());
                                }
                        }
                }
        }
        
        public void free() {
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Deallocating resources...");
                m_ctrl_ctx.free();
                
                for (HRMBusinessPlugin plugin : m_plugins) {
                        if (plugin != null) {
                                try {
                                        plugin.init(m_ctrl_ctx);
                                } catch (HRMBusinessPluginException ex) {
                                        Prompt.log(Prompt.ERROR, getClass().toString(),
                                                "Error found while initializing plugin: " + 
                                                        plugin.get_name());
                                }
                        }
                }
        }
        
}
