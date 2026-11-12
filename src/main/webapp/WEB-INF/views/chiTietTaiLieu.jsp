<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Chi tiết tài liệu - LibMan</title>

    <style>
        * { box-sizing: border-box; }

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #eef3f8;
            color: #1e293b;
        }

        .container {
            max-width: 850px;
            margin: 40px auto;
            padding: 30px;
            background: white;
            border-radius: 14px;
        }

        h1 {
            color: #174ea6;
            line-height: 1.4;
        }

        a { color: #174ea6; }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th, td {
            padding: 14px;
            border-bottom: 1px solid #e2e8f0;
            text-align: left;
            vertical-align: top;
        }

        th {
            width: 180px;
            background: #f8fafc;
        }

        .description {
            white-space: pre-wrap;
            overflow-wrap: anywhere;
        }

        .footer { margin-top: 24px; }
    </style>
</head>
<body>
<div class="container">
    <c:url var="quayLaiUrl" value="/ban-doc/tim-tai-lieu">
        <c:if test="${not empty tuKhoa}">
            <c:param name="tuKhoa" value="${tuKhoa}"/>
        </c:if>
    </c:url>

    <a href="<c:out value='${quayLaiUrl}'/>">
        ← Quay lại tìm kiếm
    </a>

    <h1><c:out value="${taiLieu.tenTaiLieu}"/></h1>

    <table>
        <tr>
            <th>Mã tài liệu</th>
            <td><c:out value="${taiLieu.maTaiLieu}"/></td>
        </tr>
        <tr>
            <th>Loại tài liệu</th>
            <td><c:out value="${taiLieu.tenLoaiTaiLieu}"/></td>
        </tr>
        <tr>
            <th>Tác giả</th>
            <td>
                <c:out value="${taiLieu.tacGia}" default="Chưa cập nhật"/>
            </td>
        </tr>
        <tr>
            <th>Nhà xuất bản</th>
            <td>
                <c:out value="${taiLieu.nhaXuatBan}" default="Chưa cập nhật"/>
            </td>
        </tr>
        <tr>
            <th>Năm xuất bản</th>
            <td>
                <c:out value="${taiLieu.namXuatBan}" default="Chưa cập nhật"/>
            </td>
        </tr>
        <tr>
            <th>Số lượng</th>
            <td><c:out value="${taiLieu.soLuong}"/></td>
        </tr>
        <tr>
            <th>Mô tả</th>
            <td class="description"><c:out value="${taiLieu.moTa}" default="Chưa có mô tả"/></td>
        </tr>
    </table>

    <p class="footer">
        <a href="${pageContext.request.contextPath}/trang-chu">
            Về trang chính
        </a>
    </p>
</div>
</body>
</html>