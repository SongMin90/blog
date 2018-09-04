package com.my.blog.website.utils;

import java.util.List;
import java.util.Map;

/**
 * 数据工具类
 */
public class DataUtil {

    public static boolean isEmpty(String object) {
        return object != null && !object.equals("");
    }

    public static boolean isEmpty(String[] object) {
        return object != null && object.length > 0;
    }

    public static boolean isEmpty(Integer object) {
        return object != null;
    }

    public static boolean isEmpty(List object) {
        return object != null && object.size() > 0;
    }

    public static boolean isEmpty(Map object) {
        return object != null && object.size() > 0;
    }

    public static boolean isEmpty(Long object) {
        return object != null;
    }

}
