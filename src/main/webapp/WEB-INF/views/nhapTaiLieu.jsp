<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Nhập tài liệu - LibMan</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #eef3f8;
            color: #1e293b;
            margin: 0;
            padding: 24px;
        }
        main {
            max-width: 1100px;
            margin: auto;
            padding: 28px;
            background: white;
            border-radius: 12px;
        }
        h1, h2, a { color: #174ea6; }
        input, select, textarea {
            padding: 9px;
            margin: 5px 0;
            max-width: 100%;
            box-sizing: border-box;
        }
        button {
            padding: 10px 16px;
            cursor: pointer;
        }
        label { display: block; margin-top: 10px; }
        .error { color: #b91c1c; }
        .box {
            padding: 16px;
            background: #f1f5f9;
            margin: 20px 0;
        }
        table { width: 100%; border-collapse: collapse; }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        .scroll { overflow-x: auto; }
        .actions {
            display: flex;
            gap: 16px;
            margin-top: 24px;
            flex-wrap: wrap;
        }
        .primary { background: #174ea6; color: white; border: 0; }
        details { margin: 24px 0; }
        summary { cursor: pointer; }
    </style>
</head>
<body>
<main>
    <a href="${pageContext.request.contextPath}/trang-chu">← Trang chính</a>

    <h1>Lập phiếu nhập tài liệu</h1>

    <div class="box">
        Nhà cung cấp của phiếu đang soạn:
        <strong><c:out value="${nhaCungCap.tenNhaCungCap}"/></strong>
        (<c:out value="${nhaCungCap.maNhaCungCap}"/>)
        <p>Muốn đổi nhà cung cấp, hãy hủy bản nháp rồi chọn lại.</p>
    </div>

    <c:if test="${not empty loi}">
        <p class="error" role="alert"><c:out value="${loi}"/></p>
    </c:if>

    <h2>Tìm tài liệu theo mã</h2>

    <form action="${pageContext.request.contextPath}/nhan-vien/nhap-tai-lieu"
          method="get">
        <label for="ma">Mã tài liệu</label>
        <input id="ma" name="ma"
               value="<c:out value='${maTim}'/>"
               maxlength="50" placeholder="Ví dụ: TL001" required>
        <button type="submit">Tìm</button>
    </form>

    <c:if test="${khongTimThay}">
        <p>Chưa có mã tài liệu này. Bạn có thể thêm mới ở phần bên dưới.</p>
    </c:if>

    <c:if test="${not empty taiLieuTimThay}">
        <div class="box">
            <strong>
                <c:out value="${taiLieuTimThay.maTaiLieu}"/> —
                <c:out value="${taiLieuTimThay.tenTaiLieu}"/>
            </strong>

            <form action="${pageContext.request.contextPath}/nhan-vien/nhap-tai-lieu"
                  method="post">
                <input type="hidden" name="hanhDong" value="themDong">
                <input type="hidden" name="maPhieu"
                       value="${sessionScope.nhapMaPhieu}">
                <input type="hidden" name="taiLieuId"
                       value="${taiLieuTimThay.id}">

                <label>Số lượng nhập</label>
                <input name="soLuong" type="number"
                       min="1" max="2147483647" step="1" required>

                <label>Đơn giá (VNĐ)</label>
                <input name="donGia" type="number"
                       min="0" max="9999999999999.99"
                       step="0.01" required>

                <button type="submit">Thêm vào danh sách</button>
            </form>
        </div>
    </c:if>

    <details>
        <summary><strong>Thêm tài liệu mới nếu chưa có</strong></summary>

        <form action="${pageContext.request.contextPath}/nhan-vien/nhap-tai-lieu"
              method="post">
            <input type="hidden" name="hanhDong" value="themTaiLieu">
            <input type="hidden" name="maPhieu"
                   value="${sessionScope.nhapMaPhieu}">

            <label>Mã tài liệu *</label>
            <input name="maTaiLieu" maxlength="50"
                   pattern="[a-zA-Z0-9_-]{1,50}"
                   value="<c:out value='${maTim}'/>" required>

            <label>Tên tài liệu *</label>
            <input name="tenTaiLieu" maxlength="255" size="55" required>

            <label>Loại tài liệu *</label>
            <select name="loaiTaiLieu">
                <option value="SACH">Sách</option>
                <option value="GIAO_TRINH">Giáo trình</option>
                <option value="TAP_CHI">Tạp chí</option>
            </select>

            <label>Tác giả</label>
            <input name="tacGia" maxlength="255">

            <label>Nhà xuất bản</label>
            <input name="nhaXuatBan" maxlength="255">

            <label>Năm xuất bản</label>
            <input name="namXuatBan" type="number" min="1" max="9999">

            <label>Mô tả</label>
            <textarea name="moTa" rows="3" cols="55" maxlength="5000"></textarea>

            <p>
                <button type="submit">Lưu tài liệu mới</button>
            </p>
        </form>
    </details>

    <h2>Danh sách tài liệu nhập</h2>

    <c:choose>
        <c:when test="${empty danhSach}">
            <p>Chưa có tài liệu trong danh sách nhập.</p>
        </c:when>
        <c:otherwise>
            <div class="scroll">
                <table>
                    <thead>
                    <tr>
                        <th>Mã</th>
                        <th>Tên tài liệu</th>
                        <th>Số lượng</th>
                        <th>Đơn giá</th>
                        <th>Thành tiền</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${danhSach}" var="dong" varStatus="st">
                        <tr>
                            <td><c:out value="${dong.maTaiLieu}"/></td>
                            <td><c:out value="${dong.tenTaiLieu}"/></td>
                            <td><c:out value="${dong.soLuong}"/></td>
                            <td>
                                <fmt:formatNumber value="${dong.donGia}"
                                                  minFractionDigits="0"
                                                  maxFractionDigits="2"/> ₫
                            </td>
                            <td>
                                <fmt:formatNumber value="${dong.thanhTien}"
                                                  minFractionDigits="0"
                                                  maxFractionDigits="2"/> ₫
                            </td>
                            <td>
                                <form action="${pageContext.request.contextPath}/nhan-vien/nhap-tai-lieu"
                                      method="post">
                                    <input type="hidden" name="hanhDong" value="xoaDong">
                                    <input type="hidden" name="maPhieu"
                                           value="${sessionScope.nhapMaPhieu}">
                                    <input type="hidden" name="viTri"
                                           value="${st.index}">
                                    <button type="submit">Xóa dòng</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>

    <h3>
        Tổng tiền:
        <fmt:formatNumber value="${tongTien}"
                          minFractionDigits="0"
                          maxFractionDigits="2"/> ₫
    </h3>

    <p>Danh sách đang là bản nháp. Số lượng kho chỉ tăng khi xác nhận nhập.</p>

    <div class="actions">
        <c:if test="${not empty danhSach}">
            <form action="${pageContext.request.contextPath}/nhan-vien/nhap-tai-lieu"
                  method="post"
                  onsubmit="return confirm('Xác nhận lưu phiếu và nhập các tài liệu này vào kho?');">
                <input type="hidden" name="hanhDong" value="xacNhan">
                <input type="hidden" name="maPhieu"
                       value="${sessionScope.nhapMaPhieu}">
                <button type="submit" class="primary">Xác nhận nhập</button>
            </form>
        </c:if>

        <form action="${pageContext.request.contextPath}/nhan-vien/nhap-tai-lieu"
              method="post"
              onsubmit="return confirm('Hủy danh sách nhập đang soạn?');">
            <input type="hidden" name="hanhDong" value="huy">
            <input type="hidden" name="maPhieu"
                   value="${sessionScope.nhapMaPhieu}">
            <button type="submit">Hủy bản nháp</button>
        </form>
    </div>
</main>
</body>
</html>