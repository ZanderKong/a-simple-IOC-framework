项目原作者：diaozxin007/xilidou-framework (github.com)

以下为我学习以后得逐行手撕笔记，并尝试着提出了一些优化思路。
笔记在线阅读地址：https://www.zanderkong.com/988.html

项目使用 json 作为配置文件。使用 maven 管理 jar 包的依赖。
在这个框架中我们的对象都是单例的，并不支持Spirng的多种作用域。框架的实现使用了cglib 和 Java 的反射。项目中我还使用了 lombok 用来简化代码。

整个简易 IOC 分为 3 个package、在包 bean 中定义了我们框架的数据结构。core 是我们框架的核心逻辑所在。utils 是一些通用工具类。

# 1、bean 
`BeanDefinition`是我们项目的核心数据结构。用于描述我们需要 IoC 框架管理的对象：
```
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
```
其中，
@Data 和 @ToString 是 Lombok 库提供的注解，用来自动生成 getter、setter、toString 等方法。
BeanDefinition 类包含以下属性：
-   name：Bean的名字
-   className：Bean的类名
-   interfaceName：Bean实现的接口名
-   constructorArgs：Bean的构造函数参数列表
-   propertyArgs：Bean 的属性列表

# 2、Utils
## 工具类包里面的对象：`ClassUtils`
`ClassUtils` 负责处理 Java 类的加载, 代码如下：
```
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
```
这段代码定义了一个 `ClassUtils` 类，其中包含了两个静态方法：
-   `getDefultClassLoader()` 方法返回当前线程的默认类加载器
-   `loadClass(String className)` 方法通过默认类加载器加载指定名称的类。如果类不存在，则抛出 `ClassNotFoundException` 异常并打印堆栈跟踪。

这些方法可以用于动态加载类和资源，特别是在运行时需要加载一些在编译时未知的类或者在运行时需要加载一些可插拔的组件。

## 工具类包里面的对象：`BeanUtils`
```
public class BeanUtils {  
    //负责处理对象的实例化  
    public static <T> T instanceByCglib(Class<T> clz,Constructor ctr,Object[] args) {  
        //使用 cglib 的 Enhancer        Enhancer enhancer = new Enhancer();  
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
```
上述代码定义了一个 `BeanUtils` 类，其中包含了一个静态方法：
-   `instanceByCglib(Class<T> clz,Constructor ctr,Object[] args)` 方法负责处理对象的实例化。这个方法使用了 CGLIB 的 Enhancer 来创建代理对象。Enhancer 的 `setSuperclass` 方法用于设置代理目标，`setCallback` 方法用于设置回调对象，`create` 方法用于创建代理对象。
    这个方法可以用于创建对象的代理，特别是运行时需要动态创建代理对象。

