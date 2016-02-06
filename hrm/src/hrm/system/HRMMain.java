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

import hrm.model.DataComponentException;
import hrm.controller.HRMRequestFilter;
import hrm.model.DataComponent;
import hrm.model.DataComponentFactory;
import hrm.utils.AsciiStream;
import hrm.utils.Prompt;
import hrm.utils.ResourceScanner;
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
import java.io.File;
import java.util.EnumSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletRegistration;
import hrm.model.DataComponentManager;

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
//                ServletRegistration.Dynamic ds = m_servlet_ctx.addServlet(
//                        "/" + servlet_namespace + "/DispatcherServlet", 
//                        HRMDispatcherServlet.class);
//                ds.addMapping("*.jsp");
                Map<String, ? extends ServletRegistration> servlets = m_servlet_ctx.getServletRegistrations();
                for (String servlet_name : servlets.keySet()) {
                        ServletRegistration servlet_reg = servlets.get(servlet_name);
                        Prompt.log(Prompt.NORMAL, getClass().toString(), 
                                "Servlets " + servlet_name + " is registered as: " + servlet_reg.getMappings());
                }
                FilterRegistration.Dynamic df = m_servlet_ctx.addFilter(
                        "/" + servlet_namespace + "/RequestFilter", HRMRequestFilter.class);
                df.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, new String[]{"*.jsp"});
                Map<String, ? extends FilterRegistration> filters = m_servlet_ctx.getFilterRegistrations();
                for (String filter_name : filters.keySet()) {
                        FilterRegistration filter_reg = filters.get(filter_name);
                        Prompt.log(Prompt.NORMAL, getClass().toString(), 
                                "Filter " + filter_name + " is registered as: " + 
                                        filter_reg.getUrlPatternMappings());
                }
                // set up the context path
                String context_path = m_servlet_ctx.getInitParameter("system-root");
                Prompt.log(Prompt.NORMAL, getClass().toString(), 
                        "Setting context path to: " + context_path);
                ResourceScanner.init_context_path(context_path);
                Prompt.log(Prompt.NORMAL, getClass().toString(), 
                        "Initializing system context...");
                if (m_ctrl_ctx == null) {
                        String system_root = m_servlet_ctx.getInitParameter("system-root");
                        String system_user = m_servlet_ctx.getInitParameter("system-user");
                        String system_passcode = m_servlet_ctx.getInitParameter("system-passcode");
                        system_root = system_root == null ? "." : system_root;
                        system_user = system_user == null ? "default_user" : system_user;
                        system_passcode = system_passcode == null ? "default_passcode" : system_passcode;
                        m_ctrl_ctx = new InternalHRMSystemContext(system_root, system_user, system_passcode);
                }
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
                
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Initializing Data Components...");
                
                // Data components will be added to this manager
                DataComponentManager mgr = m_ctrl_ctx.get_preset_manager();
                
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
                                        DataComponent comp = 
                                                DataComponentFactory.create_from_file(
                                                        DataComponentFactory.FORM_MODULE_COMPONENT,in);
                                        mgr.add_system_component(comp);
                                } catch (Exception ex) {
                                        Prompt.log(Prompt.WARNING, getClass().toString(), 
                                                "Failed to load in FormModulePreset, Details: " + 
                                                        ex.getMessage());
                                }
                        }
                } catch (FileNotFoundException ex) {
                        Prompt.log(Prompt.ERROR, getClass().toString(), "Failed to load in all form presets.");
                }
                
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
