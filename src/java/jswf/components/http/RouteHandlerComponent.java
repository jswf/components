package jswf.components.http;

import jswf.components.http.exceptions.RouteNotFoundException;
import jswf.components.http.routeHandlerComponent.Request;
import jswf.components.http.routeHandlerComponent.Response;
import jswf.components.http.routeHandlerComponent.Route;

import jswf.framework.*;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;

public class RouteHandlerComponent extends AbstractComponent {

    protected List<Route> routes;
    protected HashMap<String, List<Route>> initializedRoutes;

    Environment environment;

    public RouteHandlerComponent() {
        routes = new ArrayList<Route>();
        initializedRoutes = new HashMap<String, List<Route>>();
    }

    public void invoke(Environment environment) {
        this.environment = environment;

        Request request = (Request) environment.getRequest();
        Response response = (Response) environment.getResponse();

        String uri = request.getRequestURI();
        if (!uri.endsWith("/")) {
            uri += "/";
        }

        String method = request.getMethod();

        Route route = this.getRouteMatch(method, uri);

        if (route != null) {
            request.setRoute(route);

            try {
                route.getHandler().handle(this.environment);
            } catch (Exception e) {
                environment.setException(e);
            }
        } else {
            RouteNotFoundException exception = new RouteNotFoundException("Route "+uri+" not found.");
            environment.setException(exception);
        }

        next(environment);
    }

    public RouteHandlerComponent addGet(String name, String path, RouteHandlerInterface handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_GET);

        Route route = new Route(methods, name, path, handler);
        addRoute(route);

        return this;
    }

    public RouteHandlerComponent addGet(String path, RouteHandlerInterface handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_GET);

        Route route = new Route(methods, DigestUtils.md5Hex(path), path, handler);
        addRoute(route);

        return this;
    }

    public RouteHandlerComponent addPost(String name, String path, RouteHandlerInterface handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_POST);

        Route route = new Route(methods, name, path, handler);
        addRoute(route);

        return this;
    }

    public RouteHandlerComponent addPost(String path, RouteHandlerInterface handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_POST);

        Route route = new Route(methods, DigestUtils.md5Hex(path), path, handler);
        addRoute(route);

        return this;
    }

    public RouteHandlerComponent addPut(String name, String path, RouteHandlerInterface handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_PUT);

        Route route = new Route(methods, name, path, handler);
        addRoute(route);

        return this;
    }

    public RouteHandlerComponent addPut(String path, RouteHandlerInterface handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_PUT);

        Route route = new Route(methods, DigestUtils.md5Hex(path), path, handler);
        addRoute(route);

        return this;
    }

    public RouteHandlerComponent addDelete(String name, String path, RouteHandlerInterface handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_DELETE);

        Route route = new Route(methods, name, path, handler);
        addRoute(route);

        return this;
    }

    public RouteHandlerComponent addDelete(String path, RouteHandlerInterface handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_DELETE);

        Route route = new Route(methods, DigestUtils.md5Hex(path), path, handler);
        addRoute(route);

        return this;
    }

    public RouteHandlerComponent addAny(String name, String path, RouteHandlerInterface handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_ANY);

        Route route = new Route(methods, name, path, handler);
        addRoute(route);

        return this;
    }

    public RouteHandlerComponent addAny(String path, RouteHandlerInterface handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_ANY);

        Route route = new Route(methods, DigestUtils.md5Hex(path), path, handler);
        addRoute(route);

        return this;
    }

    public void addRoute(Route route) {
        routes.add(route);
    }

    protected Route getRouteMatch(String method, String uri) {
        if (!initializedRoutes.isEmpty()) {
            List<Route> routesForMethod = initializedRoutes.get(method);
            if (routesForMethod != null && !routesForMethod.isEmpty()) {
                for (Route route: routesForMethod) {
                    if (route.matches(uri)) {
                        return route;
                    }
                }
            }
        }

        for (Route route: routes) {
            if (route.matchesMethod(method) && route.matches(uri)) {
                List<Route> routesForMethod = initializedRoutes.get(method);
                if (routesForMethod == null) {
                    routesForMethod = new ArrayList<>();
                    initializedRoutes.put(method, routesForMethod);
                }

                routesForMethod.add(route);

                return route;
            }
        }

        return null;
    }

}
