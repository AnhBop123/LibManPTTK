package filter;

import model.TaiKhoan;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = {
        "/trang-chu",
        "/ban-doc/*",
        "/nhan-vien/*"
})
public class DangNhapFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request =
                (HttpServletRequest) servletRequest;
        HttpServletResponse response =
                (HttpServletResponse) servletResponse;

        response.setHeader("Cache-Control", "no-store");

        HttpSession session = request.getSession(false);
        TaiKhoan taiKhoan = session == null
                ? null
                : (TaiKhoan) session.getAttribute("taiKhoan");

        if (taiKhoan == null) {
            response.sendRedirect(request.getContextPath() + "/dang-nhap");
            return;
        }

        String path = request.getServletPath();

        boolean saiQuyen =
                (path.startsWith("/ban-doc/")
                        && !"BAN_DOC".equals(taiKhoan.getVaiTro()))
                        || (path.startsWith("/nhan-vien/")
                        && !"NHAN_VIEN".equals(taiKhoan.getVaiTro()));

        if (saiQuyen) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }
}