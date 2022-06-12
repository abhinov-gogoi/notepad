package com.notepad.Controller;

import com.notepad.Services.ApiGatwayService;
import com.notepad.Services.MongoServices;
import com.notepad.util.Literal;
import com.notepad.util.Utility;
import com.notepad.util.WebHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class DocBookController {

    @Autowired
    private HttpServletRequest request;

    /**
     * hold the mainService
     */
    MongoServices mongoServices;

    /**
     * constructor
     */
    public DocBookController() {
        mongoServices = MongoServices.getInstance();
    }


    /**
     * test API
     */
    @RequestMapping(value = WebHook.apiTest, method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE, headers = "Accept=application/json")
    public String apiTest() {
        return "OK";
    }

    /**
     * test API for mongo connection
     *
     * @return
     */
    @RequestMapping(value = WebHook.mongoTest, method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE, headers = "Accept=application/json")
    public String mongoTest() {
        try {
            if (mongoServices.testMongoConnection()) {
                return "Mongo is up" + request.getRemoteHost();
            } else {
                return "Mongo is down" + request.getRemoteHost();
            }
        } catch (Exception e) {
            return "Something went wrong " + e.getMessage();
        }
    }


    /**
     * @param rqst_map {
     *                 "id" : "fh387f3h3n9q8d.c2c2bc",
     *                 "name": "UHES",
     *                 "sequence" : 2
     *                 "description": "uhes desc",
     *                 "icon": "mdi mdi-application",
     *                 }
     *
     *                 <table border=1px>
     *                 <tr>
     *                 <th>Key</th>
     *                 <th>Sample Data</th>
     *                 <th>Data Type</th>
     *                 <th>Constraint</th>
     *                 <th>Description</th>
     *                 </tr>
     *                 <tr>
     *                 <td>id</td>
     *                 <td>fh387f3h3n9q8d.c2c2bc</td>
     *                 <td>String</td>
     *                 <td>Mandatory</td>
     *                 <td>Id of the project</td>
     *                 </tr>
     *                 <tr>
     *                 <td>name</td>
     *                 <td>UHES</td>
     *                 <td>String</td>
     *                 <td>Mandatory</td>
     *                 <td>Name of the project</td>
     *                 </tr>
     *                 <tr>
     *                 <td>description</td>
     *                 <td>uhes is smart meter project</td>
     *                 <td>String</td>
     *                 <td>Optional</td>
     *                 <td>description for the project</td></td>
     *                 </tr>
     *                 <tr>
     *                 <td>icon</td>
     *                 <td>mdi mdi-application</td>
     *                 <td>String</td>
     *                 <td>Optional</td>
     *                 <td>icon class name for this project</td></td>
     *                 </tr>
     *                 </table>
     * @param Headers  <table border=1px>
     *                                                                 <tr>
     *                                                                 <th>Header Key</th>
     *                                                                 <th>Sample Data</th>
     *                                                                 <th>Data Type</th>
     *                                                                 <th>Constraint</th>
     *                                                                 <th>Description</th>
     *                                                                 </tr>
     *                                                                 <tr>
     *                                                                 <td>User-Agent</td>
     *                                                                 <td>Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36</td>
     *                                                                 <td>String</td>
     *                                                                 <td>Mandatory</td>
     *                                                                 <td>user agent</td>
     *                                                                 </tr>
     *                                                                 <tr>
     *                                                                 <td>Content-Type</td>
     *                                                                 <td>application/json</td>
     *                                                                 <td>String</td>
     *                                                                 <td>Mandatory</td>
     *                                                                 <td>Request content type.</td>
     *                                                                 </tr>
     *                                                                 <tr>
     *                                                                 <td>api_gateway</td>
     *                                                                 <td>NOTEPAD</td>
     *                                                                 <td>String</td>
     *                                                                 <td>Mandatory</td>
     *                                                                 <td>Hold the Token value.</td>
     *                                                                 </tr>
     *                                                                 </table>
     *                                                                 <br>
     * @see <b>Functionality: </b> This API is used to create/ update project name/description in docbook
     * <br>
     * <b>SUCCESS MESSAGE:</b> <br>
     *
     * <b>ERROR MESSAGE:</b>
     */
    @RequestMapping(value = WebHook.createUpdateProjectDetails, method = RequestMethod.POST, headers = Literal.APPLICATION_JSON)
    public Object createUpdateProjectDetails(@RequestBody Map<String, Object> rqst_map) {
        Map<String, Object> ret_map = new HashMap<String, Object>(Literal.SIX);
        try {
            long s1 = System.currentTimeMillis();
            /**
             * Hold the token values
             */
            Map<String, Object> map_gateway = ApiGatwayService.getInstance()
                    .checkApiGateway(request.getHeader(Literal.api_gateway));
            /**
             * Check for token
             */
            if (map_gateway.get(Literal.STATUS).toString().equals(Literal.ERROR)) {
                /**
                 * Add the request URL
                 */
                map_gateway.put(Literal.REQUEST_DATA, Utility.makeRquestString(rqst_map));
                return map_gateway;
            }
            /**
             * Check for id null value
             */
            if (Utility.chkNullObj(rqst_map.get(Literal.id))) {
                ret_map.put(Literal.STATUS, Literal.ERROR);
                ret_map.put(Literal.MESSAGE, Literal.PROJECT_ID_NULL);
                ret_map.put(Literal.REQUEST_DATA, Utility.makeRquestString(rqst_map));
                return ret_map;
            }
            /**
             * Check for name null value
             */
            if (Utility.chkNullObj(rqst_map.get(Literal.name))) {
                ret_map.put(Literal.STATUS, Literal.ERROR);
                ret_map.put(Literal.MESSAGE, Literal.PROJECT_NAME_NULL);
                ret_map.put(Literal.REQUEST_DATA, Utility.makeRquestString(rqst_map));
                return ret_map;
            }
            /**
             * check description null
             */
            if (Utility.chkNullObj(rqst_map.get(Literal.description))) {
                rqst_map.put(Literal.description, Literal.EMPTY_STRING);
            } else if (!Utility.chkDescriptionLength(rqst_map.get(Literal.description).toString())) {
                ret_map.put(Literal.STATUS, Literal.ERROR);
                ret_map.put(Literal.MESSAGE, Literal.DESCRIPTION_EXCEED);
                ret_map.put(Literal.REQUEST_DATA, Utility.makeRquestString(rqst_map));
                return ret_map;
            }
            /**
             * call the service
             */
            if (MongoServices.getInstance().createUpdateProjectDetails(
                    rqst_map.get(Literal.id).toString(),
                    rqst_map.get(Literal.name).toString(),
                    rqst_map.get(Literal.description).toString(),
                    rqst_map.get(Literal.icon).toString())) {
                ret_map.put(Literal.STATUS, Literal.SUCCESS);
                ret_map.put(Literal.MESSAGE, Literal.DOCBOOK_PROJECT_UPDATED);
                ret_map.put(Literal.API_TIME, Utility.apiMill(s1, System.currentTimeMillis()));
                return ret_map;
            }

        } catch (Exception e) {
            Utility.printStackTrace(e, this.getClass().getName());
        }
        ret_map.put(Literal.STATUS, Literal.ERROR);
        ret_map.put(Literal.MESSAGE, Literal.SOMETHING_WENT_WRONG);
        ret_map.put(Literal.REQUEST_DATA, Utility.makeRquestString(rqst_map));
        return ret_map;
    }

    /**
     * @param rqst_map {
     *                 "project_id" : "fh387f3h3n9q8d.c2c2bc",
     *                 "section_id" : "jfy33r29382y3b3.xuw984c7",
     *                 "sequence" : 1,
     *                 "name": "Getting Started",
     *                 "content": "<h1><font face='Arial'><b>Getting Started</b>&#160;</font></h1>"
     *                 }
     *
     *                 <table border=1px>
     *                 <tr>
     *                 <th>Key</th>
     *                 <th>Sample Data</th>
     *                 <th>Data Type</th>
     *                 <th>Constraint</th>
     *                 <th>Description</th>
     *                 </tr>
     *                 <tr>
     *                 <td>project_id</td>
     *                 <td>fh387f3h3n9q8d.c2c2bc</td>
     *                 <td>String</td>
     *                 <td>Mandatory</td>
     *                 <td>Id of the project</td>
     *                 </tr>
     *                 <tr>
     *                 <td>section_id</td>
     *                 <td>jfy33r29382y3b3.xuw984c7</td>
     *                 <td>String</td>
     *                 <td>Mandatory</td>
     *                 <td>Id of the section</td>
     *                 </tr>
     *                 <tr>
     *                 <td>sequence</td>
     *                 <td>1</td>
     *                 <td>Integer</td>
     *                 <td>Mandatory</td>
     *                 <td>#Sequence of the section</td>
     *                 </tr>
     *                 <tr>
     *                 <td>name</td>
     *                 <td>Getting Started</td>
     *                 <td>String</td>
     *                 <td>Mandatory</td>
     *                 <td>Name of the section</td>
     *                 </tr>
     *                 <tr>
     *                 <td>content</td>
     *                 <td>html</td>
     *                 <td>html</td>
     *                 <td>Optional</td>
     *                 <td>html content of the section</td></td>
     *                 </tr>
     *                 </table>
     * @param Headers  <table border=1px>
     *                                                                 <tr>
     *                                                                 <th>Header Key</th>
     *                                                                 <th>Sample Data</th>
     *                                                                 <th>Data Type</th>
     *                                                                 <th>Constraint</th>
     *                                                                 <th>Description</th>
     *                                                                 </tr>
     *                                                                 <tr>
     *                                                                 <td>User-Agent</td>
     *                                                                 <td>Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36</td>
     *                                                                 <td>String</td>
     *                                                                 <td>Mandatory</td>
     *                                                                 <td>user agent</td>
     *                                                                 </tr>
     *                                                                 <tr>
     *                                                                 <td>Content-Type</td>
     *                                                                 <td>application/json</td>
     *                                                                 <td>String</td>
     *                                                                 <td>Mandatory</td>
     *                                                                 <td>Request content type.</td>
     *                                                                 </tr>
     *                                                                 <tr>
     *                                                                 <td>api_gateway</td>
     *                                                                 <td>NOTEPAD</td>
     *                                                                 <td>String</td>
     *                                                                 <td>Mandatory</td>
     *                                                                 <td>Hold the Token value.</td>
     *                                                                 </tr>
     *                                                                 </table>
     *                                                                 <br>
     * @see <b>Functionality: </b> This API is used to create/ update section details in docbook
     * <br>
     * <b>SUCCESS MESSAGE:</b> <br>
     *
     * <b>ERROR MESSAGE:</b>
     */
    @RequestMapping(value = WebHook.createUpdateSectionDetails, method = RequestMethod.POST, headers = Literal.APPLICATION_JSON)
    public Object createUpdateSectionDetails(@RequestBody Map<String, Object> rqst_map) {
        Map<String, Object> ret_map = new HashMap<String, Object>(Literal.SIX);
        try {
            long s1 = System.currentTimeMillis();
            /**
             * Hold the token values
             */
            Map<String, Object> map_gateway = ApiGatwayService.getInstance()
                    .checkApiGateway(request.getHeader(Literal.api_gateway));
            /**
             * Check for token
             */
            if (map_gateway.get(Literal.STATUS).toString().equals(Literal.ERROR)) {
                /**
                 * Add the request URL
                 */
                map_gateway.put(Literal.REQUEST_DATA, Utility.makeRquestString(rqst_map));
                return map_gateway;
            }
            /**
             * Check for project id null value
             */
            if (Utility.chkNullObj(rqst_map.get(Literal.project_id))) {
                ret_map.put(Literal.STATUS, Literal.ERROR);
                ret_map.put(Literal.MESSAGE, Literal.PROJECT_ID_NULL);
                ret_map.put(Literal.REQUEST_DATA, Utility.makeRquestString(rqst_map));
                return ret_map;
            }
            /**
             * Check for section id null value
             */
            if (Utility.chkNullObj(rqst_map.get(Literal.section_id))) {
                ret_map.put(Literal.STATUS, Literal.ERROR);
                ret_map.put(Literal.MESSAGE, Literal.SECTION_ID_NULL);
                ret_map.put(Literal.REQUEST_DATA, Utility.makeRquestString(rqst_map));
                return ret_map;
            }
            /**
             * Check for name null value
             */
            if (Utility.chkNullObj(rqst_map.get(Literal.name))) {
                ret_map.put(Literal.STATUS, Literal.ERROR);
                ret_map.put(Literal.MESSAGE, Literal.SECTION_NAME_NULL);
                ret_map.put(Literal.REQUEST_DATA, Utility.makeRquestString(rqst_map));
                return ret_map;
            }
            /**
             * check name length
             */
            if (!Utility.chkDescriptionLength(rqst_map.get(Literal.name).toString())) {
                ret_map.put(Literal.STATUS, Literal.ERROR);
                ret_map.put(Literal.MESSAGE, Literal.SECTION_NAME_EXCEED);
                ret_map.put(Literal.REQUEST_DATA, Utility.makeRquestString(rqst_map));
                return ret_map;
            }
            /**
             * check for sequence
             */
            rqst_map.putIfAbsent(Literal.sequence, Literal.ZERO_VALUE);
            /**
             * check for content
             */
            rqst_map.putIfAbsent(Literal.content, Literal.EMPTY_STRING);
            /**
             * call the service
             */
            if (MongoServices.getInstance().createUpdateSectionDetails(
                    rqst_map.get(Literal.project_id).toString(),
                    rqst_map.get(Literal.section_id).toString(),
                    rqst_map.get(Literal.sequence).toString(),
                    rqst_map.get(Literal.name).toString(),
                    rqst_map.get(Literal.content).toString().replaceAll("\"", "'"))) {
                ret_map.put(Literal.STATUS, Literal.SUCCESS);
                ret_map.put(Literal.MESSAGE, Literal.DOCBOOK_SECTION_UPDATED);
                ret_map.put(Literal.API_TIME, Utility.apiMill(s1, System.currentTimeMillis()));
                return ret_map;
            }
        } catch (Exception e) {
            Utility.printStackTrace(e, this.getClass().getName());
        }
        ret_map.put(Literal.STATUS, Literal.ERROR);
        ret_map.put(Literal.MESSAGE, Literal.SOMETHING_WENT_WRONG);
        ret_map.put(Literal.REQUEST_DATA, Utility.makeRquestString(rqst_map));
        return ret_map;
    }

    /**
     * @param rqst_map {
     *                 "section_id" : "jfy33r29382y3b3.xuw984c7",
     *                 "sequence" : 1
     *                 }
     *
     *                 <table border=1px>
     *                 <tr>
     *                 <th>Key</th>
     *                 <th>Sample Data</th>
     *                 <th>Data Type</th>
     *                 <th>Constraint</th>
     *                 <th>Description</th>
     *                 </tr>
     *                 <tr>
     *                 <td>project_id</td>
     *                 <td>fh387f3h3n9q8d.c2c2bc</td>
     *                 <td>String</td>
     *                 <td>Mandatory</td>
     *                 <td>Id of the project</td>
     *                 </tr>
     *                 <tr>
     *                 <td>section_id</td>
     *                 <td>jfy33r29382y3b3.xuw984c7</td>
     *                 <td>String</td>
     *                 <td>Mandatory</td>
     *                 <td>Id of the section</td>
     *                 </tr>
     *                 <tr>
     *                 <td>sequence</td>
     *                 <td>1</td>
     *                 <td>Integer</td>
     *                 <td>Mandatory</td>
     *                 <td>#Sequence of the section</td>
     *                 </tr>
     *                 <tr>
     *                 <td>name</td>
     *                 <td>Getting Started</td>
     *                 <td>String</td>
     *                 <td>Mandatory</td>
     *                 <td>Name of the section</td>
     *                 </tr>
     *                 <tr>
     *                 <td>content</td>
     *                 <td>html</td>
     *                 <td>html</td>
     *                 <td>Optional</td>
     *                 <td>html content of the section</td></td>
     *                 </tr>
     *                 </table>
     * @param Headers  <table border=1px>
     *                                                                 <tr>
     *                                                                 <th>Header Key</th>
     *                                                                 <th>Sample Data</th>
     *                                                                 <th>Data Type</th>
     *                                                                 <th>Constraint</th>
     *                                                                 <th>Description</th>
     *                                                                 </tr>
     *                                                                 <tr>
     *                                                                 <td>User-Agent</td>
     *                                                                 <td>Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36</td>
     *                                                                 <td>String</td>
     *                                                                 <td>Mandatory</td>
     *                                                                 <td>user agent</td>
     *                                                                 </tr>
     *                                                                 <tr>
     *                                                                 <td>Content-Type</td>
     *                                                                 <td>application/json</td>
     *                                                                 <td>String</td>
     *                                                                 <td>Mandatory</td>
     *                                                                 <td>Request content type.</td>
     *                                                                 </tr>
     *                                                                 <tr>
     *                                                                 <td>api_gateway</td>
     *                                                                 <td>NOTEPAD</td>
     *                                                                 <td>String</td>
     *                                                                 <td>Mandatory</td>
     *                                                                 <td>Hold the Token value.</td>
     *                                                                 </tr>
     *                                                                 </table>
     *                                                                 <br>
     * @see <b>Functionality: </b> This API is used to update section sequences in docbook
     * <br>
     * <b>SUCCESS MESSAGE:</b> <br>
     *
     * <b>ERROR MESSAGE:</b>
     */
    @RequestMapping(value = WebHook.updateSectionSequence, method = RequestMethod.POST, headers = Literal.APPLICATION_JSON)
    public Object updateSectionSequence(@RequestBody Map<String, Object> rqst_map) {
        Map<String, Object> ret_map = new HashMap<String, Object>(Literal.SIX);
        try {
            long s1 = System.currentTimeMillis();
            /**
             * Hold the token values
             */
            Map<String, Object> map_gateway = ApiGatwayService.getInstance()
                    .checkApiGateway(request.getHeader(Literal.api_gateway));
            /**
             * Check for token
             */
            if (map_gateway.get(Literal.STATUS).toString().equals(Literal.ERROR)) {
                /**
                 * Add the request URL
                 */
                map_gateway.put(Literal.REQUEST_DATA, Utility.makeRquestString(rqst_map));
                return map_gateway;
            }
            /**
             * Check for section id null value
             */
            if (Utility.chkNullObj(rqst_map.get(Literal.section_id))) {
                ret_map.put(Literal.STATUS, Literal.ERROR);
                ret_map.put(Literal.MESSAGE, Literal.SECTION_ID_NULL);
                ret_map.put(Literal.REQUEST_DATA, Utility.makeRquestString(rqst_map));
                return ret_map;
            }
            /**
             * call the service
             */
            if (MongoServices.getInstance().updateSectionSequence(
                    rqst_map.get(Literal.section_id).toString(),
                    Integer.parseInt(rqst_map.get(Literal.sequence).toString()))) {
                ret_map.put(Literal.STATUS, Literal.SUCCESS);
                ret_map.put(Literal.MESSAGE, Literal.DOCBOOK_SECTION_UPDATED);
                ret_map.put(Literal.API_TIME, Utility.apiMill(s1, System.currentTimeMillis()));
                return ret_map;
            }
        } catch (Exception e) {
            Utility.printStackTrace(e, this.getClass().getName());
        }
        ret_map.put(Literal.STATUS, Literal.ERROR);
        ret_map.put(Literal.MESSAGE, Literal.SOMETHING_WENT_WRONG);
        ret_map.put(Literal.REQUEST_DATA, Utility.makeRquestString(rqst_map));
        return ret_map;
    }

    /**
     * @param rqst_map {
     *                 "id" : "fh387f3h3n9q8d.c2c2bc",
     *                 "name": "UHES",
     *                 "description": "uhes desc",
     *                 "icon": "mdi mdi-application",
     *                 }
     *
     *                 <table border=1px>
     *                 <tr>
     *                 <th>Key</th>
     *                 <th>Sample Data</th>
     *                 <th>Data Type</th>
     *                 <th>Constraint</th>
     *                 <th>Description</th>
     *                 </tr>
     *                 <tr>
     *                 <td>id</td>
     *                 <td>fh387f3h3n9q8d.c2c2bc</td>
     *                 <td>String</td>
     *                 <td>Mandatory</td>
     *                 <td>Id of the project</td>
     *                 </tr>
     *                 <tr>
     *                 <td>name</td>
     *                 <td>UHES</td>
     *                 <td>String</td>
     *                 <td>Mandatory</td>
     *                 <td>Name of the project</td>
     *                 </tr>
     *                 <tr>
     *                 <td>description</td>
     *                 <td>uhes is smart meter project</td>
     *                 <td>String</td>
     *                 <td>Optional</td>
     *                 <td>description for the project</td></td>
     *                 </tr>
     *                 <tr>
     *                 <td>icon</td>
     *                 <td>mdi mdi-application</td>
     *                 <td>String</td>
     *                 <td>Optional</td>
     *                 <td>icon class name for this project</td></td>
     *                 </tr>
     *                 </table>
     * @param Headers  <table border=1px>
     *                                                                 <tr>
     *                                                                 <th>Header Key</th>
     *                                                                 <th>Sample Data</th>
     *                                                                 <th>Data Type</th>
     *                                                                 <th>Constraint</th>
     *                                                                 <th>Description</th>
     *                                                                 </tr>
     *                                                                 <tr>
     *                                                                 <td>User-Agent</td>
     *                                                                 <td>Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36</td>
     *                                                                 <td>String</td>
     *                                                                 <td>Mandatory</td>
     *                                                                 <td>user agent</td>
     *                                                                 </tr>
     *                                                                 <tr>
     *                                                                 <td>Content-Type</td>
     *                                                                 <td>application/json</td>
     *                                                                 <td>String</td>
     *                                                                 <td>Mandatory</td>
     *                                                                 <td>Request content type.</td>
     *                                                                 </tr>
     *                                                                 <tr>
     *                                                                 <td>api_gateway</td>
     *                                                                 <td>NOTEPAD</td>
     *                                                                 <td>String</td>
     *                                                                 <td>Mandatory</td>
     *                                                                 <td>Hold the Token value.</td>
     *                                                                 </tr>
     *                                                                 </table>
     *                                                                 <br>
     * @see <b>Functionality: </b> This API is used to get project details from docbook
     * <br>
     * <b>SUCCESS MESSAGE:</b> <br>
     *
     * <b>ERROR MESSAGE:</b>
     */
    @RequestMapping(value = WebHook.getProjectDetails, method = RequestMethod.GET, headers = Literal.APPLICATION_JSON)
    public Object getProjectDetails(@RequestParam(value = Literal.id, required = false) String id) {
        Map<String, Object> ret_map = new HashMap<String, Object>(Literal.SIX);
        try {
            long s1 = System.currentTimeMillis();
            /**
             * Hold the token values
             */
            Map<String, Object> map_gateway = ApiGatwayService.getInstance()
                    .checkApiGateway(request.getHeader(Literal.api_gateway));
            /**
             * Check for token
             */
            if (map_gateway.get(Literal.STATUS).toString().equals(Literal.ERROR)) {
                /**
                 * Add the request URL
                 */
                return map_gateway;
            }
            /**
             * Check for id null value
             */
            if (Utility.chkNullObj(id)) {
                id = Literal.ALL;
            }
            /**
             * call the service
             */
            ret_map.put(Literal.STATUS, Literal.SUCCESS);
            ret_map.put(Literal.DATA, MongoServices.getInstance().getProjectDetails(id));
            ret_map.put(Literal.API_TIME, Utility.apiMill(s1, System.currentTimeMillis()));
            return ret_map;
        } catch (Exception e) {
            Utility.printStackTrace(e, this.getClass().getName());
            ret_map.put(Literal.STATUS, Literal.ERROR);
            ret_map.put(Literal.MESSAGE, Literal.SOMETHING_WENT_WRONG);
            return ret_map;
        }
    }

    /**
     * @param rqst_map {
     *                 "project_id" : "fh387f3h3n9q8d.c2c2bc",
     *                 "section_id" : "fh38733r29382y3b3.xuw984"
     *                 }
     *
     *                 <table border=1px>
     *                 <tr>
     *                 <th>Key</th>
     *                 <th>Sample Data</th>
     *                 <th>Data Type</th>
     *                 <th>Constraint</th>
     *                 <th>Description</th>
     *                 </tr>
     *                 <tr>
     *                 <td>project_id</td>
     *                 <td>fh387f3h3n9q8d.c2c2bc</td>
     *                 <td>String</td>
     *                 <td>Mandatory</td>
     *                 <td>Id of the project</td>
     *                 </tr>
     *                 <tr>
     *                 <td>section_id</td>
     *                 <td>jfy33r29382y3b3.xuw984c7</td>
     *                 <td>String</td>
     *                 <td>Mandatory</td>
     *                 <td>Id of the section</td>
     *                 </tr>
     *                 </table>
     * @param Headers  <table border=1px>
     *                                                                 <tr>
     *                                                                 <th>Header Key</th>
     *                                                                 <th>Sample Data</th>
     *                                                                 <th>Data Type</th>
     *                                                                 <th>Constraint</th>
     *                                                                 <th>Description</th>
     *                                                                 </tr>
     *                                                                 <tr>
     *                                                                 <td>User-Agent</td>
     *                                                                 <td>Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36</td>
     *                                                                 <td>String</td>
     *                                                                 <td>Mandatory</td>
     *                                                                 <td>user agent</td>
     *                                                                 </tr>
     *                                                                 <tr>
     *                                                                 <td>Content-Type</td>
     *                                                                 <td>application/json</td>
     *                                                                 <td>String</td>
     *                                                                 <td>Mandatory</td>
     *                                                                 <td>Request content type.</td>
     *                                                                 </tr>
     *                                                                 <tr>
     *                                                                 <td>api_gateway</td>
     *                                                                 <td>NOTEPAD</td>
     *                                                                 <td>String</td>
     *                                                                 <td>Mandatory</td>
     *                                                                 <td>Hold the Token value.</td>
     *                                                                 </tr>
     *                                                                 </table>
     *                                                                 <br>
     * @see <b>Functionality: </b> This API is used to get project+section details from docbook
     * <br>
     * <b>SUCCESS MESSAGE:</b> <br>
     *
     * <b>ERROR MESSAGE:</b>
     */
    @RequestMapping(value = WebHook.getSectionDetails, method = RequestMethod.POST, headers = Literal.APPLICATION_JSON)
    public Object getSectionDetails(@RequestBody Map<String, Object> rqst_map) {
        Map<String, Object> ret_map = new HashMap<String, Object>(Literal.SIX);
        try {
            long s1 = System.currentTimeMillis();
            /**
             * Hold the token values
             */
            Map<String, Object> map_gateway = ApiGatwayService.getInstance()
                    .checkApiGateway(request.getHeader(Literal.api_gateway));
            /**
             * Check for token
             */
            if (map_gateway.get(Literal.STATUS).toString().equals(Literal.ERROR)) {
                /**
                 * Add the request URL
                 */
                return map_gateway;
            }
            /**
             * Check for project id null value
             */
            if (Utility.chkNullObj(rqst_map.get(Literal.project_id))) {
                ret_map.put(Literal.STATUS, Literal.ERROR);
                ret_map.put(Literal.MESSAGE, Literal.PROJECT_ID_NULL);
                ret_map.put(Literal.REQUEST_DATA, Utility.makeRquestString(rqst_map));
                return ret_map;
            }
            /**
             * call the service
             */
            ret_map.put(Literal.STATUS, Literal.SUCCESS);
            ret_map.put(Literal.DATA, MongoServices.getInstance().getProjectContent(rqst_map.get(Literal.project_id).toString()));
            ret_map.put(Literal.API_TIME, Utility.apiMill(s1, System.currentTimeMillis()));
            return ret_map;
        } catch (Exception e) {
            Utility.printStackTrace(e, this.getClass().getName());
            ret_map.put(Literal.STATUS, Literal.ERROR);
            ret_map.put(Literal.MESSAGE, Literal.SOMETHING_WENT_WRONG);
            ret_map.put(Literal.REQUEST_DATA, Utility.makeRquestString(rqst_map));
            return ret_map;
        }
    }

    /**
     * @param rqst_map {
     *                 "project_id" : "fh387f3h3n9q8d.c2c2bc",
     *                 "section_id" : "fh38733r29382y3b3.xuw984"
     *                 }
     *
     *                 <table border=1px>
     *                 <tr>
     *                 <th>Key</th>
     *                 <th>Sample Data</th>
     *                 <th>Data Type</th>
     *                 <th>Constraint</th>
     *                 <th>Description</th>
     *                 </tr>
     *                 <tr>
     *                 <td>project_id</td>
     *                 <td>fh387f3h3n9q8d.c2c2bc</td>
     *                 <td>String</td>
     *                 <td>Mandatory</td>
     *                 <td>Id of the project</td>
     *                 </tr>
     *                 <tr>
     *                 <td>section_id</td>
     *                 <td>jfy33r29382y3b3.xuw984c7</td>
     *                 <td>String</td>
     *                 <td>Mandatory</td>
     *                 <td>Id of the section</td>
     *                 </tr>
     *                 </table>
     * @param Headers  <table border=1px>
     *                                                                 <tr>
     *                                                                 <th>Header Key</th>
     *                                                                 <th>Sample Data</th>
     *                                                                 <th>Data Type</th>
     *                                                                 <th>Constraint</th>
     *                                                                 <th>Description</th>
     *                                                                 </tr>
     *                                                                 <tr>
     *                                                                 <td>User-Agent</td>
     *                                                                 <td>Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36</td>
     *                                                                 <td>String</td>
     *                                                                 <td>Mandatory</td>
     *                                                                 <td>user agent</td>
     *                                                                 </tr>
     *                                                                 <tr>
     *                                                                 <td>Content-Type</td>
     *                                                                 <td>application/json</td>
     *                                                                 <td>String</td>
     *                                                                 <td>Mandatory</td>
     *                                                                 <td>Request content type.</td>
     *                                                                 </tr>
     *                                                                 <tr>
     *                                                                 <td>api_gateway</td>
     *                                                                 <td>NOTEPAD</td>
     *                                                                 <td>String</td>
     *                                                                 <td>Mandatory</td>
     *                                                                 <td>Hold the Token value.</td>
     *                                                                 </tr>
     *                                                                 </table>
     *                                                                 <br>
     * @see <b>Functionality: </b> This API is used to get project+section details from docbook
     * <br>
     * <b>SUCCESS MESSAGE:</b> <br>
     *
     * <b>ERROR MESSAGE:</b>
     */
    @RequestMapping(value = WebHook.deleteDocbook, method = RequestMethod.POST, headers = Literal.APPLICATION_JSON)
    public Object deleteDocbook(@RequestBody Map<String, Object> rqst_map) {
        Map<String, Object> ret_map = new HashMap<String, Object>(Literal.SIX);
        try {
            long s1 = System.currentTimeMillis();
            /**
             * Hold the token values
             */
            Map<String, Object> map_gateway = ApiGatwayService.getInstance()
                    .checkApiGateway(request.getHeader(Literal.api_gateway));
            /**
             * Check for token
             */
            if (map_gateway.get(Literal.STATUS).toString().equals(Literal.ERROR)) {
                /**
                 * Add the request URL
                 */
                ret_map.put(Literal.MESSAGE, Literal.API_GATE_WAY_NULL);
                return map_gateway;
            }
            /**
             * delete section
             */
            if (!Utility.chkNullObj(rqst_map.get(Literal.section_id)) &&
                    MongoServices.getInstance().deleteSection(rqst_map.get(Literal.section_id).toString())) {
                ret_map.put(Literal.STATUS, Literal.SUCCESS);
                ret_map.put(Literal.MESSAGE, Literal.DELETED_SECTION);
                ret_map.put(Literal.API_TIME, Utility.apiMill(s1, System.currentTimeMillis()));
                return ret_map;
            }
            /**
             * delete project
             */
            if (!Utility.chkNullObj(rqst_map.get(Literal.project_id)) &&
                    MongoServices.getInstance().deleteProject(rqst_map.get(Literal.project_id).toString())) {
                ret_map.put(Literal.STATUS, Literal.SUCCESS);
                ret_map.put(Literal.MESSAGE, Literal.DELETED_PROJECT);
                ret_map.put(Literal.API_TIME, Utility.apiMill(s1, System.currentTimeMillis()));
                return ret_map;
            }
        } catch (Exception e) {
            Utility.printStackTrace(e, this.getClass().getName());
        }
        ret_map.put(Literal.STATUS, Literal.ERROR);
        ret_map.put(Literal.MESSAGE, Literal.SOMETHING_WENT_WRONG);
        ret_map.put(Literal.REQUEST_DATA, Utility.makeRquestString(rqst_map));
        return ret_map;
    }


}
