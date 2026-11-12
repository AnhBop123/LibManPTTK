package dao;

import model.TaiLieu;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaiLieuDAO {

    public List<TaiLieu> timTheoTen(String tuKhoa) throws SQLException {
        List<TaiLieu> danhSach = new ArrayList<>();

        String sql = """
                SELECT id, ma_tai_lieu, ten_tai_lieu, loai_tai_lieu,
                       tac_gia, nha_xuat_ban, nam_xuat_ban, mo_ta, so_luong
                FROM tai_lieu
                WHERE ten_tai_lieu LIKE ? ESCAPE '!'
                ORDER BY ten_tai_lieu, id
                """;

        // Xử lý %, _ như ký tự người dùng nhập,
        // không để chúng trở thành ký tự đại diện của LIKE.
        String tuKhoaDaXuLy = tuKhoa
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "%" + tuKhoaDaXuLy + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    danhSach.add(docTaiLieu(resultSet));
                }
            }
        }

        return danhSach;
    }

    public TaiLieu timTheoId(int id) throws SQLException {
        String sql = """
                SELECT id, ma_tai_lieu, ten_tai_lieu, loai_tai_lieu,
                       tac_gia, nha_xuat_ban, nam_xuat_ban, mo_ta, so_luong
                FROM tai_lieu
                WHERE id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return docTaiLieu(resultSet);
                }
            }
        }

        return null;
    }

    private TaiLieu docTaiLieu(ResultSet resultSet) throws SQLException {
        TaiLieu taiLieu = new TaiLieu();

        taiLieu.setId(resultSet.getInt("id"));
        taiLieu.setMaTaiLieu(resultSet.getString("ma_tai_lieu"));
        taiLieu.setTenTaiLieu(resultSet.getString("ten_tai_lieu"));
        taiLieu.setLoaiTaiLieu(resultSet.getString("loai_tai_lieu"));
        taiLieu.setTacGia(resultSet.getString("tac_gia"));
        taiLieu.setNhaXuatBan(resultSet.getString("nha_xuat_ban"));

        int nam = resultSet.getInt("nam_xuat_ban");
        taiLieu.setNamXuatBan(resultSet.wasNull() ? null : nam);

        taiLieu.setMoTa(resultSet.getString("mo_ta"));
        taiLieu.setSoLuong(resultSet.getInt("so_luong"));

        return taiLieu;
    }
    public TaiLieu timTheoMa(String ma) throws SQLException {
        String sql = "SELECT * FROM tai_lieu WHERE ma_tai_lieu = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, ma);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return docTaiLieu(resultSet);
                }
            }
        }

        return null;
    }

    // Trả về false nếu mã tài liệu đã tồn tại.
    public boolean themMoi(TaiLieu taiLieu) throws SQLException {
        String sql = """
            INSERT INTO tai_lieu
                (ma_tai_lieu, ten_tai_lieu, loai_tai_lieu,
                 tac_gia, nha_xuat_ban, nam_xuat_ban, mo_ta, so_luong)
            VALUES (?, ?, ?, ?, ?, ?, ?, 0)
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, taiLieu.getMaTaiLieu());
            statement.setString(2, taiLieu.getTenTaiLieu());
            statement.setString(3, taiLieu.getLoaiTaiLieu());
            statement.setString(4, taiLieu.getTacGia());
            statement.setString(5, taiLieu.getNhaXuatBan());

            if (taiLieu.getNamXuatBan() == null) {
                statement.setNull(6, java.sql.Types.SMALLINT);
            } else {
                statement.setInt(6, taiLieu.getNamXuatBan());
            }

            statement.setString(7, taiLieu.getMoTa());
            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                return false;
            }

            throw e;
        }
    }
}