package com.notepad.STM.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class STMUtility {

    public static Object makeRquestString(Map<String, Object> req_map) {
        try {
            return req_map;
        } catch (Exception e) {
            return "Request Error";
        }
    }

    public static boolean chkNullObj(Object obj_data) {
        if (obj_data == null || obj_data.toString().trim().equals(STMLiteral.EMPTY_STRING)) {
            return STMLiteral.BOOLEAN_TRUE;
        }
        return STMLiteral.BOOLEAN_FALSE;
    }

    public static long apiMill(final long s1, final long s2) {
        try {
            return s2 - s1;
        } catch (Exception e) {
            return -1;
        }
    }

    public static void printStackTrace(Exception e, String name) {
        e.printStackTrace();
    }

    public static boolean chkDescriptionLength(String description) {
        try {
            if (description.length() < STMLiteral.DESCRIPTION_LENGTH) {
                return STMLiteral.BOOLEAN_TRUE;
            }
        } catch (Exception e) {
            STMUtility.printStackTrace(e, null);
        }
        return STMLiteral.BOOLEAN_FALSE;
    }

    public static long getCurrentMilli() {
        return System.currentTimeMillis();
    }

    public static List<Map<String, Object>> resultSetToArrayList(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<Map<String, Object>> list = new ArrayList<>();
        while (rs.next()){
            Map<String, Object> row = new HashMap<String, Object>(columns);
            for(int i=1; i<=columns; ++i){
                row.put(md.getColumnName(i),rs.getObject(i));
            }
            list.add(row);
        }
        return list;
    }

}
