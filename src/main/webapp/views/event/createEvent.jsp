<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>

<html>
    <head>
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/views/includes/css/style.css" />
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/views/includes/css/jquery-ui.css" />
        <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyAYEfUIf2Kj5AnnczPVzRLYN-m4eVkn1Rw&sensor=false"></script>
        <script type="text/javascript" src="<%= request.getContextPath() %>/views/includes/js/googlemaps.js" ></script>
        <script src="<%= request.getContextPath() %>/views/includes/js/jquery.min.js"></script>
        <script src="<%= request.getContextPath() %>/views/includes/js/jquery.ui.min.js"></script>
        <script src="<%= request.getContextPath() %>/views/includes/js/jquery.ui.datepicker.pl.js"></script>
        <title><tag:message code="app.createEvent"/></title>
    </head>
    
    <body onload="initializeCreate()"> 
        
        <c:import url="/views/includes/navigation.jsp"/>
        
        <%-- <h1>Utwórz spotkanie:</h1> --%>
        
    	<form:form commandName="event" OnSubmit="return eventSubmit();">
            <c:import url="/views/includes/eventForm.jsp">
                <c:param name="disabled" value="false" />
                <c:param name="header" value="Utwórz spotkanie" />
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
                    <td style="width: 10%" class="center"><input type="submit" id="buttonSub" value="Stwórz spotkanie"/><td>
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

    </body>
    
</html>