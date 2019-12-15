<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>ucBusca - Connected URLs</title>
</head>

<body>
<h1>Connected URLs</h1>
<c:choose>

    <c:when test="${pages.size() == 0}">
        <h2>This page has no connections</h2>
    </c:when>
    <c:otherwise>
        <ul>
            <c:forEach items="${pages}" var="page">
                <li><c:out value="${page.get('title')}"/></li>
                <c:out value="${page.get('url')}"/><br>
                <c:out value="${page.get('citation')}"/><br><br>
            </c:forEach>
        </ul>
    </c:otherwise>
</c:choose>

</body>
</html>
