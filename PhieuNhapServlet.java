package controller;

import dao.PhieuNhapDAO;
import model.PhieuNhap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/nhan-vien/phieu-nhap")
public class PhieuNhapServlet extends HttpServlet {

    private final PhieuNhapDAO phieuNhapDAO = new PhieuNhapDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        int id = layId(request);

        if (id <= 0) {
            response.sendError(400, "ID phiếu không hợp lệ.");
            return;
        }

        try {
            PhieuNhap phieu = phieuNhapDAO.timTheoId(id);

            if (phieu == null) {
                response.sendError(404, "Không tìm thấy phiếu nhập.");
                return;
            }

            HttpSession session = request.getSession();

            if (session.getAttribute("thanhToanToken") == null) {
                session.setAttribute(
                        "thanhToanToken", UUID.randomUUID().toString()
                );
            }

            request.setAttribute("phieu", phieu);
            request.getRequestDispatcher("/WEB-INF/views/phieuNhap.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            log("Lỗi đọc phiếu nhập.", e);
            response.sendError(500, "Không thể đọc phiếu nhập.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {

        String token = (String) request.getSession()
                .getAttribute("thanhToanToken");

        if (token == null || !token.equals(request.getParameter("token"))) {
            response.sendError(403, "Yêu cầu không hợp lệ. Hãy tải lại phiếu.");
            return;
        }

        int id = layId(request);

        if (id <= 0) {
            response.sendError(400, "ID phiếu không hợp lệ.");
            return;
        }

        try {
            if (phieuNhapDAO.timTheoId(id) == null) {
                response.sendError(404, "Không tìm thấy phiếu nhập.");
                return;
            }

            phieuNhapDAO.ghiNhanThanhToan(id);

            response.sendRedirect(request.getContextPath()
                    + "/nhan-vien/phieu-nhap?id=" + id);

        } catch (SQLException e) {
            log("Lỗi ghi nhận thanh toán.", e);
            response.sendError(500, "Không thể ghi nhận thanh toán.");
        }
    }

    private int layId(HttpServletRequest request) {
        try {
            return Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}