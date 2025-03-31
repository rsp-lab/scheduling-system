<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<html>
    <head>
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/views/includes/css/style.css" />
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/views/includes/css/jquery-ui.css" />
        <script src="<%= request.getContextPath() %>/views/includes/js/jquery.min.js"></script>
        <script src="<%= request.getContextPath() %>/views/includes/js/jquery.ui.min.js"></script>
        <title><tag:message code="app.addAccount"/></title>
    </head>
    <body>
        <jsp:include page="../includes/navigation.jsp"/>
        
    	<form:form modelAttribute="account">
            <c:import url="/views/includes/registerForm.jsp">
                <c:param name="roleList" value="true" />
                <c:param name="header" value="Dodaj użytkownika" />
            </c:import>
            <input type="submit" id="buttonSub"/>
    	</form:form>
        
        <script>
            $(document).ready(function()
            {
                $(function()
                {
                    $("#buttonSub").button();
                });
            });
        </script>
    </body>
</html>