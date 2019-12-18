package CombinePDF.databases;

import CombinePDF.Main;

import java.sql.*;

public class ScreenDatabase {
    private static String path = Main.DATA;

    /**
     * Created the screen table in the database if it exists
     * @param fileName name of the database
     */
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

    /**
     * TODO: Something more efficient (redesign the database)
     * Deletes the saved screen table (dimensions) to reset the values
     * @param fileName name of the database
     */
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

    /**
     * Sets the last screen size used in the database
     * @param dbName name of the database
     * @param width width of the application window
     * @param height height of the application window
     */
    public static void insert(String dbName, double width, double height) {
        deleteScreenTable(dbName);
        Database.createDatabase(dbName);
        String sql = "INSERT INTO dimensions(width, height) VALUES(?,?);";

        try {
            Connection conn = Database.connect(dbName);
            PreparedStatement fileStatement = conn.prepareStatement(sql);
            fileStatement.setDouble(1, width);
            fileStatement.setDouble(2, height);
            fileStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Returns a String array of the saved screen size which was last used
     * @param dbName name of the database
     * @return a string array of the screen size [width, height]
     */
    public static String[] screen(String dbName) {
        String sql = "SELECT * FROM dimensions";

        try {
            Connection conn = Database.connect(dbName);
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

}
