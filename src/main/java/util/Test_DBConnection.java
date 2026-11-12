package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Test_DBConnection {

    public static void main(String[] args) {
        String sql = """
                SELECT ma_tai_lieu, ten_tai_lieu
                FROM tai_lieu
                WHERE ten_tai_lieu LIKE ?
                ORDER BY ma_tai_lieu
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            System.out.println("Kết nối MySQL thành công!");
            System.out.println("Database: " + connection.getCatalog());

            statement.setString(1, "%Java%");

            try (ResultSet resultSet = statement.executeQuery()) {
                int count = 0;

                while (resultSet.next()) {
                    System.out.println(
                            resultSet.getString("ma_tai_lieu")
                                    + " - "
                                    + resultSet.getString("ten_tai_lieu")
                    );
                    count++;
                }

                System.out.println("Số tài liệu tìm thấy: " + count);
            }

        } catch (SQLException e) {
            System.err.println("Kiểm tra database thất bại.");
            e.printStackTrace();
        }
    }
}