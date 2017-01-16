package java.jswf.components.http;

import jswf.framework.Environment;

import java.jswf.components.http.routeHandlerComponent.Route;
import java.jswf.components.http.routeHandlerComponent.Request;
import java.jswf.components.http.routeHandlerComponent.Response;
import java.jswf.components.http.statiFilesServerComponent.StaticFileRoute;

import jswf.framework.RouteHandlerInterface;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.HttpOutput;
import org.eclipse.jetty.util.URIUtil;

import java.io.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class StaticFilesServerComponent extends RouteHandlerComponent implements RouteHandlerInterface {

    private String basePath;

    private ArrayList<String> allowedFileExtensions;

    private MimeTypes mimeTypes;

    public StaticFilesServerComponent() {
        routes = new ArrayList<>();
        allowedFileExtensions = new ArrayList<>();
        initializedRoutes = new HashMap<>();

        allowedFileExtensions.add("html");
        allowedFileExtensions.add("css");
        allowedFileExtensions.add("js");

        allowedFileExtensions.add("jpeg");
        allowedFileExtensions.add("jpg");
        allowedFileExtensions.add("png");
        allowedFileExtensions.add("gif");

        allowedFileExtensions.add("otf");
        allowedFileExtensions.add("eot");
        allowedFileExtensions.add("svg");
        allowedFileExtensions.add("ttf");
        allowedFileExtensions.add("woff");
        allowedFileExtensions.add("woff2");

        mimeTypes = new MimeTypes();
    }

    public String getBasePath() {
        return basePath;
    }

    public StaticFilesServerComponent setBasePath(String basePath) {
        this.basePath = basePath;

        return this;
    }

    public StaticFilesServerComponent addPath(String path, String uri) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_GET);
        methods.add(Route.METHOD_HEAD);

        Route route = new StaticFileRoute(methods, path, uri, this);
        addRoute(route);

        return this;
    }

    public StaticFilesServerComponent addAllowedFileExtension(String fileExtension) {
        allowedFileExtensions.add(fileExtension);

        return this;
    }

    public void invoke(Environment environment) {
        Request request = (Request) environment.getRequest();

        String uri = request.getRequestURI();
        String method = request.getMethod();

        Route route = this.getRouteMatch(method, uri);

        if (route != null) {
            request.setRoute(route);

            try {
                route.getHandler().handle(environment);
            } catch (Exception e) {
                if (!(e instanceof FileNotFoundException)) {
                    environment.setException(e);
                }

                next(environment);
            }
        } else {
            next(environment);
        }
    }

    public void handle(Environment environment) throws Exception {
        Request request = (Request) environment.getRequest();
        Response response = (Response) environment.getResponse();

        StaticFileRoute route = (StaticFileRoute) request.getRoute();

        String path = URIUtil.addPaths(basePath, route.getPath());
        path = URIUtil.addPaths(path, request.getRequestURI());

        File file = new File(path);

        if (!file.exists()) {
            throw new FileNotFoundException("Static file [" + path + "] does not exists.");
        }

        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf('.')+1);

        if (allowedFileExtensions.size() > 0 && !allowedFileExtensions.contains(fileExtension)) {
            throw new FileNotFoundException("Static file [" + path + "] was found but is not allowed by the extension [" + fileExtension + "].");
        }

        String lastModified = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.of("GMT")));

        String ifModifiedSinceHeader = request.getHeader("If-Modified-Since");
        if (lastModified.equals(ifModifiedSinceHeader)) {
            response.setStatus(304);
        } else {
            final FileInputStream fileInputStream = new FileInputStream(file);

            HttpOutput httpOutputStream = (HttpOutput) response.getOutputStream();

            response.addHeader(HttpHeader.LAST_MODIFIED.toString(), lastModified);
            response.setContentType(mimeTypes.getMimeByExtension(path));
            response.setContentLength(Math.toIntExact(file.length()));

            httpOutputStream.sendContent(fileInputStream);

            httpOutputStream.close();
            fileInputStream.close();
        }

    }

}
