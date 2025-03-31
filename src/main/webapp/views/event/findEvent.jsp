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
        <title><tag:message code="app.findEvent"/></title>
    </head>
    <body> 
        <c:import url="/views/includes/navigation.jsp"/>
        
        <form:form modelAttribute="searchFilter">
            <c:choose>
                <c:when test="${param.user == 'true'}">
                    <c:import url="/views/includes/eventList.jsp" >
                        <c:param name="user" value="true" />
                        <c:param name="header" value="My events" />
                    </c:import>
                </c:when>
                <c:otherwise>
                    <c:import url="/views/includes/eventList.jsp" >
                        <c:param name="user" value="false" />
                        <c:param name="header" value="Find event" />
                    </c:import>
                </c:otherwise>
            </c:choose>
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