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
package hrm.model;

/**
 * Factory to construct a SystemPreset.
 * @author davis
 */
public class SystemPresetFactory {
        public static final int         DBFORM_MODULE_PRESET = 1;
        public static final int         AUTHEN_PRESET = 2;
        
        public static SystemPreset create_by_type_and_name(int type, String name) {
                switch (type) {
                        case DBFORM_MODULE_PRESET:
                                return new DBFormModulePreset(name);
                        case AUTHEN_PRESET:
                                return new AuthenPreset(name);
                        default:
                                return null;
                }
        }
}
