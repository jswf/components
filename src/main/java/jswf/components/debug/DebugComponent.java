package jswf.components.debug;

import jswf.components.generic.HttpRoute;
import jswf.components.generic.HttpRequest;
import jswf.components.generic.HttpResponse;
import jswf.components.http.AbstractRouteBasedComponent;
import jswf.framework.AbstractComponent;
import jswf.framework.Environment;
import jswf.framework.ServiceInterface;
import jswf.framework.ServicesContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebugComponent extends AbstractComponent {

    Environment environment;

    @Override
    public void invoke(Environment environment) {
        this.environment = environment;

        HttpRequest httpRequest = (HttpRequest) environment.getRequest();
        HttpResponse httpResponse = (HttpResponse) environment.getResponse();

        String uri = httpRequest.getRequestURI();
        if (uri.startsWith("/framework/debug")) {

            try {
                httpResponse.addContent("<h1>Framework Debug!</h1><br>");

                // Services
                httpResponse.addContent("<h3>Services:</h3><br>");
                HashMap<String, ServiceInterface> services = ServicesContainer.getServices();

                httpResponse.addContent("<table border='1'>");
                httpResponse.addContent("<thead><tr>");
                httpResponse.addContent("<th>Name</th><th>Class</th>");
                httpResponse.addContent("</tr></thead>");
                httpResponse.addContent("<tbody>");
                for (Map.Entry<String, ServiceInterface> entry : services.entrySet()) {
                    httpResponse.addContent("<tr><td>" + entry.getKey() + "</td><td>" + entry.getValue().getClass() + "</td></tr>");
                }
                httpResponse.addContent("</tbody>");
                httpResponse.addContent("</table>");


                // Routes
                httpResponse.addContent("<h3>HTTP Routes:</h3><br>");


                httpResponse.addContent("<table border='1'>");
                httpResponse.addContent("<thead><tr>");
                httpResponse.addContent("<th>Name</th><th>Path</th><th>Handler</th>");
                httpResponse.addContent("</tr></thead>");
                httpResponse.addContent("<tbody>");

                for (Map.Entry<String, ServiceInterface> entry : services.entrySet()) {
                    ServiceInterface service = (ServiceInterface) entry.getValue();

                    if (service instanceof AbstractRouteBasedComponent) {
                        AbstractRouteBasedComponent routeBasedComponent = (AbstractRouteBasedComponent) service;

                        List<HttpRoute> routes = routeBasedComponent.getRoutes();

                        httpResponse.addContent("<tr><td colspan='3'><strong>Component: " + service.getClass().toString() + "</strong></td></tr>");

                        for (HttpRoute route : routes) {
                            httpResponse.addContent("<tr><td>" + route.getName() + "</td><td>" + route.getPath() + "</td><td>" + route.getHandler().getName() + "</td></tr>");
                        }
                    }
                }

                httpResponse.addContent("</tbody>");
                httpResponse.addContent("</table>");

            } catch (Exception e) {
                environment.setException(e);
            }

        } else {
            next(environment);
        }
    }

}
