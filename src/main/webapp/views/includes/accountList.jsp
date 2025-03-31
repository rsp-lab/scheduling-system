<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<table id="list" style="width: 1000px">
    <caption>${param.header}</caption>
    <tr>
        <th> <tag:message code="account.lp"/> </th>
        <c:choose>
            <c:when test="${orderBy == 'registerDate' && orderType == 'DESC'}">
                <th> <a href="<%= request.getContextPath() %>/findAccount?order=registerDate:ASC"><tag:message code="account.registerDateASC"/></a> </th>
            </c:when>
            <c:otherwise>
                <th> <a href="<%= request.getContextPath() %>/findAccount?order=registerDate:DESC"><tag:message code="account.registerDateDESC"/></a> </th>
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${orderBy == 'username' && orderType == 'DESC'}">
                <th> <a href="<%= request.getContextPath() %>/findAccount?order=username:ASC"><tag:message code="account.usernameASC"/></a> </th>
            </c:when>
            <c:otherwise>
                <th> <a href="<%= request.getContextPath() %>/findAccount?order=username:DESC"><tag:message code="account.usernameDESC"/></a> </th>
            </c:otherwise>
        </c:choose>
        <th>Usuń</th>
    </tr>
    <c:forEach var="account" items="${accounts}" varStatus="counter" begin="0" step="1">
        <tr>
            <td class="center"> ${counter.count} </td>
            <td class="center"> ${account.registerDate} </td>
            <td class="center">
                <a href="<%= request.getContextPath() %>/detailsAccount?id=<c:out value="${account.id}" />"> ${account.username} </a>
            </td>
            <td class="center"> <a href="<%= request.getContextPath() %>/deleteAccount?id=<c:out value="${account.id}" />"><tag:message code="app.delete"/></a> </td>
        </tr>
    </c:forEach>
</table>

<table id="formTable" style="width: 1000px">
    <tr>
        <td style="width: 60%"> <tag:message code="account.findAccount"/> po nazwie: </td>
        <td style="width: 30%">
            <form:input path="name"/>
        </td>
        <td style="width: 10%" class="left">
            <input type="submit" name="submitType" id="buttonSub" value="Znajdź użytkownika"/>
        </td>
    </tr>
</table>
