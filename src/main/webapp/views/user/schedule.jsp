<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<html>
    <head>
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/views/includes/css/style.css" />
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/views/includes/css/jquery-ui.css" />
        <script src="<%= request.getContextPath() %>/views/includes/js/jquery.min.js"></script>
        <script src="<%= request.getContextPath() %>/views/includes/js/jquery.ui.min.js"></script>
        <script type="text/javascript" src="<%= request.getContextPath() %>/views/includes/js/schedule.js" ></script>
        <title><tag:message code="app.userPage"/></title>
    </head>
    <body>
        <jsp:include page="../includes/navigation.jsp"/>
        
        <br>
        
        <div id="scheduleTabs">
            <ul>
                <li><a href="#tab-1">Create schedule</a></li>
                <c:if test="${uploadItem.schedule != null}">
                    <li><a href="#tab-2">Schedule details</a></li>
                </c:if>
            </ul>
            
            <div id="tab-1">
                <table id="schedule" style="width: 250px">
                    <caption>Create schedule</caption>
                    <tr>
                        <th colspan=2> Send XML file </th>
                    </tr>
                    <tr>
                        <form:form method="POST" action="/user/schedule" enctype="multipart/form-data">
                        <td class="center" style="width: 50%; padding: .1em;">
                                <input id="fileInput" type="file" name="fileParam" style="display: none"/>
                                <input type="text" id="fileText" style="display: none">
                                <input id="buttonOpen" type="button" value="Choose file" onclick="handleFile()">
                        </td>
                        <td class="center" style="padding: .1em;">
                                <input type="submit" id="buttonSub" value="    Send    " />
                        </td>
                        </form:form>
                    </tr>
                </table>
                <br>

                You can use a sample file: "schedule_conf.xml" <br>
                After running the algorithm, the generated schedule will appear in the "Schedule details" tab.

                <%-- TODO turned off --%>
                <%--
                <table id="schedule">
                    <tr>
                        <th colspan="2">Stwórz z wykorzystaniem formatki</th>
                    </tr>
                    <tr>
                        <td colspan="2" class="center" style="padding: .1em;">
                            <a href="#" onclick="addPromoter()" id="buttonPromoters">Dodaj prowadzącego</a>
                            <a href="#" onclick="addCourse()" id="buttonCourses">Dodaj przedmiot spotkania</a>
                            <a href="#" onclick="addRoom()" id="buttonRooms">Dodaj pokój</a>
                            <a href="#" onclick="addGroup()" id="buttonGroups">Dodaj grupę uczestników</a>
                            <a href="#" onclick="addEvent()" id="buttonEvents">Dodaj spotkanie</a>
                        </td>
                    </tr>
                    <tr>
                        <th>Nazwa pola</th>
                        <th>Pole</th>
                    </tr>
                    <tr>
                        <td class="center" style="width: 20%" valign="top"> <font color="#000080" size="2"> Prowadzący </font> </td>
                        <td style="padding: .1em;" valign="top"> <div id="promoters"></div> </td>
                    </tr>
                    <tr>
                        <td class="center" style="width: 20%" valign="top" > <font color="#000080" size="2"> Przedmioty </font> </td>
                        <td style="padding: .1em;" valign="top"> <div id="courses"></div> </td>
                    </tr>
                    <tr>
                        <td class="center" style="width: 20%" valign="top" > <font color="#000080" size="2"> Pokoje </font> </td>
                        <td style="padding: .1em;" valign="top"> <div id="rooms"></div> </td>
                    </tr>
                    <tr>
                        <td class="center" style="width: 20%" valign="top" > <font color="#000080" size="2"> Uczestnicy </font> </td>
                        <td style="padding: .1em;" valign="top"> <div id="groups"></div> </td>
                    </tr>
                    <tr>
                        <td class="center" style="width: 20%" valign="top" > <font color="#000080" size="2"> Spotkania </font> </td>
                        <td style="padding: .1em;" valign="top"> <div id="events"></div> </td>
                    </tr>
                </table>
                --%>

                <%-- 
                <font color="red"><div id="errorList"></div></font>
                --%>
                <%--
                <a href="<%= request.getContextPath() %>/saveSchedule" onClick="append_href(this)" id="buttonSave"><tag:message code="schedule.submit"/></a>
                --%>
            </div>
            
            <c:if test="${uploadItem.schedule != null}">
            <div id="tab-2">
                <jsp:include page="../includes/scheduleCalendar.jsp"/>
                <br>
                <a href="<%= request.getContextPath() %>/downloadSchedule?linkID=<c:out value="${uploadItem.link}" />" id="buttonDownload"><tag:message code="schedule.download"/></a>
            </div>
            </c:if>
            
        </div>
        
        <c:import url="/views/includes/footer.jsp" />
        
        <script type="text/javascript">
            function handleFile()
            {
                var form_file = document.getElementById('fileInput');
                var form_text = document.getElementById('fileText');
                form_file.click();
                form_text.value = form_file.value;
            }
        </script>
        
        <script>
            $(document).ready(function()
            {
                $(function()
                {
                    $("#scheduleTabs").tabs();
                    $("#buttonOpen").button();
                    $("#buttonSub").button();
                    $("#buttonDownload").button();
                    $("#buttonPromoters").button();
                    $("#buttonCourses").button();
                    $("#buttonRooms").button();
                    $("#buttonGroups").button();
                    $("#buttonEvents").button();
                    $("#buttonSave").button();
                });
            });
        </script>
        
    </body>
</html>