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
package test;

import hrm.view.HtmlUIBuilder;
import hrm.view.HtmlUIBuilder.InsertionPoint;
import hrm.view.HtmlUIBuilder.UINode;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 *
 * @author davis
 */
public class TestUIBuilder {
        
        @Rule public final TestName m_test_name = new TestName();
        
        public TestUIBuilder() {
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
        public void build_and_output_ui() {
                                String user_account = 
"<!DOCTYPE html>\n" +
"<html>\n" +
"    <head>\n" +
"        <meta charset=\"UTF-8\"/>\n" +
"        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
"        <link rel=\"stylesheet\" href=\"css/tranquil.css\"/>\n" +
"        <link rel=\"stylesheet\" href=\"css/emotionless.css\"/>\n" +
"\n" +
"        <title>HRM - Login</title>\n" +
"    </head>\n" +
"    <body class=\"emotionless\">\n" +
"        <h1 class=\"heading\">\n" +
"            <a class=\"no-decoration\" href=\"index.jsp\">HRM 0.1</a>\n" +
"        </h1>\n" +
"        <div id=\"DETAILED-ACCOUNT-MANAGEMENT-UI\"/>\n" +
"    </body>\n" +
"</html>";
                
                String login_html = 
"<!DOCTYPE html>\n" +
"<html>\n" +
"    <head>\n" +
"        <meta charset=\"UTF-8\"/>\n" +
"        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
"        <link rel=\"stylesheet\" href=\"css/tranquil.css\"/>\n" +
"        <link rel=\"stylesheet\" href=\"css/emotionless.css\"/>\n" +
"\n" +
"        <title>HRM - Account</title>\n" +
"    </head>\n" +
"    <body class=\"emotionless\">\n" +
"        <div class=\"central\">\n" +
"            <h2 class=\"heading-text\">HRM Login</h2>\n" +
"            \n" +
"            <div class=\"central-text\">Username</div>\n" +
"            <input id=\"txb-user-name\" class=\"central-input\" type=\"text\"/>\n" +
"            \n" +
"            <div class=\"central-text\">Password</div>\n" +
"            <input id=\"txb-password\" class=\"central-input\" type=\"password\"/>\n" +
"            \n" +
"            <button id=\"btn-login\" class=\"central-button\">login</button>\n" +
"        </div>\n" +
"    </body>\n" +
"</html>";
                String signup_html = 
"<!DOCTYPE html>\n" +
"<html>\n" +
"    <head>\n" +
"        <meta charset=\"UTF-8\"/>\n" +
"        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
"        <link rel=\"stylesheet\" href=\"css/tranquil.css\"/>\n" +
"        <link rel=\"stylesheet\" href=\"css/emotionless.css\"/>\n" +
"\n" +
"        <title>HRM - Account</title>\n" +
"    </head>\n" +
"    <body class=\"emotionless\">\n" +
"        <div class=\"central\">\n" +
"            <h2 class=\"heading-text\">HRM Signup</h2>\n" +
"            \n" +
"            <div class=\"central-text\">Username</div>\n" +
"            <input id=\"txb-user-name\" class=\"central-input\" type=\"text\"/>\n" +
"            \n" +
"            <div class=\"central-text\">Password</div>\n" +
"            <input id=\"txb-password\" class=\"central-input\" type=\"password\"/>\n" +
"            <div class=\"central-text\">Retype password</div>\n" +
"            <input id=\"txb-retype-password\" class=\"central-input\" type=\"password\"/>\n" +
"            \n" +
"            <div class=\"central-text\">Email Address</div>\n" +
"            <input id=\"txb-email\" class=\"central-input\" type=\"text\"/>\n" +
"            \n" +
"            <button id=\"btn-signup\" class=\"central-button\">sign up</button>\n" +
"        </div>\n" +
"    </body>\n" +
"</html>";
                                
                HtmlUIBuilder ui = new HtmlUIBuilder();
                UINode root = ui.create_node(true, "root");
                Map<String, HtmlUIBuilder.InsertionPoint> insps = root.insert_html(user_account, true);
                
                System.out.println("Insertion points generated: " + insps);
                InsertionPoint insp = insps.get("DETAILED-ACCOUNT-MANAGEMENT-UI");
                
                System.out.println("Using login ui: ");
                UINode mgm_ui = ui.create_node(false, "login ui");
                mgm_ui.insert_html(login_html, false);
                insp.link_ui_node(mgm_ui);
                System.out.println("Resulting UI: \n" + root);
                
                System.out.println("Switching ui to signup: ");
                UINode mgm_ui2 = ui.create_node(false, "signup ui");
                mgm_ui.insert_html(signup_html, false);
                insp.link_ui_node(mgm_ui);
                System.out.println("Resulting UI: \n" + root);
                
        }
        
        @Test
        public void build_and_switch_ui() {
        }
}
