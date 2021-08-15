package com.luoxuwei.jsrouter;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.luoxuwei.jsrouter.base.BaseJavaScriptInterface;
import com.luoxuwei.jsrouter.base.DefaultJavaScriptInterface;
import com.luoxuwei.jsrouter.template.IRouteGroup;
import com.luoxuwei.jsrouter.template.IRouteRoot;
import com.luoxuwei.jsrouter.utils.ClassUtils;
import com.luoxuwei.jsrouter.utils.Consts;
import com.luoxuwei.jsrouter.utils.Logger;
import com.luoxuwei.jsrouter.utils.PackageUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by 罗旭维 on 2021/8/12.
 */
public class JSRouter {
    static Map<String, Class<? extends IRouteGroup>> groupsIndex = new HashMap<>();
    static Map<String, String> pathIndex = new HashMap<>();
    static Map<String, Class<?>> routes = new HashMap<>();
    private volatile static JSRouter instance = null;
    private volatile static boolean hasInit = false;
    private static boolean registerByPlugin;
    private static boolean registerByHardcode;
    private static String[] modules = null; //new String[]{"app", "app1", "moduletest"};
    public static void init(Application application) {
        if (hasInit) return;

        try {
            long startInit = System.currentTimeMillis();
            loadRouterHardcode();
            if (!registerByHardcode) {
                loadRouterByPlugin();
                if (registerByPlugin) {
                    Logger.info("Load router map by plugin.");
                } else {
                    Set<String> routerMap = null;
                    if (PackageUtils.isNewVersion(application)) {
                        routerMap = ClassUtils.getClassByPackageName(application.getApplicationContext(), Consts.ROUTE_ROOT_PAKCAGE);


                        if (!routerMap.isEmpty()) {
                            application.getSharedPreferences(Consts.JSOUTER_SP_CACHE_KEY, Context.MODE_PRIVATE).edit().putStringSet(Consts.JSOUTER_SP_KEY_MAP, routerMap).apply();
                        }
                        PackageUtils.updateVersion(application);
                    } else {
                        Logger.info("Load router map from cache.");
                        routerMap = new HashSet<>(application.getSharedPreferences(Consts.JSOUTER_SP_CACHE_KEY, Context.MODE_PRIVATE).getStringSet(Consts.JSOUTER_SP_KEY_MAP, new HashSet<>()));
                    }
                    Logger.info("Find router map finished, map size = " + routerMap.size() + ", cost " + (System.currentTimeMillis() - startInit) + " ms.");
                    startInit = System.currentTimeMillis();
                    for (String className : routerMap) {
                        if (className.startsWith(Consts.ROUTE_ROOT_PAKCAGE + Consts.DOT + Consts.SDK_NAME + Consts.SEPARATOR + Consts.SUFFIX_ROOT)) {
                            IRouteRoot routeRoot = (IRouteRoot) Class.forName(className).getConstructor().newInstance();
                            routeRoot.loadInto(groupsIndex, pathIndex);
                        }
                    }
                }
            } else {
                Logger.info("Load router map by hardcode.");
            }

            hasInit = true;
            Logger.info("Load root element finished, cost " + (System.currentTimeMillis() - startInit) + " ms.");
            if (groupsIndex.size() == 0) {
                Logger.error("No mapping files were found, check your configuration please!");
            }
        } catch (Exception e) {
            throw new RuntimeException("JSRouter init exception! [" + e.getMessage() + "]");
        }
    }

    private static void loadRouterHardcode() {
        registerByHardcode = false;
        if (modules != null) {
            registerByHardcode = true;
            for (String module : modules) {
                //com.luoxuwei.jsrouter.routes.JSRouter$$Root$$module
                String className = Consts.ROUTE_ROOT_PAKCAGE + Consts.DOT + Consts.SDK_NAME + Consts.SEPARATOR + Consts.SUFFIX_ROOT + Consts.SEPARATOR + module;
                try {
                    Class<?> clazz = Class.forName(className);
                    IRouteRoot routeRoot = (IRouteRoot) clazz.getConstructor().newInstance();
                    routeRoot.loadInto(groupsIndex, pathIndex);
                } catch (Exception e) {

                }
            }
        }
    }

    //gradle 插件会在这个方法中插入加载路由组的代码
    //loadRouter("com.luoxuwei.jsrouter.routes.IRoute$$Root&&app")
    private static void loadRouterByPlugin() {
        registerByPlugin = false;
    }

    private static void loadRouter(String className) {
        if (!TextUtils.isEmpty(className)) {
            try {
                Class<?> clazz = Class.forName(className);
                IRouteRoot routeRoot = (IRouteRoot) clazz.getConstructor().newInstance();
                routeRoot.loadInto(groupsIndex, pathIndex);
                if (!registerByPlugin) {
                    registerByPlugin = true;
                }
            } catch (Exception e) {

            }
        }

    }

    public BaseJavaScriptInterface navigation(String path) {
        Class<?> interfaceClass = routes.get(path);
        if (interfaceClass == null) {
            String group = pathIndex.get(path);
            if (!TextUtils.isEmpty(group)) {
                try {
                    addRouteGroupDynamic(group);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Logger.error("There is no route match the path [" + path + "]");
            }

            interfaceClass = routes.get(path);
        }

        if (interfaceClass != null) {
            try {
                return (BaseJavaScriptInterface) interfaceClass.getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new DefaultJavaScriptInterface();
    }

    public void addRouteGroupDynamic(String group) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (groupsIndex.containsKey(group)) {
            groupsIndex.get(group).getConstructor().newInstance().loadInto(routes);
            groupsIndex.remove(group);
        }
    }

    public static JSRouter getInstance() {
        if (!hasInit) {
            throw new RuntimeException("JSRouter::Init::Invoke init(context) first!");
        }

        if (instance == null) {
            synchronized (JSRouter.class) {
                if (instance == null) {
                    instance = new JSRouter();
                }
            }
        }

        return instance;
    }

}
