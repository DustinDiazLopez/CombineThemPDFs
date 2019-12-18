package CombinePDF.databases;

import CombinePDF.History;
import CombinePDF.Main;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryDatabase {
    public static String path = Main.DATA;

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
            Connection conn = Database.connect(dbName);
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

    public static List<History> history(String dbName, String sortDESCorASC) {
        String sql = "SELECT * FROM history ORDER BY history_id " + sortDESCorASC;

        List<History> histories = new ArrayList<>();
        History temp;

        try {
            Connection conn = Database.connect(dbName);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                temp = new History(
                        rs.getInt("history_id"),
                        rs.getDate("created").toString(),
                        rs.getString("file_path")
                );

                histories.add(temp);
            }

            return histories;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
