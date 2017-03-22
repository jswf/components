package jswf.components.http;

import jswf.components.generic.HttpRequest;
import jswf.components.generic.HttpResponse;
import jswf.framework.AbstractComponent;
import jswf.framework.Environment;
import org.eclipse.jetty.http.HttpStatus;

import java.net.URL;

/**
 * The component logs the uri, schema and method for the request.
 * Then the uri, time taken and response code for the response.
 * It also logs any exception that has been passed through the environment.
 */
public class LogRequestComponent extends AbstractComponent {

    public void invoke(Environment environment) {
        HttpRequest httpRequest = (HttpRequest) environment.getRequest();
        HttpResponse httpResponse = (HttpResponse) environment.getResponse();

        long initialTimestamp = System.currentTimeMillis();

        String protocol = "";

        try {
            URL url = new URL(httpRequest.getRequestURL().toString());
            protocol = url.getProtocol();
        } catch (Exception e) {}

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(initialTimestamp)
                .append(" | -> ")
                .append(httpRequest.getRequestURI())
                .append("?")
                .append(httpRequest.getQueryString())
                .append(" | ")
                .append(protocol.toUpperCase())
                .append(" | ")
                .append(httpRequest.getMethod())
        ;

        System.out.println(stringBuilder);

        next(environment);

        Exception environmentException = environment.getException();
        if (environmentException != null) {
            stringBuilder = new StringBuilder();
            stringBuilder
                    .append(initialTimestamp).append(" | Internal Exception {\n")
                    .append("              |  Class: ").append(environmentException.getClass()).append("\n")
                    .append("              |  Message: ").append(environmentException.getMessage()).append("\n")
                    .append("              |  Stack Trace: { \n")
            ;
            for (StackTraceElement element: environmentException.getStackTrace()) {
                stringBuilder.append("              |   (").append(element.getLineNumber()).append(") ").append(element.getClassName()).append("::").append(element.getMethodName()).append("\n");
            }
            stringBuilder.append("              |  }\n");
            stringBuilder.append("              | }\n");

            System.out.println(stringBuilder);
        }

        int statusCode = httpResponse.getStatus();
        long finalTimestamp = System.currentTimeMillis();

        stringBuilder = new StringBuilder();
        stringBuilder
                .append(finalTimestamp)
                .append(" | <- ")
                .append(httpRequest.getRequestURI())
                .append("?")
                .append(httpRequest.getQueryString())
                .append(" | ")
                .append((finalTimestamp - initialTimestamp))
                .append("ms | ")
                .append(statusCode)
                .append(" ")
                .append(HttpStatus.getMessage(httpResponse.getStatus()))
        ;

        System.out.println(stringBuilder);
    }

}
