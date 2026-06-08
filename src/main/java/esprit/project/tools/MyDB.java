package com.esprit.project.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyDB {
    private static final Logger LOGGER = Logger.getLogger(MyDB.class.getName());
    private static final String URL = "jdbc:mysql://localhost:3306/esprit";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private Connection connection;
    private static MyDB instance;

    private MyDB() {
        try {
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            LOGGER.info("Connection established successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to establish database connection", e);
        }
    }

    public static MyDB getInstance() {
        if (instance == null) {
            instance = new MyDB();
        }
        return instance;
    }

    public Connection getConnection() {
        return this.connection;
    }
}