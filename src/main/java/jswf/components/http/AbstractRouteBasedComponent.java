package jswf.components.http;

import jswf.components.generic.HttpRoute;
import jswf.framework.AbstractComponent;
import jswf.framework.ServiceInterface;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

abstract public class AbstractRouteBasedComponent extends AbstractComponent implements ServiceInterface {

    protected HashMap<String, Object> services;

    protected List<HttpRoute> routes;
    protected HashMap<String, List<HttpRoute>> initializedRoutes;

    public AbstractRouteBasedComponent() {
        routes = new ArrayList<HttpRoute>();
        initializedRoutes = new HashMap<String, List<HttpRoute>>();
    }

    public void addRoute(HttpRoute route) {
        routes.add(route);
    }

    public HttpRoute getRouteMatch(String method, String uri) {
        if (!initializedRoutes.isEmpty()) {
            List<HttpRoute> routesForMethod = initializedRoutes.get(method);
            if (routesForMethod != null && !routesForMethod.isEmpty()) {
                for (HttpRoute route: routesForMethod) {
                    if (route.matches(uri)) {
                        return route;
                    }
                }
            }
        }

        for (HttpRoute route: routes) {
            if (route.matchesMethod(method) && route.matches(uri)) {
                List<HttpRoute> routesForMethod = initializedRoutes.computeIfAbsent(method, k -> new ArrayList<>());

                routesForMethod.add(route);

                return route;
            }
        }

        return null;
    }

    public List<HttpRoute> getRoutes() {
        return routes;
    }

    public AbstractRouteBasedComponent addGet(String name, String uri, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(HttpRoute.METHOD_GET);

        HttpRoute route = new HttpRoute(methods, name, uri, handler);
        addRoute(route);

        return this;
    }

    public AbstractRouteBasedComponent addGet(String uri, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(HttpRoute.METHOD_GET);

        HttpRoute route = new HttpRoute(methods, DigestUtils.md5Hex(uri), uri, handler);
        addRoute(route);

        return this;
    }

    public AbstractRouteBasedComponent addPost(String name, String uri, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(HttpRoute.METHOD_POST);

        HttpRoute route = new HttpRoute(methods, name, uri, handler);
        addRoute(route);

        return this;
    }

    public AbstractRouteBasedComponent addPost(String uri, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(HttpRoute.METHOD_POST);

        HttpRoute route = new HttpRoute(methods, DigestUtils.md5Hex(uri), uri, handler);
        addRoute(route);

        return this;
    }

    public AbstractRouteBasedComponent addPut(String name, String uri, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(HttpRoute.METHOD_PUT);

        HttpRoute route = new HttpRoute(methods, name, uri, handler);
        addRoute(route);

        return this;
    }

    public AbstractRouteBasedComponent addPut(String uri, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(HttpRoute.METHOD_PUT);

        HttpRoute route = new HttpRoute(methods, DigestUtils.md5Hex(uri), uri, handler);
        addRoute(route);

        return this;
    }

    public AbstractRouteBasedComponent addDelete(String name, String uri, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(HttpRoute.METHOD_DELETE);

        HttpRoute route = new HttpRoute(methods, name, uri, handler);
        addRoute(route);

        return this;
    }

    public AbstractRouteBasedComponent addDelete(String uri, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(HttpRoute.METHOD_DELETE);

        HttpRoute route = new HttpRoute(methods, DigestUtils.md5Hex(uri), uri, handler);
        addRoute(route);

        return this;
    }

    public AbstractRouteBasedComponent addAny(String name, String uri, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(HttpRoute.METHOD_ANY);

        HttpRoute route = new HttpRoute(methods, name, uri, handler);
        addRoute(route);

        return this;
    }

    public AbstractRouteBasedComponent addAny(String uri, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(HttpRoute.METHOD_ANY);

        HttpRoute route = new HttpRoute(methods, DigestUtils.md5Hex(uri), uri, handler);
        addRoute(route);

        return this;
    }

    @Override
    public void setServices(HashMap<String, Object> services) {
        this.services = services;
    }

}
