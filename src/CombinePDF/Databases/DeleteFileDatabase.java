package CombinePDF.Databases;

import CombinePDF.History;

import java.io.File;
import java.sql.*;
import java.util.*;

public class DeleteFileDatabase {
    public static String path = new File("").getAbsolutePath() + (new File("").getAbsolutePath().contains("\\") ? "\\src\\data\\" : "/src/data/");

    public static void createTable(String fileName) {
        String url = "jdbc:sqlite:" + path + fileName;

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS file (\n" +
                "  file_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "  file_path TEXT NOT NULL\n" +
                ");";

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insert(String dbName, String path) {
        String sql = "INSERT INTO file(file_path) VALUES(?)";

        try {
            Connection conn = Database.connect(dbName);
            PreparedStatement fileStatement = conn.prepareStatement(sql);
            fileStatement.setString(1, path);
            fileStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteTable(String fileName) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + path + fileName;

        // SQL statement for creating a new table
        String sql = "DELETE FROM file";

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        createTable(fileName);
    }

    public static HashSet<String> files(String dbName) {
        String sql = "SELECT * FROM file;";

        HashSet<String> files = new HashSet<>();

        try {
            Connection conn = Database.connect(dbName);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                files.add(rs.getString("file_path"));
                System.out.println(rs.getInt("file_id") + "\t" +
                        rs.getString("file_path"));
            }

            return files;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
