package com.luoxuwei.jsrouter.template;

import java.util.Map;

/**
 * Created by 罗旭维 on 2021/8/12.
 */
public interface IRouteRoot {
    void loadInto(Map<String, Class<? extends IRouteGroup>> atlas, Map<String, String> index);
}
