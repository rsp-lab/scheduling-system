<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>

<html>
    <head>
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/views/includes/css/style.css" />
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/views/includes/css/jquery-ui.css" />
<%--        <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyAYEfUIf2Kj5AnnczPVzRLYN-m4eVkn1Rw&sensor=false"></script>--%>
<%--        <script type="text/javascript" src="<%= request.getContextPath() %>/views/includes/js/googlemaps.js" ></script>--%>
        <script src="<%= request.getContextPath() %>/views/includes/js/jquery.min.js"></script>
        <script src="<%= request.getContextPath() %>/views/includes/js/jquery.ui.min.js"></script>
        <script src="<%= request.getContextPath() %>/views/includes/js/jquery.ui.datepicker.pl.js"></script>
        <title><tag:message code="app.createEvent"/></title>
        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
        <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
        <style>
            #map { height: 100%; width: 100%; }
        </style>
    </head>
    
    <body onload="initializeCreate()"> 
        
        <c:import url="/views/includes/navigation.jsp"/>
        
    	<form:form modelAttribute="event" OnSubmit="return eventSubmit();">
            <c:import url="/views/includes/eventForm.jsp">
                <c:param name="disabled" value="false" />
                <c:param name="header" value="Create event" />
            </c:import>
            <table id="submitTable">
                <tr>
                    <td style="width: 80%; text-align: justify;" class="left">
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
                    <td style="width: 10%" class="center"><input type="submit" id="buttonSub" value="Create event"/><td>
                </tr>
            </table>
    	</form:form>
        
        <c:import url="/views/includes/footer.jsp" />
        
        <script>
            $(document).ready(function()
            {
            	$(function()
            	{
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