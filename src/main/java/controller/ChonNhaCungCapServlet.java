package controller;

import dao.NhaCungCapDAO;
import model.NhaCungCap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/nhan-vien/chon-nha-cung-cap")
public class ChonNhaCungCapServlet extends HttpServlet {

    private final NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String tuKhoa = request.getParameter("tuKhoa");

            if (tuKhoa != null) {
                tuKhoa = tuKhoa.trim();
                request.setAttribute("tuKhoa", tuKhoa);

                if (tuKhoa.isEmpty()) {
                    request.setAttribute("loi", "Vui lòng nhập tên nhà cung cấp.");
                } else if (tuKhoa.length() > 255) {
                    request.setAttribute("loi", "Từ khóa không được quá 255 ký tự.");
                } else {
                    List<NhaCungCap> danhSach =
                            nhaCungCapDAO.timTheoTen(tuKhoa);

                    request.setAttribute("danhSachNhaCungCap", danhSach);
                    request.setAttribute("daTimKiem", true);
                }
            }

            napNhaCungCapDaChon(request);
            hienThiTrang(request, response);

        } catch (SQLException e) {
            xuLyLoiDatabase(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String hanhDong = request.getParameter("hanhDong");

        try {
            if ("chon".equals(hanhDong)) {
                chonNhaCungCap(request, response);
            } else if ("them".equals(hanhDong)) {
                themNhaCungCap(request, response);
            } else {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Thao tác không hợp lệ."
                );
            }
        } catch (SQLException e) {
            xuLyLoiDatabase(request, response, e);
        }
    }

    private void chonNhaCungCap(HttpServletRequest request,
                                HttpServletResponse response)
            throws IOException, SQLException {

        int id;

        try {
            id = Integer.parseInt(request.getParameter("nhaCungCapId"));

            if (id <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "ID nhà cung cấp không hợp lệ."
            );
            return;
        }

        NhaCungCap nhaCungCap = nhaCungCapDAO.timTheoId(id);

        if (nhaCungCap == null) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Không tìm thấy nhà cung cấp."
            );
            return;
        }

        request.getSession().setAttribute("nhapNhaCungCapId", id);

        response.sendRedirect(
                request.getContextPath() + "/nhan-vien/chon-nha-cung-cap"
        );
    }

    private void themNhaCungCap(HttpServletRequest request,
                                HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        NhaCungCap nhaCungCap = new NhaCungCap();

        nhaCungCap.setMaNhaCungCap(layChuoi(request, "maNhaCungCap"));
        nhaCungCap.setTenNhaCungCap(layChuoi(request, "tenNhaCungCap"));
        nhaCungCap.setDiaChi(layChuoi(request, "diaChi"));
        nhaCungCap.setSoDienThoai(layChuoi(request, "soDienThoai"));
        nhaCungCap.setEmail(layChuoi(request, "email"));

        request.setAttribute("nccMoi", nhaCungCap);

        String loi = kiemTraDuLieu(nhaCungCap);

        if (loi != null) {
            hienThiLoiForm(request, response, loi);
            return;
        }

        int id = nhaCungCapDAO.themMoi(nhaCungCap);

        if (id == -1) {
            hienThiLoiForm(request, response,
                    "Mã nhà cung cấp đã tồn tại. "
                            + "Hãy tìm nhà cung cấp cũ hoặc dùng mã khác.");
            return;
        }

        request.getSession().setAttribute("nhapNhaCungCapId", id);

        response.sendRedirect(
                request.getContextPath() + "/nhan-vien/chon-nha-cung-cap"
        );
    }

    private String kiemTraDuLieu(NhaCungCap ncc) {
        if (!ncc.getMaNhaCungCap().matches("[a-zA-Z0-9_-]{1,50}")) {
            return "Mã nhà cung cấp gồm 1–50 ký tự: "
                    + "chữ không dấu, chữ số, dấu gạch ngang hoặc gạch dưới.";
        }

        if (ncc.getTenNhaCungCap().isEmpty()
                || ncc.getTenNhaCungCap().length() > 255) {
            return "Tên nhà cung cấp phải có từ 1 đến 255 ký tự.";
        }

        if (ncc.getDiaChi().length() > 255) {
            return "Địa chỉ không được quá 255 ký tự.";
        }

        if (!ncc.getSoDienThoai().isEmpty()
                && !ncc.getSoDienThoai().matches("[0-9+(). -]{6,20}")) {
            return "Số điện thoại không hợp lệ.";
        }

        if (!ncc.getEmail().isEmpty()
                && (ncc.getEmail().length() > 100
                || !ncc.getEmail().matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))) {
            return "Email không hợp lệ.";
        }

        return null;
    }

    private String layChuoi(HttpServletRequest request, String ten) {
        String giaTri = request.getParameter(ten);
        return giaTri == null ? "" : giaTri.trim();
    }

    private void napNhaCungCapDaChon(HttpServletRequest request)
            throws SQLException {

        HttpSession session = request.getSession();
        Integer id = (Integer) session.getAttribute("nhapNhaCungCapId");

        if (id != null) {
            NhaCungCap nhaCungCap = nhaCungCapDAO.timTheoId(id);

            if (nhaCungCap == null) {
                session.removeAttribute("nhapNhaCungCapId");
            } else {
                request.setAttribute("nhaCungCapDaChon", nhaCungCap);
            }
        }
    }

    private void hienThiLoiForm(HttpServletRequest request,
                                HttpServletResponse response,
                                String loi)
            throws SQLException, ServletException, IOException {

        request.setAttribute("loiThem", loi);
        napNhaCungCapDaChon(request);
        hienThiTrang(request, response);
    }

    private void hienThiTrang(HttpServletRequest request,
                              HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/views/chonNhaCungCap.jsp")
                .forward(request, response);
    }

    private void xuLyLoiDatabase(HttpServletRequest request,
                                 HttpServletResponse response,
                                 SQLException e)
            throws IOException {

        log("Lỗi xử lý nhà cung cấp.", e);

        response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Không thể xử lý dữ liệu nhà cung cấp."
        );
    }
}