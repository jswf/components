package jswf.components.ws.webSocketComponent;

import jswf.framework.Environment;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;

public class CustomWebSocketServerFactory extends WebSocketServerFactory {

    Environment environment;

    public CustomWebSocketServerFactory(WebSocketPolicy policy, ByteBufferPool bufferPool) {
        super(policy, bufferPool);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp)
    {
        Object pojo = super.createWebSocket(req, resp);

        if (pojo instanceof CustomSocketInterface) {
            ((CustomSocketInterface) pojo).setEnvironment(environment);
        }

        return pojo;
    }

}
