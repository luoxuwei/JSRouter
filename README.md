# 介绍

本项目是封装WebView与Native交互的框架，借鉴了[ARouter](https://github.com/alibaba/ARouter)路由表的设计思路，支持组件化，各个组件给WebView提供的接口可以在自己模块里实现，定义好与WebView交互的协议即可，框架自动注册分发，解耦JavaScript Interface具体业务逻辑与WebView的依赖。

# 使用指南

1.添加依赖

```groovy
implementation project(":jsrouter-api")
kapt project(":jsrouter-compiler")
```

2.实现自己的接口功能：继承BaseJavaScriptInterface，使用@JSRoute注解标注实现类并设置path。接口较多时建议通过设置group来分组，方便按需加载，也可以避免不同模块间path名称冲突。如果设置group必须在path里也带上，格式是/group/path

```java
//@JSRoute(path = "/group1/TestRoute1")
@JSRoute(path = "TestRoute1")
public class TestRout1 extends BaseJavaScriptInterface {
    @Override
    public void onCall(Activity activity, JSONObject param, ReturnCallback returnCallback) {
        Toast.makeText(activity, "TestRout1", Toast.LENGTH_SHORT).show();
    }
}
```

3.在Application onCreate方法里调用JSRouter.init进行初始化

```java
override fun onCreate() {
    super.onCreate()
    JSRouter.init(this)
}
```

4.设计自己的与webview交互的协议，在定义的webview JavaScriptInterface入口方法里通JSRouter路由到具体的方法对应的实现。具体的设计可以参考[DSBridge-Android](https://github.com/wendux/DSBridge-Android)项目。整合JSRouter和Webview的方式可以参考[JSRouterInject.java](https://github.com/luoxuwei/JSRouter/blob/master/jsrouter-api/src/main/java/com/luoxuwei/jsrouter/utils/JSRouterInject.java)

# 优化建议

在初始化时会加载所有路由组的Root类，这是主要影响性能的地方。有三种加载方案，默认的方案是遍历所有dex文件中的类找到所有的RouteRoot类，这是最耗时的方案。所以提供了gradle 插件，在打包时通过Transform+ASM技术扫描所有的类，为找到的每一个RouteRoot类生成硬编码的加载字节码，这样运行时就不用去找这些RouteRoot类了。要使用这个方案需要引入jsrouter-regist插件

```groovy
//root project build.gradle
classpath io.github.luoxuwei:jsrouter-register:1.0.1-SNAPSHOT

//project build.gradle
plugins {
    id 'com.luoxuwei.jsrouter'
}
```

但使用gradle插件会影响打包速度，所以最后提供一种方案，可以通过JSRouterModuleList注解设置所有有JSRoute接口的模块名，这样无论是打包时还是运行时都不需要遍历所有的类，只是牺牲了一点灵活性，但这种直接在代码里写死的方式其实并不麻烦，有新的module了在源码里加一下就行了。

```kotlin
@JSRouterModuleList(value = ["app","app1","moduletest"])
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        JSRouter.init(this)
    }
}
```

注意在拼接RouteRoot类的类名时，对module name做了处理，过滤了所有非数字和英文字母的字符，例如如果模块名是module-test实际用的是moduletest。这一点在使用JSRouterModuleList注解时要注意一下。
