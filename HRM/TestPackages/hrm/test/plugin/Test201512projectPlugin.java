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
package hrm.test.plugin;

import hrm.controller.Dispatcher;
import hrm.system.HRMBusinessPluginException;
import hrm.system.HRMSystemContext;
import hrm.test.MockInitSystemContext;
import hrm.view.JSPResolver;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TestName;
import static org.springframework.util.Assert.notNull;

/**
 * Test the Dispatcher.
 * @author davis
 */
public class Test201512projectPlugin {
        @Rule public final TestName m_test_name = new TestName();
        
        public Test201512projectPlugin() {
        }
        
        @BeforeClass
        public static void setUpClass() {
        }
        
        @AfterClass
        public static void tearDownClass() {
        }
        
        @Before
        public void setUp() {
                System.out.println("===================" + "Running Test Case: " + m_test_name.getMethodName() + "===================");
        }
        
        @After
        public void tearDown() {
                System.out.println("===================" + "Finished Test case:" + m_test_name.getMethodName() + "===================");
        }

        @Test
        public void dispatch_and_returned() throws HRMBusinessPluginException {
                MockInitSystemContext sysmock = 
                        new MockInitSystemContext("/home/davis/human-resource-management-code/HRM/web/");
                        
                HRMSystemContext ctx = sysmock.init();
                
                Set<Dispatcher> dispatchers = ctx.get_dispatcher_manager().get_all_dispatchers();
                notNull(dispatchers);
                assertTrue(!dispatchers.isEmpty());
                Object[] objs = dispatchers.toArray();
                Dispatcher dispatcher = (Dispatcher) objs[0];
                
                Dispatcher.CallerContext caller = dispatcher.get_caller_context("return-registration-form");
                Dispatcher.ReturnValue values = dispatcher.dispatch_jsp(caller);
                notNull(values);
                JSPResolver resolver = values.get_resolver();
                notNull(resolver);
                System.out.println(resolver.resolve_page_as_string());
                
                sysmock.free();
        }
}
