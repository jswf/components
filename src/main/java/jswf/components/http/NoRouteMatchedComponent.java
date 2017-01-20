package jswf.components.http;

import jswf.components.http.exceptions.RouteNotFoundException;
import jswf.components.http.routeHandlerComponent.Response;
import jswf.framework.Environment;
import jswf.framework.ServiceInterface;

public class NoRouteMatchedComponent extends AbstractRouteBasedComponent implements ServiceInterface {

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

}
