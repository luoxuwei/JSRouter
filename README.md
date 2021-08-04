# 介绍

借鉴[ARouter](https://github.com/alibaba/ARouter)路由表的设计思路，封装WebView与Native交互的框架，支持组件化，各个组件给WebView提供的接口可以在自己模块里实现，定义好与WebView交互的协议即可，框架自动注册分发，解耦JavaScript Interface具体业务逻辑与WebView的依赖。

