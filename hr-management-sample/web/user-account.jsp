<%-- 
    Document   : login
    Created on : Feb 7, 2016, 1:30:51 PM
    Author     : davis
--%>

<%@page import="hrm.view.JSPResolver"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<%=request.getAttribute("form")%>

<script>
    var submit_button_id = <%= (String) request.getAttribute("submit-button-id")%>
    var submit_target_callee = <%= (String) request.getAttribute("submit-target-callee")%>
    
    function submit_form() {
        window.location.href = "user-account.jspx?call="+ submit_target_callee + "&action=submit";
    }
    
    document.getElementById(submit_button_id).onclick = submit_form;
</script>