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

import hrm.utils.Prompt;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Initialization and shutdown of the HRM system.
 * @author davis
 */
public class HRMMain implements ServletContextListener {
        private HRMResource     m_res = null; 

        @Override
        public void contextInitialized(ServletContextEvent sce) {
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Initializing HRM...");
                m_res = HRMResourceSingleton.get_instance();
                // iniialize resources
                m_res.init(sce.getServletContext());
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce) {
                Prompt.log(Prompt.NORMAL, getClass().toString(), "Shutting down HRM...");
                // destroying resources
                m_res.free();
        }
        
}
