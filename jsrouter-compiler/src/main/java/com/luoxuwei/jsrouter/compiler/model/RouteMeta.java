package com.luoxuwei.jsrouter.compiler.model;

import com.luoxuwei.jsrouter.annotation.JSRoute;

import java.util.Map;

import javax.lang.model.element.Element;

/**
 * Created by 罗旭维 on 2021/8/13.
 */
public class RouteMeta {
    private Element rawType;        // Raw type of route
    private Class<?> destination;   // Destination
    private String path;            // Path of route
    private String group;           // Group of route

    public RouteMeta() {
    }

    public RouteMeta(JSRoute route, Element element) {
        rawType = element;
        path = route.path();
        group = route.group();
    }

    public Element getRawType() {
        return rawType;
    }

    public RouteMeta setRawType(Element rawType) {
        this.rawType = rawType;
        return this;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public RouteMeta setDestination(Class<?> destination) {
        this.destination = destination;
        return this;
    }

    public String getPath() {
        return path;
    }

    public RouteMeta setPath(String path) {
        this.path = path;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public RouteMeta setGroup(String group) {
        this.group = group;
        return this;
    }

    @Override
    public String toString() {
        return "RouteMeta{" +
                ", rawType=" + rawType +
                ", destination=" + destination +
                ", path='" + path + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}
