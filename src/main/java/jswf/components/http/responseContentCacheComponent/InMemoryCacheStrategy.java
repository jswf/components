package jswf.components.http.responseContentCacheComponent;

import java.util.HashMap;

public class InMemoryCacheStrategy implements CacheStrategyInterface {

    private HashMap<String, CachedResponseContent> cache;

    public InMemoryCacheStrategy() {
        cache = new HashMap<>();
    }

    @Override
    public CacheStrategyInterface save(String key, CachedResponseContent content) {
        cache.put(key, content);

        return this;
    }

    @Override
    public CacheStrategyInterface remove(String key) {
        cache.remove(key);

        return this;
    }

    @Override
    public CachedResponseContent restore(String key) {
        CachedResponseContent content = cache.get(key);

        if (content != null) {
            content.updateHits();
            System.out.println(key + " hits: " + String.valueOf(content.getNumberOfHits()));
        }

        return content;
    }

}
