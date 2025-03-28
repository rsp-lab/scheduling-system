<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<html>
    <head>
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/views/includes/css/style.css" />
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/views/includes/css/jquery-ui.css" />
        <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyAYEfUIf2Kj5AnnczPVzRLYN-m4eVkn1Rw&sensor=false"></script>
        <script type="text/javascript" src="<%= request.getContextPath() %>/views/includes/js/googlemaps.js" ></script>
        <script src="<%= request.getContextPath() %>/views/includes/js/jquery.min.js"></script>
        <script src="<%= request.getContextPath() %>/views/includes/js/jquery.ui.min.js"></script>
        <script src="<%= request.getContextPath() %>/views/includes/js/jquery.ui.datepicker.pl.js"></script>
        <title><tag:message code="app.manageEvent"/></title>
    </head>
    <body onload="initializeManage()"> 
        <c:import url="/views/includes/navigation.jsp"/>
        
        <%-- <h1>Zarządzaj spotkaniem</h1> --%>
        
        <c:choose>
            <c:when test="${empty event.link}" />
            <c:otherwise>
                <br>
                <table id="submitTable">
                    <tr>
                        <td style="width: 30%"> Twój link do zarządzania spotkaniem: </td>
                        <td class="left">
                            <input type="text" value="<tag:message code="app.addr"/><%= request.getContextPath() %>/manageEvent.html?linkID=<c:out value="${event.link}" />" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 30%"> Twój link do spotkania: </td>
                        <td class="left">
                            <input type="text" value="<tag:message code="app.addr"/><%= request.getContextPath() %>/detailsEvent.html?id=<c:out value="${event.id}" />" />
                        </td>
                    </tr>
                </table>
                <br/>
            </c:otherwise>
        </c:choose>
        
        <div id="tabs">
            <ul>
                <li><a href="#tab-1">Szczegóły spotkania</a></li>
                <c:if test="${event.participantCounter != 0}">
                    <li><a href="#tab-2">Zapisani użytkownicy</a></li>
                </c:if>
                <c:if test="${eventType == 'EXTENDED' || eventType == 'PRIVATE'}">
                    <li><a href="#tab-3">Chat</a></li>
                </c:if>
                <c:if test="${interval != null}">
                    <li><a href="#tab-4">Najlepszy termin spotkania</a></li>
                </c:if>
            </ul>
            
            <div id="tab-1">
                <form:form commandName="event" OnSubmit="return eventSubmit();">
                    <c:import url="/views/includes/eventForm.jsp">
                        <c:param name="disabled" value="false" />
                        <c:param name="header" value="Zarządzaj spotkaniem" />
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
                            <input id="buttonSub" type="submit" value="Aktualizuj"/>
                            <a id="buttonDeleteEvent" href="<%= request.getContextPath() %>/deleteEvent.html?id=<c:out value="${event.id}" />"><tag:message code="event.deleteEvent"/></a>
                        </td>
                        </tr>
                    </table>
                </form:form>
                
            </div>
            
            <c:if test="${event.participantCounter != 0}">
            <div id="tab-2">
                    <c:import url="/views/includes/participantList.jsp"> 
                        <c:param name="header" value="Zapisani użytkownicy" />
                    </c:import>
                    <table id="submit" style="width: 480px">
                        <tr><td>Ilość zapisanych użytkowników: <c:out value="${event.participantCounter}" /></td></tr>
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
                    <caption>Najlepszy osiągnięty termin</caption>
                    <tr>
                        <th colspan="2" style="border: 1px solid black;">Od</th>
                        <th colspan="2" style="border: 1px solid black;">Do</th>
                    </tr>
                    <tr>
                        <th > Data </th>
                        <th > Czas </th>
                        <th > Data </th>
                        <th > Czas </th>
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
                    $("#buttonDeleteEvent").button();
                });
            });
        </script>
        
    </body>
</html>