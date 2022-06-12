package com.notepad.util;

import java.util.Map;

public class Utility {

    public static Object makeRquestString(Map<String, Object> req_map) {
        try {
            return req_map;
        } catch (Exception e) {
            return "Request Error";
        }
    }

    public static boolean chkNullObj(Object obj_data) {
        if (obj_data == null || obj_data.toString().trim().equals(Literal.EMPTY_STRING)) {
            return Literal.BOOLEAN_TRUE;
        }
        return Literal.BOOLEAN_FALSE;
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
            if (description.length() < Literal.DESCRIPTION_LENGTH) {
                return Literal.BOOLEAN_TRUE;
            }
        } catch (Exception e) {
            Utility.printStackTrace(e, null);
        }
        return Literal.BOOLEAN_FALSE;
    }

    public static long getCurrentMilli() {
        return System.currentTimeMillis();
    }

}
