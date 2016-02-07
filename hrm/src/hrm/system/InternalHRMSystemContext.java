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
import hrm.model.DBFormDataManager;
import hrm.model.DBDataComponentManager;
import hrm.utils.Prompt;
import java.sql.SQLException;
import hrm.model.DataComponentManager;
import hrm.model.FormDataException;
import hrm.model.FormDataManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Internal implementation of the SystemContext.
 * @author davis
 */
public class InternalHRMSystemContext implements HRMSystemContext {
        public DataComponentManager     m_preset_mgr = null;
        public FormDataManager          m_form_mgr = null;
        public DispatcherManager        m_disp_mgr = null;
        
        private static final String     COMP_DB = "db/hrm_data_component";
        private static final String     FORM_DB = "db/hrm_form_data";
        
        public InternalHRMSystemContext(String system_root, String system_user, String system_passcode) {
                try {
                        m_preset_mgr = new DBDataComponentManager(
                                system_root + COMP_DB, system_user, system_passcode, true);
                        m_form_mgr = new DBFormDataManager(
                                system_root + FORM_DB, system_user, system_passcode, false);
                } catch (SQLException | ClassNotFoundException ex) {
                        Prompt.log(Prompt.ERROR, getClass().toString(), 
                                "Failed to initialize DBSystemFormManager, Details: " + ex.getMessage());
                }
                m_disp_mgr = new DispatcherManager();
        }
        
        @Override
        public void free() {
                if (m_preset_mgr != null) {
                        m_preset_mgr.free();
                }
                if (m_form_mgr != null) {
                        try {
                                m_form_mgr.free();
                        } catch (FormDataException ex) {
                                Prompt.log(Prompt.WARNING, getClass().toString(), ex.toString());
                        }
                }
        }
        
        @Override
        public DataComponentManager get_preset_manager() {
                return m_preset_mgr;
        }
        
        @Override
        public DispatcherManager get_dispatcher_manager() {
                return m_disp_mgr;
        }
        
        @Override
        public FormDataManager get_system_form_manager() {
                return m_form_mgr;
        }
}
