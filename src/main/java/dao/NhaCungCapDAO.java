package dao;

import model.NhaCungCap;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAO {

    public List<NhaCungCap> timTheoTen(String tuKhoa) throws SQLException {
        List<NhaCungCap> danhSach = new ArrayList<>();

        String sql = """
                SELECT id, ma_nha_cung_cap, ten_nha_cung_cap,
                       dia_chi, so_dien_thoai, email
                FROM nha_cung_cap
                WHERE ten_nha_cung_cap LIKE ? ESCAPE '!'
                ORDER BY ten_nha_cung_cap, id
                """;

        String tuKhoaDaXuLy = tuKhoa
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "%" + tuKhoaDaXuLy + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    danhSach.add(docNhaCungCap(resultSet));
                }
            }
        }

        return danhSach;
    }

    public NhaCungCap timTheoId(int id) throws SQLException {
        String sql = """
                SELECT id, ma_nha_cung_cap, ten_nha_cung_cap,
                       dia_chi, so_dien_thoai, email
                FROM nha_cung_cap
                WHERE id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return docNhaCungCap(resultSet);
                }
            }
        }

        return null;
    }

    // Trả về ID mới; trả về -1 nếu mã nhà cung cấp đã tồn tại.
    public int themMoi(NhaCungCap nhaCungCap) throws SQLException {
        String sql = """
                INSERT INTO nha_cung_cap
                    (ma_nha_cung_cap, ten_nha_cung_cap,
                     dia_chi, so_dien_thoai, email)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, nhaCungCap.getMaNhaCungCap());
            statement.setString(2, nhaCungCap.getTenNhaCungCap());
            statement.setString(3, nhaCungCap.getDiaChi());
            statement.setString(4, nhaCungCap.getSoDienThoai());
            statement.setString(5, nhaCungCap.getEmail());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

            throw new SQLException("Không lấy được ID nhà cung cấp mới.");

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                return -1;
            }

            throw e;
        }
    }

    private NhaCungCap docNhaCungCap(ResultSet resultSet)
            throws SQLException {

        NhaCungCap nhaCungCap = new NhaCungCap();

        nhaCungCap.setId(resultSet.getInt("id"));
        nhaCungCap.setMaNhaCungCap(resultSet.getString("ma_nha_cung_cap"));
        nhaCungCap.setTenNhaCungCap(resultSet.getString("ten_nha_cung_cap"));
        nhaCungCap.setDiaChi(resultSet.getString("dia_chi"));
        nhaCungCap.setSoDienThoai(resultSet.getString("so_dien_thoai"));
        nhaCungCap.setEmail(resultSet.getString("email"));

        return nhaCungCap;
    }
}