package com.example.TP.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class BeanUtilsCustom {
    public static void copySelectedProperties(Object source, Object target, String... properties) {
        BeanWrapper sourceWrapper = new BeanWrapperImpl(source);
        BeanWrapper targetWrapper = new BeanWrapperImpl(target);

        for (String property : properties) {
            targetWrapper.setPropertyValue(property, sourceWrapper.getPropertyValue(property));
        }
    }
}
