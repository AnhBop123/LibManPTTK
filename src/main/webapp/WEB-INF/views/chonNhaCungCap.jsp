<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Chọn nhà cung cấp - LibMan</title>

    <style>
        * { box-sizing: border-box; }

        body {
            margin: 0;
            padding: 24px;
            font-family: Arial, sans-serif;
            background: #eef3f8;
            color: #1e293b;
        }

        .container {
            max-width: 1100px;
            margin: auto;
            padding: 28px;
            background: white;
            border-radius: 12px;
        }

        h1, h2 { color: #174ea6; }

        a { color: #174ea6; }

        label {
            display: block;
            margin: 14px 0 6px;
        }

        input {
            width: 100%;
            padding: 10px;
            border: 1px solid #cbd5e1;
            border-radius: 6px;
        }

        button {
            padding: 10px 18px;
            border: 0;
            border-radius: 6px;
            background: #174ea6;
            color: white;
            cursor: pointer;
        }

        .search-row {
            display: flex;
            gap: 12px;
            margin-top: 8px;
        }

        .search-row input {
            flex: 1;
            min-width: 0;
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
            padding: 12px;
            border-bottom: 1px solid #e2e8f0;
            text-align: left;
        }

        th { background: #f1f5f9; }

        .error { color: #b91c1c; }

        .selected {
            margin: 24px 0;
            padding: 18px;
            background: #ecfdf5;
            border: 1px solid #a7f3d0;
            border-radius: 8px;
        }

        details {
            margin-top: 28px;
            padding: 18px;
            border: 1px solid #cbd5e1;
            border-radius: 8px;
        }

        summary {
            cursor: pointer;
            font-weight: bold;
        }

        .add-form { max-width: 650px; }

        .submit-row { margin-top: 20px; }
    </style>
</head>
<body>
<div class="container">
    <a href="${pageContext.request.contextPath}/trang-chu">
        ← Trang chính
    </a>

    <h1>Nhập tài liệu từ nhà cung cấp</h1>
    <h2>Bước 1: Chọn nhà cung cấp</h2>

    <form action="${pageContext.request.contextPath}/nhan-vien/chon-nha-cung-cap"
          method="get">

        <label for="tuKhoa">Tìm nhà cung cấp theo tên</label>

        <div class="search-row">
            <input id="tuKhoa"
                   name="tuKhoa"
                   value="<c:out value='${tuKhoa}'/>"
                   maxlength="255"
                   placeholder="Nhập tên nhà cung cấp..."
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
        <c:choose>
            <c:when test="${empty danhSachNhaCungCap}">
                <p>
                    Không tìm thấy nhà cung cấp phù hợp.
                    Bạn có thể thêm mới ở bên dưới.
                </p>
            </c:when>

            <c:otherwise>
                <div class="table-wrapper">
                    <table>
                        <thead>
                        <tr>
                            <th>Mã</th>
                            <th>Tên nhà cung cấp</th>
                            <th>Địa chỉ</th>
                            <th>Điện thoại</th>
                            <th>Thao tác</th>
                        </tr>
                        </thead>

                        <tbody>
                        <c:forEach items="${danhSachNhaCungCap}" var="ncc">
                            <tr>
                                <td>
                                    <c:out value="${ncc.maNhaCungCap}"/>
                                </td>
                                <td>
                                    <c:out value="${ncc.tenNhaCungCap}"/>
                                </td>
                                <td>
                                    <c:out value="${ncc.diaChi}"/>
                                </td>
                                <td>
                                    <c:out value="${ncc.soDienThoai}"/>
                                </td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/nhan-vien/chon-nha-cung-cap"
                                          method="post">

                                        <input type="hidden"
                                               name="hanhDong"
                                               value="chon">

                                        <input type="hidden"
                                               name="nhaCungCapId"
                                               value="${ncc.id}">

                                        <button type="submit">Chọn</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </c:if>

    <c:if test="${not empty nhaCungCapDaChon}">
        <div class="selected">
            <strong>Nhà cung cấp đã chọn:</strong>

            <p>
                <c:out value="${nhaCungCapDaChon.maNhaCungCap}"/>
                —
                <c:out value="${nhaCungCapDaChon.tenNhaCungCap}"/>
            </p>

            <p>
                Địa chỉ:
                <c:out value="${nhaCungCapDaChon.diaChi}"/>
            </p>

            <p>
                <a href="${pageContext.request.contextPath}/nhan-vien/nhap-tai-lieu">
                    Tiếp tục nhập tài liệu →
                </a>
            </p>
        </div>
    </c:if>

    <details ${not empty loiThem ? 'open' : ''}>
        <summary>Chưa có nhà cung cấp? Thêm mới</summary>

        <c:if test="${not empty loiThem}">
            <p class="error" role="alert">
                <c:out value="${loiThem}"/>
            </p>
        </c:if>

        <form class="add-form"
              action="${pageContext.request.contextPath}/nhan-vien/chon-nha-cung-cap"
              method="post">

            <input type="hidden" name="hanhDong" value="them">

            <label for="maNhaCungCap">Mã nhà cung cấp *</label>
            <input id="maNhaCungCap"
                   name="maNhaCungCap"
                   value="<c:out value='${nccMoi.maNhaCungCap}'/>"
                   maxlength="50"
                   pattern="[a-zA-Z0-9_-]{1,50}"
                   title="Chữ không dấu, chữ số, dấu gạch ngang hoặc gạch dưới"
                   placeholder="Ví dụ: NCC003"
                   required>

            <label for="tenNhaCungCap">Tên nhà cung cấp *</label>
            <input id="tenNhaCungCap"
                   name="tenNhaCungCap"
                   value="<c:out value='${nccMoi.tenNhaCungCap}'/>"
                   maxlength="255"
                   required>

            <label for="diaChi">Địa chỉ</label>
            <input id="diaChi"
                   name="diaChi"
                   value="<c:out value='${nccMoi.diaChi}'/>"
                   maxlength="255">

            <label for="soDienThoai">Số điện thoại</label>
            <input id="soDienThoai"
                   name="soDienThoai"
                   type="tel"
                   value="<c:out value='${nccMoi.soDienThoai}'/>"
                   maxlength="20">

            <label for="email">Email</label>
            <input id="email"
                   name="email"
                   type="email"
                   value="<c:out value='${nccMoi.email}'/>"
                   maxlength="100">

            <div class="submit-row">
                <button type="submit">Lưu và chọn nhà cung cấp</button>
            </div>
        </form>
    </details>
</div>
</body>
</html>