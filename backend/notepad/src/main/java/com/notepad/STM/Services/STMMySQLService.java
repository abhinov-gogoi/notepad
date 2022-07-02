
package com.notepad.STM.Services;


import com.notepad.STM.Dao.STMMainMySQLDao;
import com.notepad.STM.util.STMLiteral;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class STMMySQLService {

    /**
     * Hold the Main mysql DAO
     */
    private STMMainMySQLDao dao_mysql;

    /**
     * Hold the Main service
     */
    private static STMMySQLService instance;

    /**
     * {@link Constructor}
     */
    private STMMySQLService() {
        dao_mysql = STMMainMySQLDao.getInstance();
    }

    /**
     * @return
     */
    public static STMMySQLService getInstance() {
        /**
         * Check for the Null
         */
        if (instance == null) {
            instance = new STMMySQLService();
        }
        return instance;
    }

    public boolean testMySQLConnection(String url, String username, String password) throws Exception {
        try {
            return dao_mysql.testMySQLConnection(url, username, password);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public List<String> getTables(String url, String username, String password) {
        List<String> tables = new ArrayList<>();
        List<Map<String, Object>> list_map = executeRawQuery("show tables", url, username, password);
        list_map.forEach(map ->{
            map.values().forEach(value -> tables.add(value.toString()));
        });
        return tables;
    }

    public List<Map<String, Object>> executeRawQuery(String sql, String url, String username, String password) {
        return dao_mysql.executeRawQuery(sql, url, username, password);
    }
}


