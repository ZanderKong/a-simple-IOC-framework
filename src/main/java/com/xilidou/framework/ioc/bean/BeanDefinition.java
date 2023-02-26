package com.xilidou.framework.ioc.bean;

import lombok.Data;
import lombok.ToString;

import java.util.List;


/**
 * @author Zhengxin
 */
@Data
@ToString
public class BeanDefinition {
    //BeanDefinition 是我们项目的核心数据结构。用于描述我们需要 IoC 框架管理的对象。
    private String name;  // Bean 的名称

    private String className;  // Bean 的类名

    private String interfaceName;  // Bean 实现的接口名

    private List<ConstructorArg> constructorArgs;  // Bean 构造函数的参数列表

    private List<PropertyArg> propertyArgs;  // Bean 属性的列表

}
