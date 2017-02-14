package jswf.components.http.staticFilesServerComponent;

import jswf.components.generic.EnvironmentStatus;
import jswf.components.generic.HttpRequest;
import jswf.components.generic.HttpResponse;
import jswf.components.generic.RequestHandlerInterface;
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
        HttpRequest httpRequest = (HttpRequest) environment.getRequest();
        HttpResponse httpResponse = (HttpResponse) environment.getResponse();

        File file = (File) environment.getCustom(StaticFileHandler.FILE);
        MimeTypes mimeTypes = (MimeTypes) environment.getCustom(StaticFileHandler.MIME_TYPES);

        String lastModified = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.of("GMT")));

        String ifModifiedSinceHeader = httpRequest.getHeader("If-Modified-Since");
        if (lastModified.equals(ifModifiedSinceHeader)) {
            httpResponse.setStatus(304);
        } else {
            final FileInputStream fileInputStream = new FileInputStream(file);

            HttpOutput httpOutputStream = (HttpOutput) httpResponse.getOutputStream();

            httpResponse.addHeader(HttpHeader.LAST_MODIFIED.toString(), lastModified);
            httpResponse.setContentType(mimeTypes.getMimeByExtension(file.getPath()));
            httpResponse.setContentLength(Math.toIntExact(file.length()));
            httpResponse.setStatus(HttpStatus.OK_200);

            httpOutputStream.sendContent(fileInputStream);

            httpOutputStream.close();
            fileInputStream.close();
        }

        environment.setStatus(EnvironmentStatus.REQUEST_HANDLED);
    }

}
