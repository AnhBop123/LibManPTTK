package controller;

import dao.NhaCungCapDAO;
import dao.PhieuNhapDAO;
import dao.TaiLieuDAO;
import model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet("/nhan-vien/nhap-tai-lieu")
public class NhapTaiLieuServlet extends HttpServlet {

    private final TaiLieuDAO taiLieuDAO = new TaiLieuDAO();
    private final NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO();
    private final PhieuNhapDAO phieuNhapDAO = new PhieuNhapDAO();

    @SuppressWarnings("unchecked")
    private List<ChiTietPhieuNhap> layDanhSach(HttpSession session) {
        return (List<ChiTietPhieuNhap>) session.getAttribute("nhapDanhSach");
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        synchronized (session) {
            try {
                if (session.getAttribute("nhapMaPhieu") == null) {
                    Integer nhaCungCapId =
                            (Integer) session.getAttribute("nhapNhaCungCapId");

                    if (nhaCungCapId == null
                            || nhaCungCapDAO.timTheoId(nhaCungCapId) == null) {
                        response.sendRedirect(request.getContextPath()
                                + "/nhan-vien/chon-nha-cung-cap");
                        return;
                    }

                    session.setAttribute("nhapNccBanNhap", nhaCungCapId);
                    session.setAttribute(
                            "nhapMaPhieu", "PN-" + UUID.randomUUID()
                    );
                    session.setAttribute(
                            "nhapDanhSach", new ArrayList<ChiTietPhieuNhap>()
                    );
                }

                String ma = chuoi(request, "ma");

                if (!ma.isEmpty()) {
                    if (ma.length() > 50) {
                        request.setAttribute("loi", "Mã tài liệu quá dài.");
                    } else {
                        TaiLieu taiLieu = taiLieuDAO.timTheoMa(ma);
                        request.setAttribute("taiLieuTimThay", taiLieu);

                        if (taiLieu == null) {
                            request.setAttribute("khongTimThay", true);
                        }
                    }
                }

                request.setAttribute("maTim", ma);
                hienThi(request, response);

            } catch (SQLException e) {
                log("Lỗi mở màn hình nhập tài liệu.", e);
                response.sendError(500, "Không thể tải dữ liệu nhập tài liệu.");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        synchronized (session) {
            String maPhieu = (String) session.getAttribute("nhapMaPhieu");
            String maGuiLen = request.getParameter("maPhieu");

            if (maPhieu == null || !maPhieu.equals(maGuiLen)) {
                response.sendError(409,
                        "Bản nháp đã thay đổi hoặc đã được lưu. Hãy tải lại trang.");
                return;
            }

            String hanhDong = chuoi(request, "hanhDong");

            try {
                switch (hanhDong) {
                    case "themDong" -> themDong(request);
                    case "xoaDong" -> xoaDong(request);
                    case "themTaiLieu" -> {
                        String maMoi = themTaiLieu(request);

                        response.sendRedirect(request.getContextPath()
                                + "/nhan-vien/nhap-tai-lieu?ma="
                                + URLEncoder.encode(
                                maMoi, StandardCharsets.UTF_8
                        ));
                        return;
                    }
                    case "huy" -> {
                        xoaBanNhap(session);
                        session.removeAttribute("nhapNhaCungCapId");

                        response.sendRedirect(request.getContextPath()
                                + "/nhan-vien/chon-nha-cung-cap");
                        return;
                    }
                    case "xacNhan" -> {
                        List<ChiTietPhieuNhap> danhSach = layDanhSach(session);

                        if (danhSach.isEmpty()) {
                            throw new IllegalArgumentException(
                                    "Hãy thêm ít nhất một tài liệu."
                            );
                        }

                        TaiKhoan taiKhoan =
                                (TaiKhoan) session.getAttribute("taiKhoan");

                        int id = phieuNhapDAO.luu(
                                maPhieu,
                                (Integer) session.getAttribute("nhapNccBanNhap"),
                                taiKhoan.getId(),
                                new ArrayList<>(danhSach)
                        );

                        xoaBanNhap(session);
                        session.removeAttribute("nhapNhaCungCapId");

                        response.sendRedirect(request.getContextPath()
                                + "/nhan-vien/phieu-nhap?id=" + id);
                        return;
                    }
                    default -> throw new IllegalArgumentException(
                            "Thao tác không hợp lệ."
                    );
                }

                response.sendRedirect(request.getContextPath()
                        + "/nhan-vien/nhap-tai-lieu");

            } catch (IllegalArgumentException e) {
                request.setAttribute("loi", e.getMessage());
                hienThiAnToan(request, response);

            } catch (SQLException e) {
                log("Lỗi xử lý nhập tài liệu.", e);
                response.setStatus(500);
                request.setAttribute("loi",
                        "Không thể hoàn tất thao tác. "
                                + "Bản nháp vẫn được giữ để bạn thử lại.");
                hienThiAnToan(request, response);
            }
        }
    }

    private void themDong(HttpServletRequest request) throws SQLException {
        int id = soNguyen(chuoi(request, "taiLieuId"), "ID tài liệu");
        int soLuong = soNguyen(chuoi(request, "soLuong"), "Số lượng");

        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }

        String gia = chuoi(request, "donGia");

        if (!gia.matches("\\d{1,13}(\\.\\d{1,2})?")) {
            throw new IllegalArgumentException(
                    "Đơn giá phải không âm, tối đa 13 chữ số phần nguyên "
                            + "và 2 chữ số thập phân. Ví dụ: 80000 hoặc 80000.50."
            );
        }

        BigDecimal donGia = new BigDecimal(gia).setScale(2);
        TaiLieu taiLieu = taiLieuDAO.timTheoId(id);

        if (taiLieu == null) {
            throw new IllegalArgumentException("Tài liệu không tồn tại.");
        }

        layDanhSach(request.getSession()).add(new ChiTietPhieuNhap(
                taiLieu.getId(),
                taiLieu.getMaTaiLieu(),
                taiLieu.getTenTaiLieu(),
                soLuong,
                donGia
        ));
    }

    private void xoaDong(HttpServletRequest request) {
        int viTri = soNguyen(chuoi(request, "viTri"), "Vị trí dòng");
        List<ChiTietPhieuNhap> danhSach = layDanhSach(request.getSession());

        if (viTri < 0 || viTri >= danhSach.size()) {
            throw new IllegalArgumentException(
                    "Dòng nhập không còn tồn tại. Hãy tải lại trang."
            );
        }

        danhSach.remove(viTri);
    }

    private String themTaiLieu(HttpServletRequest request)
            throws SQLException {

        TaiLieu taiLieu = new TaiLieu();

        String ma = chuoi(request, "maTaiLieu");
        String ten = chuoi(request, "tenTaiLieu");
        String loai = chuoi(request, "loaiTaiLieu");
        String tacGia = chuoi(request, "tacGia");
        String nxb = chuoi(request, "nhaXuatBan");
        String nam = chuoi(request, "namXuatBan");
        String moTa = chuoi(request, "moTa");

        if (!ma.matches("[a-zA-Z0-9_-]{1,50}")) {
            throw new IllegalArgumentException("Mã tài liệu không hợp lệ.");
        }

        if (ten.isEmpty() || ten.length() > 255) {
            throw new IllegalArgumentException(
                    "Tên tài liệu phải có 1–255 ký tự."
            );
        }

        if (!List.of("SACH", "GIAO_TRINH", "TAP_CHI").contains(loai)) {
            throw new IllegalArgumentException("Loại tài liệu không hợp lệ.");
        }

        if (tacGia.length() > 255 || nxb.length() > 255
                || moTa.length() > 5000) {
            throw new IllegalArgumentException(
                    "Tác giả/NXB tối đa 255 ký tự; mô tả tối đa 5000 ký tự."
            );
        }

        if (!nam.isEmpty()) {
            int namXuatBan = soNguyen(nam, "Năm xuất bản");

            if (namXuatBan < 1 || namXuatBan > 9999) {
                throw new IllegalArgumentException("Năm xuất bản không hợp lệ.");
            }

            taiLieu.setNamXuatBan(namXuatBan);
        }

        taiLieu.setMaTaiLieu(ma);
        taiLieu.setTenTaiLieu(ten);
        taiLieu.setLoaiTaiLieu(loai);
        taiLieu.setTacGia(tacGia);
        taiLieu.setNhaXuatBan(nxb);
        taiLieu.setMoTa(moTa);

        if (!taiLieuDAO.themMoi(taiLieu)) {
            throw new IllegalArgumentException(
                    "Mã tài liệu đã tồn tại. Hãy tìm lại bằng mã này."
            );
        }

        return ma;
    }

    private void hienThi(HttpServletRequest request,
                         HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        HttpSession session = request.getSession();
        Integer nccId = (Integer) session.getAttribute("nhapNccBanNhap");

        NhaCungCap nhaCungCap = nhaCungCapDAO.timTheoId(nccId);

        if (nhaCungCap == null) {
            response.sendError(409, "Nhà cung cấp của bản nháp không còn tồn tại.");
            return;
        }

        List<ChiTietPhieuNhap> danhSach = layDanhSach(session);

        BigDecimal tongTien = danhSach.stream()
                .map(ChiTietPhieuNhap::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        request.setAttribute("nhaCungCap", nhaCungCap);
        request.setAttribute("danhSach", new ArrayList<>(danhSach));
        request.setAttribute("tongTien", tongTien);

        request.getRequestDispatcher("/WEB-INF/views/nhapTaiLieu.jsp")
                .forward(request, response);
    }

    private void hienThiAnToan(HttpServletRequest request,
                               HttpServletResponse response)
            throws ServletException, IOException {

        try {
            hienThi(request, response);
        } catch (SQLException e) {
            log("Lỗi tải lại bản nháp.", e);
            response.sendError(500, "Không thể tải bản nháp.");
        }
    }

    private String chuoi(HttpServletRequest request, String ten) {
        String giaTri = request.getParameter(ten);
        return giaTri == null ? "" : giaTri.trim();
    }

    private int soNguyen(String giaTri, String tenTruong) {
        try {
            return Integer.parseInt(giaTri);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    tenTruong + " phải là số nguyên hợp lệ."
            );
        }
    }

    private void xoaBanNhap(HttpSession session) {
        session.removeAttribute("nhapMaPhieu");
        session.removeAttribute("nhapNccBanNhap");
        session.removeAttribute("nhapDanhSach");
    }
}