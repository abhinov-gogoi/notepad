
package com.notepad.STM.Services;

import com.notepad.STM.Dao.STMMainMongoDao;
import com.notepad.STM.util.STMLiteral;

import java.lang.reflect.Constructor;

public class STMMongoServices {
    /**
     * Hold the Main mongo DAO
     */
    private STMMainMongoDao dao_mongo;
    /**
     * Hold the Main service
     */
    private static STMMongoServices instance;

    /**
     * {@link Constructor}
     */
    private STMMongoServices() {
        dao_mongo = STMMainMongoDao.getInstance();
    }

    /**
     * @return
     */
    public static STMMongoServices getInstance() {
        /**
         * Check for the Null
         */
        if (instance == null) {
            instance = new STMMongoServices();
        }
        return instance;
    }

    public boolean testMongoDbUriConnection(String uri, String database) {
        try {
            return dao_mongo.testMongoDbUriConnection(uri, database);
        } catch (Exception e) {
            return STMLiteral.BOOLEAN_FALSE;
        }
    }
}
