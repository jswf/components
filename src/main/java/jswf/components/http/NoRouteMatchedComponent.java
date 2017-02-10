package jswf.components.http;

import jswf.components.http.exceptions.RouteNotFoundException;
import jswf.components.http.routeHandlerComponent.Response;
import jswf.framework.Environment;
import jswf.framework.ServiceInterface;

import java.util.HashMap;

public class NoRouteMatchedComponent extends AbstractRouteBasedComponent implements ServiceInterface {

    private HashMap<String, Object> services;

    @Override
    public void invoke(Environment environment) {
        Exception e = environment.getException();

        if (e instanceof RouteNotFoundException) {
            Response response = (Response) environment.getResponse();
            response.setStatus(404);
            next(environment);
        }
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

    @Override
    public void setServices(HashMap<String, Object> services) {
        this.services = services;
    }

}
