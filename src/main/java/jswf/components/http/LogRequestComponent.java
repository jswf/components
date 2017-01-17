package jswf.components.http;

import jswf.components.http.routeHandlerComponent.Request;
import jswf.components.http.routeHandlerComponent.Response;
import jswf.framework.AbstractComponent;
import jswf.framework.Environment;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class LogRequestComponent extends AbstractComponent {

    private static final Logger logger = LoggerFactory.getLogger("LogRequestComponent");

    public void invoke(Environment environment) {
        Request request = (Request) environment.getRequest();
        Response response = (Response) environment.getResponse();

        long initialTimestamp = System.currentTimeMillis();

        String protocol = "";

        try {
            URL url = new URL(request.getRequestURL().toString());
            protocol = url.getProtocol();
        } catch (Exception e) {}

        System.out.println(initialTimestamp + " | -> " + request.getRequestURI() + " | " + protocol.toUpperCase() + " | " + request.getMethod());

        next(environment);

        Exception environmentException = environment.getException();
        if (environmentException != null) {
            System.out.println(initialTimestamp + " | Internal Exception {");
            System.out.println("              | Class: " + environmentException.getClass());
            System.out.println("              | Message: " + environmentException.getMessage());
            System.out.println("              | Internal Exception }");
        }

        int statusCode = response.getStatus();
        long finalTimestamp = System.currentTimeMillis();
        System.out.println(finalTimestamp + " | <- " + request.getRequestURI() + " | " + (finalTimestamp - initialTimestamp) + "ms | " +  statusCode + " " + HttpStatus.getMessage(response.getStatus()));
    }

}
