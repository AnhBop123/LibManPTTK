package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaoTaiKhoanMau {

    public static void main(String[] args) {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                taoTaiKhoan(connection, "bandoc", "Bandoc@123", "BAN_DOC");
                int nhanVienId = taoTaiKhoan(
                        connection, "nhanvien", "Nhanvien@123", "NHAN_VIEN"
                );

                String sql = """
                        INSERT INTO nhan_vien (tai_khoan_id, ho_ten)
                        SELECT ?, ?
                        WHERE NOT EXISTS (
                            SELECT 1 FROM nhan_vien WHERE tai_khoan_id = ?
                        )
                        """;

                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {
                    statement.setInt(1, nhanVienId);
                    statement.setString(2, "Nhân viên thư viện mẫu");
                    statement.setInt(3, nhanVienId);
                    statement.executeUpdate();
                }

                connection.commit();
                System.out.println("Khởi tạo tài khoản mẫu thành công!");
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    private static int taoTaiKhoan(
            Connection connection,
            String tenDangNhap,
            String matKhau,
            String vaiTro
    ) throws SQLException {

        String findSql = "SELECT id FROM tai_khoan WHERE ten_dang_nhap = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(findSql)) {
            statement.setString(1, tenDangNhap);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }

        String insertSql = """
                INSERT INTO tai_khoan
                    (ten_dang_nhap, mat_khau_hash, vai_tro)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(
                insertSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, tenDangNhap);
            statement.setString(2, PasswordUtil.hash(matKhau));
            statement.setString(3, vaiTro);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("Không lấy được ID tài khoản mới.");
    }
}