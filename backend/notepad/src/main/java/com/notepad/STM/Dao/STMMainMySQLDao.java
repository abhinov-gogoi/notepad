package com.notepad.STM.Dao;

import com.google.gson.Gson;
import com.notepad.STM.util.STMUtility;

import java.sql.*;
import java.util.List;
import java.util.Map;

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
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://" + url, username, password)) {
            return conn.isValid(20000);
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }


    public List<Map<String, Object>> executeRawQuery(String sql, String url, String username, String password) {
        // Open a connection
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            // Extract data from result set
            return STMUtility.resultSetToArrayList(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
