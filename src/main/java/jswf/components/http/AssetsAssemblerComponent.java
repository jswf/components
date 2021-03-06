package jswf.components.http;

import jswf.components.generic.EnvironmentStatus;
import jswf.components.generic.HttpRequest;
import jswf.components.generic.HttpResponse;
import jswf.components.http.assetsAssemblerComponent.AbstractAssetAssembler;
import jswf.components.http.assetsAssemblerComponent.CssAssetAssembler;
import jswf.components.http.assetsAssemblerComponent.JsAssetAssembler;
import jswf.components.http.responseContentCacheComponent.CachedResponseContent;
import jswf.framework.AbstractComponent;
import jswf.framework.Environment;
import jswf.framework.ServiceInterface;
import jswf.framework.ServicesContainer;
import org.eclipse.jetty.http.HttpStatus;

public class AssetsAssemblerComponent extends AbstractComponent implements ServiceInterface {

    protected String basePath;

    protected String cssPath = "/css";

    protected String jsPath = "/js";

    protected String cssUri = "/css/";

    protected String jsUri = "/js/";

    protected boolean loadMinifiedVersions = false;

    // cache the content for a whole day
    // if the client browser needs to be updated use a query parameter to force the browser to update the assets
    protected long browserCacheDurationMinutes = 24L * 60L * 60L;

    // cache the content in the server for a whole day
    protected long serverCacheDurationMinutes = 24L * 60L * 60L * 1000L;

    protected ResponseContentCacheComponent responseContentCacheComponent;
    protected boolean triedToGrabResponseContentCacheComponent = false;

    public AssetsAssemblerComponent() {}

    public AssetsAssemblerComponent setBasePath(String basePath) {
        this.basePath = basePath;

        return this;
    }

    public String getBasePath() {
        return basePath;
    }

    public AssetsAssemblerComponent setCssPath(String cssPath) {
        this.cssPath = cssPath;

        return this;
    }

    public String getCssPath() {
        return cssPath;
    }

    public AssetsAssemblerComponent setJsPath(String jsPath) {
        this.jsPath = jsPath;

        return this;
    }

    public String getJsPath() {
        return jsPath;
    }

    public AssetsAssemblerComponent setCssUri(String cssUri) {
        this.cssUri = cssUri;

        return this;
    }

    public String getCssUri() {
        return cssUri;
    }

    public AssetsAssemblerComponent setJsUri(String jsUri) {
        this.jsUri = jsUri;

        return this;
    }

    public String getJsUri() {
        return jsUri;
    }

    public AssetsAssemblerComponent loadMinifiedVersions(boolean loadMinifiedVersions) {
        this.loadMinifiedVersions = loadMinifiedVersions;

        return this;
    }

    public AssetsAssemblerComponent setBrowserCacheDurationMinutes(long browserCacheDurationMinutes) {
        this.browserCacheDurationMinutes = browserCacheDurationMinutes;

        return this;
    }

    public long getBrowserCacheDurationMinutes() {
        return browserCacheDurationMinutes;
    }

    public AssetsAssemblerComponent setServerCacheDurationMinutes(long serverCacheDurationMinutes) {
        this.serverCacheDurationMinutes = serverCacheDurationMinutes * 60L * 1000L;

        return this;
    }

    public long getServerCacheDurationMinutes() {
        return serverCacheDurationMinutes;
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

        HttpRequest httpRequest = (HttpRequest) environment.getRequest();

        if (httpRequest.getMethod().equals("GET")) {
            String requestUri = httpRequest.getRequestURI();

            AbstractAssetAssembler assembler = null;
            String assemblerPath = null;
            String contentType = "";

            if (requestUri.equals(cssUri)) {
                assembler = new CssAssetAssembler();
                assembler.loadMinifiedVersions(loadMinifiedVersions);
                assemblerPath = cssPath;
                contentType = "text/css";
            }

            if (requestUri.equals(jsUri)) {
                assembler = new JsAssetAssembler();
                assembler.loadMinifiedVersions(loadMinifiedVersions);
                assemblerPath = jsPath;
                contentType = "application/javascript";
            }

            if (assembler != null) {
                String query = httpRequest.getQueryParameter("files");
                if (query != null) {
                    HttpResponse httpResponse = (HttpResponse) environment.getResponse();

                    try {
                        String content = assembler.process(query.split(","), basePath, assemblerPath);

                        long contentLength = (long) content.length();

                        httpResponse = (HttpResponse) environment.getResponse();
                        httpResponse.setHeader("Content-Type", contentType);
                        httpResponse.setHeader("Cache-Control", "max-age=" + browserCacheDurationMinutes);
                        httpResponse.setContentLengthLong(contentLength);
                        httpResponse.addContent(content);

                        if (getResponseContentCacheComponent(environment) != null) {
                            CachedResponseContent responseContent = new CachedResponseContent();
                            responseContent
                                    .generateFromResponse(httpResponse)
                                    .setCacheDuration(serverCacheDurationMinutes)
                                    .setContent(content)
                            ;
                            responseContentCacheComponent.addContent(ResponseContentCacheComponent.generateKey(httpRequest), responseContent);
                        }
                    } catch (Exception e) {
                        httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
                        environment.setException(e);
                    }

                    environment.setStatus(EnvironmentStatus.REQUEST_HANDLED);
                }
            }
        }

        next(environment);
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

    private ResponseContentCacheComponent getResponseContentCacheComponent(Environment environment) {
        if (!triedToGrabResponseContentCacheComponent) {
            responseContentCacheComponent = (ResponseContentCacheComponent) ServicesContainer.getService(ResponseContentCacheComponent.class.getName());
            triedToGrabResponseContentCacheComponent = true;
        }

        return responseContentCacheComponent;
    }

}
