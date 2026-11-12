<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập - LibMan</title>
    <style>
        * { box-sizing: border-box; }
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #eef3f8;
            min-height: 100vh;
            display: grid;
            place-items: center;
        }
        .card {
            width: min(420px, 92vw);
            background: white;
            padding: 32px;
            border-radius: 16px;
            box-shadow: 0 8px 30px #00000012;
        }
        h1 { margin-top: 0; color: #174ea6; }
        label { display: block; margin: 18px 0 8px; }
        input {
            width: 100%;
            padding: 12px;
            border: 1px solid #cbd5e1;
            border-radius: 8px;
        }
        button {
            width: 100%;
            margin-top: 24px;
            padding: 12px;
            background: #174ea6;
            color: white;
            border: 0;
            border-radius: 8px;
            cursor: pointer;
        }
        .error { color: #b91c1c; }
    </style>
</head>
<body>
<div class="card">
    <h1>LibMan</h1>
    <p>Đăng nhập hệ thống quản lý thư viện</p>

    <c:if test="${not empty loi}">
        <p class="error" role="alert"><c:out value="${loi}"/></p>
    </c:if>

    <form action="${pageContext.request.contextPath}/dang-nhap"
          method="post">

        <label for="tenDangNhap">Tên đăng nhập</label>
        <input id="tenDangNhap"
               name="tenDangNhap"
               type="text"
               maxlength="50"
               autocomplete="username"
               required>

        <label for="matKhau">Mật khẩu</label>
        <input id="matKhau"
               name="matKhau"
               type="password"
               autocomplete="current-password"
               required>

        <button type="submit">Đăng nhập</button>
        <c:if test="${param.dangKy eq 'thanhCong'}">
            <p style="color: #15803d;" role="status">
                Đăng ký thành công! Bạn hãy đăng nhập bằng tài khoản vừa tạo.
            </p>
        </c:if>
    </form>
    <p style="text-align: center; margin-top: 20px;">
        Chưa có tài khoản?
        <a href="${pageContext.request.contextPath}/dang-ky">
            Đăng ký bạn đọc
        </a>
    </p>

    <p style="font-size: 13px; color: #64748b; text-align: center;">
        Nhân viên thư viện sử dụng tài khoản được cấp.
    </p>
</div>
</body>
</html>