package com.bibmovel.client.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by vinibrenobr11 on 18/09/2018 at 10:11
 */
public abstract class ConnectionFactory {

    private static Connection connection;

    public static Connection getSQLConnection() throws SQLException {

        if (connection.isValid(10))
            return connection;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        connection = DriverManager.getConnection("jdbc:mysql://192.168.0.100:3306/BibMovel"
                , "root", "1234");

        return connection;
    }
}
