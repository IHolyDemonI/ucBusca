<%@ taglib prefix="s" uri="/struts-tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>ucBusca - Admin Menu</title>
</head>

<body>
<h1 align="center">Admin Menu</h1>
<br>

<div id="screen">
    <div name="Search" align="center">
        <s:form action="SearchAction" method="post">
            <s:textfield name="searchTarget"/>
            <s:submit value="Search" name="search" class="button"/>
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

    <div name="Index" align="center">
        <s:form action="IndexAction" method="post">
            Index a new URL<br>
            <s:textfield name="newURL"/>
            <s:textfield type="number" name="level" min="1" max="7"/>
            <s:submit value="Index" name="index" class="button"/>
        </s:form>
    </div>

    <br>

    <div name="GrantPrivileges" align="center">
        <s:form action="GrantPrivilegesAction" method="post">
            Grant privileges <br>
            <s:textfield name="targetUser"/>
            <s:submit value="GrantPrivileges" name="grantPrivileges" class="button"/>
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

    <br>

    <div name="AdministrationPage" align="center">
        <s:form action="AdministrationPageAction" method="post">
            <s:submit value="AdministrationPage" name="administrationPage" class="button"/>
        </s:form>
    </div>
</div>
</body>
</html>
