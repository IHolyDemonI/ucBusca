<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>ucBusca - Search Result</title>
</head>

<body>
<h1 align="center">Search Results</h1>
<br>

<div id="main">
    <c:choose>
        <c:when test="${!searchedTitles.isEmpty()}">
            <c:forEach begin="0" end="${searchedTitles.size() - 1}" var="i">
                <c:out value="${searchedTitles.get(i)}" /> <br />
                <c:out value="${searchedURLs.get(i)}" /> <br />
                <c:out value="${searchedCitations.get(i)}" /> <br /> <br>
            </c:forEach>
        </c:when>
        <c:otherwise>
            No searches found :(
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
