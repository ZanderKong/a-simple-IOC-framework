package com.xilidou.framework.ioc.utils;

public class ClassUtils {
//    负责处理 Java 类的加载
    public static ClassLoader getDefultClassLoader(){
        //获取当前线程的类加载器
        return Thread.currentThread().getContextClassLoader();
    }

    public static Class loadClass(String className){
        try {
            return getDefultClassLoader().loadClass(className);
            //getDefultClassLoader() 方法返回当前线程的默认类加载器
            //loadClass(String className) 方法通过默认类加载器加载指定名称的类。如果类不存在，则抛出 ClassNotFoundException 异常并打印堆栈跟踪。
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
