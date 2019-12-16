package CombinePDF.Databases;

import CombinePDF.Main;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static String path = new File("").getAbsolutePath() + (new File("").getAbsolutePath().contains("\\") ? "\\src\\data\\" : "/src/data/");

    /**
     * Gets the connection to the database
     * @param fileName name of the database
     * @return returns the connection to the database
     */
    static Connection connect(String fileName) {
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
                Main.setLog(meta.getDriverName() + " - " + fileName + " database created or already exists.\n");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
