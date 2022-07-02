package com.notepad.STM.Dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.result.UpdateResult;
import com.notepad.STM.util.STMLiteral;
import com.notepad.STM.util.STMMongoDBConnectionInfo;
import com.notepad.STM.util.STMMongoDbConnection;
import com.notepad.STM.util.STMUtility;
import org.bson.Document;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class STMMainMongoDao {
    /**
     * mongo template for mongo connection
     */
    private MongoTemplate mongoTemplate;
    /**
     * Create object mapper instance
     */
    ObjectMapper mapper = new ObjectMapper();

    /**
     * Call constructor
     */
    public STMMainMongoDao() {
        /**
         * Get mongo template connection
         */
        mongoTemplate = STMMongoDbConnection.getInstance().mongoTemplate();
    }

    /**
     * Class instance
     */
    private static STMMainMongoDao instance;

    /**
     * @return instance of this class
     */
    public static STMMainMongoDao getInstance() {
        /**
         * Check for null
         */
        if (instance == null) {
            instance = new STMMainMongoDao();
        }
        return instance;
    }

    /**
     * This method is used to check mongo is up or down.
     *
     * @return
     */
    public boolean testMongoDbUriConnection(String uri, String database) {
        try {
            MongoTemplate testMongoTemplate = new MongoTemplate(STMMongoDbConnection.mongoClientURI(uri), database);
            testMongoTemplate.dropCollection(STMMongoDBConnectionInfo.test_col);
            /**
             * try to create a collection in this db
             */
            testMongoTemplate.createCollection(STMMongoDBConnectionInfo.test_col);
            /**
             * return true if collection exists
             */
            if (testMongoTemplate.collectionExists(STMMongoDBConnectionInfo.test_col)) {
                /**
                 * delete the collection
                 */
                testMongoTemplate.dropCollection(STMMongoDBConnectionInfo.test_col);
                /**
                 * mongo is up, return true
                 */
                return STMLiteral.BOOLEAN_TRUE;
            } else {
                return STMLiteral.BOOLEAN_FALSE;
            }
        } catch (Exception e) {
            STMUtility.printStackTrace(e, this.getClass().getName());
            return STMLiteral.BOOLEAN_FALSE;
        }
    }

    /**
     * Generic method to insert a document into mongo collection.
     *
     * @param document
     * @param collection
     * @return
     */
    public boolean insertDocument(final Document document, final String collection) {
        try {
            mongoTemplate.insert(document, collection);
            return STMLiteral.BOOLEAN_TRUE;
        } catch (DuplicateKeyException de) {
            return STMLiteral.BOOLEAN_FALSE;
        } catch (Exception e) {
            STMUtility.printStackTrace(e, this.getClass().getName());
            return STMLiteral.BOOLEAN_FALSE;
        }
    }

    /**
     * Generic method to find a documents in a mongo collection with query.
     *
     * @param collection MongoDb Collection name for this search
     * @return
     */
    public List<Map<String, Object>> getDocuments(final Query query, final String collection) {
        try {
            /**
             * Get the data
             */
            return new ArrayList<Map<String, Object>>(mongoTemplate.find(query, Document.class, collection));
        } catch (Exception e) {
            STMUtility.printStackTrace(e, this.getClass().getName());
            return STMLiteral.EMPTY_ARRAYLIST;
        }
    }

    /**
     * Generic method to find a documents in a mongo collection with filter.
     *
     * @param search_doc Search/Projection options
     * @param filter_doc Filter options
     * @param collection MongoDb Collection name for this search
     * @return
     */
    public List<Map<String, Object>> getDocuments(final Document filter_doc, final String collection) {
        return getDocuments(filter_doc, null, collection);
    }

    /**
     * Generic method to find a documents in a mongo collection with filter and projection.
     *
     * @param filter_doc Filter options
     * @param search_doc Search/Projection options
     * @param collection MongoDb Collection name for this search
     * @return
     */
    public List<Map<String, Object>> getDocuments(final Document filter_doc, final Document search_doc, final String collection) {
        try {
            Query query;
            if (search_doc == null || search_doc.isEmpty()) {
                query = new BasicQuery(filter_doc);
            } else {
                query = new BasicQuery(filter_doc, search_doc);
            }
            /**
             * Get the data
             */
            return new ArrayList<Map<String, Object>>(mongoTemplate.find(query, Document.class, collection));
        } catch (Exception e) {
            STMUtility.printStackTrace(e, this.getClass().getName());
            return STMLiteral.EMPTY_ARRAYLIST;
        }
    }

    /**
     * Generic method to find a documents in a mongo collection with Pagination.
     * By default, sort by creation time in descending
     *
     * @param filter_doc Filter options
     * @param search_doc Search/Projection options
     * @param collection MongoDb Collection name for this search
     * @param count      page start count /page index
     * @param r_count    the size of the page to be returned.
     */
    public List<Map<String, Object>> getDocuments(final Document filter_doc, final Document search_doc, final String collection, int count, int r_count,String order_key) {
        try {
            Query query;
            if (search_doc == null || search_doc.isEmpty()) {
                query = new BasicQuery(filter_doc);
            } else {
                query = new BasicQuery(filter_doc, search_doc);
            }
            /**
             * add pagination query into main query
             */
            query.with(PageRequest.of(count, r_count, Sort.by(Sort.Direction.DESC, order_key)));
            /**
             * Get the data
             */
            return new ArrayList<Map<String, Object>>(mongoTemplate.find(query, Document.class, collection));
        } catch (Exception e) {
            STMUtility.printStackTrace(e, this.getClass().getName());
            return new ArrayList<Map<String, Object>>();
        }
    }
    /**
     * Generic method to find a documents in a mongo collection with Pagination.
     * By default, sort by creation time in descending
     *
     * @param filter_doc Filter options
     * @param search_doc Search/Projection options
     * @param collection MongoDb Collection name for this search
     */
    public List<Map<String, Object>> getDocuments(final Document filter_doc, final Document search_doc, final String collection,final String order_key,final String order) {
        try {
            Query query;
            if (search_doc == null || search_doc.isEmpty()) {
                query = new BasicQuery(filter_doc);
            } else {
                query = new BasicQuery(filter_doc, search_doc);
            }
            /**
             * 
             */
            if(order.equalsIgnoreCase(STMLiteral.ASC)) {
            	/**
                 * add pagination query into main query
                 */
                query.with (Sort.by(Sort.Direction.ASC, order_key));
            }else {
            	/**
                 * add pagination query into main query
                 */
                query.with (Sort.by(Sort.Direction.DESC, order_key));
            }
            
            /**
             * Get the data
             */
            return new ArrayList<Map<String, Object>>(mongoTemplate.find(query, Document.class, collection));
        } catch (Exception e) {
            STMUtility.printStackTrace(e, this.getClass().getName());
            return new ArrayList<Map<String, Object>>();
        }
    }

    /**
     * Generic method to update a document into mongo collection with query.
     * Update will NOT insert new document, if document is not found
     *
     * @param query
     * @param update
     * @param collection
     * @return
     */
    public boolean updateDocument(final Query query, final Update update, final String collection) {
        try {
            /**
             * update data in MONGODB
             */
            UpdateResult upd = mongoTemplate.updateFirst(query, update, collection);
            return upd.wasAcknowledged();
        } catch (Exception e) {
            STMUtility.printStackTrace(e, this.getClass().getName());
            return STMLiteral.BOOLEAN_FALSE;
        }
    }

    /**
     * Generic method to update a document into mongo collection with filter.
     * Update will NOT insert new document, if document is not found
     *
     * @param query
     * @param update
     * @param collection
     * @return
     */
    public boolean updateDocument(final Document filter_doc, final Update update, final String collection) {
        try {
            Query query = new BasicQuery(filter_doc);
            /**
             * update data in MONGODB
             */
            UpdateResult upd = mongoTemplate.updateFirst(query, update, collection);
            return upd.wasAcknowledged();
        } catch (Exception e) {
            STMUtility.printStackTrace(e, this.getClass().getName());
            return STMLiteral.BOOLEAN_FALSE;
        }
    }

    /**
     * Generic method to upsert a document into mongo collection.
     * Upsert will insert new document if not found (update+insert)
     *
     * @param query
     * @param update
     * @param collection
     * @return
     */
    public boolean upsertDocument(final Query query, final Update update, final String collection) {
        try {
            /**
             * upsert data in MONGODB
             */
            UpdateResult upd = mongoTemplate.upsert(query, update, collection);
            return upd.wasAcknowledged();
        } catch (Exception e) {
            STMUtility.printStackTrace(e, this.getClass().getName());
            return STMLiteral.BOOLEAN_FALSE;
        }
    }


    /**
     * Generic method to find a documents in a mongo collection with query.
     *
     * @param search_doc Search/Projection options
     * @param filter_doc Filter options
     * @param collection MongoDb Collection name for this search
     * @return
     */
    public Map<String, Object> getDocument(final Query query, final String collection) {
        try {
            /**
             * Get the data
             */
            return mongoTemplate.findOne(query, Document.class, collection);
        } catch (Exception e) {
            STMUtility.printStackTrace(e, this.getClass().getName());
            return STMLiteral.EMPTY_HASHMAP;
        }
    }

    /**
     * Generic method to find a documents in a mongo collection with filter and search doc.
     *
     * @param search_doc Search/Projection options
     * @param filter_doc Filter options
     * @param collection MongoDb Collection name for this search
     * @return
     */
    public Map<String, Object> getDocument(final  Document filter_doc, final Document srch_doc, final String collection) {
        try {
            Query query;
            if(srch_doc!=null) {
                query = new BasicQuery(filter_doc, srch_doc);
            } else {
                query = new BasicQuery(filter_doc);
            }
            /**
             * Get the data
             */
            return mongoTemplate.findOne(query, Document.class, collection);
        } catch (Exception e) {
            STMUtility.printStackTrace(e, this.getClass().getName());
            return STMLiteral.EMPTY_HASHMAP;
        }
    }

    /**
     * Generic method to delete documents in a mongo collection with query.
     *
     * @param search_doc Search/Projection options
     * @param filter_doc Filter options
     * @param collection MongoDb Collection name for this search
     * @return
     */
    public boolean deleteDocuments(final Query query, final String collection) {
        try {
            /**
             * Get the data
             */
            return mongoTemplate.remove(query, collection).wasAcknowledged();
        } catch (Exception e) {
            STMUtility.printStackTrace(e, this.getClass().getName());
            return STMLiteral.BOOLEAN_FALSE;
        }
    }


}
