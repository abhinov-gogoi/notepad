
package com.notepad.STM.Services;


import com.notepad.STM.Dao.STMMainMySQLDao;
import com.notepad.STM.util.STMLiteral;

import java.lang.reflect.Constructor;

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
}


