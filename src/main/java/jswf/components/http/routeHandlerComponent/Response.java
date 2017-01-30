package jswf.components.http.routeHandlerComponent;

import jswf.framework.ResponseInterface;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

public class Response implements ResponseInterface {

    protected HttpServletResponse httpServletResponse;

    public Response(HttpServletResponse response) {
        httpServletResponse = response;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    public Response setStatus(int status) {
        httpServletResponse.setStatus(status);

        return this;
    }

    public int getStatus() {
        return httpServletResponse.getStatus();
    }

    public Response setContentType(String contentType) {
        httpServletResponse.setContentType(contentType);

        return this;
    }

    public String getContentType() {
        return httpServletResponse.getContentType();
    }

    public Response setContentLength(int length) {
        httpServletResponse.setContentLength(length);

        return this;
    }

    public Response setContentLengthLong(long length) {
        httpServletResponse.setContentLengthLong(length);

        return this;
    }

    public void addContent(String content) throws IOException {
        httpServletResponse.getWriter().write(content);
        httpServletResponse.setStatus(200);
    }

    public void addContent(String content, int statusCode) throws IOException {
        httpServletResponse.getWriter().write(content);
        httpServletResponse.setStatus(statusCode);
    }

    public PrintWriter getWriter() throws IOException {
        return httpServletResponse.getWriter();
    }

    public void addCookie(Cookie cookie) {
        httpServletResponse.addCookie(cookie);
    }

    public boolean containsHeader(String name) {
        return httpServletResponse.containsHeader(name);
    }

    public String encodeURL(String url) {
        return httpServletResponse.encodeURL(url);
    }

    public String encodeRedirectURL(String url) {
        return httpServletResponse.encodeRedirectURL(url);
    }

    public void sendError(int sc) throws IOException {
        httpServletResponse.sendError(sc);
    }

    public void sendError(int sc, String msg) throws IOException {
        httpServletResponse.sendError(sc, msg);
    }

    public void sendRedirect(String location) throws IOException {
        httpServletResponse.sendRedirect(location);
    }

    public void addHeader(String name, String value) {
        httpServletResponse.addHeader(name, value);
    }

    public void addHeader(String name, int value) {
        httpServletResponse.addIntHeader(name, value);
    }

    public void addHeader(String name, long date) {
        httpServletResponse.addDateHeader(name, date);
    }

    public void setHeader(String name, String value) {
        httpServletResponse.setHeader(name, value);
    }

    public void setHeader(String name, int value) {
        httpServletResponse.setIntHeader(name, value);
    }

    public void setHeader(String name, long value) {
        httpServletResponse.setDateHeader(name, value);
    }

    public String getHeader(String name) {
        return httpServletResponse.getHeader(name);
    }

    public Collection<String> getHeaders(String name) {
        return httpServletResponse.getHeaders(name);
    }

    public Collection<String> getHeaderNames() {
        return httpServletResponse.getHeaderNames();
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return httpServletResponse.getOutputStream();
    }

    public int getBufferSize() {
        return httpServletResponse.getBufferSize();
    }



}