## 工具类包里面的对象：`JsonUtils`
```
public class JsonUtils {  
  
    private static final ObjectMapper mapper = new ObjectMapper();//获取 ObjectMapper 实例。  
  
    private JsonUtils() {  
    }  
  
    public static ObjectMapper getObjectMapper() {  
        return mapper;  
    }  
  
    public static <T> T readValue(String json, Class<T> cls) {  
        //将 JSON 字符串转换为指定类型的对象。  
        try {  
            return mapper.readValue(json, cls);  
        } catch (Exception var3) {  
            return null;  
        }  
    }  
  
    public static <T> T readValue(InputStream is,Class<T> cls){  
        //将 JSON 输入流转换为指定类型的对象。  
        try{  
            return mapper.readValue(is,cls);  
        }catch (Exception e){  
            return null;  
        }  
    }  
  
    public static <T> T readValue(byte[] bytes, Class<T> cls) {  
        //将字节数组转换为指定类型的对象。  
        try {  
            return mapper.readValue(bytes, cls);  
        } catch (Exception var3) {  
            return null;  
        }  
    }  
  
    public static <T> T readValue(String json, TypeReference valueTypeRef) {  
        //将 JSON 字符串转换为指定类型的对象。  
        try {  
            return mapper.readValue(json, valueTypeRef);  
        } catch (Exception var3) {  
            return null;  
        }  
    }  
  
    public static <T> T readValue(byte[] bytes, TypeReference valueTypeRef) {  
        //将字节数组转换为指定类型的对象。  
        try {  
            return mapper.readValue(bytes, valueTypeRef);  
        } catch (Exception var3) {  
            return null;  
        }  
    }  
  
    public static <T> T readValue(InputStream is,TypeReference valueTypeRef){  
        //将 JSON 输入流转换为指定类型的对象。  
        try{  
            return mapper.readValue(is,valueTypeRef);  
        }catch (Exception e){  
            return null;  
        }  
    }  
    public static String writeValue(Object entity) {  
        //将对象转换为 JSON 字符串。  
        try {  
            return mapper.writeValueAsString(entity);  
        } catch (Exception var2) {  
            return null;  
        }  
    }  
  
    public static byte[] writeByteValue(Object entity) {  
        //将对象转换为字节数组。  
        try {  
            return mapper.writeValueAsBytes(entity);  
        } catch (Exception var2) {  
            return null;  
        }  
    }  
  
    static {  
        mapper.enable(SerializationFeature.INDENT_OUTPUT);  
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);  
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);  
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);  
        mapper.getDeserializationConfig().withoutFeatures(new DeserializationFeature[]{DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES});  
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);  
        mapper.configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true);  
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);  
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);  
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);  
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));  
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);  
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  
    }  
  
}
```
`JsonUtils` 封装了 Jackson 库，提供了一些方便的方法来读取和写入 JSON 数据:
-   `public static ObjectMapper getObjectMapper()`：获取 ObjectMapper 实例。
-   `public static <T> T readValue(String json, Class<T> cls)`：将 JSON 字符串转换为指定类型的对象。
-   `public static <T> T readValue(InputStream is,Class<T> cls)`：将 JSON 输入流转换为指定类型的对象。
-   `public static <T> T readValue(byte[] bytes, Class<T> cls)`：将字节数组转换为指定类型的对象。
-   `public static <T> T readValue(String json, TypeReference valueTypeRef)`：将 JSON 字符串转换为指定类型的对象。
-   `public static <T> T readValue(byte[] bytes, TypeReference valueTypeRef)`：将字节数组转换为指定类型的对象。
-   `public static <T> T readValue(InputStream is,TypeReference valueTypeRef)`：将 JSON 输入流转换为指定类型的对象。
-   `public static String writeValue(Object entity)`：将对象转换为 JSON 字符串。
-   `public static byte[] writeByteValue(Object entity)` ：将对象转换为字节数组。
    当然这只是一个简易的实现，如果想要进一步完善，可以：
    1、每个方法中返回的 null 值，可以根据实际的异常类型，替换成更合适的返回。这样可以方便调用者更好的处理错误。
    2、除了返回错误信息，也可以在异常时调用处理异常处理程序。
    3、可以为 mapper 设置一个单例，避免每次调用 getObjectMapper () 时都会创建一个新的 mapper 实例。
    4、可以再为 readVaule 和 WriteValue 设计重载一个接受 File/Path 对象的方法。

## 工具类包里面的对象：`ReflectionUtils`
    `ReflectionUtils` 主要通过 Java 的反射原理来完成对象的依赖注入。简单来说，就是通过 injectField 方法来为某个对象的某个成员变量注入一个值。
```
  
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
```

