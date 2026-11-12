<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đăng ký bạn đọc - LibMan</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            padding: 24px 0;
            font-family: Arial, sans-serif;
            background: #eef3f8;
            min-height: 100vh;
            display: grid;
            place-items: center;
        }

        .card {
            width: min(460px, 92vw);
            background: white;
            padding: 32px;
            border-radius: 16px;
            box-shadow: 0 8px 30px #00000012;
        }

        h1 {
            margin-top: 0;
            color: #174ea6;
        }

        label {
            display: block;
            margin: 18px 0 8px;
        }

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

        .error {
            color: #b91c1c;
        }

        .hint {
            color: #64748b;
            font-size: 13px;
            line-height: 1.5;
        }

        .footer {
            text-align: center;
            margin-bottom: 0;
        }

        a {
            color: #174ea6;
        }
    </style>
</head>
<body>
<div class="card">
    <h1>Đăng ký bạn đọc</h1>
    <p>Tạo tài khoản để sử dụng hệ thống LibMan.</p>

    <c:if test="${not empty loi}">
        <p class="error" role="alert">
            <c:out value="${loi}"/>
        </p>
    </c:if>

    <form action="${pageContext.request.contextPath}/dang-ky"
          method="post">

        <label for="tenDangNhap">Tên đăng nhập</label>
        <input id="tenDangNhap"
               name="tenDangNhap"
               type="text"
               value="<c:out value='${tenDangNhap}'/>"
               minlength="4"
               maxlength="50"
               pattern="[a-zA-Z0-9_]{4,50}"
               title="4–50 ký tự: chữ không dấu, chữ số hoặc dấu gạch dưới"
               autocomplete="username"
               required>

        <p class="hint">
            Dùng 4–50 ký tự: chữ không dấu, chữ số hoặc dấu gạch dưới.
        </p>

        <label for="matKhau">Mật khẩu</label>
        <input id="matKhau"
               name="matKhau"
               type="password"
               minlength="8"
               maxlength="128"
               autocomplete="new-password"
               required>

        <p class="hint">Mật khẩu có từ 8 đến 128 ký tự.</p>

        <label for="xacNhanMatKhau">Xác nhận mật khẩu</label>
        <input id="xacNhanMatKhau"
               name="xacNhanMatKhau"
               type="password"
               minlength="8"
               maxlength="128"
               autocomplete="new-password"
               required>

        <button type="submit">Đăng ký</button>
    </form>

    <p class="footer">
        Đã có tài khoản?
        <a href="${pageContext.request.contextPath}/dang-nhap">
            Đăng nhập
        </a>
    </p>
</div>
</body>
</html>