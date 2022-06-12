
package com.notepad.Services;

import com.notepad.Dao.MainMongoDao;
import com.notepad.util.Literal;
import com.notepad.util.MongoDBConnectionInfo;
import com.notepad.util.Utility;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


import java.lang.reflect.Constructor;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoServices {
    /**
     * Hold the Main mongo DAO
     */
    private MainMongoDao dao_mongo;
    /**
     * Hold the Main service
     */
    private static MongoServices instance;

    /**
     * {@link Constructor}
     */
    private MongoServices() {
        dao_mongo = MainMongoDao.getInstance();
    }

    /**
     * @return
     */
    public static MongoServices getInstance() {
        /**
         * Check for the Null
         */
        if (instance == null) {
            instance = new MongoServices();
        }
        return instance;
    }

    /**
     * @return
     */
    public boolean testMongoConnection() {
        try {
            return dao_mongo.testMongoConnection();
        } catch (Exception e) {
            return Literal.BOOLEAN_FALSE;
        }
    }

    public boolean populateMongo() {
        try {

            return Literal.BOOLEAN_TRUE;
        } catch (Exception e) {
            return Literal.BOOLEAN_FALSE;
        }
    }

    public boolean createUpdateProjectDetails(final String project_id,
                                              final String project_name,
                                              final String project_description,
                                              final String icon_class_name) {
        try {
            /**
             * Get the existing doc
             */
            Document filter_doc = new Document(Literal._id, project_id);
            /**
             * Update the doc
             */
            Update update = new Update();
            update.set(Literal.name, project_name.trim());
            update.set(Literal.icon, icon_class_name.trim());
            if (!Utility.chkNullObj(project_description.trim())) {
                update.set(Literal.description, project_description.trim());
            }
            long curr_time = Utility.getCurrentMilli();
            update.set(Literal.collapsed, true);
            update.set(Literal.update_time_ms, new Timestamp(curr_time));
            update.set(Literal.update_time, curr_time);
            /**
             * update the collection
             */
            return dao_mongo.upsertDocument(new BasicQuery(filter_doc), update, MongoDBConnectionInfo.project_info);
        } catch (Exception e) {
            Utility.printStackTrace(e, this.getClass().getName());
            return Literal.BOOLEAN_FALSE;
        }
    }

    public List<Map<String, Object>> getProjectDetails(final String id) {
        try {
            /**
             * create the filter doc
             */
            Document filter_doc = new Document();
            if (id.equals(Literal.ALL)) {
            } else {
                filter_doc = new Document(Literal._id, id);
            }
            /**
             * search doc
             */
            Document srch_doc = new Document();
            srch_doc.append(Literal._id, Literal.ONE_INT);
            srch_doc.append(Literal.name, Literal.ONE_INT);
            srch_doc.append(Literal.description, Literal.ONE_INT);
            srch_doc.append(Literal.icon, Literal.ONE_INT);
            srch_doc.append(Literal.collapsed,  Literal.ONE_INT);
            /**
             * get the collection
             */
            List<Map<String, Object>> projects = dao_mongo.getDocuments(filter_doc, srch_doc, MongoDBConnectionInfo.project_info);
            for (Map<String, Object> map : projects) {
                map.put(Literal.sections, getAllSectionsForProject(map.get(Literal._id).toString()));
            }
            return projects;
        } catch (Exception e) {
            Utility.printStackTrace(e, this.getClass().getName());
            return Literal.EMPTY_ARRAYLIST;
        }
    }

    public Map<String, Object> getProjectContent(final String project_id) {
        try {
            /**
             * create the filter doc
             */
            Document filter_doc = new Document(Literal._id, project_id);
            /**
             * search doc
             */
            Document srch_doc = new Document();
            srch_doc.append(Literal._id, Literal.ONE_INT);
            srch_doc.append(Literal.name, Literal.ONE_INT);
            srch_doc.append(Literal.description, Literal.ONE_INT);
            srch_doc.append(Literal.icon, Literal.ONE_INT);
            srch_doc.append(Literal.collapsed,  Literal.ONE_INT);
            /**
             * get the collection
             */
            Map<String, Object> project = dao_mongo.getDocument(filter_doc, srch_doc, MongoDBConnectionInfo.project_info);
            Map<String, Object> result = new HashMap<>(project);
            result.put(Literal.sections, getSectionDetails(project_id));
            return result;
        } catch (Exception e) {
            Utility.printStackTrace(e, this.getClass().getName());
            return Literal.EMPTY_HASHMAP;
        }
    }

    private List<Map<String, Object>> getAllSectionsForProject(String project_id) {
        try {
            /**
             * create the filter doc
             */
            Document filter_doc = new Document().append(Literal.project_id, project_id);
            /**
             * Search doc
             */
            Document srch_doc = new Document();
            srch_doc.append(Literal._id, Literal.ONE_INT);
            srch_doc.append(Literal.sequence, Literal.ONE_INT);
            srch_doc.append(Literal.name, Literal.ONE_INT);
            /**
             * make the query
             */
            Query qry= new BasicQuery(filter_doc, srch_doc).with(Sort.by(Sort.Direction.ASC, Literal.sequence));

            return dao_mongo.getDocuments(qry, MongoDBConnectionInfo.sections);
        } catch (Exception e) {
            Utility.printStackTrace(e, this.getClass().getName());
            return Literal.EMPTY_ARRAYLIST;
        }
    }

    public boolean createUpdateSectionDetails(String project_id,
                                              String section_id,
                                              String sequence,
                                              String name,
                                              String content) {
        try {
            /**
             * Get the existing doc
             */
            Document filter_doc = new Document(Literal._id, section_id);
            /**
             * Update the doc
             */
            Update update = new Update();
            update.set(Literal.project_id, project_id.trim());
            update.set(Literal.sequence, Integer.parseInt(sequence.trim()));
            update.set(Literal.name, name.trim());
            update.set(Literal.editMode, false);
            if (!Utility.chkNullObj(content)) {
                update.set(Literal.content, content.trim());
            }
            long curr_time = Utility.getCurrentMilli();
            update.set(Literal.update_time_ms, new Timestamp(curr_time));
            update.set(Literal.update_time, curr_time);
            /**
             * update the collection
             */
            return dao_mongo.upsertDocument(new BasicQuery(filter_doc), update, MongoDBConnectionInfo.sections);
        } catch (Exception e) {
            Utility.printStackTrace(e, this.getClass().getName());
            return Literal.BOOLEAN_FALSE;
        }
    }
    public List<Map<String, Object>> getSectionDetails(String project_id) {
        try {
            /**
             * create the filter doc
             */
            Document filter_doc = new Document();
            filter_doc.append(Literal.project_id, project_id);
            /**
             * Query
             */
            Query qry =new BasicQuery(filter_doc).with(Sort.by(Sort.Direction.ASC,  Literal.sequence));
            /**
             * get the collection
             */
            return dao_mongo.getDocuments(qry, MongoDBConnectionInfo.sections);
        } catch (Exception e) {
            Utility.printStackTrace(e, this.getClass().getName());
            return Literal.EMPTY_ARRAYLIST;
        }
    }

    public boolean deleteSection(String section_id) {
        try {
            /**
             * create the filter doc
             */
            Document filter_doc = new Document();
            filter_doc.append(Literal._id, section_id);
            /**
             * Query
             */
            Query qry = new BasicQuery(filter_doc);
            /**
             * get the collection
             */
            return dao_mongo.deleteDocuments(qry, MongoDBConnectionInfo.sections);
        } catch (Exception e) {
            Utility.printStackTrace(e, this.getClass().getName());
            return Literal.BOOLEAN_FALSE;
        }
    }

    public boolean deleteProject(String project_id) {
        try {
            /**
             * create the filter doc for project_info col
             */
            Document filter_doc_project_info = new Document(Literal._id, project_id);
            /**
             * Query
             */
            Query qry_project_id = new BasicQuery(filter_doc_project_info);

            /**
             * create the filter doc for section col
             */
            Document filter_doc_section = new Document(Literal.project_id, project_id);
            /**
             * Query
             */
            Query qry_section = new BasicQuery(filter_doc_section);
            /**
             * delete
             */
            return (dao_mongo.deleteDocuments(qry_project_id, MongoDBConnectionInfo.project_info) &&
                    dao_mongo.deleteDocuments(qry_section, MongoDBConnectionInfo.sections));
        } catch (Exception e) {
            Utility.printStackTrace(e, this.getClass().getName());
            return Literal.BOOLEAN_FALSE;
        }
    }

    public boolean updateSectionSequence(String section_id, int sequence) {
        try {
            /**
             * Get the existing doc
             */
            Document filter_doc = new Document(Literal._id, section_id);
            /**
             * Update the doc
             */
            Update update = new Update();
            update.set(Literal.sequence, sequence);
            /**
             * update the collection
             */
            return dao_mongo.updateDocument(new BasicQuery(filter_doc), update, MongoDBConnectionInfo.sections);
        } catch (Exception e) {
            Utility.printStackTrace(e, this.getClass().getName());
            return Literal.BOOLEAN_FALSE;
        }
    }
}
