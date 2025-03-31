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
        <script src="<%= request.getContextPath() %>/views/includes/js/jquery.ui.datepicker.pl.js"></script>
        <script type="text/javascript" src="<%= request.getContextPath() %>/views/includes/js/popup.js" ></script>
        <title><tag:message code="app.manageEvent"/></title>
        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
        <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
        <style>
            #map { height: 100%; width: 100%; }
        </style>
    </head>
    <body onload="initializeManage()"> 
        <c:import url="/views/includes/navigation.jsp"/>
        
        <c:choose>
            <c:when test="${empty event.link}" />
            <c:otherwise>
                <br>
                <table id="submitTable">
                    <tr>
                        <td style="width: 30%"> Your link to manage the event: </td>
                        <td class="left">
                            <input type="text" value="<tag:message code="app.addr"/><%= request.getContextPath() %>/manageEvent?linkID=<c:out value="${event.link}" />" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 30%"> Your link for the event: </td>
                        <td class="left">
                            <input type="text" value="<tag:message code="app.addr"/><%= request.getContextPath() %>/detailsEvent?id=<c:out value="${event.id}" />" />
                        </td>
                    </tr>
                </table>
                <br/>
            </c:otherwise>
        </c:choose>
        
        <div id="tabs">
            <ul>
                <li><a href="#tab-1">Event details</a></li>
                <c:if test="${event.participantCounter != 0}">
                    <li><a href="#tab-2">Participants</a></li>
                </c:if>
                <c:if test="${eventType == 'EXTENDED' || eventType == 'PRIVATE'}">
                    <li><a href="#tab-3">Chat</a></li>
                </c:if>
                <c:if test="${interval != null}">
                    <li><a href="#tab-4">The best event date and time</a></li>
                </c:if>
            </ul>
            
            <div id="tab-1">
                <form:form modelAttribute="event" OnSubmit="return eventSubmit();">
                    <c:import url="/views/includes/eventForm.jsp">
                        <c:param name="disabled" value="false" />
                        <c:param name="header" value="Manage the event" />
                    </c:import>
                    <table id="submitTable">
                        <tr>
                        <td style="width: 75%; text-align: justify;" class="left">
                            <font color="#770000">
                            <form:errors path="name" />
                            <form:errors path="description" />
                            <form:errors path="location" />
                            <form:errors path="duration" />
                            <form:errors path="startDate" />
                            <form:errors path="startTimestamp" />
                            <form:errors path="endDate" />
                            <form:errors path="endTimestamp" />
                            <form:errors path="username" />
                            <form:errors path="contact" />
                            <form:errors path="account.username" />
                            </font>
                        <td>
                        <td>
                            <input id="buttonSub" type="submit" value="Update"/>
                            <a id="buttonDeleteEvent" href="<%= request.getContextPath() %>/deleteEvent?id=<c:out value="${event.id}" />"><tag:message code="event.deleteEvent"/></a>
                        </td>
                        </tr>
                    </table>
                </form:form>
                
            </div>
            
            <c:if test="${event.participantCounter != 0}">
            <div id="tab-2">
                    <c:import url="/views/includes/participantList.jsp"> 
                        <c:param name="header" value="Participants" />
                    </c:import>
                    <table id="submit" style="width: 480px">
                        <tr><td>Number of participants: <c:out value="${event.participantCounter}" /></td></tr>
                    </table>
            </div>
            </c:if>
            
            <c:if test="${eventType == 'EXTENDED' || eventType == 'PRIVATE'}">
            <div id="tab-3">
                <c:import url="/views/includes/chatForm.jsp" >
                    <c:param name="sourcePage" value="manage" />
                </c:import>
            </div>
            </c:if>
            
            <c:if test="${interval != null}">
            <div id="tab-4">
                <table id="general">
                    <caption>The best event date and time</caption>
                    <tr>
                        <th colspan="2" style="border: 1px solid black;">From</th>
                        <th colspan="2" style="border: 1px solid black;">Until</th>
                    </tr>
                    <tr>
                        <th > Date </th>
                        <th > Time </th>
                        <th > Date </th>
                        <th > Time </th>
                    </tr>
                    <tr>
                        <td class="center"> ${interval.startDate} </td>
                        <td class="center"> ${interval.startTimestamp} </td>
                        <td class="center"> ${interval.endDate} </td>
                        <td class="center"> ${interval.endTimestamp} </td>
                    </tr>
                </table>
            </div>
            </c:if>
        </div>
        
        <c:import url="/views/includes/footer.jsp" />
   
        <script>
            $(document).ready(function()
            {
                $(function()
                {
                	$("#tabs").tabs();
                	
            		$("#datepicker").datepicker();
            		$("#datepicker").datepicker( $.datepicker.regional["pl"] );
                	$("#datepicker").datepicker("option", "dateFormat", "yy-mm-dd");
                	$("#datepicker").datepicker("option", "showButtonPanel", true );
                	$("#datepicker").datepicker("option", "changeMonth", true );
                	$("#datepicker").datepicker("option", "changeYear", true );
                	$("#datepicker").datepicker("option", "showOtherMonths", true );
                	$("#datepicker").datepicker("option", "selectOtherMonths", true );
                	
            		$("#datepicker2").datepicker();
            		$("#datepicker2").datepicker( $.datepicker.regional["pl"] );
                	$("#datepicker2").datepicker("option", "dateFormat", "yy-mm-dd");
                	$("#datepicker2").datepicker("option", "showButtonPanel", true );
                	$("#datepicker2").datepicker("option", "changeMonth", true );
                	$("#datepicker2").datepicker("option", "changeYear", true );
                	$("#datepicker2").datepicker("option", "showOtherMonths", true );
                	$("#datepicker2").datepicker("option", "selectOtherMonths", true );
                	
                    $("#buttonSub").button();
                    $("#buttonSend").button();
                    $("#buttonDeleteEvent").button();
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

            // Obsługa kliknięcia na mapie
            map.on('click', function (e) {
                var lat = e.latlng.lat;
                var lng = e.latlng.lng;

                // Usuń poprzedni marker, jeśli istnieje
                if (marker) {
                    map.removeLayer(marker);
                }

                // Dodaj nowy marker na kliknięte miejsce
                marker = L.marker([lat, lng]).addTo(map)

                // Wpisanie wartości do pól formularza
                document.getElementById('latitude').value = lat;
                document.getElementById('longitude').value = lng;
            });
        </script>

    </body>
</html>