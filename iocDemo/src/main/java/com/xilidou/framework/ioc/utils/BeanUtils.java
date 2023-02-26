package com.xilidou.framework.ioc.utils;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

import java.lang.reflect.Constructor;

public class BeanUtils {
    //负责处理对象的实例化
    public static <T> T instanceByCglib(Class<T> clz,Constructor ctr,Object[] args) {
        //使用 cglib 的 Enhancer
        Enhancer enhancer = new Enhancer();
        //设置代理目标。传入类。
        enhancer.setSuperclass(clz);
        //设置单一回调对象，在调用中拦截对目标方法的使用。
        //NoOp.INSTANCE NoOp回调把对方法调用直接委派到这个方法在父类中的实现
        //也可以理解成真实对象直接调用方法
        enhancer.setCallback(NoOp.INSTANCE);

        if(ctr == null){
            return (T) enhancer.create();
        }else {
            return (T) enhancer.create(ctr.getParameterTypes(),args);
        }
    }

}
