<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/views/includes/css/style.css" />
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/views/includes/css/jquery-ui.css" />
        <script src="<%= request.getContextPath() %>/views/includes/js/jquery.min.js"></script>
        <script src="<%= request.getContextPath() %>/views/includes/js/jquery.ui.min.js"></script>
        <title><tag:message code="app.findAccount"/></title>
    </head>
    <body> 
        <c:import url="/views/includes/navigation.jsp"/>
        
        <%-- <h1>Znajdź użytkownika</h1> --%>
        
        <form:form modelAttribute="searchFilter">
            <c:import url="/views/includes/accountList.jsp" >
                <c:param name="header" value="Znajdź użytkownika" />
            </c:import>
        </form:form>
        
        <c:import url="/views/includes/footer.jsp" />
        
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