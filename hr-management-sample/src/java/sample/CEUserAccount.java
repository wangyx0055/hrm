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
package sample;

import hrm.controller.CalleeContext;
import hrm.controller.Parameter;
import hrm.controller.ReturnValue;
import hrm.utils.Attribute;
import hrm.view.BasicJSPResolver;
import hrm.view.JSPResolver;
import hrm.view.UIBuilder.InsertionPoint;
import hrm.view.UIBuilder.UINode;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author davis
 */
public class CEUserAccount extends CalleeContext {
        
        private class Retrieval extends ReturnValue {
                
                private final String m_which;
                
                public Retrieval(String which) {
                        m_which = which;
                }

                @Override
                public String get_redirected_page_uri() {
                        return null;
                }

                @Override
                public Set<Attribute> get_session_attribute() {
                        return null;
                }

                @Override
                public Set<Attribute> get_requst_attribute() {
                        return null;
                }

                @Override
                public JSPResolver get_resolver() {
                        BasicJSPResolver resolver = new BasicJSPResolver("user-account");
                        UINode root = resolver.create_node(true, "user-account");
                        
                        Map<String, InsertionPoint> insps = 
                                root.insert_html(Assets.get_user_account_html_fragment(), true);
                        InsertionPoint insp = insps.get("DETAILED-ACCOUNT-MANAGEMENT-UI");
                        UINode mgm_ui = resolver.create_node(false, "mgm-ui");
                        if (m_which.equals("login")) {
                                mgm_ui.insert_html(Assets.get_login_html_fragment(), false);
                        } else {
                                mgm_ui.insert_html(Assets.get_signup_html_fragment(), false);
                        }
                        insp.link_ui_node(mgm_ui);
                        return resolver;
                }  
        }
        
        private class Submission extends ReturnValue {
                
                private final String m_which;
                
                public Submission(String which) {
                        m_which = which;
                }

                @Override
                public String get_redirected_page_uri() {
                        return null;
                }

                @Override
                public Set<Attribute> get_session_attribute() {
                        return null;
                }

                @Override
                public Set<Attribute> get_requst_attribute() {
                        return null;
                }

                @Override
                public JSPResolver get_resolver() {
                        return null;
                }  
        }
        
        private final Parameter         user_name = new Parameter("user-name");
        private final Parameter         password = new Parameter("password");
        private final Parameter         remember_me = new Parameter("remember-me");

        @Override
        public ReturnValue process(String action) {
                switch (action) {
                        case "retrieve-login":
                                return new Retrieval("login");
                        case "retrieve-signup":
                                return new Retrieval("signup");
                        case "submit-login":
                                require(user_name);
                                require(password);
                                require(remember_me);
                                return new Submission("login");
                        case "submit-signup":
                                return new Submission("signup");
                        default:
                                return null;
                }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
}
