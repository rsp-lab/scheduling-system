<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<div id="layer">
    <br><br><br><br><br>
    <div id="popupLayer">
    
        <c:if test="${param.extended == 'true'}">
            <a id="buttonAdd" href="#" onclick="add()">Add your convenient participation time</a>
            <a id="buttonDelete" href="#" onclick="deleteAll('intervalList')">Delete all</a>
            <br>
            <table id="general">
                <tr><th>Intervals</th></tr>
                <tr><td> <div id="intervalList"></div> </td></tr>
                <tr><td> <div id="errorList"></div> </td></tr>
            </table>
        </c:if>
        
        <table id="general">
            <tr>
                <th>Name</th>
                <th>Element</th>
            </tr>
            <tr>
                <td style="padding: .1em;"> Username: </td>
                <security:authorize access="hasRole('ROLE_ANONYMOUS')">
                    <td style="padding: .1em;"> <input name="usernamePopup" type="text"/> </td>
                </security:authorize>
                <security:authorize access="hasRole('ROLE_USER')">
                    <td style="padding: .1em;"> <input name="usernamePopup" type="text" value="<security:authentication property="principal.username" />"/> </td>
                </security:authorize>
            </tr>
            <tr>
                <td style="padding: .1em;"> Contact/Email: </td>
                <td style="padding: .1em;"> <input name="contactPopup" type="text"/> </td>
            </tr>
        </table>
        
        <input id="intervals" name="intervals" type="hidden"/>
        
        <input id="buttonSendInterval" type="submit" value="Send">
        <a id="buttonHide" href="#" onclick="hideIt();"><tag:message code="app.cancel"/></a>
    
    </div>
</div>


