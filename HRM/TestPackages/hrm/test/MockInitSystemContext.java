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
package hrm.test;

import hrm.system.HRMMain;
import hrm.system.HRMSystemContext;
import hrm.system.MockHRMSystemContext;
import javax.servlet.ServletContextEvent;
import org.springframework.mock.web.MockServletContext;

/**
 * Mock and initialize the system context.
 * @author davis
 */
public class MockInitSystemContext {
        HRMMain                 m_main = new HRMMain(new MockHRMSystemContext());
        MockServletContext      m_sc = new MockServletContext();
        
        public MockInitSystemContext(String context_path) {
                m_sc.setContextPath(context_path);
        }
        
        public HRMSystemContext init() {
                m_main.contextInitialized(new ServletContextEvent(m_sc));
                return HRMMain.get_system_context();
        }
        
        public void free() {
                m_main.contextDestroyed(new ServletContextEvent(m_sc));
        }
}
