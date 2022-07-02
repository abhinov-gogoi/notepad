package com.notepad.STM.Controller;

import com.notepad.STM.Services.STMApiGatwayService;
import com.notepad.STM.Services.STMMongoServices;
import com.notepad.STM.util.STMLiteral;
import com.notepad.STM.util.STMUtility;
import com.notepad.STM.util.STMWebHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class STMController {

    @Autowired
    private HttpServletRequest request;

    /**
     * hold the mainService
     */
    STMMongoServices mongoServices;

    /**
     * constructor
     */
    public STMController() {
        mongoServices = STMMongoServices.getInstance();
    }


    /**
     * test API
     */
    @RequestMapping(value = STMWebHook.apiTestSTM, method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE, headers = "Accept=application/json")
    public String apiTest() {
        return "OK";
    }



    /**
     * @param rqst_map
     *                 {
     *                 "mongodb_uri" : "mongodb+srv://groot:root@meow.nqrji.mongodb.net/meow",
     *                 "database" : "stm_db"
     *                 }
     *
     *    <table border=1px>
     *    <tr>
     *    <th>Key</th>
     *    <th>Sample Data</th>
     *    <th>Data Type</th>
     *    <th>Constraint</th>
     *    <th>Description</th>
     *    </tr>
     *    <tr>
     *    <td>id</td>
     *    <td>fh387f3h3n9q8d.c2c2bc</td>
     *    <td>String</td>
     *    <td>Mandatory</td>
     *    <td>Id of the project</td>
     *    </tr>
     *    </table>
     * @param Headers  <table border=1px>
     *    <tr>
     *    <th>Header Key</th>
     *    <th>Sample Data</th>
     *    <th>Data Type</th>
     *    <th>Constraint</th>
     *    <th>Description</th>
     *    </tr>
     *    <tr>
     *    <td>User-Agent</td>
     *    <td>Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36</td>
     *    <td>String</td>
     *    <td>Mandatory</td>
     *    <td>user agent</td>
     *    </tr>
     *    <tr>
     *    <td>Content-Type</td>
     *    <td>application/json</td>
     *    <td>String</td>
     *    <td>Mandatory</td>
     *    <td>Request content type.</td>
     *    </tr>
     *    <tr>
     *    <td>api_gateway</td>
     *    <td>NOTEPAD</td>
     *    <td>String</td>
     *    <td>Mandatory</td>
     *    <td>Hold the Token value.</td>
     *    </tr>
     *    </table>
     *    <br>
     * <b>Functionality: </b>
     * <br>
     * <b>SUCCESS MESSAGE:</b> <br>
     *
     * <b>ERROR MESSAGE:</b>
     */
    @RequestMapping(value = STMWebHook.testMongoDbUriConnection, method = RequestMethod.POST, headers = STMLiteral.APPLICATION_JSON)
    public Object testMongoDbUriConnection(@RequestBody Map<String, Object> rqst_map) {
        Map<String, Object> ret_map = new HashMap<String, Object>(STMLiteral.SIX);
        try {
            long s1 = System.currentTimeMillis();
            /**
             * Hold the token values
             */
            Map<String, Object> map_gateway = STMApiGatwayService.getInstance()
                    .checkApiGateway(request.getHeader(STMLiteral.api_gateway));
            /**
             * Check for token
             */
            if (map_gateway.get(STMLiteral.STATUS).toString().equals(STMLiteral.ERROR)) {
                /**
                 * Add the request URL
                 */
                map_gateway.put(STMLiteral.REQUEST_DATA, STMUtility.makeRquestString(rqst_map));
                return map_gateway;
            }
            /**
             * Check for null uri string
             */
            if (STMUtility.chkNullObj(rqst_map.get(STMLiteral.mongodb_uri))) {
                ret_map.put(STMLiteral.STATUS, STMLiteral.ERROR);
                ret_map.put(STMLiteral.MESSAGE, STMLiteral.URI_NULL);
                ret_map.put(STMLiteral.REQUEST_DATA, STMUtility.makeRquestString(rqst_map));
                return ret_map;
            }

            /**
             * Check for null database
             */
            if (STMUtility.chkNullObj(rqst_map.get(STMLiteral.database))) {
                ret_map.put(STMLiteral.STATUS, STMLiteral.ERROR);
                ret_map.put(STMLiteral.MESSAGE, STMLiteral.MONGO_DATABASE_NULL);
                ret_map.put(STMLiteral.REQUEST_DATA, STMUtility.makeRquestString(rqst_map));
                return ret_map;
            }

            /**
             * call the service
             */
            if (STMMongoServices.getInstance()
                    .testMongoDbUriConnection(rqst_map.get(STMLiteral.mongodb_uri).toString(), rqst_map.get(STMLiteral.database).toString())) {
                ret_map.put(STMLiteral.STATUS, STMLiteral.SUCCESS);
                ret_map.put(STMLiteral.MESSAGE, STMLiteral.MONGODB_CONNECTION_SUCCESSFUL);
                ret_map.put(STMLiteral.API_TIME, STMUtility.apiMill(s1, System.currentTimeMillis()));
                return ret_map;
            }

        } catch (Exception e) {
            STMUtility.printStackTrace(e, this.getClass().getName());
        }
        ret_map.put(STMLiteral.STATUS, STMLiteral.ERROR);
        ret_map.put(STMLiteral.MESSAGE, STMLiteral.SOMETHING_WENT_WRONG);
        ret_map.put(STMLiteral.REQUEST_DATA, STMUtility.makeRquestString(rqst_map));
        return ret_map;
    }

}
