package jswf.components.http;

import jswf.components.http.routeHandlerComponent.Route;
import jswf.framework.AbstractComponent;
import jswf.framework.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

abstract public class AbstractRouteBasedComponent extends AbstractComponent {

    protected List<Route> routes;
    protected HashMap<String, List<Route>> initializedRoutes;

    Environment environment;

    public AbstractRouteBasedComponent() {
        routes = new ArrayList<Route>();
        initializedRoutes = new HashMap<String, List<Route>>();
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

    public List<Route> getRoutes() {
        return routes;
    }
}
