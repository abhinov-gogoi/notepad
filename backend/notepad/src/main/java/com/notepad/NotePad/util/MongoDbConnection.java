package com.notepad.NotePad.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Objects;


public class MongoDbConnection {
    /**
     * Hold the class instance
     */
    private static MongoDbConnection instance;
    /**
     * Dynamic DB configuration
     */
    public static MongoClient MONGOCLIENT = mongoClient();
    /**
     * Hold the mongo_template instance
     */
    public static MongoTemplate mongo_template;

    public static MongoDbConnection getInstance() {
        /**
         * Check for the Null
         */
        if (instance == null) {
            instance = new MongoDbConnection();
        }
        return instance;
    }

    /**
     * Get mongo client
     */
    public static MongoClient mongoClient() {
        try {
			return MongoClients.create(MongoDBConnectionInfo.REMOTE_URI);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Mongotemplate
     */
    public MongoTemplate mongoTemplate() {
        try {
            if (mongo_template == null) {
                mongo_template = new MongoTemplate(Objects.requireNonNull(mongoClient()), MongoDBConnectionInfo.mongoDb_Databse);
            }
            return mongo_template;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Mongotemplate for any db name
     *
     * @return
     */
    public MongoTemplate mongoTemplate(String db_name) {
        try {
            return new MongoTemplate(Objects.requireNonNull(mongoClient()), db_name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
