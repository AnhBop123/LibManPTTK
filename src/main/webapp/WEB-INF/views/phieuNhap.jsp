<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Phiếu nhập - LibMan</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            color: #1e293b;
            background: #eef3f8;
            padding: 24px;
        }
        main {
            max-width: 1000px;
            margin: auto;
            padding: 30px;
            background: white;
        }
        h1 { text-align: center; }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 24px;
        }
        th, td {
            border: 1px solid #bbb;
            padding: 10px;
            text-align: left;
        }
        .total { text-align: right; font-size: 20px; }
        .actions {
            display: flex;
            flex-wrap: wrap;
            gap: 16px;
            align-items: center;
            margin-top: 28px;
        }
        button { padding: 10px 16px; cursor: pointer; }
        .signatures {
            display: flex;
            justify-content: space-around;
            margin-top: 40px;
            min-height: 110px;
            text-align: center;
        }
        @media print {
            .actions { display: none; }
            body { background: white; padding: 0; }
            main { padding: 0; }
            tr { break-inside: avoid; }
        }
    </style>
</head>
<body>
<main>
    <p><strong>THƯ VIỆN ĐẠI HỌC — LIBMAN</strong></p>
    <h1>PHIẾU NHẬP TÀI LIỆU</h1>

    <p>Mã phiếu: <c:out value="${phieu.maPhieuNhap}"/></p>
    <p>
        Ngày nhập:
        <fmt:formatDate value="${phieu.ngayNhap}"
                        pattern="dd/MM/yyyy HH:mm:ss"/>
    </p>
    <p>Nhà cung cấp: <c:out value="${phieu.tenNhaCungCap}"/></p>
    <p>Nhân viên lập: <c:out value="${phieu.tenNhanVien}"/></p>

    <p>
        Thanh toán:
        <c:choose>
            <c:when test="${phieu.trangThaiThanhToan eq 'DA_THANH_TOAN'}">
                <strong>Đã thanh toán</strong>
                —
                <fmt:formatDate value="${phieu.ngayThanhToan}"
                                pattern="dd/MM/yyyy HH:mm:ss"/>
            </c:when>
            <c:otherwise>
                <strong>Chưa thanh toán</strong>
            </c:otherwise>
        </c:choose>
    </p>

    <table>
        <thead>
        <tr>
            <th>STT</th>
            <th>Mã</th>
            <th>Tài liệu</th>
            <th>Số lượng</th>
            <th>Đơn giá</th>
            <th>Thành tiền</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${phieu.chiTiet}" var="dong" varStatus="st">
            <tr>
                <td>${st.count}</td>
                <td><c:out value="${dong.maTaiLieu}"/></td>
                <td><c:out value="${dong.tenTaiLieu}"/></td>
                <td>${dong.soLuong}</td>
                <td>
                    <fmt:formatNumber value="${dong.donGia}"
                                      maxFractionDigits="2"/> ₫
                </td>
                <td>
                    <fmt:formatNumber value="${dong.thanhTien}"
                                      maxFractionDigits="2"/> ₫
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <p class="total">
        <strong>
            Tổng tiền:
            <fmt:formatNumber value="${phieu.tongTien}"
                              maxFractionDigits="2"/> ₫
        </strong>
    </p>

    <div class="signatures">
        <div>
            <strong>Nhân viên thư viện</strong>
            <p>(Ký, ghi rõ họ tên)</p>
        </div>
        <div>
            <strong>Đại diện nhà cung cấp</strong>
            <p>(Ký, ghi rõ họ tên)</p>
        </div>
    </div>

    <div class="actions">
        <button type="button" onclick="window.print()">
            In hóa đơn / phiếu nhập
        </button>

        <c:if test="${phieu.trangThaiThanhToan eq 'CHUA_THANH_TOAN'}">
            <form action="${pageContext.request.contextPath}/nhan-vien/phieu-nhap"
                  method="post"
                  onsubmit="return confirm('Bạn xác nhận đã thanh toán cho nhà cung cấp?');">
                <input type="hidden" name="id" value="${phieu.id}">
                <input type="hidden" name="token"
                       value="${sessionScope.thanhToanToken}">
                <button type="submit">Ghi nhận đã thanh toán</button>
            </form>
        </c:if>

        <a href="${pageContext.request.contextPath}/nhan-vien/chon-nha-cung-cap">
            Lập phiếu nhập mới
        </a>

        <a href="${pageContext.request.contextPath}/trang-chu">
            Trang chính
        </a>
    </div>
</main>
</body>
</html>