package com.luoxuwei.jsrouter.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by 罗旭维 on 2021/8/12.
 */
public class Logger {
    private static boolean isShowLog = false;
    private static boolean isShowStackTrace = false;
    private static boolean isMonitorMode = false;

    private static String defaultTag = Consts.TAG;

    public static void showLog(boolean showLog) {
        isShowLog = showLog;
    }

    public static void showStackTrace(boolean showStackTrace) {
        isShowStackTrace = showStackTrace;
    }

    public static void showMonitor(boolean showMonitor) {
        isMonitorMode = showMonitor;
    }

    public static void debug(String tag, String message) {
        if (isShowLog) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            Log.d(TextUtils.isEmpty(tag) ? defaultTag : tag, message + getExtInfo(stackTraceElement));
        }
    }

    public static void info(String message) {
        info(defaultTag, message);
    }

    public static void info(String tag, String message) {
        if (isShowLog) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            Log.i(TextUtils.isEmpty(tag) ? defaultTag : tag, message + getExtInfo(stackTraceElement));
        }
    }

    public static void warning(String message) {
        warning(defaultTag, message);
    }

    public static void warning(String tag, String message) {
        if (isShowLog) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            Log.w(TextUtils.isEmpty(tag) ? defaultTag : tag, message + getExtInfo(stackTraceElement));
        }
    }

    public static void error(String message) {
        error(defaultTag, message);
    }

    public static void error(String tag, String message) {
        if (isShowLog) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            Log.e(TextUtils.isEmpty(tag) ? defaultTag : tag, message + getExtInfo(stackTraceElement));
        }
    }

    public static void error(String tag, String message, Throwable e) {
        if (isShowLog) {
            Log.e(TextUtils.isEmpty(tag) ? defaultTag : tag, message, e);
        }
    }


    public static void monitor(String message) {
        if (isShowLog && isMonitorMode()) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            Log.d(defaultTag + "::monitor", message + getExtInfo(stackTraceElement));
        }
    }

    public static boolean isMonitorMode() {
        return isMonitorMode;
    }

    public static String getExtInfo(StackTraceElement stackTraceElement) {
        if (isShowStackTrace) {
            String separator = " & ";
            StringBuilder sb = new StringBuilder("[");

            String threadName = Thread.currentThread().getName();
            String fileName = stackTraceElement.getFileName();
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            long threadID = Thread.currentThread().getId();
            int lineNumber = stackTraceElement.getLineNumber();

            sb.append("ThreadId=").append(threadID).append(separator);
            sb.append("ThreadName=").append(threadName).append(separator);
            sb.append("FileName=").append(fileName).append(separator);
            sb.append("ClassName=").append(className).append(separator);
            sb.append("MethodName=").append(methodName).append(separator);
            sb.append("LineNumber=").append(lineNumber);

            sb.append(" ] ");
            return sb.toString();
        }

        return "";
    }
}
