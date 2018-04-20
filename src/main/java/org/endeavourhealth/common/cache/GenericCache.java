package org.endeavourhealth.common.cache;

import java.util.Stack;

public abstract class GenericCache<ObjectType> implements ICache {
    private final Stack<ObjectType> _cache = new Stack<>();

    public GenericCache() {
        CacheManager.registerCache(this);
    }

    public ObjectType pop() {
        ObjectType object = null;

        if (_cache.size() > 0) {
            do {
                object = _cache.pop();
                if (!isValid(object))
                    object = null;
            } while (object == null && _cache.size() > 0);
        }

        if (object == null)
            object = this.create();

        return object;
    }

    public void push(ObjectType object) {
        if (isValid(object))
            _cache.push(object);
    }

    protected boolean isValid(ObjectType object) { return true; }

    protected abstract ObjectType create();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public long getSize() {
        return _cache.size();
    }

    @Override
    public void clearCache() {
        _cache.empty();
    }
}
