package jswf.components.http.responseContentCacheComponent;

public interface CacheStrategyInterface {

    public CacheStrategyInterface save(String key, CachedResponseContent content);

    public CacheStrategyInterface remove(String key);

    public CachedResponseContent restore(String key);

}
