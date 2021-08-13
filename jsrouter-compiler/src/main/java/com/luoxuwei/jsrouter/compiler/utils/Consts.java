package com.luoxuwei.jsrouter.compiler.utils;

/**
 * Created by 罗旭维 on 2021/8/13.
 */
public class Consts {
    public final static String PACKAGE = "com.luoxuwei.jsrouter";
    public final static String ANNOTATION_TYPE_ROUTE = PACKAGE + ".annotation.JSRoute";
    public final static String KEY_MODULE_NAME = "JSROUTER_MODULE_NAME";
    public static final String PROJECT = "JSRouter";
    public static final String PREFIX_OF_LOGGER = PROJECT + "::Compiler ";
    public static final String NO_MODULE_NAME_TIPS = "These no module name, at 'build.gradle', like :\n" +
            "android {\n" +
            "    defaultConfig {\n" +
            "        ...\n" +
            "        javaCompileOptions {\n" +
            "            annotationProcessorOptions {\n" +
            "                arguments = [JSROUTER_MODULE_NAME: project.getName()]\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
}
