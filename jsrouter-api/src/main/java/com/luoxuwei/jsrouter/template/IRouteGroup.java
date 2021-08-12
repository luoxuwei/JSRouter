package com.luoxuwei.jsrouter.template;

import java.util.Map;

/**
 * Created by 罗旭维 on 2021/8/12.
 */
public interface IRouteGroup {
    void loadInto(Map<String, Class<?>> atlas);
}
