package com.luoxuwei.jsrouter.demo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.luoxuwei.jsrouter.JSRouter

/**
 * Created by 罗旭维 on 2021/8/4.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(view: View) {
        JSRouter.init(application)
        JSRouter.getInstance().navigation("MyRoute").onCall(this, null)
        JSRouter.getInstance().navigation("xxx").onCall(this, null)
    }
}