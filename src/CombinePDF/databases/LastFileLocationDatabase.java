package CombinePDF.databases;

import CombinePDF.Main;

import java.sql.*;

public class LastFileLocationDatabase {
    private static String path = Main.DATA;
    private static String TABLE_NAME = "file_location";

    public static void createLastFileLocationTable(String fileName) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + path + fileName;

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(\n" +
                "  path TEXT NOT NULL\n" +
                ");";

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteLastFileLocationTable(String fileName) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + path + fileName;

        // SQL statement for creating a new table
        String sql = "DELETE FROM " + TABLE_NAME;

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insert(String dbName, String path) {
        deleteLastFileLocationTable(dbName);
        Database.createDatabase(dbName);
        String sql = "INSERT INTO " + TABLE_NAME + "(path) VALUES(?);";

        try {
            Connection conn = Database.connect(dbName);
            PreparedStatement fileStatement = conn.prepareStatement(sql);
            fileStatement.setString(1, path);
            fileStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String location(String dbName) {
        String sql = "SELECT * FROM " + TABLE_NAME;

        try {
            Connection conn = Database.connect(dbName);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            String path = "";

            while (rs.next()) {
                path = rs.getString("path");
            }

            return path;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

}
