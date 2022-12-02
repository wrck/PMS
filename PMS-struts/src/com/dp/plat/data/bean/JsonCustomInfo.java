package com.dp.plat.data.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Json自定义信息
 * 
 * @author w02611
 */
public class JsonCustomInfo<K, V> extends HashMap<K, V> {

    private static final long serialVersionUID = 1L;

    private JsonCustomInfo delegate;

    public JsonCustomInfo() {
        super();
    }

    public JsonCustomInfo(Map<K, V> map) {
        super();
        if (map != null) {
            this.putAll(map);
        }
    }

    public JsonCustomInfo(JsonCustomInfo delegate, Map<K, V> map) {
        super();
        this.delegate = delegate;
        if (map != null) {
            this.putAll(map);
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        if (delegate != null && map != null) {
            delegate.putAll(map);
        }
        super.putAll(map);
    }

    @Override
    public V put(K key, V value) {
        if (delegate != null) {
            delegate.put(key, value);
        }
        return super.put(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (delegate != null) {
            delegate.putIfAbsent(key, value);
        }
        return super.putIfAbsent(key, value);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        if (delegate != null) {
            delegate.merge(key, value, remappingFunction);
        }
        return super.merge(key, value, remappingFunction);
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (delegate != null) {
            delegate.remove(key, value);
        }
        return super.remove(key, value);
    }

    @Override
    public V remove(Object key) {
        if (delegate != null) {
            delegate.remove(key);
        }
        return super.remove(key);
    }

    @Override
    public void clear() {
        if (delegate != null) {
            delegate.clear();
        }
        super.clear();
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        if (delegate != null) {
            delegate.replace(key, oldValue, newValue);
        }
        return super.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        if (delegate != null) {
            delegate.replace(key, value);
        }
        return super.replace(key, value);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        if (delegate != null) {
            delegate.replaceAll(function);
        }
        super.replaceAll(function);
    }

    /**
     * 获取委派对象
     * 
     * @return
     */
    public JsonCustomInfo getDelegate() {
        return delegate;
    }

    /**
     * 设置委派对象
     * 
     * @param delegate
     */
    public void setDelegate(JsonCustomInfo delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public V get(Object key) {
        if ("iterator".equals(key) && !this.containsKey(key)) {
            try {
                return (V)new ArrayList<Object>(this.entrySet());
            } catch (Exception e) {
            }
        }
        return super.get(key);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        if ("iterator".equals(key) && !this.containsKey(key)) {
            try {
                return (V) new ArrayList<Object>(this.entrySet());
            } catch (Exception e) {
            }
        }
        return super.getOrDefault(key, defaultValue);
    }

    /**
     * 获取迭代器
     * @return
     */
    public Iterator<Entry<K, V>> getIterator() {
        return this.entrySet().iterator();
    }
}
