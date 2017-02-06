package jswf.components.http;

import jswf.components.generic.EnvironmentStatus;
import jswf.components.generic.RequestHandlerInterface;
import jswf.components.http.exceptions.RouteNotFoundException;
import jswf.components.http.routeHandlerComponent.Request;
import jswf.components.http.routeHandlerComponent.Response;
import jswf.components.http.routeHandlerComponent.Route;
import jswf.framework.Environment;
import jswf.framework.ServiceInterface;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.InvalidClassException;
import java.util.ArrayList;

public class RouteHandlerComponent extends AbstractRouteBasedComponent implements ServiceInterface {

    Environment environment;

    public RouteHandlerComponent() {
        super();
    }

    public void invoke(Environment environment) {
        if (environment.isStatus(EnvironmentStatus.REQUEST_HANDLED)) {
            next(environment);
            return;
        }

        if (environment.hasException()) {
            next(environment);
            return;
        }

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
                Class<?> clazz = route.getHandler();
                Object instance = clazz.newInstance();
                if (instance instanceof RequestHandlerInterface) {
                    RequestHandlerInterface handler = (RequestHandlerInterface) instance;
                    handler.handle(this.environment);
                    environment.setStatus(EnvironmentStatus.REQUEST_HANDLED);
                } else {
                    throw new InvalidClassException(clazz.toString() + " must implement " + RequestHandlerInterface.class.getName());
                }
            } catch (Exception e) {
                environment.setException(e);
                environment.setStatus(EnvironmentStatus.REQUEST_HANDLED);
            }
        } else {
            RouteNotFoundException exception = new RouteNotFoundException("Route "+uri+" not found.");
            environment.setException(exception);
        }

        next(environment);
    }

    public RouteHandlerComponent addGet(String name, String path, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_GET);

        Route route = new Route(methods, name, path, handler);
        addRoute(route);

        return this;
    }

    public RouteHandlerComponent addGet(String path, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_GET);

        Route route = new Route(methods, DigestUtils.md5Hex(path), path, handler);
        addRoute(route);

        return this;
    }

    public RouteHandlerComponent addPost(String name, String path, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_POST);

        Route route = new Route(methods, name, path, handler);
        addRoute(route);

        return this;
    }

    public RouteHandlerComponent addPost(String path, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_POST);

        Route route = new Route(methods, DigestUtils.md5Hex(path), path, handler);
        addRoute(route);

        return this;
    }

    public RouteHandlerComponent addPut(String name, String path, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_PUT);

        Route route = new Route(methods, name, path, handler);
        addRoute(route);

        return this;
    }

    public RouteHandlerComponent addPut(String path, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_PUT);

        Route route = new Route(methods, DigestUtils.md5Hex(path), path, handler);
        addRoute(route);

        return this;
    }

    public RouteHandlerComponent addDelete(String name, String path, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_DELETE);

        Route route = new Route(methods, name, path, handler);
        addRoute(route);

        return this;
    }

    public RouteHandlerComponent addDelete(String path, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_DELETE);

        Route route = new Route(methods, DigestUtils.md5Hex(path), path, handler);
        addRoute(route);

        return this;
    }

    public RouteHandlerComponent addAny(String name, String path, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_ANY);

        Route route = new Route(methods, name, path, handler);
        addRoute(route);

        return this;
    }

    public RouteHandlerComponent addAny(String path, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_ANY);

        Route route = new Route(methods, DigestUtils.md5Hex(path), path, handler);
        addRoute(route);

        return this;
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }
}
