<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>

<table id="general">
    <caption>${param.header}</caption>
    <tr>
        <th> <tag:message code="account.lp"/> </th>
        <th> <tag:message code="account.username"/> </th>
        <th> <tag:message code="account.contact"/> </th>
    </tr>
    <c:forEach var="par" items="${participants}" varStatus="counter" begin="0" step="1">
        <tr>
            <td class="center"> ${counter.count} </td>
            <td class="center"> ${par.username} </td>
            <td class="center"> ${par.contact} </td>
        </tr>
    </c:forEach>
</table>
