package jswf.components.http;

import jswf.components.generic.EnvironmentStatus;
import jswf.components.generic.HttpRequest;
import jswf.components.generic.HttpResponse;
import jswf.components.http.responseContentCacheComponent.CacheStrategyInterface;
import jswf.components.http.responseContentCacheComponent.CachedResponseContent;
import jswf.framework.AbstractComponent;
import jswf.framework.Environment;
import jswf.framework.ServiceInterface;
import org.eclipse.jetty.http.HttpStatus;

import java.io.InvalidClassException;
import java.util.HashMap;

public class ResponseContentCacheComponent extends AbstractComponent implements ServiceInterface {

    private HashMap<String, Object> services;

    private CacheStrategyInterface cacheStrategy;

    public ResponseContentCacheComponent() {
    }

    public CacheStrategyInterface getCacheStrategy() {
        return cacheStrategy;
    }

    public ResponseContentCacheComponent setCacheStrategy(CacheStrategyInterface cacheStrategy) {
        this.cacheStrategy = cacheStrategy;

        return this;
    }

    public void invoke(Environment environment) {
        if (cacheStrategy == null) {
            Exception e = new InvalidClassException("Cache strategy must be set for " + ResponseContentCacheComponent.class.getName() + " to properly work.");
            environment.setException(e);
            environment.setStatus(EnvironmentStatus.REQUEST_HANDLED);
            next(environment);
            return;
        }

        if (environment.isStatus(EnvironmentStatus.REQUEST_HANDLED)) {
            next(environment);
            return;
        }

        if (environment.hasException()) {
            next(environment);
            return;
        }

        HttpRequest httpRequest = (HttpRequest) environment.getRequest();

        String key = generateKey(httpRequest);
        CachedResponseContent responseContent = cacheStrategy.restore(key);

        if (responseContent != null) {
            if (responseContent.isExpired()) {
                cacheStrategy.remove(key);
                next(environment);
                return;
            }

            HttpResponse httpResponse = (HttpResponse) environment.getResponse();

            try {
                responseContent.buildResponse(httpResponse);
            } catch (Exception e) {
                httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
                environment.setException(e);
            }

            environment.setStatus(EnvironmentStatus.REQUEST_HANDLED);
        }

        next(environment);
    }

    public ResponseContentCacheComponent addContent(String url, CachedResponseContent content) {
        cacheStrategy.save(url, content);

        return this;
    }

    public static String generateKey(HttpRequest httpRequest) {
        return httpRequest.getRequestURI() + "?" + httpRequest.getQueryString();
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

    @Override
    public void setServices(HashMap<String, Object> services) {
        this.services = services;
    }

}
