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
package sample;

import hrm.controller.Dispatcher;
import hrm.model.FormModule;
import hrm.model.FormModulePreset;
import hrm.model.SystemPresetManager;
import hrm.system.HRMSystemContext;
import hrm.utils.Attribute;
import hrm.utils.Prompt;
import hrm.view.FormModuleJSPResolver;
import hrm.view.JSPResolver;
import java.util.Set;

/**
 *
 * @author davis
 */
public class ReturnRegistrationForm implements Dispatcher.CalleeContext {
        private Set<Attribute>          m_attris;
        private final HRMSystemContext  m_ctx;
        
        public ReturnRegistrationForm(HRMSystemContext ctx) {
                m_ctx = ctx;
        }

        @Override
        public void add_params(Set<Attribute> attri) {
                m_attris = attri;
        }
        
        public class ReturnValue implements Dispatcher.ReturnValue {
                private final JSPResolver       m_resolver;
                
                public ReturnValue(JSPResolver resovler) {
                        m_resolver = resovler;
                }
                
                @Override
                public String get_redirected_page_uri() {
                        return "/hr-archive-admin-reg.jsp";
                }

                @Override
                public Set<Attribute> get_session_attribute() {
                        return null;
                }

                @Override
                public Set<Attribute> get_requst_attribute() {
                        return null;
                }

                @Override
                public JSPResolver get_resolver() {
                        return m_resolver;
                }
        }
        
        @Override
        public Dispatcher.ReturnValue get_return_value() {
                SystemPresetManager presets = m_ctx.get_preset_manager();
                FormModulePreset preset = (FormModulePreset) presets.get_system_preset(hrm.system.HRMDefaultName.dbformmodulepreset());
                if (preset == null) {
                        Prompt.log(Prompt.ERROR, getClass().toString(), 
                                "cannot load in deform module preset");
                        return null;
                }
                FormModule module = preset.get_module("hr-archive");
                JSPResolver resolver = new FormModuleJSPResolver(module);
                resolver.add_resolvable(hrm.view.JSPResolver.PageElement.DropDownList, "Level I Facility");
                resolver.add_resolvable(hrm.view.JSPResolver.PageElement.DropDownList, "Level II Facility");
                resolver.add_non_resolvable(hrm.view.JSPResolver.PageElement.LineBreak);
                return new ReturnValue(resolver);
        }
        
}
