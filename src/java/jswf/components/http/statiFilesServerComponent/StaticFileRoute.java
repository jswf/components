package jswf.components.http.statiFilesServerComponent;

import jswf.components.http.routeHandlerComponent.Route;
import jswf.framework.RouteHandlerInterface;

import java.util.ArrayList;

public class StaticFileRoute extends Route {

    protected String path;

    public StaticFileRoute(ArrayList<String> methods, String path, String uri, RouteHandlerInterface handler) {
        super(methods, path, uri, handler);

        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
