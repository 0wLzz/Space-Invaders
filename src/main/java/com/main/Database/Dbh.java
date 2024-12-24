package com.main.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Dbh {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/spaceinvaders";
        String user = "root";
        String password = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }

        return DriverManager.getConnection(url, user, password);
    }
}
