package jswf.components.http;

import jswf.framework.Environment;

import jswf.components.http.exceptions.RouteNotFoundException;
import jswf.components.http.routeHandlerComponent.Response;

import org.eclipse.jetty.http.HttpStatus;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DummyExceptionRendererComponent extends RouteHandlerComponent {

    public DummyExceptionRendererComponent() {}

    public void invoke(Environment environment) {
        Response response = (Response) environment.getResponse();

        Exception exception = environment.getException();

        if (exception != null) {
            if (exception instanceof RouteNotFoundException) {
                response.setStatus(HttpStatus.NOT_FOUND_404);
            } else {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
            }

            try {
                String responseContent;
                responseContent = response.getStatus() + ", " + exception.getMessage() + "\n\n";
                responseContent += "Stack Trace: \n";

                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);

                responseContent += sw.toString();

                response.addContent(responseContent);
            } catch (Exception e) {}
        }
    }

}
