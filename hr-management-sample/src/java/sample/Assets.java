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

/**
 *
 * @author davis
 */
public class Assets {

        public static String get_user_account_html_fragment() {
                String user_account = 
"<!DOCTYPE html>\n" +
"<html>\n" +
"    <head>\n" +
"        <meta charset=\"UTF-8\" />\n" +
"        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
"        <link rel=\"stylesheet\" href=\"css/tranquil.css\" />\n" +
"        <link rel=\"stylesheet\" href=\"css/emotionless.css\" />\n" +
"\n" +
"        <title>HRM - User Account</title>\n" +
"    </head>\n" +
"    <body class=\"emotionless\">\n" +
"        <h1 class=\"heading\">\n" +
"            <a class=\"no-decoration\" href=\"index.jsp\">HRM 0.1</a>\n" +
"        </h1>\n" +
"        <div id=\"DETAILED-ACCOUNT-MANAGEMENT-UI\" class=\"central\"/>\n" +
"    </body>\n" +
"</html>";
                return user_account;
        }
        
        public static String get_login_html_fragment() {
                String login_html = 
"<!DOCTYPE html>\n" +
"<html>\n" +
"    <head>\n" +
"        <meta charset=\"UTF-8\" />\n" +
"        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
"        <link rel=\"stylesheet\" href=\"css/tranquil.css\" />\n" +
"        <link rel=\"stylesheet\" href=\"css/emotionless.css\"/>\n" +
"\n" +
"        <title>HRM - Account</title>\n" +
"    </head>\n" +
"    <body class=\"emotionless\">\n" +
"            <h2 class=\"heading-text\">HRM Login</h2>\n" +
"            \n" +
"            <div class=\"central-text\">Username</div>\n" +
"            <input id=\"txb-user-name\" class=\"central-input\" type=\"text\"/>\n" +
"            \n" +
"            <div class=\"central-text\">Password</div>\n" +
"            <input id=\"txb-password\" class=\"central-input\" type=\"password\"/>\n" +
"            \n" +
"            <button id=\"btn-login\" class=\"central-button\">login</button>\n" +
"    </body>\n" +
"</html>";
                return login_html;
        }
        
        public static String get_signup_html_fragment() {
                String signup_html = 
"<!DOCTYPE html>\n" +
"<html>\n" +
"    <head>\n" +
"        <meta charset=\"UTF-8\" />\n" +
"        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
"        <link rel=\"stylesheet\" href=\"css/tranquil.css\" />\n" +
"        <link rel=\"stylesheet\" href=\"css/emotionless.css\" />\n" +
"\n" +
"        <title>HRM - Account</title>\n" +
"    </head>\n" +
"    <body class=\"emotionless\">\n" +
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
"    </body>\n" +
"</html>";
                return signup_html;
        }
}
