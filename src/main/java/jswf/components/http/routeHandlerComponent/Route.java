package jswf.components.http.routeHandlerComponent;

import jswf.components.generic.AbstractRoute;

import java.util.ArrayList;
import java.util.HashMap;

public class Route extends AbstractRoute {

    public Route(ArrayList<String> methods, String name, String uri, Class<?> handler) {
        this.uriParameters = new HashMap<String, String>();

        this.setName(name);
        this.setHandler(handler);
        this.setMethods(methods);
        this.setUri(uri);
    }

}
