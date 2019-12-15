<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>ucBusca - My Search History</title>
</head>

<body>
<h1>My Search History</h1>
<c:choose>

    <c:when test="${mySearchHistory.size() == 0}">
        <h2>You have no Searches</h2>
    </c:when>
    <c:otherwise>
        <ul>
        <c:forEach items="${mySearchHistory}" var="word">
            <li><c:out value="${word}"/></li>
        </c:forEach>
        </ul>
    </c:otherwise>
</c:choose>

</body>
</html>
