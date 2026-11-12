package dao;

import model.TaiKhoan;
import util.DBConnection;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaiKhoanDAO {

    public TaiKhoan dangNhap(String tenDangNhap, String matKhau)
            throws SQLException {

        String sql = """
                SELECT id, ten_dang_nhap, mat_khau_hash, vai_tro
                FROM tai_khoan
                WHERE ten_dang_nhap = ? AND hoat_dong = TRUE
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, tenDangNhap);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()
                        && PasswordUtil.verify(
                        matKhau,
                        resultSet.getString("mat_khau_hash"))) {

                    return new TaiKhoan(
                            resultSet.getInt("id"),
                            resultSet.getString("ten_dang_nhap"),
                            resultSet.getString("vai_tro")
                    );
                }
            }
        }

        return null;
    }
    public boolean dangKyBanDoc(String tenDangNhap, String matKhau)
            throws SQLException {

        String sql = """
            INSERT INTO tai_khoan
                (ten_dang_nhap, mat_khau_hash, vai_tro, hoat_dong)
            VALUES (?, ?, 'BAN_DOC', TRUE)
            """;

        String matKhauHash = PasswordUtil.hash(matKhau);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, tenDangNhap);
            statement.setString(2, matKhauHash);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            // MySQL: trùng giá trị của cột UNIQUE.
            // Trong thao tác này, đó là tên đăng nhập đã tồn tại.
            if (e.getErrorCode() == 1062) {
                return false;
            }

            throw e;
        }
    }
}