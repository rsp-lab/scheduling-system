<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<form:hidden path="id" />
<form:hidden path="createdCounter" />
<form:hidden path="password" />
<form:hidden path="newPassword" />
<form:hidden path="repeatedPassword" />
<table id="general">
    <caption>${param.header}</caption>
    <tr>
        <th> Nazwa pola </th>
        <th> Pole </th>
    </tr>
    <tr>
        <td> <form:label path="username"><tag:message code="account.username"/></form:label> </td>
        <td> <form:input path="username" disabled="${param.disabled}"/><font color="red"><form:errors path="username" /></font> </td>
    </tr>
    <tr>
        <td> <form:label path="registerDate"><tag:message code="account.registerDate"/></form:label> </td> 
        <td> <form:input path="registerDate" disabled="${param.disabled}"/><font color="red"><form:errors path="registerDate" /></font> </td>
    </tr>
    <tr>
        <td> <form:label path="lastLogin"><tag:message code="account.lastLogin"/></form:label> </td> 
        <td> <form:input path="lastLogin" disabled="${param.disabled}"/><font color="red"><form:errors path="lastLogin" /></font> </td>
    </tr>
</table>
