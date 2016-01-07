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
import hrm.servlets.HRMDispatcherServlet;
import hrm.utils.AsciiStream;
import hrm.utils.Prompt;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import hrm.utils.ResourceScanner;
import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletRegistration;

/**
 * Initialization and shutdown of the HRM system.
 * @author davis
 */
public class HRMMain implements ServletContextListener {
        
        private static HRMSystemContext m_ctrl_ctx = null;
        private ServletContext          m_servlet_ctx;
        private List<HRMBusinessPlugin> m_plugins;
        
        public HRMMain() {
                // normal initialization
        }
        
        public HRMMain(HRMSystemContext ctximpl) {
                // mock initialization
                m_ctrl_ctx = ctximpl;
        }
        
        public static HRMSystemContext get_system_context() {
                return m_ctrl_ctx;
        }
        
        @Override
        public void contextInitialized(ServletContextEvent sce) {
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Initializing HRM...");
                m_servlet_ctx = sce.getServletContext();
                
                // register servlets
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Initializing Servlets...");
                String servlet_namespace = m_servlet_ctx.getInitParameter("servlet-namespace");
                ServletRegistration.Dynamic ds = m_servlet_ctx.addServlet(
                        "/" + servlet_namespace + "/DispatcherServlet", 
                        HRMDispatcherServlet.class);
                ds.addMapping("*.jsp");
                Map<String, ? extends ServletRegistration> servlets = 
                        m_servlet_ctx.getServletRegistrations();
                for (String servlet_name : servlets.keySet()) {
                        ServletRegistration reg = servlets.get(servlet_name);
                        Prompt.log(Prompt.NORMAL, getClass().toString(), 
                                "Servlets " + servlet_name + " is registered as: " + reg.getMappings());
                }
                // set up the context path
                String context_path = m_servlet_ctx.getInitParameter("system-root");
                Prompt.log(Prompt.NORMAL, getClass().toString(), 
                        "Setting context path to: " + context_path);
                ResourceScanner.init_context_path(context_path);
                Prompt.log(Prompt.NORMAL, getClass().toString(), 
                        "Initializing system context...");
                if (m_ctrl_ctx == null) m_ctrl_ctx = new InternalHRMSystemContext();
                // loading config files
                Prompt.log(Prompt.NORMAL, getClass().toString(), 
                        "Loading external configuration files...");
                init();
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce) {
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Shutting down HRM...");
                // destroying resources
                free();
        }
        
        private List<HRMBusinessPlugin> load_plugin(InputStream in, String plugin_conf) {
                ArrayList<HRMBusinessPlugin> importers = new ArrayList();
                String entrance = "";
                try {
                        String content = AsciiStream.extract(in);
                        String[] entrances = content.split(System.lineSeparator());
                        for (String e : entrances) {
                                entrance = e;
                                Class<?> clazz = Class.forName(e);
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
                // constructing Form Module Preset
                hrm.model.FormModulePreset form_preset = 
                        new hrm.model.FormModulePreset(HRMDefaultName.dbformmodulepreset());
                
                String base = "CONF/";
                try {
                        List<InputStream> ins = 
                                ResourceScanner.open_external_files_at(base, new ResourceScanner.Filter() {
                                @Override
                                public boolean is_accepted(File file) {
                                        return file.getName().endsWith(".xml");
                                }
                        });
                        for (InputStream in : ins) {
                                if (in == null) continue;
                                try {
                                        form_preset.add_module_from_file(in);
                                } catch (SystemPresetException ex) {
                                        Prompt.log(Prompt.WARNING, getClass().toString(), 
                                                "Failed to load in FormModulePreset, Details: " + 
                                                        ex.getMessage());
                                }
                        }
                } catch (FileNotFoundException ex) {
                        Prompt.log(Prompt.ERROR, getClass().toString(), "Failed to load in all form presets.");
                }
                // add presets to manager
                SystemPresetManager mgr = m_ctrl_ctx.get_preset_manager();
                mgr.add_system_preset(form_preset);
                
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Loading plugins...");
                InputStream in;
                try {
                        in = ResourceScanner.open_external_file("CONF/hrm-plugin");
                        m_plugins = load_plugin(in, "CONF/hrm-plugin");
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
                } catch (FileNotFoundException ex) {
                        Logger.getLogger(HRMMain.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
        }
        
        public void free() {
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Deallocating resources...");
                if (m_ctrl_ctx != null) m_ctrl_ctx.free();
                else Prompt.log(Prompt.WARNING, getClass().toString(), "System context was not initialized");
                        
                if (m_plugins != null) {
                        for (HRMBusinessPlugin plugin : m_plugins) {
                                if (plugin != null) {
                                        try {
                                                plugin.free();
                                        } catch (HRMBusinessPluginException ex) {
                                                Prompt.log(Prompt.ERROR, getClass().toString(),
                                                        "Error found while initializing plugin: " + 
                                                                plugin.get_name());
                                        }
                                }
                        }
                } else {
                        Prompt.log(Prompt.WARNING, getClass().toString(), "Plugins were not initialized");
                }
        }
        
}
