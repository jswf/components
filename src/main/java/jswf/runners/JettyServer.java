package jswf.runners;

import jswf.components.http.routeHandlerComponent.Request;
import jswf.components.http.routeHandlerComponent.Response;
import jswf.framework.ComponentInterface;
import jswf.framework.Environment;
import jswf.framework.RunnerInterface;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

public class JettyServer extends AbstractHandler implements RunnerInterface {

    protected org.eclipse.jetty.server.Server server;

    protected String hostname = "localhost";

    protected int port = 8888;

    protected int idleTimeOut = 30000;

    protected ComponentInterface component;

    protected HashMap<String, Object> services;

    public JettyServer() {
        server = new org.eclipse.jetty.server.Server();
    }

    public String getHostname() {
        return hostname;
    }

    public JettyServer setHostname(String hostname) {
        this.hostname = hostname;

        return this;
    }

    public int getPort() {
        return port;
    }

    public JettyServer setPort(int port) {
        this.port = port;

        return this;
    }

    public org.eclipse.jetty.server.Server getServer() {
        return server;
    }

    public void run(ComponentInterface component, HashMap<String, Object> services) throws Exception {
        this.component = component;
        this.services = services;

        ServerConnector connector = new ServerConnector(this.server);
        connector.setHost(hostname);
        connector.setPort(port);
        connector.setIdleTimeout(idleTimeOut);

        ContextHandler context = new ContextHandler();
        context.setContextPath("/");
        context.setHandler(this);

        server.addConnector(connector);
        server.setHandler(context);

        server.start();
        server.join();
    }

    public void handle(String target, org.eclipse.jetty.server.Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Environment environment = new Environment(services);
        environment
                .setRequest(new Request(request))
                .setResponse(new Response(response))
                .setCustom("baseRequest", baseRequest)
        ;

        component.invoke(environment);

        baseRequest.setHandled(true);
    }

}
