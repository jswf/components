package jswf.components.http;

import jswf.components.generic.HttpResponse;
import jswf.components.generic.exceptions.RouteNotFoundException;
import jswf.framework.Environment;
import org.eclipse.jetty.http.HttpStatus;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DummyExceptionRendererComponent extends RouteHandlerComponent {

    public DummyExceptionRendererComponent() {}

    public void invoke(Environment environment) {
        HttpResponse httpResponse = (HttpResponse) environment.getResponse();

        Exception exception = environment.getException();

        if (exception != null) {
            if (exception instanceof RouteNotFoundException) {
                httpResponse.setStatus(HttpStatus.NOT_FOUND_404);
            } else {
                httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
            }

            try {
                String responseContent;
                responseContent = httpResponse.getStatus() + ", " + exception.getMessage() + "\n\n";
                responseContent += "Stack Trace: \n";

                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);

                responseContent += sw.toString();

                httpResponse.addContent(responseContent);
            } catch (Exception e) {}
        }
    }

}
