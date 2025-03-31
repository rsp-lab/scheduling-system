<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>  

<%@ page import="java.util.*" %>
<%@ page import="pl.radek.ss.lib.schedule.*" %>

<%
    List<Group> groupMap = (List<Group>) request.getAttribute("mapGroup");
    Map<Integer, List<EventSchedule>> classMap = (Map<Integer, List<EventSchedule>>) request.getAttribute("mapEvent");
	Map<Integer, List<Room>> roomMap = (Map<Integer, List<Room>>) request.getAttribute("mapRoom");
	Map<Integer, List<Integer>> timeIdMap = (Map<Integer, List<Integer>>) request.getAttribute("mapTimeId");
	Map<Integer, List<Integer>> timeDayMap = (Map<Integer, List<Integer>>) request.getAttribute("mapTimeDay");
    
	List<Integer> pos = new ArrayList<>();
    boolean flag = true;
    
    int hours = Integer.parseInt("" + request.getAttribute("hours"));
    int min_hour = Integer.parseInt("" + request.getAttribute("min_hour"));
%>

<%
    for (Iterator<Group> iterator = groupMap.iterator(); iterator.hasNext();)
    {
    	Group group = iterator.next();
        pos.clear();
        
        %>
            <br>
            <table id="schedule" style="width: 1000px;">
                <caption><% out.println("(" + group.getId() + ") " + group.getName()); %></caption>
                <tr>
                    <th style="width: 5%"></th>
                    <th style="width: 19%">Monday</th>
                    <th style="width: 19%">Tuesday</th>
                    <th style="width: 19%">Wednesday</th>
                    <th style="width: 19%">Thursday</th>
                    <th style="width: 19%">Friday</th>
                    <th style="width: 19%">Other</th>
                </tr>
                
                <%
                    for(int i = 0; i < hours; i++)
                    {
                        out.println("<tr style=\"height: 27px;\">");
                        out.println("<td class=\"center\"><b>" + (i+min_hour) + "-" + (i+min_hour+1) + "</b></td>");
                        
                        for(int day = 0; day < 5; day++)
                        {
                            int count = 0;
                        	for (Iterator<Integer> it = timeDayMap.get(group.getId()).iterator(); it.hasNext();)
                        	{
                        		Integer listDay = (Integer) it.next();
                                if(listDay == day) 
                                {
                                	int timeId = timeIdMap.get(group.getId()).get(count);
                                    if(timeId == i) 
                                    {
                                    	EventSchedule cc = classMap.get(group.getId()).get(count);
                                        Room room = roomMap.get(group.getId()).get(count);
                                        int duration = cc.getDuration();
                                           
                                        for (Iterator<Integer> itPOS = pos.iterator(); itPOS.hasNext();)
                                        {
                                            Integer positionDay = (Integer) itPOS.next();
                                            Integer positionTime = (Integer) itPOS.next();
                                            if(positionDay == day && positionTime == i)
                                            {
                                                flag = false;
                                                break;
                                            }
                                        }
                                        
                                        if(flag)
                                        {
                                            if(duration > 1)
                                            {
                                                for(int k = 1; k < duration; k++)
                                                {
                                                	pos.add(day);
                                                    pos.add(i+k);
                                                }
                                                out.println("<td rowspan=" + duration + " class=\"center\" style=\"border: 1px solid black;\">");  
                                                out.println("<b>");
                                                out.println(cc.getCourse().getName());
                                                out.println("</b>");
                                            	out.println("<br>");
                                            	out.println(room.getName());
                                            	out.println("<br>");
                                            	out.println(cc.getPromoter().getName());
                                                out.println("</td>");
                                            }
                                            if(duration == 1)
                                            {
                                        	    out.println("<td class=\"center\" style=\"border: 1px solid black;\">");
                                            	out.println(cc.getCourse().getName());
                                            	out.println("<br>");
                                            	out.println(room.getName());
                                            	out.println("<br>");
                                            	out.println(cc.getPromoter().getName());
                                                out.println("</td>");
                                            }
                                        }
                                        
                                        flag = true;
                                        break;
                                    }
                                }
                                if(listDay > day || count == timeDayMap.get(group.getId()).size()-1)
                                {
                                	out.println("<td class=\"center\"></td>");
                                    break;
                                }
                                count++;
                            }
                            
                        }
                        out.println("</tr>");
                    }
                %>
                
                
            </table>
        <%
    }
%>

<%-- 
GroupList:
<table border="1">
    <tr><th>Key</th></tr>
    <c:forEach var="group" items="${mapGroup}" varStatus="status">
    <tr>      
      <td>${group.id} ${group.name}</td>
    </tr>
    </c:forEach>
</table>

Classes:
<table border="1">
    <tr><th>Key</th><th>value</th></tr>
    <c:forEach var="entry" items="${mapClass}" varStatus="status">
    <tr>      
      <td>${entry.key}</td>
      <td>${entry.value}</td>
    </tr>
    </c:forEach>
</table>

Rooms:
<table border="1">
    <tr><th>Key</th><th>value</th></tr>
    <c:forEach var="entry" items="${mapRoom}" varStatus="status">
    <tr>      
      <td>${entry.key}</td>
      <td>${entry.value}</td>
    </tr>
    </c:forEach>
</table>

TimeID:
<table border="1">
    <tr><th>Key</th><th>value</th></tr>
    <c:forEach var="entry" items="${mapTimeId}" varStatus="status">
    <tr>      
      <td>${entry.key}</td>
      <td>${entry.value}</td>
    </tr>
    </c:forEach>
</table>

TimeDay:
<table border="1">
    <tr><th>Key</th><th>value</th></tr>
    <c:forEach var="entry" items="${mapTimeDay}" varStatus="status">
    <tr>      
      <td>${entry.key}</td>
      <td>${entry.value}</td>
    </tr>
    </c:forEach>
</table> 
--%>