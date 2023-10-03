package database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class DatabaseConnection {
    private static Connection CONNECTION;

    private static volatile DatabaseConnection INSTANCE;

    private DatabaseConnection() {
    }

    public static DatabaseConnection getInstance() {
        synchronized (DatabaseConnection.class) {
            if (INSTANCE == null) {
                INSTANCE = new DatabaseConnection();
                CONNECTION = getDatabaseConnection();
            }
            return INSTANCE;
        }
    }

    public static DatabaseConnection getInstance(String filePath) {
        synchronized (DatabaseConnection.class) {
            if (INSTANCE == null) {
                INSTANCE = new DatabaseConnection();
                CONNECTION = getDatabaseConnection(filePath);
            }
            return INSTANCE;
        }
    }


    public Connection getCONNECTION() {
        return CONNECTION;
    }

    private static Connection getDatabaseConnection() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            return DriverManager.getConnection(dbUrl());
        } catch (SQLException | ClassNotFoundException e) {
            return null;
        }
    }

    private static Connection getDatabaseConnection(String filePath) {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            return DriverManager.getConnection(dbUrl(filePath));
        } catch (SQLException | ClassNotFoundException e) {
            return null;
        }
    }

    private static String dbUrl() {
        try {
            List<Path> list = Files.walk(Path.of(System.getProperties().getProperty("user.dir")), 4, FileVisitOption.FOLLOW_LINKS)
                    .toList();
            for (Path path : list) {
                if (path.endsWith("application.properties")) {
                    FileReader fileReader = new FileReader(path.toFile());
                    BufferedReader br = new BufferedReader(fileReader);
                    String line = br.readLine();
                    while (line != null) {
                        line = line.strip();
                        if (line.startsWith("oracle.connection.url=")) {
                            return line.substring("oracle.connection.url=".length()).trim();
                        }
                        line = br.readLine();
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Require file with name : application.properties");
        }
        throw new RuntimeException("File not found!");
    }

    private static String dbUrl(String filePath) {
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fileReader);
            String line = br.readLine();
            while (line != null) {
                line = line.strip();
                if (line.startsWith("oracle.connection.url=")) {
                    return line.substring("oracle.connection.url=".length()).trim();
                }
                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Require file with name : application.properties");
        }
        throw new RuntimeException("File not found!");
    }

}
