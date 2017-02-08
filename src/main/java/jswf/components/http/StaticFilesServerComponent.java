package jswf.components.http;

import jswf.components.generic.EnvironmentStatus;
import jswf.components.http.routeHandlerComponent.Request;
import jswf.components.http.routeHandlerComponent.Response;
import jswf.components.http.routeHandlerComponent.Route;
import jswf.components.http.staticFilesServerComponent.StaticFileHandler;
import jswf.components.http.staticFilesServerComponent.StaticFileRoute;

import jswf.framework.Environment;
import jswf.framework.ServiceInterface;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.util.URIUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InvalidClassException;
import java.util.ArrayList;

/**
 * Component to serve static files.
 */
public class StaticFilesServerComponent extends AbstractRouteBasedComponent implements ServiceInterface {

    private String basePath;

    private ArrayList<String> allowedFileExtensions;

    private MimeTypes mimeTypes;

    public StaticFilesServerComponent() {
        super();

        allowedFileExtensions = new ArrayList<>();

        allowedFileExtensions.add("html");
        allowedFileExtensions.add("css");
        allowedFileExtensions.add("map");
        allowedFileExtensions.add("js");

        allowedFileExtensions.add("jpeg");
        allowedFileExtensions.add("jpg");
        allowedFileExtensions.add("png");
        allowedFileExtensions.add("gif");
        allowedFileExtensions.add("ico");

        allowedFileExtensions.add("otf");
        allowedFileExtensions.add("eot");
        allowedFileExtensions.add("svg");
        allowedFileExtensions.add("ttf");
        allowedFileExtensions.add("woff");
        allowedFileExtensions.add("woff2");

        allowedFileExtensions.add("txt");
        allowedFileExtensions.add("xml");
        allowedFileExtensions.add("csv");

        allowedFileExtensions.add("mp4");
        allowedFileExtensions.add("mpg");
        allowedFileExtensions.add("mpeg");
        allowedFileExtensions.add("ogg");
        allowedFileExtensions.add("swf");

        mimeTypes = new MimeTypes();
    }

    /**
     * Returns the basePath value
     *
     * @return A string or null if it was not set
     */
    public String getBasePath() {
        return basePath;
    }

    /**
     * Sets the base directory. It could be your application level directory or any other location outside the application.
     *
     * @param basePath The base path string
     * @return this
     */
    public StaticFilesServerComponent setBasePath(String basePath) {
        this.basePath = basePath;

        return this;
    }

    /**
     * Adds a path to the list. If the $uri is matched to the request uri the path will be used to locate the file.
     * $uri could be a regular expression. Use: /{(.*)*} to match the path structure plus the filename and extension.
     *
     * @param path Path to the files to be served
     * @param uri Uri to match against the Request uri
     * @return
     */
    public StaticFilesServerComponent addPath(String path, String uri) {
        ArrayList<String> methods = new ArrayList<>();
        methods.add(Route.METHOD_GET);
        methods.add(Route.METHOD_HEAD);

        addRoute(new StaticFileRoute(methods, path, uri, StaticFileHandler.class));

        return this;
    }

    /**
     * By default the component has some allowed extensions ("html","css","map","js", "jpeg","jpg","png","gif", "otf","eot","svg","ttf","woff","woff2")
     * USe this method to add custom allowed extensions.
     *
     * @param fileExtension Extension to be allowed without the dot, i.e: mp4
     * @return this
     */
    public StaticFilesServerComponent addAllowedFileExtension(String fileExtension) {
        allowedFileExtensions.add(fileExtension);

        return this;
    }

    /**
     * Use this method if you want to override the allowed extensions.
     * The extensions in the array should be defined without the dot i.e.: mp4
     *
     * @return this
     */
    public StaticFilesServerComponent setAllowedFileExtension(ArrayList<String> extensions) {
        allowedFileExtensions = extensions;

        return this;
    }

    public void invoke(Environment environment) {
        if (environment.isStatus(EnvironmentStatus.REQUEST_HANDLED)) {
            next(environment);
            return;
        }

        if (environment.hasException()) {
            next(environment);
            return;
        }

        Request request = (Request) environment.getRequest();

        StaticFileRoute route = (StaticFileRoute) this.getRouteMatch(request.getMethod(), request.getRequestURI());

        if (route != null) {
            request.setRoute(route);

            try {
                String path = URIUtil.addPaths(basePath, route.getPath());
                path = URIUtil.addPaths(path, request.getRequestURI());

                File file = new File(path);

                if (!file.exists()) {
                    throw new FileNotFoundException("Static file [" + path + "] does not exist.");
                }

                String fileName = file.getName();
                String fileExtension = FilenameUtils.getExtension(fileName);

                if (allowedFileExtensions.size() > 0 && !allowedFileExtensions.contains(fileExtension)) {
                    throw new FileNotFoundException("Static file [" + path + "] was found but is not allowed due to the extension [" + fileExtension + "].");
                }

                Class<?> clazz = route.getHandler();
                Object instance = clazz.newInstance();
                if (instance instanceof StaticFileHandler) {
                    StaticFileHandler handler = (StaticFileHandler) instance;
                    environment.setCustom(StaticFileHandler.FILE, file);
                    environment.setCustom(StaticFileHandler.MIME_TYPES, mimeTypes);
                    handler.handle(environment);
                } else {
                    throw new InvalidClassException(clazz.toString() + " must implement " + StaticFileHandler.class);
                }
            } catch (Exception e) {
                environment.setStatus(EnvironmentStatus.REQUEST_HANDLED);
                environment.setException(e);

                Response response = (Response) environment.getResponse();
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);

                if (e instanceof FileNotFoundException) {
                    response.setStatus(HttpStatus.NOT_FOUND_404);
                }

                next(environment);
            }
        } else {
            next(environment);
        }
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

}
