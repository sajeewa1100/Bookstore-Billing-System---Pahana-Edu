<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="sidebar.jsp" />


<html>
<head><title>Hello JSP</title></head>
<body>
    <h2>Hello from JSP!</h2>
    <a href="${pageContext.request.contextPath}/hello">Go to HelloServlet</a>
</body>
</html>