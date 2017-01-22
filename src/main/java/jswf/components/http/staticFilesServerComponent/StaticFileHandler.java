package jswf.components.http.staticFilesServerComponent;

import jswf.components.generic.EnvironmentStatus;
import jswf.components.generic.RequestHandlerInterface;
import jswf.components.http.routeHandlerComponent.Request;
import jswf.components.http.routeHandlerComponent.Response;
import jswf.framework.Environment;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.HttpOutput;

import java.io.File;
import java.io.FileInputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * StaticFileServerComponent route handler.
 */
public class StaticFileHandler implements RequestHandlerInterface {

    public static final String FILE = "staticFileServer.file";
    public static final String MIME_TYPES = "staticFileServer.mimeTypes";

    public void handle(Environment environment) throws Exception {
        Request request = (Request) environment.getRequest();
        Response response = (Response) environment.getResponse();

        File file = (File) environment.getCustom(StaticFileHandler.FILE);
        MimeTypes mimeTypes = (MimeTypes) environment.getCustom(StaticFileHandler.MIME_TYPES);

        String lastModified = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.of("GMT")));

        String ifModifiedSinceHeader = request.getHeader("If-Modified-Since");
        if (lastModified.equals(ifModifiedSinceHeader)) {
            response.setStatus(304);
        } else {
            final FileInputStream fileInputStream = new FileInputStream(file);

            HttpOutput httpOutputStream = (HttpOutput) response.getOutputStream();

            response.addHeader(HttpHeader.LAST_MODIFIED.toString(), lastModified);
            response.setContentType(mimeTypes.getMimeByExtension(file.getPath()));
            response.setContentLength(Math.toIntExact(file.length()));
            response.setStatus(HttpStatus.OK_200);

            httpOutputStream.sendContent(fileInputStream);

            httpOutputStream.close();
            fileInputStream.close();
        }

        environment.setStatus(EnvironmentStatus.REQUEST_HANDLED);
    }

}
