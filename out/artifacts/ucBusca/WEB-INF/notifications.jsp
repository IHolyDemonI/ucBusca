<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>ucBusca - Notifications</title>
</head>

<body>
<h1 align="center">Your Notifications</h1>
<br>

<div id="main">
    <c:choose>
        <c:when test="${serverAnswer == null}">
            A problem occurred during the search!
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${!notifications.isEmpty()}">
                    <c:forEach items="${notifications}" var="item">
                        <c:out value="${item}" /> <br />
                    </c:forEach>
                </c:when>
            </c:choose>
            <br />
            <c:choose>
                <c:when test="${!newNotifications.isEmpty()}">
                    <c:forEach items="${newNotifications}" var="item">
                        (new) <c:out value="${item}" /> <br />
                    </c:forEach>
                </c:when>
            </c:choose>
        </c:otherwise>
    </c:choose>

</div>
</body>