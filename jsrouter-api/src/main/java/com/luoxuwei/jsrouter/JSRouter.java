package com.luoxuwei.jsrouter;

import android.app.Application;

import com.luoxuwei.jsrouter.template.IRouteGroup;
import com.luoxuwei.jsrouter.template.IRouteRoot;
import com.luoxuwei.jsrouter.utils.ClassUtils;
import com.luoxuwei.jsrouter.utils.Consts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by 罗旭维 on 2021/8/12.
 */
public class JSRouter {
    static Map<String, Class<? extends IRouteGroup>> groupsIndex = new HashMap<>();
    static Map<String, String> pathIndex = new HashMap<>();
    static Map<String, Class<?>> routes = new HashMap<>();

    public static void init(Application application) {
        try {
            Set<String> routerMap = ClassUtils.getClassByPackageName(application.getApplicationContext(), Consts.ROUTE_ROOT_PAKCAGE);
            for (String className : routerMap) {
                if (className.startsWith(Consts.ROUTE_ROOT_PAKCAGE + Consts.DOT + Consts.SDK_NAME + Consts.SEPARATOR + Consts.SUFFIX_ROOT)) {
                    IRouteRoot routeGroup = (IRouteRoot) Class.forName(className).getConstructor().newInstance();
                    routeGroup.loadInto(groupsIndex, pathIndex);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("JSRouter init logistics center exception! [" + e.getMessage() + "]");
        }
    }

}
