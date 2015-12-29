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

import hrm.controller.DispatcherManager;
import hrm.model.DBSystemFormManager;
import hrm.model.DBSystemPresetManager;
import hrm.model.SystemFormManager;
import hrm.model.SystemPresetManager;

/**
 * Internal implementation of the SystemContext.
 * @author davis
 */
public class InternalHRMSystemContext implements HRMSystemContext {
        public final SystemPresetManager        m_preset_mgr;
        public final SystemFormManager          m_form_mgr;
        public final DispatcherManager          m_disp_mgr;
        
        public InternalHRMSystemContext() {
                m_preset_mgr = new DBSystemPresetManager(false, true);
                m_form_mgr = new DBSystemFormManager(false, true);
                m_disp_mgr = new DispatcherManager();
        }
        
        @Override
        public void free() {
                DBSystemPresetManager mgr = (DBSystemPresetManager) m_preset_mgr;
                mgr.free();
        }
        
        @Override
        public SystemPresetManager get_preset_manager() {
                return m_preset_mgr;
        }
        
        @Override
        public DispatcherManager get_dispatcher_manager() {
                return m_disp_mgr;
        }
        
        @Override
        public SystemFormManager get_system_form_manager() {
                return m_form_mgr;
        }
}
