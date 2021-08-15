package com.luoxuwei.jsrouter.demo

import android.app.Application
import com.luoxuwei.jsrouter.JSRouter
import com.luoxuwei.jsrouter.annotation.JSRouterModuleList

/**
 * Created by 罗旭维 on 2021/8/13.
 */
@JSRouterModuleList(value = ["app","app1","moduletest"])
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        JSRouter.init(this)
    }
}