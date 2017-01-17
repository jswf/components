package jswf.components.http.staticFilesServerComponent;

import jswf.components.generic.RequestHandlerInterface;
import jswf.components.http.routeHandlerComponent.Request;
import jswf.components.http.routeHandlerComponent.Response;
import jswf.framework.Environment;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.HttpOutput;

import java.io.File;
import java.io.FileInputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class StaticFileHandler implements RequestHandlerInterface {

    public void handle(Environment environment) throws Exception {
        Request request = (Request) environment.getRequest();
        Response response = (Response) environment.getResponse();

        File file = (File) environment.getCustom("staticFileServer.file");
        MimeTypes mimeTypes = (MimeTypes) environment.getCustom("staticFileServer.mimeTypes");

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

            httpOutputStream.sendContent(fileInputStream);

            httpOutputStream.close();
            fileInputStream.close();
        }
    }

}
