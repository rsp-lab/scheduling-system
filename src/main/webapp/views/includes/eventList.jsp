<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<table id="list">
    <caption>${param.header}</caption>
    <tr>
        <c:choose>
            <c:when test="${orderBy == 'name' && orderType == 'DESC'}">
                <th> <a href="<%= request.getContextPath() %>/findEvent?order=name:ASC&user=${user}"><tag:message code="event.nameASC"/></a> </th>
            </c:when>
            <c:otherwise>
                <th> <a href="<%= request.getContextPath() %>/findEvent?order=name:DESC&user=${user}"><tag:message code="event.nameDESC"/></a> </th>
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${orderBy == 'location' && orderType == 'DESC'}">
                <th> <a href="<%= request.getContextPath() %>/findEvent?order=location:ASC&user=${user}"><tag:message code="event.locationASC"/></a> </th>
            </c:when>
            <c:otherwise>
                <th> <a href="<%= request.getContextPath() %>/findEvent?order=location:DESC&user=${user}"><tag:message code="event.locationDESC"/></a> </th>
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${orderBy == 'creationDate' && orderType == 'DESC'}">
                <th> <a href="<%= request.getContextPath() %>/findEvent?order=creationDate:ASC&user=${user}"><tag:message code="event.creationDateASC"/></a> </th>
            </c:when>
            <c:otherwise>
                <th> <a href="<%= request.getContextPath() %>/findEvent?order=creationDate:DESC&user=${user}"><tag:message code="event.creationDateDESC"/></a> </th>
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${orderBy == 'username' && orderType == 'DESC'}">
                <th> <a href="<%= request.getContextPath() %>/findEvent?order=username:ASC&user=${user}"><tag:message code="event.usernameASC"/></a> </th>
            </c:when>
            <c:otherwise>
                <th> <a href="<%= request.getContextPath() %>/findEvent?order=username:DESC&user=${user}"><tag:message code="event.usernameDESC"/></a> </th>
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${orderBy == 'contact' && orderType == 'DESC'}">
                <th> <a href="<%= request.getContextPath() %>/findEvent?order=contact:ASC&user=${user}"><tag:message code="event.contactASC"/></a> </th>
            </c:when>
            <c:otherwise>
                <th> <a href="<%= request.getContextPath() %>/findEvent?order=contact:DESC&user=${user}"><tag:message code="event.contactDESC"/></a> </th>
            </c:otherwise>
        </c:choose>
        <security:authorize access="hasRole('ROLE_ADMIN')">
        	<th> <tag:message code="event.deleteEvent"/> </th>
        </security:authorize>
    </tr>
    <c:forEach var="event" items="${events}" varStatus="counter" begin="0" step="1">
        <tr>
            <c:if test="${param.user == true}">
                <td class="left">
                    <a href="<%= request.getContextPath() %>/manageEvent?linkID=<c:out value="${event.link}" />"> ${event.name} </a>
                </td>
            </c:if>
            <c:if test="${param.user == false}">
                <td class="left">
                    <a href="<%= request.getContextPath() %>/detailsEvent?id=<c:out value="${event.id}" />"> ${event.name} </a>
                </td>
            </c:if>
            <td class="center"> ${event.location} </td>
            <td class="left"> ${event.creationDate} </td>
            <td class="center"> ${event.username} </td>
            <td class="center"> ${event.contact} </td>
            <security:authorize access="hasRole('ROLE_ADMIN')">
                <td class="center"> <a href="<%= request.getContextPath() %>/deleteEvent?id=<c:out value="${event.id}" />"><tag:message code="app.delete"/></a> </td>
            </security:authorize>
        </tr>
    </c:forEach>
</table>

<table id="formTable">
    <tr>
        <td style="width: 60%"><tag:message code="event.findEvent"/> using event name:</td>
        <td style="width: 30%">
            <form:input path="name"/>
        </td>
        <td style="width: 10%" class="left">
            <input type="submit" name="submitType" id="buttonSub" value="Find event"/>
        </td>
    </tr>
</table>

<form:hidden path="user" />
