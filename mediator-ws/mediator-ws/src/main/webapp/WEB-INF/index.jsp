<%@ page language="java" contentType="text/html; charset=utf-8"
       pageEncoding="utf-8"%>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>File Upload</title>
</head>
<body>
<form method="post" action="pack" enctype="multipart/form-data">
<div>
	<span>选择医院</span>
	<select name="hospitalId">
	    <c:forEach var="item" items="${hospitals}">
	        <option value="${item.id}" ${item.id == selectedHosp ? 'selected="selected"' : ''}>${item.name}</option>
	    </c:forEach>
	</select>
</div>

<div>
	当前版本 <input type="text" name="currVersion"/>
</div>

<div>
	新版本 <input type="text" name="newVersion"/>
</div>

<div>
	<input type="file" name="file" id="fileChooser"/><br/><br/>
	<input type="submit" value="Upload" />
</div>
</form>
</body>
</html>