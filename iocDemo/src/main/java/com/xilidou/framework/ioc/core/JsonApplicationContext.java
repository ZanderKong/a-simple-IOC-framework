package com.xilidou.framework.ioc.core;

import com.xilidou.framework.ioc.bean.BeanDefinition;
import com.xilidou.framework.ioc.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.InputStream;
import java.util.List;

/**
 * @author Zhengxin
 */
public class JsonApplicationContext extends BeanFactoryImpl{

    private String fileName; //fileName 为 JSON 文件的名称

    public JsonApplicationContext(String fileName) {
        this.fileName = fileName;
    }
    //构造函数，接收 JSON 文件的名称作为参数
    public void init(){
        loadFile();
    }
    //完成初始化操作

    private void loadFile(){
        //函数用于从 JSON 文件中读取 Bean 的定义并将其注册到容器中

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        //获取 JSON 文件的输入流

        List<BeanDefinition> beanDefinitions = JsonUtils.readValue(is,new TypeReference<List<BeanDefinition>>(){});
        //将 JSON 文件中的内容反序列化为 BeanDefinition 对象的列表。
        if(beanDefinitions != null && !beanDefinitions.isEmpty()) {
            //判断 beanDefinitions 是否为 null 或空列表。
            for (BeanDefinition beanDefinition : beanDefinitions) {
                //遍历 beanDefinitions 列表。
                registerBean(beanDefinition.getName(), beanDefinition);
                //使用 beanDefinition 的名称和定义注册 Bean。
            }
        }

    }


}
