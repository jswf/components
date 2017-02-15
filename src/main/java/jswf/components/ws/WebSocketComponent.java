package jswf.components.ws;

import jswf.components.generic.EnvironmentStatus;
import jswf.components.generic.HttpRequest;
import jswf.components.generic.HttpResponse;
import jswf.components.generic.HttpRoute;
import jswf.components.http.AbstractRouteBasedComponent;
import jswf.components.ws.webSocketComponent.CustomSocketInterface;
import jswf.components.ws.webSocketComponent.CustomWebSocketServerFactory;
import jswf.framework.Environment;

import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.InvalidClassException;
import java.util.ArrayList;

public class WebSocketComponent extends AbstractRouteBasedComponent {

    public WebSocketComponent() {}

    public WebSocketComponent addRoute(String uri, String name, Class<?> handler) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(HttpRoute.METHOD_GET);
        methods.add(HttpRoute.METHOD_POST);

        addRoute(new HttpRoute(methods, name, uri, handler));

        return this;
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
        String method = httpRequest.getMethod();

        HttpRoute route = this.getRouteMatch(method, uri);

        if (route != null) {
            httpRequest.setRoute(route);

            try {
                if (!handle(environment, route)) {
                    next(environment);
                }
            } catch (Exception e) {
                if (!(e instanceof FileNotFoundException)) {
                    environment.setException(e);
                }

                next(environment);
            }
        } else {
            next(environment);
        }
    }

    public boolean handle(Environment environment, HttpRoute route) throws Exception {
        HttpRequest httpRequest = (HttpRequest) environment.getRequest();
        HttpResponse httpResponse = (HttpResponse) environment.getResponse();

        org.eclipse.jetty.server.Request baseRequest = (org.eclipse.jetty.server.Request) environment.getCustom("baseRequest");

        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
        CustomWebSocketServerFactory webSocketFactory = new CustomWebSocketServerFactory(baseRequest.getContext(), policy, new MappedByteBufferPool());
        webSocketFactory.setEnvironment(environment);

        Class<?> clazz = route.getHandler();
        if (CustomSocketInterface.class.isAssignableFrom(clazz)) {
            webSocketFactory.register(route.getHandler());
        } else {
            throw new InvalidClassException(clazz.toString() + " must implement " + CustomSocketInterface.class.getName());
        }

        HttpServletRequest servletRequest = httpRequest.getHttpServletRequest();
        HttpServletResponse servletResponse = httpResponse.getHttpServletResponse();

        if (webSocketFactory.isUpgradeRequest(servletRequest, servletResponse))
        {
            webSocketFactory.start();

            // We have an upgrade httpRequest
            if (webSocketFactory.acceptWebSocket(servletRequest, servletResponse)) {
                return true;
            }

            // If we reach this point, it means we had an incoming httpRequest to upgrade
            // but it was either not a proper websocket upgrade, or it was possibly rejected
            // due to incoming httpRequest constraints (controlled by WebSocketCreator)
            if (servletResponse.isCommitted()) {
                // not much we can do at this point.
                return true;
            }
        }

        return false;
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

}
