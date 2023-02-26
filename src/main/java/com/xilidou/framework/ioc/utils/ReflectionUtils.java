package com.xilidou.framework.ioc.utils;

import java.lang.reflect.Field;
//引入Java反射机制的Field类

public class ReflectionUtils {
    //通过Java 的反射原理来完成对象的依赖注入
    public static void injectField(Field field,Object obj,Object value) throws IllegalAccessException {
        if(field != null) {
            field.setAccessible(true);
            //将field的访问权限设置为可访问
            field.set(obj, value);
            //为传入的obj对象的field成员变量注入传入的value值
        }
    }
}
