package com.luoxuwei.jsrouter.routes;

import com.luoxuwei.jsrouter.template.IRouteGroup;
import com.luoxuwei.jsrouter.template.IRouteRoot;

import java.util.Map;

/**
 * Created by 罗旭维 on 2021/8/13.
 */
public class JSRouter$$Root$$app1 implements IRouteRoot {
    @Override
    public void loadInto(Map<String, Class<? extends IRouteGroup>> atlas, Map<String, String> index) {
        index.put("MyRoute", "app1");
        atlas.put("app1", JSRouter$$Group$$app1.class);
    }
}
