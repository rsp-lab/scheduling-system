<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<html>
    <head>
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/views/includes/css/style.css" />
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/views/includes/css/jquery-ui.css" />
        <script src="<%= request.getContextPath() %>/views/includes/js/jquery.min.js"></script>
        <script src="<%= request.getContextPath() %>/views/includes/js/jquery.ui.min.js"></script>
        <title><tag:message code="app.createEvent"/></title>
    </head>
    <body> 
        <c:import url="/views/includes/navigation.jsp"/>
        
        <%-- <h1>Dane użytkownika:</h1> --%>
               
        <br>
               
        <div id="tabs">
            <ul>
                <li><a href="#tab-1">Dane użytkownika</a></li>
                <security:authorize access="hasRole('ROLE_ADMIN')">
                <c:if test="${account.createdCounter != 0}">
                    <li><a href="#tab-2">Spotkania użytkownika</a></li>
                </c:if>
                </security:authorize>
            </ul>
            <div id="tab-1">
            
            	<form:form modelAttribute="account">
                    <c:import url="/views/includes/accountForm.jsp">
                        <c:param name="disabled" value="true" />
                        <c:param name="header" value="Dane użytkownika" />
                    </c:import>
                </form:form>
                
                <a id="buttonDelete" href="<%= request.getContextPath() %>/deleteAccount?id=<c:out value="${account.id}" />"><tag:message code="app.delete"/></a>
            
            </div>
            
            <security:authorize access="hasRole('ROLE_ADMIN')">
            <c:if test="${account.createdCounter != 0}">
            <div id="tab-2">
            
                <form:form modelAttribute="searchFilter">
                        <c:import url="/views/includes/eventList.jsp" >
                            <c:param name="user" value="false" />
                            <c:param name="header" value="Spotkania użytkownika" />
                        </c:import>
                        Ilość utworzonych spotkań: <font color="#000077"> <c:out value="${account.createdCounter}" /> </font>
                    <input name="username" type="hidden" value="${account.username}" />
                </form:form>
            
            </div>
            </c:if>
            </security:authorize>
            
        </div>
               
                
        <c:import url="/views/includes/footer.jsp" />

        <script>
            $(document).ready(function()
            {
                $(function()
                {
                	$("#tabs").tabs();
                    $("#buttonDelete").button();
                    $("#buttonSub").button();
                });
            });
        </script>
    </body>
</html>