
<%-- 
Copyright (C) 2015 davis

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
--%>

<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="css/tranquil.css">

        <title>HRM - Index Page</title>
    </head>
    <body class="vernal">
        <div class="login-link">
            <button id="btn-login-link" class="login-link" type="button">login</button>
            <button id="btn-signup-link" class="login-link" type="button">sign up</button>
        </div>
        <h1 id="txt-logo" class="heading">
            <a class="no-decoration" href="index.jsp">HRM 0.1 - Welcome</a>
        </h1>
        <div class="banner">
            <button id="btn-me-and-others" class="banner-menu" type="button">Me & others</button>
            <button id="btn-blog" class="banner-menu" type="button">Blog</button>
            <button id="btn-plans" class="banner-menu" type="button">Plans</button>
            <div class="dropdown-group">
                <button id="btn-personnel-access" class="banner-menu" type="button">Personnel Access</button>
                <div id="dd-personnel-access" class="dropdown-content">
                    <a href="hr-archive-admin-reg.jsp?call=return-registration-form">Archive Registration</a>
                    <a href="hr-archive-admin-query.jsp">Archive Query</a>
                    <a href="hr-archive-admin-mod.jsp">Archive Modification</a>
                </div>
            </div>
            <div class="dropdown-group">
                <button id="btn-manager-access" class="banner-menu" type="button">Manager Access</button>
                <div id="dd-manager-access" class="dropdown-content">
                    <a href="hr-archive-admin-review.jsp">Archive Review</a>
                    <a href="hr-archive-admin-query.jsp">Archive Query</a>
                    <a href="hr-archive-admin-removal.jsp">Archive Removal</a>
                </div>
            </div>
            <div class="dropdown-group">
                <button id="btn-specialist-access" class="banner-menu" type="button">Payroll Specialist Access</button>
                <div id="dd-specialist-access" class="dropdown-content">
                    <a href="hr-salary-reg.jsp">Salary Registration</a>
                    <a href="hr-salary-query.jsp">Salary Query</a>
                    <a href="hr-payroll-reg.jsp">Payroll Registration</a>
                    <a href="hr-payroll-query.jsp">Payroll Query</a>
                </div>
            </div>
            <div class="dropdown-group">
                <button id="btn-payroll-manager-access" class="banner-menu" type="button">Payroll Manager Access</button>
                <div id="dd-payroll-manager-access" class="dropdown-content">
                    <a href="hr-salary-reg.jsp">Salary Registration</a>
                    <a href="hr-salary-review.jsp">Salary Review</a>
                    <a href="hr-salary-query.jsp">Salary Query</a>
                    <a href="hr-payroll-reg.jsp">Payroll Registration</a>
                    <a href="hr-payroll-query.jsp">Payroll Query</a>
                    <a href="hr-payroll-review.jsp">Payroll Review</a>
                </div>
            </div>
            <button id="btn-system-admin-access" class="banner-menu" type="button">System Admin Access</button>
        </div>
        <div class="two-sided">
            <div class="forum-board left">
                <h1 style="margin-left: 20px; margin-top: 10px">Forum Board</h1>
                <div class="content-board">
                    <div id="forum-thread">Thread 1</div>
                    <div id="forum-thread">Thread 2</div>
                    <div id="forum-thread">Thread 3</div>
                    <div id="forum-thread">Thread 4</div>
                    <div id="forum-thread">Thread 5</div>
                </div>
            </div>

            <div class="reminder-list right">
                <h1 style="margin-left: 20px; margin-top: 10px">Recent Post/Response</h1>
                <div class="content-board">
                    <div id="forum-thread">Thread 1</div>
                    <div id="forum-thread">Thread 2</div>
                    <div id="forum-thread">Thread 3</div>
                    <div id="forum-thread">Thread 4</div>
                    <div id="forum-thread">Thread 5</div>
                </div>
            </div>
        </div>

        <script>
            // redirect to proper index page
            function load_window(event) {
                if (location.search.slice(1) === "") {
                    //window.location.replace("index.jspx?call=CEIndex&action=retrieve");
                }
            }

            var g_current_dropdown = null;

            // utils
            function hide_dropdown(dropdown_obj) {
                if (dropdown_obj !== null) {
                    style = window.getComputedStyle(g_current_dropdown);
                    display = style.getPropertyValue('display');
                    if (display === 'block') {
                        g_current_dropdown.classList.toggle('show');
                    }
                }
            }

            function toggle_unique_dropdown(dropdown_obj) {
                if (dropdown_obj !== null && g_current_dropdown === dropdown_obj) {
                    g_current_dropdown = dropdown_obj;
                    g_current_dropdown.classList.toggle('show');
                } else {
                    hide_dropdown(g_current_dropdown);
                    g_current_dropdown = dropdown_obj;
                    if (g_current_dropdown !== null) {
                        g_current_dropdown.classList.toggle('show');
                    }
                }
            }

            // button events
            function clk_login_link(event) {
                window.location.href = "user-account.jspx?call=CEUserAccount&action=retrieve-login&return=form";
            }

            function clk_signup_link(event) {
                window.location.href = "user-account.jspx?call=CEUserAccount&action=retrieve-signup&return=form";
            }

            function clk_me_and_others(event) {
                toggle_unique_dropdown(null);
            }

            function clk_blog(event) {
                toggle_unique_dropdown(null);
                alert("not supported yet");
            }

            function clk_plans(event) {
                toggle_unique_dropdown(null);
                alert("not supported yet");
            }

            function clk_personnel_access(event) {
                toggle_unique_dropdown(document.getElementById("dd-personnel-access"));
            }

            function clk_manager_access(event) {
                toggle_unique_dropdown(document.getElementById("dd-manager-access"));
            }

            function clk_specialist_access(event) {
                toggle_unique_dropdown(document.getElementById("dd-specialist-access"));
            }

            function clk_payroll_manager_access(event) {
                toggle_unique_dropdown(document.getElementById("dd-payroll-manager-access"));
            }

            function clk_system_admin_access(event) {
            }
            
            window.onload = load_window;
            document.getElementById("txt-logo").onclick = load_window;
            document.getElementById("btn-login-link").onclick = clk_login_link;
            document.getElementById("btn-signup-link").onclick = clk_signup_link;
            document.getElementById("btn-blog").onclick = clk_blog;
            document.getElementById("btn-plans").onclick = clk_plans;
            document.getElementById("btn-personnel-access").onclick = clk_personnel_access;
            document.getElementById("btn-manager-access").onclick = clk_manager_access;
            document.getElementById("btn-specialist-access").onclick = clk_specialist_access;
            document.getElementById("btn-payroll-manager-access").onclick = clk_payroll_manager_access;
            document.getElementById("btn-system-admin-access").onclick = clk_system_admin_access;
        </script>
    </body>
</html>
