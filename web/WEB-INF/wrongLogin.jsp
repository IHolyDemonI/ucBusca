<%@ taglib prefix="s" uri="/struts-tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Wrong Login</title>
</head>

<body>
    Try again
    <br>

    <div name="login" class="center">
        <s:form action="LoginAction" method="post">
            Username: <s:textfield name="username"/>
            <br>
            Password: <s:password name="password"/>
            <br>
            <s:submit value="Login" name="login" class="button"/>
        </s:form>
    </div>

</body>
</html>
