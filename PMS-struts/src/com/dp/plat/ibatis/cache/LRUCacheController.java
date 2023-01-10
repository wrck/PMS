package com.dp.plat.ibatis.cache;

import org.apache.commons.lang3.ObjectUtils;

import com.dp.plat.ibatis.handler.FastjsonTypeHandler;
import com.ibatis.sqlmap.engine.cache.CacheModel;
import com.ibatis.sqlmap.engine.cache.lru.LruCacheController;

public class LRUCacheController extends LruCacheController {

    @Override
    public Object getObject(CacheModel cacheModel, Object key) {
        Object value = super.getObject(cacheModel, key);
        value = clone(value);
        return value;
    }

    @Override
    public void putObject(CacheModel cacheModel, Object key, Object value) {
        value = clone(value);
        super.putObject(cacheModel, key, value);
    }

    private Object clone(Object value) {
        if (value != null) {
            Object cloned = ObjectUtils.clone(value);
            if (cloned != null) {
                return cloned;
            }
            FastjsonTypeHandler handler = new FastjsonTypeHandler(value.getClass());
            String json = handler.toJsonString(value);
            return handler.parse(json);
        }
        return value;
    }

}
