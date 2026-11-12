package controller;

import dao.TaiLieuDAO;
import model.TaiLieu;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/ban-doc/chi-tiet-tai-lieu")
public class ChiTietTaiLieuServlet extends HttpServlet {

    private final TaiLieuDAO taiLieuDAO = new TaiLieuDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        int id;

        try {
            id = Integer.parseInt(request.getParameter("id"));

            if (id <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "ID tài liệu không hợp lệ."
            );
            return;
        }

        try {
            TaiLieu taiLieu = taiLieuDAO.timTheoId(id);

            if (taiLieu == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Không tìm thấy tài liệu."
                );
                return;
            }

            // Giữ từ khóa để nút quay lại mở đúng kết quả trước đó.
            String tuKhoa = request.getParameter("tuKhoa");

            if (tuKhoa != null && tuKhoa.length() <= 255) {
                request.setAttribute("tuKhoa", tuKhoa);
            }

            request.setAttribute("taiLieu", taiLieu);

            request.getRequestDispatcher("/WEB-INF/views/chiTietTaiLieu.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            log("Lỗi đọc chi tiết tài liệu.", e);

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Không thể tải thông tin tài liệu."
            );
        }
    }
}