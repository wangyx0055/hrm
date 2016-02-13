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
import hrm.system.HRMSystemContext;
import hrm.system.HRMBusinessPluginException;
import hrm.system.HRMBusinessPlugin;

/**
 * Business specific project
 *
 * @author davis
 */
public class HRManagementLoader implements HRMBusinessPlugin {

        public HRManagementLoader() {
        }

        @Override
        public void init(HRMSystemContext context) throws HRMBusinessPluginException {
                // construct a dispatcher
                Dispatcher dp = new Dispatcher();
                dp.register_controller_call(new CEUserAccount(), CEUserAccount.class.getSimpleName(),
                                            Dispatcher.PageCategory.JspPage);
                // upload the dispatcher
                context.get_dispatcher_manager().add_dispatcher(dp);
        }

        @Override
        public String get_name() {
                return getClass().toString();
        }

        @Override
        public void free() throws HRMBusinessPluginException {
        }
}
