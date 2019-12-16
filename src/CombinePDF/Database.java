package CombinePDF;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Database {
    private static String path;

    /**
     * Gets the connection to the database
     * @param fileName name of the database
     * @return returns the connection to the database
     */
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

    /**
     * Creates the database
     * @param fileName name of the database
     */
    public static void createDatabase(String fileName) {
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

    /**
     * Creates the history table
     * @param fileName name of the database
     */
    public static void createHistoryTable(String fileName) {
        String url = "jdbc:sqlite:" + path + fileName;

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS history (\n" +
                "  history_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "  created DATETIME NOT NULL,\n" +
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

    public static void insert(String dbName, String paths) {
        String sql = "INSERT INTO history(created, file_path) VALUES(?,?)";

        try {
            Connection conn = connect(dbName);
            PreparedStatement fileStatement = conn.prepareStatement(sql);
            fileStatement.setDate(1, new Date(System.currentTimeMillis()));
            fileStatement.setString(2, paths);
            fileStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *
     * @param fileName name of the database
     */
    public static void deleteHistoryTable(String fileName) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + path + fileName;

        // SQL statement for creating a new table
        String sql = "DELETE FROM history";

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        createHistoryTable(fileName);
    }

    public static List<History> history(String dbName) {
        String sql = "SELECT * FROM history;";

        List<History> histories = new ArrayList<>();
        History temp;

        try {
            Connection conn = connect(dbName);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                temp = new History(rs.getInt("history_id"),
                        rs.getDate("created").toString(),
                        rs.getString("file_path"));

                histories.add(temp);

                System.out.println( "From DB: " +
                        rs.getInt("history_id") + "\t" +
                        rs.getDate("created") + "\t" +
                        rs.getString("file_path")
                );

            }

            return histories;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

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
        createDatabase(dbName);
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

    /**
     * Returns a String array of the saved screen size which was last used
     * @param dbName name of the database
     * @return a string array of the screen size [width, height]
     */
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

}
