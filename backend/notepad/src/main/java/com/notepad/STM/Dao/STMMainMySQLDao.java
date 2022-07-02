package com.notepad.STM.Dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class STMMainMySQLDao {

    /**
     * Call constructor
     */
    public STMMainMySQLDao() {

    }

    private static STMMainMySQLDao instance;

    /**
     * @return instance of this class
     */
    public static STMMainMySQLDao getInstance() {
        if (instance == null) {
            instance = new STMMainMySQLDao();
        }
        return instance;
    }

    /**
     * This method is used to check mysql is up or down.
     */
    public boolean testMySQLConnection(String url, String username, String password) throws SQLException {
        // Open a connection
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://"+url, username, password)) {
             return conn.isValid(20000);
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }
}
