<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>

<table id="general">
    <caption>${param.header}</caption>
    <tr>
        <th>Nazwa</th>
        <th>Pole</th>
    </tr>
	<tr>
        <td><form:label path="username"><tag:message code="addUser.login"/></form:label></td>
        <td><form:input path="username" /><font color="red"><form:errors path="username" /></font></td>
	</tr>
    <tr>
		<td><form:label path="password"><tag:message code="addUser.password"/></form:label></td> 
		<td><form:password path="password" /><font color="red"><form:errors path="password" /></font></td>
	</tr>
	<tr>
		<td><form:label path="repeatedPassword"><tag:message code="addUser.repeatPassword"/></form:label></td> 
		<td><form:password path="repeatedPassword" /><font color="red"><form:errors path="repeatedPassword" /></font></td>
	</tr>
    <c:choose>
        <c:when test="${param.roleList == 'true'}">
            <tr>
                <td><form:label path="roles"><tag:message code="addUser.role"/></form:label></td>
                <td>
                    <form:select path="roles" multiple="false" id="role">
                            <form:options items="${roles}"/>
                        </form:select>
                    <font color="red"><form:errors path="roles" /></font>
                </td>            
            </tr>
        </c:when>
        <c:otherwise />
    </c:choose>
</table>
