<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Bạn đọc - LibMan</title>
</head>
<body>
    <h1>Trang chính bạn đọc</h1>

    <p>
        Xin chào,
        <strong><c:out value="${sessionScope.taiKhoan.tenDangNhap}"/></strong>
    </p>

    <p>
        <a href="${pageContext.request.contextPath}/ban-doc/tim-tai-lieu">
            Tìm tài liệu
        </a>
    </p>

    <form action="${pageContext.request.contextPath}/dang-xuat"
          method="post">
        <button type="submit">Đăng xuất</button>
    </form>
</body>
</html>