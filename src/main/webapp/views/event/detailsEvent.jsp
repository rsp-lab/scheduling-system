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
        <script type="text/javascript" src="<%= request.getContextPath() %>/views/includes/js/popup.js" ></script>
        <title><tag:message code="app.createEvent"/></title>
        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
        <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
        <style>
            #map { height: 100%; width: 100%; }
        </style>
    </head>
    <body onload="initializeDetails()"> 
        <c:import url="/views/includes/navigation.jsp"/>

        <%-- <h1>Szczegóły spotkania:</h1> --%>

        <br>
        
        <div id="tabs">
            <ul>
                <li><a href="#tab-1">Event details</a></li>
                <security:authorize access="hasAnyRole('ROLE_ANONYMOUS', 'ROLE_USER')">
                <c:if test="${eventType == 'EXTENDED' || eventType == 'PRIVATE'}">
                    <li><a href="#tab-2">Chat</a></li>
                </c:if>
                </security:authorize>
            </ul>
            
            <div id="tab-1">
                <form:form modelAttribute="event" OnSubmit="return validate();">
                    <c:import url="/views/includes/eventForm.jsp">
                        <c:param name="disabled" value="true" />
                        <c:param name="header" value="Event details" />
                    </c:import>
                
                    <security:authorize access="hasAnyRole('ROLE_ANONYMOUS', 'ROLE_USER')">
                        <table id="submitTable">
                            <tr><td><a id="buttonShow" href="#" onclick="showIt();"><tag:message code="event.registerEvent"/></a></td></tr>
                        </table>
                    </security:authorize>
                
                    <security:authorize access="hasRole('ROLE_ANONYMOUS')">
                        <c:if test="${eventType == 'PUBLIC'}">
                            <c:import url="/views/includes/intervalForm.jsp">
                                <c:param name="extended" value="false" />
                            </c:import>
                        </c:if>
                        <c:if test="${eventType == 'EXTENDED' || eventType == 'PRIVATE'}">
                            <c:import url="/views/includes/intervalForm.jsp">
                                <c:param name="extended" value="true" />
                            </c:import>
                        </c:if>
                    </security:authorize>
                
                    <security:authorize access="hasRole('ROLE_USER')">
                        <c:if test="${eventType == 'PUBLIC'}">
                            <c:import url="/views/includes/intervalForm.jsp">
                                <c:param name="extended" value="false" />
                            </c:import>
                        </c:if>
                        <c:if test="${eventType == 'EXTENDED' || eventType == 'PRIVATE'}">
                            <c:import url="/views/includes/intervalForm.jsp">
                                <c:param name="extended" value="true" />
                            </c:import>
                        </c:if>
                    </security:authorize>
                </form:form>
        
                <security:authorize access="hasRole('ROLE_ADMIN')">
                    <a id="buttonDeleteEvent" href="<%= request.getContextPath() %>/deleteEvent?id=<c:out value="${event.id}" />"><tag:message code="app.delete"/></a>
                </security:authorize>
            </div>

            <security:authorize access="hasAnyRole('ROLE_ANONYMOUS', 'ROLE_USER')">
            <c:if test="${eventType == 'EXTENDED' || eventType == 'PRIVATE'}">
                <div id="tab-2">
                     <c:import url="/views/includes/chatForm.jsp" >
                        <c:param name="sourcePage" value="details" />
                     </c:import>
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
                    $("#buttonShow").button();
                    $("#buttonAdd").button();
                    $("#buttonDelete").button();
                    $("#buttonHide").button();
                    $("#buttonDeleteEvent").button();
                    $("#buttonSend").button();
                    $("#buttonSendInterval").button();
                });
            });
        </script>

        <script>
            var latitude = <%= request.getAttribute("latitude") %>;
            var longitude = <%= request.getAttribute("longitude") %>;

            var map = L.map('map').setView([latitude, longitude], 10);

            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            }).addTo(map);

            var marker = L.marker([latitude, longitude]).addTo(map);
        </script>
    </body>
</html>