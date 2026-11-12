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
import java.util.List;

@WebServlet("/ban-doc/tim-tai-lieu")
public class TimTaiLieuServlet extends HttpServlet {

    private final TaiLieuDAO taiLieuDAO = new TaiLieuDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String tuKhoa = request.getParameter("tuKhoa");

        if (tuKhoa != null) {
            tuKhoa = tuKhoa.trim();
            request.setAttribute("tuKhoa", tuKhoa);

            if (tuKhoa.isEmpty()) {
                request.setAttribute("loi", "Vui lòng nhập tên tài liệu.");
            } else if (tuKhoa.length() > 255) {
                request.setAttribute("loi", "Từ khóa không được quá 255 ký tự.");
            } else {
                try {
                    List<TaiLieu> danhSach = taiLieuDAO.timTheoTen(tuKhoa);

                    request.setAttribute("danhSachTaiLieu", danhSach);
                    request.setAttribute("soKetQua", danhSach.size());
                    request.setAttribute("daTimKiem", true);

                } catch (SQLException e) {
                    log("Lỗi tìm kiếm tài liệu.", e);

                    response.setStatus(
                            HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                    );

                    request.setAttribute("loi",
                            "Không thể tìm kiếm lúc này. Vui lòng thử lại sau.");
                }
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/timTaiLieu.jsp")
                .forward(request, response);
    }
}