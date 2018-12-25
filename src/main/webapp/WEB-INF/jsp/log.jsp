<%--
  Created by IntelliJ IDEA.
  User: Dmytro K.
  Date: 21.12.2018
  Time: 19:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>sad</title>
</head>
<body>
<table>
    <tr>
        <td colspan="2">
            <input type="text" id="username" placeholder="Username"/>
            <button type="button" onclick="connect();" >Connect</button>
        </td>
    </tr>
    <tr>
        <td>
            <textarea readonly="true" rows="10" cols="80" id="log"></textarea>
        </td>
    </tr>
    <tr>
        <td>
            <input type="text" size="51" id="msg" placeholder="Message"/>
            <button type="button" onclick="send();" >Send</button>
        </td>
    </tr>
</table>
</body>

<script type="text/javascript" src="${pageContext.servletContext.contextPath}/js/JsonParser.js"></script>
<script src="${pageContext.servletContext.contextPath}/js/websocket.js"></script>
</html>
