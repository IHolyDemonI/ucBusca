<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>ucBusca</title>
</head>

<body>
<h1 align="center">ucBusca</h1>
<br>

<div id="screen">
    <div name="Search" align="center">
        <s:form action="SearchAction" method="post">
            Searching for: <s:textfield name="searchTarget"/>
            <br>
            <s:submit value="Search" name="search" class="button"/>
        </s:form>
    </div>

    <br>

    <div name="Login" align="center">
        <s:form action="LoginAction" method="post">
            Username: <s:textfield name="username"/>
            <br>
            Password: <s:password name="password"/>
            <br>
            <s:submit value="Login" name="login" class="button"/>
        </s:form>
    </div>

    <br>

    <div name="Register" align="center">
        <s:form action="RegisterAction" method="post">
            Username: <s:textfield name="username"/>
            <br>
            Password: <s:password name="password"/>
            <br>
            <s:submit value="Register" name="register" class="button"/>
        </s:form>
    </div>
</div>
</body>
</html>
