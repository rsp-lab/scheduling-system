<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>


<div class="chatTable">
    <table id="general" style="width: 960px">
        <tr>
            <th colspan="2"> Chat </th>
        </tr>
        <tr>
            <td class="left"> </td>
        </tr>
    </table>
</div>


<%-- 
<table id="general" style="width: 960px">
    <tr>
        <th colspan="2"> Chat </th>
    </tr>
    <c:forEach var="message" items="${messages}" begin="0" step="1">
    <tr>
        <td style="width: 30%"> ${message.date} &nbsp; <font color="#000077">${message.author}</font>&nbsp;:</td>
        <td class="left"> &nbsp;&nbsp;&nbsp; ${message.text} </td>
    </tr>
    </c:forEach>
</table>
--%>

<table id="submitTable">
    <tr>
        <security:authorize access="hasRole('ROLE_ANONYMOUS')">
            <td class="right"> Username: </td>
            <td> <input id="messageUsername" type="text" /> </td>
        </security:authorize>
        <security:authorize access="hasRole('ROLE_USER')">
            <td class="right"> Username: </td>
            <td>  <input id="messageUsername" disabled="disabled" type="text" value="<security:authentication property="principal.username" />"/> </td>
        </security:authorize>
        <td class="right"> Message text: </td>
        <td> <input id="messageText" type="text" /> </td>
        <td class="center" style="width: 10%"> <button id="buttonSend" onclick="saveAjax()" title="Button">Send message</button> </td>
    </tr>
</table>

<input id="messagePage" type="hidden" value="${param.sourcePage}"/>

<%-- <a id="buttonSend" href="<%= request.getContextPath() %>/saveMessage" onClick="append_href(this)">Wyślij</a> --%>
<%-- <button id="demo" onclick="saveAjax()" title="Button">Get the time!</button> --%>
<script>
    function append_href(theLink)
    {
       href = theLink.href;
       var messageUsername = document.getElementById('messageUsername').value;
       var messageText = document.getElementById('messageText').value;
       var id = ${event.id};
       var page = document.getElementById('messagePage').value;
       var sresults = '?messageUsername=' + messageUsername + '&messageText=' + messageText + '&id=' + id + '&page=' + page;
       theLink.href = href + sresults;
    }
	
    function getAjax()
    {
        $.ajax({
        	contentType: "application/x-www-form-urlencoded;charset=UTF-8",
            url: 'getMessageQuery',
            data: ({
            	id : ${event.id}
            }),
            success: function(data)
            {
            	$(".chatTable td:eq(0)").empty();
            	var $array = data.split('/#/');
        		console.log("LOG: " + $array);
            	for($i = $array.length-1; $i >= 0; $i--)
        		{
        			var $message = $array[$i].split('/separator/');
        			var $date = $message[0];
        			var $author = $message[1];
        			var $text = $message[2];

                    $(".chatTable td:eq(0)").append($date).append(" ").append($author).append(": ").append($text).append("<br>");
        		}
            }
        });
    }
    
    function saveAjax()
    {
        $.ajax({
        	contentType: "application/x-www-form-urlencoded;charset=UTF-8",
            url: 'saveMessageQuery',
            data: ({
            	messageUsername : $("#messageUsername").val(),
            	messageText : $("#messageText").val(),
            	id : ${event.id}
            }),
            success: function(data)
            {
            	$(".chatTable td:eq(0)").empty();
            	$("#messageText").empty();
            	var $array = data.split('/#/');
            	for($i = $array.length-1; $i >= 0; $i--)
        		{
        			var $message = $array[$i].split('/separator/');
        			var $date = $message[0];
        			var $author = $message[1];
        			var $text = $message[2];
                    $(".chatTable td:eq(0)").append($date).append(" ").append($author).append(": ").append($text).append("<br>");
        		}
            }
        });
    }
</script>

<script>
    $(document).ready(function()
    {
        $(function()
        {
        	getAjax();
        });
    });
</script>
