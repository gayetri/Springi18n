<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<html>
<body>
<h1>hello!</h1>

<h3>
    welcome.springmvc : <spring:message code="${message}" text="default text"/>
</h3>
<span style="float: right">
    <a href="?lang=en">en</a>
    |
    <a href="?lang=de">de</a>
</span>
</body>
</html>