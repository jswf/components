package jswf.components.http;

import jswf.components.generic.HttpRequest;
import jswf.components.generic.HttpResponse;
import jswf.framework.AbstractComponent;
import jswf.framework.Environment;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * The component logs the uri, schema and method for the request.
 * Then the uri, time taken and response code for the response.
 * It also logs any exception that has been passed through the environment.
 */
public class LogRequestComponent extends AbstractComponent {

    private static final Logger logger = LoggerFactory.getLogger("LogRequestComponent");

    public void invoke(Environment environment) {
        HttpRequest httpRequest = (HttpRequest) environment.getRequest();
        HttpResponse httpResponse = (HttpResponse) environment.getResponse();

        long initialTimestamp = System.currentTimeMillis();

        String protocol = "";

        try {
            URL url = new URL(httpRequest.getRequestURL().toString());
            protocol = url.getProtocol();
        } catch (Exception e) {}

        System.out.println(initialTimestamp + " | -> " + httpRequest.getRequestURI() + " | " + protocol.toUpperCase() + " | " + httpRequest.getMethod());

        next(environment);

        Exception environmentException = environment.getException();
        if (environmentException != null) {
            System.out.println(initialTimestamp + " | Internal Exception {");
            System.out.println("              |  Class: " + environmentException.getClass());
            System.out.println("              |  Message: " + environmentException.getMessage());
            System.out.println("              |  Stack Trace: { ");
            for (StackTraceElement element: environmentException.getStackTrace()) {
                System.out.println("              |   (" + element.getLineNumber() + ") " + element.getClassName() + "::" + element.getMethodName());
            }
            System.out.println("              |  }");
            System.out.println("              | }");
        }

        int statusCode = httpResponse.getStatus();
        long finalTimestamp = System.currentTimeMillis();
        System.out.println(finalTimestamp + " | <- " + httpRequest.getRequestURI() + " | " + (finalTimestamp - initialTimestamp) + "ms | " +  statusCode + " " + HttpStatus.getMessage(httpResponse.getStatus()));
    }

}
