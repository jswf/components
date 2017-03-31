package jswf.components.generic;

import jswf.components.generic.exceptions.InvalidRouteParameterValueException;
import jswf.components.generic.exceptions.RouteParameterExpectedException;
import jswf.framework.RouteInterface;

import java.util.ArrayList;
import java.util.HashMap;
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
    protected Map<String, String> uriParametersRegex;
    protected Map<String, String> uriStaticParametersRegex;

    protected Pattern compiledUri;

    protected Pattern compiledPath;

    protected Class<?> handler;

    public HttpRoute(ArrayList<String> methods, String name, String uri, Class<?> handler) {
        this.uriParameters = new HashMap<>();
        this.uriParametersRegex = new HashMap<>();
        this.uriStaticParametersRegex = new HashMap<>();

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

        StringBuilder regex = new StringBuilder("^/");

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

                            segmentRegex = "(?<" + parameterName + ">" + parts[0] + ")";

                            uriParameters.put(parameterName, "");
                            uriParametersRegex.put(parameterName, segmentRegex);
                            uriParametersCounter++;
                        }

                        if (parts.length == 2) {
                            segmentRegex = "(?<" + parts[0] + ">" + parts[1] + ")";
                            uriParameters.put(parts[0], "");
                            uriParametersRegex.put(parts[0], segmentRegex);
                        }

                        if (segmentRegex.length() > 0) {
                            regex.append(segmentRegex).append("/");
                        }
                    } else {
                        regex.append(segment).append("/");
                        uriStaticParametersRegex.put(segment, segment);
                    }
                }
            }
        }

        regex.replace(regex.length()-1, regex.length(), "$");

        this.compiledUri = Pattern.compile(regex.toString());
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

    public Map<String, String> getUriParameters() {
        return uriParameters;
    }

    public Map<String, String> getUriRegexes() {
        return uriParametersRegex;
    }

    public String generateUri(Map<String, String> parameters) throws Exception {
        matchesParameters(parameters);

        String uri = compiledUri.toString();

        for (Map.Entry<String, String> parameter: uriParametersRegex.entrySet()) {
            uri = uri.replace(parameter.getValue(), parameters.get(parameter.getKey()));
        }

        for (Map.Entry<String, String> parameter: uriStaticParametersRegex.entrySet()) {
            uri = uri.replace(parameter.getValue(), parameter.getKey());
        }

        uri = uri.substring(1, uri.length() -1);

        if (uri.length() == 0) {
            uri = "/";
        }

        return uri;
    }

    protected boolean matchesParameters(Map<String, String> parameters) throws Exception {
        for (Map.Entry<String, String> parameter: uriParametersRegex.entrySet()) {
            String parameterKey = parameter.getKey();
            if (parameters.containsKey(parameterKey)) {
                String passedParameterValue = parameters.get(parameterKey);
                Pattern pattern = Pattern.compile(parameter.getValue());
                Matcher matcher = pattern.matcher(passedParameterValue);
                if (!matcher.find()) {
                    StringBuilder sb = new StringBuilder("Invalid parameter [");
                    sb
                            .append(parameterKey).append("] value in Route [").append(name)
                            .append("], parameter expected to be compatible with regex: ")
                            .append(parameter.getValue());
                    throw new InvalidRouteParameterValueException(sb.toString());
                }
            } else {
                StringBuilder sb = new StringBuilder("Route [");
                sb.append(name).append("] expects parameter [").append(parameter.getKey())
                        .append("] to be provided, parameters provided: ").append(String.join(",", parameters.keySet()));
                throw new RouteParameterExpectedException(sb.toString());
            }
        }

        return true;
    }

    protected String normalizeUri(String uri) {
        uri = uri.replace("//", "/");

        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length()-1);
        }

        return uri;
    }

}
