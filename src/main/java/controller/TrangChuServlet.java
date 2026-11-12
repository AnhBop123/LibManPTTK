package controller;

import model.TaiKhoan;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/trang-chu")
public class TrangChuServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        TaiKhoan taiKhoan = (TaiKhoan) request.getSession()
                .getAttribute("taiKhoan");

        String view;

        if ("BAN_DOC".equals(taiKhoan.getVaiTro())) {
            view = "/WEB-INF/views/trangChuBanDoc.jsp";
        } else if ("NHAN_VIEN".equals(taiKhoan.getVaiTro())) {
            view = "/WEB-INF/views/trangChuNhanVien.jsp";
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        request.getRequestDispatcher(view).forward(request, response);
    }
}