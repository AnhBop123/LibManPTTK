package controller;

import dao.TaiKhoanDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/dang-ky")
public class DangKyServlet extends HttpServlet {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        if (daDangNhap(request)) {
            response.sendRedirect(request.getContextPath() + "/trang-chu");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/dangKy.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        if (daDangNhap(request)) {
            response.sendRedirect(request.getContextPath() + "/trang-chu");
            return;
        }

        String tenDangNhap = request.getParameter("tenDangNhap");
        String matKhau = request.getParameter("matKhau");
        String xacNhanMatKhau = request.getParameter("xacNhanMatKhau");

        tenDangNhap = tenDangNhap == null ? "" : tenDangNhap.trim();

        // Chỉ giữ lại tên đăng nhập khi form có lỗi.
        request.setAttribute("tenDangNhap", tenDangNhap);

        if (!tenDangNhap.matches("[a-zA-Z0-9_]{4,50}")) {
            hienThiLoi(request, response,
                    "Tên đăng nhập phải có 4–50 ký tự, "
                            + "chỉ gồm chữ không dấu, chữ số và dấu gạch dưới.");
            return;
        }

        // Không trim mật khẩu để giữ nguyên nội dung người dùng nhập.
        if (matKhau == null || matKhau.isBlank()
                || matKhau.length() < 8 || matKhau.length() > 128) {
            hienThiLoi(request, response,
                    "Mật khẩu phải có 8–128 ký tự và không được chỉ gồm khoảng trắng.");
            return;
        }

        if (!matKhau.equals(xacNhanMatKhau)) {
            hienThiLoi(request, response,
                    "Mật khẩu xác nhận không khớp.");
            return;
        }

        try {
            boolean thanhCong = taiKhoanDAO.dangKyBanDoc(
                    tenDangNhap, matKhau
            );

            if (!thanhCong) {
                hienThiLoi(request, response,
                        "Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.");
                return;
            }

            response.sendRedirect(
                    request.getContextPath() + "/dang-nhap?dangKy=thanhCong"
            );

        } catch (SQLException e) {
            log("Lỗi database khi đăng ký bạn đọc.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            hienThiLoi(request, response,
                    "Không thể đăng ký lúc này. Vui lòng thử lại sau.");
        }
    }

    private boolean daDangNhap(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        return session != null
                && session.getAttribute("taiKhoan") != null;
    }

    private void hienThiLoi(HttpServletRequest request,
                            HttpServletResponse response,
                            String thongBao)
            throws ServletException, IOException {

        request.setAttribute("loi", thongBao);

        request.getRequestDispatcher("/WEB-INF/views/dangKy.jsp")
                .forward(request, response);
    }
}