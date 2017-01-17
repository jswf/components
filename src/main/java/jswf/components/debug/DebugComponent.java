package jswf.components.debug;

import jswf.components.http.AbstractRouteBasedComponent;
import jswf.components.http.routeHandlerComponent.Request;
import jswf.components.http.routeHandlerComponent.Response;
import jswf.components.http.routeHandlerComponent.Route;
import jswf.framework.AbstractComponent;
import jswf.framework.Environment;
import jswf.framework.ServiceInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebugComponent extends AbstractComponent {

    Environment environment;

    @Override
    public void invoke(Environment environment) {
        this.environment = environment;

        Request request = (Request) environment.getRequest();
        Response response = (Response) environment.getResponse();

        String uri = request.getRequestURI();
        if (uri.startsWith("/framework/debug")) {

            try {
                response.addContent("<h1>Framework Debug!</h1><br>");

                // Services
                response.addContent("<h3>Services:</h3><br>");
                HashMap<String, Object> services = environment.getServices();

                response.addContent("<table border='1'>");
                response.addContent("<thead><tr>");
                response.addContent("<th>Name</th><th>Class</th>");
                response.addContent("</tr></thead>");
                response.addContent("<tbody>");
                for (Map.Entry<String, Object> entry : services.entrySet()) {
                    response.addContent("<tr><td>" + entry.getKey() + "</td><td>" + entry.getValue().getClass() + "</td></tr>");
                }
                response.addContent("</tbody>");
                response.addContent("</table>");


                // Routes
                response.addContent("<h3>HTTP Routes:</h3><br>");


                response.addContent("<table border='1'>");
                response.addContent("<thead><tr>");
                response.addContent("<th>Name</th><th>Path</th><th>Handler</th>");
                response.addContent("</tr></thead>");
                response.addContent("<tbody>");

                for (Map.Entry<String, Object> entry : services.entrySet()) {
                    ServiceInterface service = (ServiceInterface) entry.getValue();

                    if (service instanceof AbstractRouteBasedComponent) {
                        AbstractRouteBasedComponent routeHandlerComponent = (AbstractRouteBasedComponent) service;

                        List<Route> routes = routeHandlerComponent.getRoutes();

                        response.addContent("<tr><td colspan='3'><strong>Component: " + service.getClass().toString() + "</strong></td></tr>");

                        for (Route route : routes) {
                            response.addContent("<tr><td>" + route.getName() + "</td><td>" + route.getPath() + "</td><td>" + route.getHandler().getName() + "</td></tr>");
                        }
                    }
                }

                response.addContent("</tbody>");
                response.addContent("</table>");

            } catch (Exception e) {
                environment.setException(e);
            }

        } else {
            next(environment);
        }
    }

}
