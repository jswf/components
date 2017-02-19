package jswf.components.generic;

import jswf.framework.RequestInterface;
import jswf.framework.RouteInterface;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class HttpRequest implements RequestInterface {

    protected HttpServletRequest httpServletRequest;

    protected RouteInterface route;

    protected boolean isBodyExtracted = false;
    protected String body;

    protected boolean isQueryParametersExtracted = false;
    Map<String, String> queryParameters;

    public HttpRequest(HttpServletRequest request) {
        httpServletRequest = request;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public RouteInterface getRoute() {
        return route;
    }

    public HttpRequest setRoute(RouteInterface route) {
        this.route = route;

        return this;
    }

    public String getBody() throws IOException {
        if (!isBodyExtracted) {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = null;

            // TODO: 6/21/2016 Take in consideration character encoding header coming from the client to encode the body properly
            try {
                bufferedReader = httpServletRequest.getReader();
                if (bufferedReader != null) {
                    char[] charBuffer = new char[128];
                    int bytesRead = -1;
                    while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                        stringBuilder.append(charBuffer, 0, bytesRead);
                    }
                } else {
                    stringBuilder.append("");
                }

                body = stringBuilder.toString();
                isBodyExtracted = true;
            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
        }

        return body;
    }

    //// TODO: 11/11/2016 Support escaped characters
    // TODO: 11/11/2016 Support arrays
    public Map<String, String> getQueryParameters() {
        if (!isQueryParametersExtracted) {
            queryParameters = new HashMap<>();
            String queryString = this.getQueryString();

            if (queryString != null) {
                String[] params = queryString.split("&");
                for (String param : params) {
                    String[] parts = param.split("=");
                    String name = parts[0];
                    String value = "";
                    if (parts.length > 1) {
                        value = parts[1];
                    }
                    queryParameters.put(name, value);
                }
            }
        }

        return queryParameters;
    }

    public String getQueryParameter(String parameter) {
        return getQueryParameters().get(parameter);
    }

    public String getParameter(String name) {
        return httpServletRequest.getParameter(name);
    }

    public Map<String, String[]> getParameters(String name) {
        return httpServletRequest.getParameterMap();
    }

    //Exposing the HttpServletRequest methods
    public String getRequestURI() {
        return  httpServletRequest.getRequestURI();
    }

    public String getMethod() {
        return  httpServletRequest.getMethod();
    }

    public String getAuthType() {
        return httpServletRequest.getAuthType();
    }

    public Cookie[] getCookies() {
        return httpServletRequest.getCookies();
    }

    public long getDateHeader(String name) {
        return httpServletRequest.getDateHeader(name);
    }

    public String getHeader(String name) {
        return httpServletRequest.getHeader(name);
    }

    public Enumeration<String> getHeaders(String name){
        return httpServletRequest.getHeaders(name);
    }

    public Enumeration<String> getHeaderNames() {
        return httpServletRequest.getHeaderNames();
    }

    public int getIntHeader(String name) {
        return httpServletRequest.getIntHeader(name);
    }

    public String getPathInfo() {
        return httpServletRequest.getPathInfo();
    }

    public String getPathTranslated() {
        return httpServletRequest.getPathTranslated();
    }

    public String getContextPath() {
        return httpServletRequest.getContextPath();
    }

    public String getQueryString() {
        return httpServletRequest.getQueryString();
    }

    public String getRemoteUser() {
        return httpServletRequest.getRemoteUser();
    }

    public String getRequestedSessionId() {
        return httpServletRequest.getRequestedSessionId();
    }

    public StringBuffer getRequestURL() {
        return httpServletRequest.getRequestURL();
    }

    public String getServletPath() {
        return httpServletRequest.getServletPath();
    }

    public HttpSession getSession(boolean create) {
        return httpServletRequest.getSession(create);
    }

    public HttpSession getSession() {
        return httpServletRequest.getSession();
    }

    public String changeSessionId() {
        return httpServletRequest.changeSessionId();
    }

    public boolean isRequestedSessionIdValid() {
        return httpServletRequest.isRequestedSessionIdValid();
    }

    public boolean isRequestedSessionIdFromCookie() {
        return httpServletRequest.isRequestedSessionIdFromCookie();
    }

    public boolean isRequestedSessionIdFromURL() {
        return httpServletRequest.isRequestedSessionIdFromURL();
    }

    public Collection<Part> getParts() throws IOException, ServletException {
        return httpServletRequest.getParts();
    }

    public Part getPart(String name) throws IOException, ServletException {
        return httpServletRequest.getPart(name);
    }

    public HttpRequest setAttribute(String name, Object obj) {
        httpServletRequest.setAttribute(name, obj);

        return this;
    }

    public Object getAttribute(String name) {
        return httpServletRequest.getAttribute(name);
    }

    public Enumeration<String> getAttributeNames() {
        return httpServletRequest.getAttributeNames();
    }

    public boolean isAsyncSupported() {
        return httpServletRequest.isAsyncSupported();
    }

    public boolean isAsyncStarted() {
        return httpServletRequest.isAsyncStarted();
    }

    public AsyncContext startAsync() throws IllegalStateException {
        return httpServletRequest.startAsync();
    }

    public BufferedReader getReader() throws IOException {
        return httpServletRequest.getReader();
    }

    public ServletInputStream getInputStream() throws IOException {
        return httpServletRequest.getInputStream();
    }

    public HttpRequest setCharacterEncoding(String encoding) throws Exception {
        httpServletRequest.setCharacterEncoding(encoding);

        return this;
    }

    public String getCharacterEncoding() {
        return httpServletRequest.getCharacterEncoding();
    }

    public boolean isAjax() {
        String header = getHeader("X-Requested-With");
        return header != null && header.toLowerCase().equals("xmlhttprequest");
    }

}
