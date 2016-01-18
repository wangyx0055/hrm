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

import java.io.InputStream;

/**
 * Factory to construct a DataComponent.
 * @author davis
 */
public class DataComponentFactory {
        public static final int         FORM_MODULE_COMPONENT = 1;
        public static final int         AUTHEN_COMPONENT = 2;
        public static final int         FORM_QUERY_COMPONENT = 3;
        
        /**
         * Create a system component through type and its name.
         * @param type type of the component.
         * @param name the name of it.
         * @return the component created.
         * @throws hrm.model.DataComponentException
         */
        public static DataComponent create_by_type_and_name(int type, String name) 
                throws DataComponentException {
                switch (type) {
                        case FORM_MODULE_COMPONENT:
                                return new FormModule(name);
                        case AUTHEN_COMPONENT:
                                return new Authen(name);
                        case FORM_QUERY_COMPONENT:
                                return new FormQuery(name);
                        default:
                                throw new DataComponentException(
                                        DataComponentException.Error.InvalidComponent).
                                        add_extra_info("Unexpectd type: " + type);
                }
        }
        
        /**
         * Create a system component through file stream that defines it.
         * @param type type of the component.
         * @param in the name of it.
         * @return if there is no error detected, return the component created from via the file stream.
         * @throws hrm.model.DataComponentException
         */
        public static DataComponent create_from_file(int type, InputStream in) throws DataComponentException {
                switch (type) {
                        case FORM_MODULE_COMPONENT: {
                                throw new UnsupportedOperationException();
                        }
                        case AUTHEN_COMPONENT: {
                                throw new UnsupportedOperationException();
                        }
                        case FORM_QUERY_COMPONENT: {
                                throw new UnsupportedOperationException();
                        }
                        default:
                                throw new DataComponentException(
                                        DataComponentException.Error.InvalidComponent).
                                        add_extra_info("Unexpectd type: " + type);
                }
        }
}
