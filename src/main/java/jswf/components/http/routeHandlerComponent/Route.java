package jswf.components.http.routeHandlerComponent;

import jswf.framework.RouteHandlerInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Route extends AbstractRoute {

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_PATCH = "PATCH";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_ANY = "ANY";

    public static final String PROTOCOL_HTTP = "HTTP";
    public static final String PROTOCOL_HTTPS = "HTTPS";
    public static final String PROTOCOL_ANY = "HTTPS";

    protected Pattern compiledUri;

    protected ArrayList<String> methods;

    protected RouteHandlerInterface handler;

    protected String protocol = Route.PROTOCOL_ANY;

    protected HashMap<String, String> uriParameters;

    public Route(ArrayList<String> methods, String name, String uri, RouteHandlerInterface handler) {
        this.uriParameters = new HashMap<String, String>();

        this.setName(name);
        this.setHandler(handler);
        this.setMethods(methods);
        this.setUri(uri);
    }

    public Route setUri(String uri) {
        uri = normalizeUri(uri);

        super.setUri(uri);
        setCompiledUri(uri);

        return this;
    }

    public Route setMethods(ArrayList<String> methods) {
        this.methods = methods;

        return this;
    }

    public ArrayList<String> getMethods() {
        return methods;
    }

    public boolean matchesMethod(String method) {
        return (methods.contains(method) || methods.contains(METHOD_ANY));
    }

    public Matcher matcher(String uri) {
        return this.compiledUri.matcher(uri);
    }

    protected void setCompiledUri(String path) {
        String[] segments = path.split("/");

        String regex = "^/";

        int uriParametersCounter = 1;

        if (segments.length > 0) {
            for (String segment : segments) {
                segment = segment.trim();
                if (segment.length() > 0) {
                    if (segment.charAt(0) == '{' && segment.charAt(segment.length() - 1) == '}') {
                        segment = segment.substring(1, segment.length() - 1);
                        String[] parts = segment.split(":");

                        String segmentRegex = "";

                        if (parts.length == 1) {
                            String parameterName = "uriParameter"+uriParametersCounter;

                            segmentRegex = "(?<"+parameterName+">(.*))/";

                            uriParameters.put(parameterName, "");
                            uriParametersCounter++;
                        }

                        if (parts.length == 2) {
                            segmentRegex = "(?<" + parts[0] + ">" + parts[1] + ")/";
                            uriParameters.put(parts[0], "");
                        }

                        regex += segmentRegex;
                    } else {
                        regex += "("+segment+")/";
                    }
                }
            }
        }

        regex = regex.substring(0, regex.length()-1);

        regex += "$";

        this.compiledUri = Pattern.compile(regex);
    }

    public boolean matches(String uri) {
        Matcher matcher = matcher(normalizeUri(uri));

        if (matcher.find()) {
            for (Map.Entry<String, String> parameter: uriParameters.entrySet()) {
                uriParameters.put(parameter.getKey(), matcher.group(parameter.getKey()));
            }

            return true;
        }

        return false;
    }

    public String getUriParameter(String index) {
        return uriParameters.get(index);
    }

    protected String normalizeUri(String uri) {
        uri = uri.replace("//", "/");

        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length()-1);
        }

        return uri;
    }

}
