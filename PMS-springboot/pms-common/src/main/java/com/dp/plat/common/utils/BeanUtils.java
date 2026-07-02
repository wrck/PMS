package com.dp.plat.common.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import java.util.HashSet;
import java.util.Set;
public class BeanUtils {
    public static void copyNonNullProperties(Object src, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }
    private static String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        Set<String> empty = new HashSet<>();
        for (var pd : src.getPropertyDescriptors()) {
            if (src.getPropertyValue(pd.getName()) == null) empty.add(pd.getName());
        }
        return empty.toArray(new String[0]);
    }
}
