package jswf.components.http;

import jswf.components.generic.HttpResponse;
import jswf.components.generic.exceptions.RouteNotFoundException;
import jswf.framework.AbstractComponent;
import jswf.framework.Environment;

public class NoRouteMatchedComponent extends AbstractComponent {

    @Override
    public void invoke(Environment environment) {
        Exception e = environment.getException();

        if (e instanceof RouteNotFoundException) {
            HttpResponse httpResponse = (HttpResponse) environment.getResponse();
            httpResponse.setStatus(404);
            next(environment);
        }
    }

}
