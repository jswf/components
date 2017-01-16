package jswf.components.http.routeHandlerComponent;

import jswf.framework.RouteHandlerInterface;
import jswf.framework.RouteInterface;

import java.util.regex.Pattern;

public abstract class AbstractRoute implements RouteInterface {

    protected String name;

    protected String uri;

    protected Pattern compiledPath;

    protected RouteHandlerInterface handler;

    public AbstractRoute setName(String name) {
        this.name = name;

        return this;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public AbstractRoute setUri(String uri) {
        this.uri = uri;

        return this;
    }

    public Pattern getCompiledPath() {
        return compiledPath;
    }

    public AbstractRoute setCompiledPath(Pattern compiledPath) {
        this.compiledPath = compiledPath;

        return this;
    }

    public RouteHandlerInterface getHandler() {
        return handler;
    }

    public AbstractRoute setHandler(RouteHandlerInterface handler) {
        this.handler = handler;

        return this;
    }

}
