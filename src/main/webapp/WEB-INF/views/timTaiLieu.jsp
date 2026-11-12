<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tìm tài liệu - LibMan</title>

    <style>
        * { box-sizing: border-box; }

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #eef3f8;
            color: #1e293b;
        }

        .container {
            max-width: 1100px;
            margin: 40px auto;
            padding: 28px;
            background: white;
            border-radius: 14px;
        }

        h1 { color: #174ea6; }

        a { color: #174ea6; }

        label {
            display: block;
            margin-bottom: 10px;
        }

        .search-row {
            display: flex;
            gap: 12px;
        }

        input {
            flex: 1;
            min-width: 0;
            padding: 12px;
            border: 1px solid #cbd5e1;
            border-radius: 8px;
        }

        button {
            padding: 12px 22px;
            border: 0;
            border-radius: 8px;
            background: #174ea6;
            color: white;
            cursor: pointer;
        }

        .table-wrapper {
            overflow-x: auto;
            margin-top: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 14px;
            border-bottom: 1px solid #e2e8f0;
            text-align: left;
        }

        th { background: #f1f5f9; }

        .error { color: #b91c1c; }

        .notice {
            padding: 16px;
            background: #f8fafc;
            border-radius: 8px;
        }
    </style>
</head>
<body>
<div class="container">
    <a href="${pageContext.request.contextPath}/trang-chu">
        ← Trang chính
    </a>

    <h1>Tìm tài liệu</h1>

    <form action="${pageContext.request.contextPath}/ban-doc/tim-tai-lieu"
          method="get">

        <label for="tuKhoa">Tên tài liệu</label>

        <div class="search-row">
            <input id="tuKhoa"
                   name="tuKhoa"
                   type="text"
                   value="<c:out value='${tuKhoa}'/>"
                   placeholder="Nhập tên hoặc một phần tên tài liệu..."
                   maxlength="255"
                   required>

            <button type="submit">Tìm kiếm</button>
        </div>
    </form>

    <c:if test="${not empty loi}">
        <p class="error" role="alert">
            <c:out value="${loi}"/>
        </p>
    </c:if>

    <c:if test="${daTimKiem}">
        <p>
            Tìm thấy <strong><c:out value="${soKetQua}"/></strong>
            tài liệu có tên chứa
            “<c:out value="${tuKhoa}"/>”.
        </p>

        <c:choose>
            <c:when test="${empty danhSachTaiLieu}">
                <p class="notice">
                    Không tìm thấy tài liệu phù hợp.
                    Bạn hãy thử từ khóa khác.
                </p>
            </c:when>

            <c:otherwise>
                <div class="table-wrapper">
                    <table>
                        <thead>
                        <tr>
                            <th>STT</th>
                            <th>Mã tài liệu</th>
                            <th>Tên tài liệu</th>
                            <th>Loại</th>
                            <th>Tác giả</th>
                            <th>Chi tiết</th>
                        </tr>
                        </thead>

                        <tbody>
                        <c:forEach items="${danhSachTaiLieu}"
                                   var="tl"
                                   varStatus="st">

                            <c:url var="chiTietUrl"
                                   value="/ban-doc/chi-tiet-tai-lieu">
                                <c:param name="id" value="${tl.id}"/>
                                <c:param name="tuKhoa" value="${tuKhoa}"/>
                            </c:url>

                            <tr>
                                <td><c:out value="${st.count}"/></td>
                                <td><c:out value="${tl.maTaiLieu}"/></td>
                                <td>
                                    <a href="<c:out value='${chiTietUrl}'/>">
                                        <c:out value="${tl.tenTaiLieu}"/>
                                    </a>
                                </td>
                                <td>
                                    <c:out value="${tl.tenLoaiTaiLieu}"/>
                                </td>
                                <td>
                                    <c:out value="${tl.tacGia}" default="—"/>
                                </td>
                                <td>
                                    <a href="<c:out value='${chiTietUrl}'/>">
                                        Xem chi tiết
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </c:if>
</div>
</body>
</html>