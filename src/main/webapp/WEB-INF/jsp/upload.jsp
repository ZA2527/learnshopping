<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>图片上传</title>
</head>
<body>

<%--如果action为空，图片会上传到当前的浏览器路径下--%>
<form action="" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="图片上传">
</form>

</body>
</html>
