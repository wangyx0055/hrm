/*
 * Copyright (C) 2016 davis
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
package hrm.utils;

import java.lang.reflect.Constructor;

/**
 *
 * @author davis
 */
public class RMIConstructor {
        
        public static void serialize_rmi_obj(RMIObj obj, Serializer s) {
                s.write_string(obj.get_class_name());
                s.write_serialized_stream(obj.serialize());
        }
        
        public static RMIObj deserialze_rmi_obj(String class_name, Serializer s) throws Exception {
                Class<?> clazz = Class.forName(class_name);
                Constructor<?> ctor = clazz.getConstructor();
                RMIObj obj = (RMIObj) ctor.newInstance();
                obj.deserialize(s.read_serialized_stream());
                return obj;
        }
}
