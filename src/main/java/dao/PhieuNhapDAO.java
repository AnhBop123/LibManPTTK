package dao;

import model.ChiTietPhieuNhap;
import model.PhieuNhap;
import util.DBConnection;

import java.sql.*;
import java.util.List;

public class PhieuNhapDAO {

    public int luu(String maPhieu, int nhaCungCapId,
                   int taiKhoanId, List<ChiTietPhieuNhap> danhSach)
            throws SQLException {

        if (danhSach == null || danhSach.isEmpty()) {
            throw new SQLException("Phiếu nhập phải có ít nhất một tài liệu.");
        }

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                // Nếu lần gửi trước đã lưu thành công thì trả lại phiếu cũ.
                Integer daLuu = timIdTheoMa(connection, maPhieu);

                if (daLuu != null) {
                    connection.commit();
                    return daLuu;
                }

                int nhanVienId = timNhanVienId(connection, taiKhoanId);
                int phieuNhapId;

                String sqlPhieu = """
                        INSERT INTO phieu_nhap
                            (ma_phieu_nhap, nha_cung_cap_id, nhan_vien_id)
                        VALUES (?, ?, ?)
                        """;

                try (PreparedStatement statement = connection.prepareStatement(
                        sqlPhieu, Statement.RETURN_GENERATED_KEYS)) {

                    statement.setString(1, maPhieu);
                    statement.setInt(2, nhaCungCapId);
                    statement.setInt(3, nhanVienId);
                    statement.executeUpdate();

                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Không lấy được ID phiếu nhập.");
                        }
                        phieuNhapId = keys.getInt(1);
                    }
                }

                String sqlChiTiet = """
                        INSERT INTO chi_tiet_phieu_nhap
                            (phieu_nhap_id, tai_lieu_id, so_luong, don_gia)
                        VALUES (?, ?, ?, ?)
                        """;

                String sqlTangSoLuong = """
                        UPDATE tai_lieu
                        SET so_luong = so_luong + ?
                        WHERE id = ? AND so_luong <= ?
                        """;

                try (PreparedStatement chiTiet =
                             connection.prepareStatement(sqlChiTiet);
                     PreparedStatement tangSoLuong =
                             connection.prepareStatement(sqlTangSoLuong)) {

                    for (ChiTietPhieuNhap dong : danhSach) {
                        if (dong.getSoLuong() <= 0
                                || dong.getDonGia() == null
                                || dong.getDonGia().signum() < 0) {
                            throw new SQLException("Dòng nhập không hợp lệ.");
                        }

                        chiTiet.setInt(1, phieuNhapId);
                        chiTiet.setInt(2, dong.getTaiLieuId());
                        chiTiet.setInt(3, dong.getSoLuong());
                        chiTiet.setBigDecimal(4, dong.getDonGia());
                        chiTiet.executeUpdate();

                        tangSoLuong.setInt(1, dong.getSoLuong());
                        tangSoLuong.setInt(2, dong.getTaiLieuId());
                        tangSoLuong.setInt(
                                3, Integer.MAX_VALUE - dong.getSoLuong()
                        );

                        if (tangSoLuong.executeUpdate() != 1) {
                            throw new SQLException(
                                    "Tài liệu không tồn tại hoặc số lượng vượt giới hạn."
                            );
                        }
                    }
                }

                connection.commit();
                return phieuNhapId;

            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    private int timNhanVienId(Connection connection, int taiKhoanId)
            throws SQLException {

        String sql = "SELECT id FROM nhan_vien WHERE tai_khoan_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, taiKhoanId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }

        throw new SQLException("Tài khoản chưa có hồ sơ nhân viên.");
    }

    private Integer timIdTheoMa(Connection connection, String ma)
            throws SQLException {

        String sql = "SELECT id FROM phieu_nhap WHERE ma_phieu_nhap = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, ma);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("id") : null;
            }
        }
    }

    public PhieuNhap timTheoId(int id) throws SQLException {
        String sql = """
                SELECT p.*, n.ten_nha_cung_cap, v.ho_ten
                FROM phieu_nhap p
                JOIN nha_cung_cap n ON n.id = p.nha_cung_cap_id
                JOIN nhan_vien v ON v.id = p.nhan_vien_id
                WHERE p.id = ?
                """;

        try (Connection connection = DBConnection.getConnection()) {
            PhieuNhap phieu;

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        return null;
                    }

                    phieu = new PhieuNhap();
                    phieu.setId(resultSet.getInt("id"));
                    phieu.setMaPhieuNhap(resultSet.getString("ma_phieu_nhap"));
                    phieu.setTenNhaCungCap(
                            resultSet.getString("ten_nha_cung_cap")
                    );
                    phieu.setTenNhanVien(resultSet.getString("ho_ten"));
                    phieu.setNgayNhap(resultSet.getTimestamp("ngay_nhap"));
                    phieu.setTrangThaiThanhToan(
                            resultSet.getString("trang_thai_thanh_toan")
                    );
                    phieu.setNgayThanhToan(
                            resultSet.getTimestamp("ngay_thanh_toan")
                    );
                }
            }

            String sqlChiTiet = """
                    SELECT c.tai_lieu_id, t.ma_tai_lieu, t.ten_tai_lieu,
                           c.so_luong, c.don_gia
                    FROM chi_tiet_phieu_nhap c
                    JOIN tai_lieu t ON t.id = c.tai_lieu_id
                    WHERE c.phieu_nhap_id = ?
                    ORDER BY c.id
                    """;

            try (PreparedStatement statement =
                         connection.prepareStatement(sqlChiTiet)) {
                statement.setInt(1, id);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        phieu.getChiTiet().add(new ChiTietPhieuNhap(
                                resultSet.getInt("tai_lieu_id"),
                                resultSet.getString("ma_tai_lieu"),
                                resultSet.getString("ten_tai_lieu"),
                                resultSet.getInt("so_luong"),
                                resultSet.getBigDecimal("don_gia")
                        ));
                    }
                }
            }

            return phieu;
        }
    }

    public void ghiNhanThanhToan(int id) throws SQLException {
        String sql = """
                UPDATE phieu_nhap
                SET trang_thai_thanh_toan = 'DA_THANH_TOAN',
                    ngay_thanh_toan = CURRENT_TIMESTAMP
                WHERE id = ?
                  AND trang_thai_thanh_toan = 'CHUA_THANH_TOAN'
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
}