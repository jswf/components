package jswf.components.generic;

import jswf.framework.RouteInterface;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRoute implements RouteInterface {

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_PATCH = "PATCH";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_COPY = "COPY";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_LINK = "LINK";
    public static final String METHOD_UNLINK = "UNLINK";
    public static final String METHOD_PURGE = "PURGE";
    public static final String METHOD_LOCK = "LOCK";
    public static final String METHOD_UNLOCK = "UNLOCK";
    public static final String METHOD_PROPFIND = "PROPFIND";
    public static final String METHOD_VIEW = "VIEW";
    public static final String METHOD_ANY = "ANY";

    public static final String PROTOCOL_HTTP = "HTTP";
    public static final String PROTOCOL_HTTPS = "HTTPS";
    public static final String PROTOCOL_ANY = "ANY";

    protected String name;

    protected String uri;

    protected String path;

    protected ArrayList<String> methods;

    protected String protocol = HttpRoute.PROTOCOL_ANY;

    protected Map<String, String> uriParameters;

    protected Pattern compiledUri;

    protected Pattern compiledPath;

    protected Class<?> handler;

    public HttpRoute(ArrayList<String> methods, String name, String uri, Class<?> handler) {
        this.uriParameters = new IdentityHashMap<>();

        this.setName(name);
        this.setHandler(handler);
        this.setMethods(methods);
        this.setUri(uri);
    }

    public HttpRoute setName(String name) {
        this.name = name;

        return this;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public HttpRoute setUri(String uri) {
        setPath(uri);
        uri = normalizeUri(uri);

        this.uri = uri;
        setCompiledUri(uri);

        return this;
    }

    public HttpRoute setPath(String path) {
        this.path = path;

        return this;
    }

    public String getPath() {
        return path;
    }

    public HttpRoute setMethods(ArrayList<String> methods) {
        this.methods = methods;

        return this;
    }

    public ArrayList<String> getMethods() {
        return methods;
    }


    public HttpRoute setCompiledPath(Pattern compiledPath) {
        this.compiledPath = compiledPath;

        return this;
    }

    public Pattern getCompiledPath() {
        return compiledPath;
    }

    public HttpRoute setHandler(Class<?> handler) {
        this.handler = handler;

        return this;
    }

    public Class<?> getHandler() {
        return handler;
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