# 3、Core
该 IoC 框架，目前只支持一种 ByName 的注入。所以我们的 BeanFactory 就只有一个方法：
```
public interface BeanFactory {  
    Object getBean(String name) throws Exception;  
}
```
并在 BeanFactoryImpl 中实现它：
```
public class BeanFactoryImpl implements BeanFactory{  
  
    private static final ConcurrentHashMap<String,Object> beanMap = new ConcurrentHashMap<>();  
  
    private static final ConcurrentHashMap<String,BeanDefinition> beanDefineMap= new ConcurrentHashMap<>();  
  
    private static final Set<String> beanNameSet = Collections.synchronizedSet(new HashSet<>());  
  
    @Override  
    public Object getBean(String name) throws Exception {  
        //查找对象是否已经实例化过  
        Object bean = beanMap.get(name);  
        if(bean != null){  
            return bean;  
        }  
        //如果没有实例化，那就需要调用createBean来创建对象  
        bean =  createBean(beanDefineMap.get(name));  
  
        if(bean != null) {  
  
            //对象创建成功以后，注入对象需要的参数  
            populatebean(bean);  
  
            //再把对象存入Map中方便下次使用。  
            beanMap.put(name,bean;  
        }  
  
        //结束返回  
        return bean;  
    }  
  
    protected void registerBean(String name, BeanDefinition bd){  
        beanDefineMap.put(name,bd);  
        beanNameSet.add(name);  
    }  
  
    private Object createBean(BeanDefinition beanDefinition) throws Exception {  
        String beanName = beanDefinition.getClassName();  
        Class clz = ClassUtils.loadClass(beanName);  
        if(clz == null) {  
            throw new Exception("can not find bean by beanName");  
        }  
        List<ConstructorArg> constructorArgs = beanDefinition.getConstructorArgs();  
        if(constructorArgs != null && !constructorArgs.isEmpty()){  
            List<Object> objects = new ArrayList<>();  
            for (ConstructorArg constructorArg : constructorArgs) {  
                objects.add(getBean(constructorArg.getRef()));  
            }  
            return BeanUtils.instanceByCglib(clz,clz.getConstructor(),objects.toArray());  
        }else {  
            return BeanUtils.instanceByCglib(clz,null,null);  
        }  
    }  
  
    private void populatebean(Object bean) throws Exception {  
        Field[] fields = bean.getClass().getSuperclass().getDeclaredFields();  
        if (fields != null && fields.length > 0) {  
            for (Field field : fields) {  
                String beanName = field.getName();  
                beanName = StringUtils.uncapitalize(beanName);  
                if (beanNameSet.contains(field.getName())) {  
                    Object fieldBean = getBean(beanName);  
                    if (fieldBean != null) {  
                        ReflectionUtils.injectField(field,bean,fieldBean);  
                    }  
                }  
            }  
        }  
    }  
}
```
该实现类 可以根据 BeanDefinition 的信息来创建实例化对象。getBean 方法可以返回实例化后的对象，如果对象不存在，则调用 createBean 方法来创建对象。如果创建成功，则调用 populateBean 注入对象需要的参数，最后再把对象存入 Map 中方便下次使用。
`getBean` 方法可以返回实例化后的对象，如果对象不存在，则调用 `createBean` 方法来创建对象。如果创建成功，则调用 `populateBean` 注入对象需要的参数，最后再把对象存入 Map 中方便下次使用。
createBean 方法会根据传入的 BeanDefinition 来创建对象。如果 BeanDefinition 中有构造器参数，则会递归调用 getBean 方法来获取依赖的对象，最后使用 Cglib 动态代理来创建对象。如果没有构造器参数，则直接使用 Cglib 动态代理来创建对象。
populateBean 方法会遍历对象的字段，如果它是一个 Bean，则会使用 getBean 方法来获取依赖的对象，并使用 ReflectionUtils 注入字段的值。
如果后续想要继续优化这个代码，可以：
1、代码中抛出了 Exception 异常，这种异常处理方式不利于代码的维护和调试。可以考虑根据具体的场景来定义更具体的异常类型，从而提高代码的可读性和可维护性。
2、getBean 方法没有对传入的 name 参数做校验。如果 name  不存在那么会返回 null，这就会导致空指针异常。所以在后续优化时，可以在方法中添加参数校验。如果 name 不存在，那么可以抛出异常。
3、beanMap、beanDefineMap 使用了 ConcurrentHashMap，虽然可以保证线程安全，但是性能存在瓶颈，可以使用 CopyOnWriteArrayList、CopyOnWriteArraySet 等集合类来替换
4、代码中的 populateBean 方法使用了反射机制来注入字段的值，这种方式虽然便捷，但是也存在一定的性能问题。如果要优化性能，可以考虑使用 JDK 的 Unsafe 类来直接修改内存中地址的值，或者使用 JDK 的 MethodHandle 类来替换反射机制，或者使用代码生成的方式来生成 setter 方法，从而实现更高效的字段注入。

7、JsonApplicationContext
```
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
```
JsonApplicationContext 类继承自 BeanFactoryImpl，用于从 JSON 文件中读取 Bean 的定义，转换为容器能够理解的 `BeanDefination`，并使用 `registerBean` 方法将其注册到容器中。
当然简易的实现里，还有不少可以优化的空间：
1、没有处理读取 JSON 文件失败的情况，例如文件不存在或格式不正确等。
2、没有对 JSON 文件中的内容进行校验，防止不合法的定义被注册到容器中。
3、可以将读取 JSON 文件和注册 Bean 的操作封装为不同的方法，提高代码的可读性和可维护性。
4、增加日志输出等手段，以方便调试和排错。
