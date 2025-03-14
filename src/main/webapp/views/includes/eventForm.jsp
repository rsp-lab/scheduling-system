<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<form:hidden path="id" />
<form:hidden path="link" />
<form:hidden path="latitude" id="lat" />
<form:hidden path="longitude" id="lon" />
<form:hidden path="participantCounter" />
<form:hidden path="creationDate" />
<security:authorize access="hasRole('ROLE_USER')">
    <form:hidden path="username" />
</security:authorize>

<table id="eventTable">
    <caption>${param.header}</caption>
    <tr>
        <th style="width: 200px">Nazwa pola</th>
        <th style="width: 280px">Pole</th>
        <th style="width: 480px">Mapa spotkania</th>
    </tr>
    <tr>
        <td> <form:label path="name"><tag:message code="event.name"/></form:label> </td>
        <td> <form:input path="name" disabled="${param.disabled}" id="name" /> </td>
        <security:authorize access="hasRole('ROLE_ANONYMOUS')"> <td rowspan="10"> <div id="map_canvas">map</div> </td> </security:authorize>
        <security:authorize access="hasRole('ROLE_USER')"> <td rowspan="10"> <div id="map_canvas">map</div> </td> </security:authorize>
        <security:authorize access="hasRole('ROLE_ADMIN')"> <td rowspan="11"> <div id="map_canvas">map</div> </td> </security:authorize>
    </tr>
    <tr>
        <td> <form:label path="description"><tag:message code="event.description"/></form:label> </td> 
        <td> <form:textarea path="description" disabled="${param.disabled}"/> </td>
    </tr>
    <tr>
        <td> <form:label path="location"><tag:message code="event.location"/></form:label> </td> 
        <td> <form:input path="location" disabled="${param.disabled}"/> </td>
    </tr>
    <tr>
        <td> <form:label path="duration"><tag:message code="event.duration"/></form:label> </td> 
        <td> <form:input path="duration" disabled="${param.disabled}"/> </td>
    </tr>
    <tr>
        <td> <form:label path="startDate"><tag:message code="event.startDate"/></form:label> </td> 
        <td> <form:input path="startDate" disabled="${param.disabled}" id="datepicker"/> </td>
    </tr>
    <tr>
        <td> <form:label path="startTimestamp"><tag:message code="event.startTimestamp"/></form:label> </td> 
        <td> <form:input path="startTimestamp" disabled="${param.disabled}"/> </td>
    </tr>
    <tr>
        <td> <form:label path="endDate"><tag:message code="event.endDate"/></form:label> </td> 
        <td> <form:input path="endDate" disabled="${param.disabled}" id="datepicker2"/> </td>
    </tr>
    <tr>
        <td> <form:label path="endTimestamp"><tag:message code="event.endTimestamp"/></form:label> </td> 
        <td> <form:input path="endTimestamp" disabled="${param.disabled}"/> </td>
    </tr>
    <security:authorize access="hasAnyRole('ROLE_ANONYMOUS', 'ROLE_ADMIN')">
        <tr>
            <td> <form:label path="username"><tag:message code="event.username"/></form:label> </td> 
            <td> <form:input path="username" disabled="${param.disabled}"/> </td>
        </tr>
    </security:authorize>
    <tr>
        <td> <form:label path="contact"><tag:message code="event.contact"/></form:label> </td> 
        <td> <form:input path="contact" disabled="${param.disabled}"/> </td>
    </tr>
    <security:authorize access="hasRole('ROLE_ADMIN')">
        <tr>
            <td> <form:label path="account.username"><tag:message code="account.username"/></form:label> </td> 
            <td> <form:input path="account.username" disabled="${param.disabled}"/> </td>
        </tr>
    </security:authorize>
    <security:authorize access="hasRole('ROLE_USER')">
        <tr>
            <td> <form:label path="type"><tag:message code="event.type"/></form:label> </td> 
            <td>
                <form:select path="type" multiple="false" id="create" disabled="${param.disabled}">
                    <form:options items="${types}"/>
                </form:select>
            </td>         
        </tr>
    </security:authorize>
</table>
