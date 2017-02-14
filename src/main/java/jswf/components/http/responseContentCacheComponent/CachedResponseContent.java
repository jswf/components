package jswf.components.http.responseContentCacheComponent;

import jswf.components.generic.HttpResponse;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class CachedResponseContent {

    protected String content;

    protected int statusCode;

    protected LinkedHashMap<String, String> headers;

    protected long numberOfHits = 1;

    protected long createdAt;

    protected long latestRequest;

    protected long cacheDuration;

    public CachedResponseContent() {
        createdAt = System.currentTimeMillis();
        headers = new LinkedHashMap<>();
        latestRequest = createdAt;
    }

    public String getContent() {
        return content;
    }

    public CachedResponseContent setContent(String content) {
        this.content = content;

        return this;
    }

    public CachedResponseContent setStatusCode(int statusCode) {
        this.statusCode = statusCode;

        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public CachedResponseContent setCreatedAt(long createdAt) {
        this.createdAt = createdAt;

        return this;
    }

    public long getLatestRequest() {
        return latestRequest;
    }

    public CachedResponseContent setLatestRequest(long latestRequest) {
        this.latestRequest = latestRequest;

        return this;
    }

    public long getNumberOfHits() {
        return numberOfHits;
    }

    public void setNumberOfHits(long numberOfHits) {
        this.numberOfHits = numberOfHits;
    }

    public void updateHits() {
        numberOfHits++;
        latestRequest = System.currentTimeMillis();
    }

    public CachedResponseContent setCacheDuration(long cacheDuration) {
        this.cacheDuration = cacheDuration;

        return this;
    }

    public long getCacheDuration() {
        return cacheDuration;
    }

    public CachedResponseContent setHeaders(LinkedHashMap<String, String> headers) {
        this.headers = headers;

        return this;
    }

    public CachedResponseContent addHeader(String header, String value) {
        this.headers.put(header, value);

        return this;
    }

    public LinkedHashMap<String, String> getHeaders() {
        return headers;
    }

    public CachedResponseContent generateFromResponse(HttpResponse httpResponse) throws Exception {
        statusCode = httpResponse.getStatus();

        Collection<String> headers = httpResponse.getHeaderNames();
        for (String header:headers) {
            addHeader(header, httpResponse.getHeader(header));
        }

        return this;
    }

    public CachedResponseContent buildResponse(HttpResponse httpResponse) throws Exception {
        for (Map.Entry<String, String> entry: headers.entrySet()) {
            httpResponse.setHeader(entry.getKey(), entry.getValue());
        }

        httpResponse.addContent(content, statusCode);

        return this;
    }

    public boolean isExpired() {
        if (cacheDuration < 0 )
            return false;

        if (System.currentTimeMillis() - createdAt > cacheDuration)
            return  true;

        return false;
    }

}
