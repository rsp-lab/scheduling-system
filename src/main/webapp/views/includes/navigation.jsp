<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<div class="top_background">
<div class="top">
    <table>
    <tr>
        <td class="left" style="width: 740px">
            <img src="<%= request.getContextPath() %>/views/includes/css/images/logo_sutip.png" alt="System uzgadniania terminów i preferencji">
        </td>
        <td>
            <div class="nav_form">
                
                    <!-- Kontrola logowania - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
                    <security:authorize access="hasRole('ROLE_ANONYMOUS')">
                            <form method="POST" action="<c:url value="/j_spring_security_check" />">
                                <table style="width: 260px">
                                    <tr>
                                        <td style="padding: .05em;"> <font color="#D9DDE0"> <tag:message code="login.username" /> </font> </td>
                                        <td style="padding: .05em;"> <input type="text" name="j_username" style="width:100%"/> </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: .05em;"> <font color="#D9DDE0"> <tag:message code="login.password" /> </font> </td>
                                        <td style="padding: .05em;"> <input type="password" name="j_password" style="width:100%"/> </td>
                                    </tr>
                                    <tr>
                                        <td colspan="2" class="center"  style="padding: .1em;">
                                            <input type="checkbox" id="rememberMe" name="_spring_security_remember_me" />
                                            <label for="rememberMe"><tag:message code="login.rememberme" /></label><input type="submit" value="Zaloguj" id="button" />
                                         </td>
                                    </tr>
                                    <c:choose>
                                        <c:when test="${not empty param.error}">
                                            <tr>
                                                <td colspan="2" class="center" style="padding: .1em;">
                                                    <font color="red">
                                                        Błąd!
                                                        <tag:message code="login.error" />
                                                    </font>
                                                </td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <tr>
                                                <td colspan="2" class="center" style="padding: .1em;">
                                                    <a id="register" href="<%= request.getContextPath() %>/registerAccount.html"><tag:message code="account.register"/></a>
                                                </td>
                                            </tr>
                                        </c:otherwise>
                                    </c:choose>
                                </table>
                            </form>
                    </security:authorize>
                    
                    <security:authorize access="hasRole('ROLE_ADMIN')">
                        <table style="width: 260px">
                        <tr>
                            <td style="padding: .05em;"> <font color="#D9DDE0"> Jesteś zalogowany jako: <security:authentication property="principal.username" /> </font> </td>
                        </tr>
                        <tr>
                            <td style="padding: .1em;" class="center"> <a id="buttonLog" href="<c:url value="/j_spring_security_logout"/>"><tag:message code="login.logout"/></a> </td>
                        </tr>
                        </table>
                    </security:authorize>
                    
                    <security:authorize access="hasRole('ROLE_USER')">
                        <table style="width: 260px">
                        <tr>
                            <td style="padding: .05em;"> <font color="#D9DDE0"> Jesteś zalogowany jako: <security:authentication property="principal.username" /> </font> </td>
                        </tr>
                        <tr>
                            <td style="padding: .1em;" class="center"> <a id="buttonLog" href="<c:url value="/j_spring_security_logout" />"><tag:message code="login.logout"/></a> </td>
                        </tr>
                        </table>
                    </security:authorize>
                
            </div>
        </td>
    </tr>
    </table>
</div>
</div>

<!-- Nawigacja - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

<div class="link_background">
<div class="link">
    
    <security:authorize access="hasRole('ROLE_ANONYMOUS')">
            <a href="<%= request.getContextPath() %>/login.jsp" onMouseOver='rollover1.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_home_hover.png"' onMouseOut='rollover1.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_home.png"'><img src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_home.png" name="rollover1"></a>
            <a href="<%= request.getContextPath() %>/createEvent.html" onMouseOver='rollover2.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_create_event_hover.png"' onMouseOut='rollover2.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_create_event.png"'><img id="img_create_event" src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_create_event.png" alt="Stwórz spotkanie" name="rollover2"></a>
            <a href="<%= request.getContextPath() %>/findEvent.html?order=creationDate:DESC&user=false" onMouseOver='rollover3.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_find_event_hover.png"' onMouseOut='rollover3.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_find_event.png"'><img id="img_find_event" src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_find_event.png" alt="Znajdź spotkanie" name="rollover3"></a>
    </security:authorize>
    
    <security:authorize access="hasRole('ROLE_ADMIN')">
            <a href="<%= request.getContextPath() %>/views/administrator/page.jsp" onMouseOver='rollover1.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_home_hover.png"' onMouseOut='rollover1.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_home.png"'> <img src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_home.png" name="rollover1"> </a>
            <a id="account" href="<%= request.getContextPath() %>/account/add.html" onMouseOver='rollover2.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_create_account_hover.png"' onMouseOut='rollover2.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_create_account.png"'> <img src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_create_account.png" name="rollover2"> </a>
            <a href="<%= request.getContextPath() %>/findEvent.html?order=creationDate:DESC&user=false" onMouseOver='rollover3.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_find_event_hover.png"' onMouseOut='rollover3.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_find_event.png"'> <img src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_find_event.png" name="rollover3"></a>
            <a href="<%= request.getContextPath() %>/findAccount.html?order=registerDate:DESC" onMouseOver='rollover4.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_find_account_hover.png"' onMouseOut='rollover4.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_find_account.png"'> <img src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_find_account.png" name="rollover4"></a>
    </security:authorize>
    
    <security:authorize access="hasRole('ROLE_USER')">
            <a href="<%= request.getContextPath() %>/views/user/page.jsp" onMouseOver='rollover1.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_home_hover.png"' onMouseOut='rollover1.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_home.png"'> <img src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_home.png" name="rollover1"> </a>
            <a href="<%= request.getContextPath() %>/user/schedule.html" onMouseOver='rollover2.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_create_schedule_hover.png"' onMouseOut='rollover2.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_create_schedule.png"'> <img src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_create_schedule.png" name="rollover2"></a>
            <a href="<%= request.getContextPath() %>/createEvent.html" onMouseOver='rollover3.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_create_event_hover.png"' onMouseOut='rollover3.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_create_event.png"'> <img src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_create_event.png" name="rollover3"> </a>
            <a href="<%= request.getContextPath() %>/findEvent.html?order=creationDate:DESC&user=true" onMouseOver='rollover4.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_my_events_hover.png"' onMouseOut='rollover4.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_my_events.png"'> <img src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_my_events.png" name="rollover4"> </a>
            <a href="<%= request.getContextPath() %>/findEvent.html?order=creationDate:DESC&user=false" onMouseOver='rollover5.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_find_event_hover.png"' onMouseOut='rollover5.src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_find_event.png"'> <img src="<%= request.getContextPath() %>/views/includes/css/images/nav_button_find_event.png" name="rollover5"> </a>
    </security:authorize>
    
    <img src="<%= request.getContextPath() %>/views/includes/css/images/nav_sep.png">
</div>
</div>

<script>
    $(document).ready(function()
        {
            $(function()
            {
                $("#rememberMe").button();
                $("#button").button();
                $("#buttonLog").button();
            });
        });
</script>