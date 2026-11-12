package controller;

import dao.TaiKhoanDAO;
import model.TaiKhoan;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/dang-nhap")
public class DangNhapServlet extends HttpServlet {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("taiKhoan") != null) {
            response.sendRedirect(request.getContextPath() + "/trang-chu");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/dangNhap.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String tenDangNhap = request.getParameter("tenDangNhap");
        String matKhau = request.getParameter("matKhau");

        if (tenDangNhap == null || tenDangNhap.isBlank()
                || matKhau == null || matKhau.isEmpty()) {
            hienThiLoi(request, response,
                    "Vui lòng nhập tên đăng nhập và mật khẩu.");
            return;
        }

        try {
            TaiKhoan taiKhoan = taiKhoanDAO.dangNhap(
                    tenDangNhap.trim(), matKhau
            );

            if (taiKhoan == null) {
                hienThiLoi(request, response,
                        "Tên đăng nhập hoặc mật khẩu không đúng.");
                return;
            }

            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("taiKhoan", taiKhoan);
            session.setMaxInactiveInterval(30 * 60);

            response.sendRedirect(request.getContextPath() + "/trang-chu");

        } catch (SQLException e) {
            log("Lỗi database khi đăng nhập.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            hienThiLoi(request, response,
                    "Không thể đăng nhập lúc này. Vui lòng thử lại sau.");
        }
    }

    private void hienThiLoi(HttpServletRequest request,
                            HttpServletResponse response,
                            String thongBao)
            throws ServletException, IOException {

        request.setAttribute("loi", thongBao);
        request.getRequestDispatcher("/WEB-INF/views/dangNhap.jsp")
                .forward(request, response);
    }
}