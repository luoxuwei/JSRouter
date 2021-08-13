package com.luoxuwei.jsrouter.routes;

import com.luoxuwei.jsrouter.demo.MyRoute;
import com.luoxuwei.jsrouter.template.IRouteGroup;

import java.util.Map;

/**
 * Created by 罗旭维 on 2021/8/13.
 */
public class JSRouter$$Group$$app1 implements IRouteGroup {
    @Override
    public void loadInto(Map<String, Class<?>> atlas) {
        atlas.put("MyRoute", MyRoute.class);
    }
}
