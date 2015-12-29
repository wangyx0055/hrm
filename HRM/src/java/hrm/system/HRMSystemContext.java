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
 * Holding useful context information for other module.
 * @author davis
 */
public interface HRMSystemContext {
        
        public void free();
        
        public SystemPresetManager get_preset_manager();
        
        public DispatcherManager get_dispatcher_manager();
        
        public SystemFormManager get_system_form_manager();
}
