<%@ taglib prefix="s" uri="/struts-tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>ucBusca - User Menu</title>
</head>

<body>
<h1 align="center">User Menu</h1>
<br>

<div id="screen">
    <div name="Search" align="center">
        <s:form action="SearchAction" method="post">
            Search for URLs<br>
            <s:textfield name="searchTarget"/>
            <s:submit value="Search" name="search"/>
        </s:form>
    </div>

    <br>

    <div name="ConnectedURL" align="center">
        <s:form action="ConnectedURLAction" method="post">
            Find URLs connected to<br>
            <s:textfield name="targetURL"/>
            <s:submit value="Find" name="connectedURL" class="button"/>
        </s:form>
    </div>

    <br>

    <div name="SearchHistory" align="center">
        <s:form action="SearchHistoryAction" method="post">
            <s:submit value="SearchHistory" name="searchHistory" class="button"/>
        </s:form>
    </div>

    <br>

    <div name="Notification" align="center">
        <s:form action="NotificationAction" method="post">
            <s:submit value="Notification" name="notification" class="button"/>
        </s:form>
    </div>
</div>
</body>
</html>
