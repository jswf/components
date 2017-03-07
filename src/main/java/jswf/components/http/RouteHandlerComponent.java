package jswf.components.http;

import jswf.components.generic.EnvironmentStatus;
import jswf.components.generic.HttpRequest;
import jswf.components.generic.HttpRoute;
import jswf.components.generic.RequestHandlerInterface;
import jswf.components.generic.exceptions.RouteNotFoundException;
import jswf.framework.Environment;

import java.io.InvalidClassException;

public class RouteHandlerComponent extends AbstractRouteBasedComponent {

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

        HttpRequest httpRequest = (HttpRequest) environment.getRequest();

        String uri = httpRequest.getRequestURI();
        if (!uri.endsWith("/")) {
            uri += "/";
        }

        String method = httpRequest.getMethod();

        HttpRoute route = this.getRouteMatch(method, uri);

        if (route != null) {
            httpRequest.setRoute(route);

            try {
                Class<?> clazz = route.getHandler();
                Object instance = clazz.newInstance();
                if (instance instanceof RequestHandlerInterface) {
                    RequestHandlerInterface handler = (RequestHandlerInterface) instance;
                    handler.handle(environment);
                    environment.setStatus(EnvironmentStatus.REQUEST_HANDLED);
                } else {
                    throw new InvalidClassException(
                            (new StringBuilder(clazz.toString()))
                                    .append(" must implement ").append(RequestHandlerInterface.class.getName())
                                    .toString()
                    );
                }
            } catch (Exception e) {
                environment.setException(e);
                environment.setStatus(EnvironmentStatus.REQUEST_HANDLED);
            }
        } else {
            RouteNotFoundException exception = new RouteNotFoundException(
                    (new StringBuilder("Route ")).append(uri).append(" not found.").toString()
            );
            environment.setException(exception);
        }

        next(environment);
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

}
