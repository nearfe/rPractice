package com.conaxgames.register;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Marko on 17.12.2018.
 */
public class UserDatabase {

    @Getter
    private Connection connection;

    public void handleConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }

            Class.forName("com.mysql.jdbc.Driver");

            String username = "nearfe";
            String password = "nearfepvp";
            String database = "Seila";
            String host = "localhost";
            int port = 3306;

            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=yes&characterEncoding=UTF-8", username, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
