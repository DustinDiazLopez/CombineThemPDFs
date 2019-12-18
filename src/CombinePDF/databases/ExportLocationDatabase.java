package CombinePDF.databases;

import CombinePDF.Main;

import java.sql.*;

public class ExportLocationDatabase {
    private static String path = Main.DATA;

    public static void createExportLocationTable(String fileName) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + path + fileName;

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS export (\n" +
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

    public static void deleteExportLocationTable(String fileName) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + path + fileName;

        // SQL statement for creating a new table
        String sql = "DELETE FROM export";

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insert(String dbName, String path) {
        deleteExportLocationTable(dbName);
        Database.createDatabase(dbName);
        String sql = "INSERT INTO export(path) VALUES(?);";

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
        String sql = "SELECT * FROM export";

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
