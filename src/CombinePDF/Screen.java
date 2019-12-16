package CombinePDF;

import java.io.File;
import java.sql.*;

public class Screen {
    private static String path;

    public static void main(String[] args) {
//        path = new File("").getAbsolutePath();
//
//        if (path.contains("\\")) path += "\\";
//        else path += "/";
//
//        String database = "screen";
//        createNewDatabase(database);
//        connect(database);
//        createNewTable(database);
//
//        Database app = new Database();
//        insert(database, 500d, 375d);
//
//        app.selectAll(database);
    }

    private static Connection connect(String fileName) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + path + fileName;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createScreenDatabase(String fileName) {
        String url = "jdbc:sqlite:" + path + fileName;

        try {
            Connection conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println(meta.getDriverName());
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteScreenTable(String fileName) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + path + fileName;

        // SQL statement for creating a new table

        String sql = "delete from dimensions";

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createScreenTable(String fileName) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + path + fileName;

        // SQL statement for creating a new table

        String sql = "CREATE TABLE IF NOT EXISTS dimensions (\n" +
                "  width double NOT NULL,\n" +
                "  height double  NOT NULL\n" +
                ");";

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void selectAll(String dbName) {
        String sql = "SELECT * FROM dimensions";

        try {
            Connection conn = connect(dbName);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getDouble("width") + "\t" +
                        rs.getDouble("height"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String[] screen(String dbName) {
        String sql = "SELECT * FROM dimensions";

        try {
            Connection conn = connect(dbName);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            String w = "";
            String h = "";

            while (rs.next()) {
                w = rs.getDouble("width") + "";
                h = rs.getDouble("height") + "";
            }

            return new String[] {w, h};
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return new String[]{};
    }

    public static void insert(String dbName, double width, double height) {
        deleteScreenTable(dbName);
        createScreenDatabase(dbName);
        String sql = "INSERT INTO dimensions(width, height) VALUES(?,?);";

        try {
            Connection conn = connect(dbName);
            PreparedStatement fileStatement = conn.prepareStatement(sql);
            fileStatement.setDouble(1, width);
            fileStatement.setDouble(2, height);
            fileStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
