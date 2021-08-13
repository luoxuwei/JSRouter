package com.luoxuwei.jsrouter.utils;

/**
 * Created by 罗旭维 on 2021/8/12.
 */
public class TextUtils {
    public static String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
