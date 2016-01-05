<%-- 
    Document   : hr-archieve-admin-reg
    Created on : Dec 21, 2015, 11:47:18 PM
    Author     : davis
--%>

<%@page import="hrm.view.JSPResolver"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>HR Archive Registration</title>
    </head>
    <body>
        <p>You current operation is on HR Archive Registration</p>
        <div>
            <form action="DispatcherServlet" method="post">
                <input type="button" name="btn-submit" value="Submit Form">
                <input type="button" name="btn-cancel" value="Cancel Action">
                <%
                        request.setAttribute("callee", "ReturnRegistrationForm");
                        JSPResolver form_result = (JSPResolver) request.getAttribute("result");
                %>
                <%=form_result%>
            </form>
        </div>
    </body>
</html>
