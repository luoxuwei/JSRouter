package com.luoxuwei.jsrouter.register.utils

import org.gradle.api.Project

/**
 * Created by 罗旭维 on 2021/8/14.
 */
class Logger {
    static org.gradle.api.logging.Logger mLogger;

    static void init(Project project) {
        mLogger = project.getLogger();
    }

    static void i(String log) {
        if (null != log && null != mLogger) {
            mLogger.info(Consts.LOG_PREFIX + log);
        }
    }

    static void e(String log) {
        if (null != log && null != mLogger) {
            mLogger.error(Consts.LOG_PREFIX + log);
        }
    }

    static void w(String log) {
        if (null != log && null != mLogger) {
            mLogger.warn(Consts.LOG_PREFIX + log);
        }
    }

}