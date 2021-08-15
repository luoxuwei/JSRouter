package com.luoxuwei.jsrouter.demo
import android.content.Intent
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.luoxuwei.jsrouter.utils.JSRouterInject

/**
 * Created by 罗旭维 on 2021/8/4.
 */
class MainActivity : AppCompatActivity() {
    var jsRouterInject: JSRouterInject? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val webView = findViewById<WebView>(R.id.webview)
        val settings: WebSettings = webView.getSettings()
        settings.javaScriptEnabled = true
        settings.setSupportMultipleWindows(true)
        settings.domStorageEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.pluginState = WebSettings.PluginState.ON // 设置插件支持

        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.setSupportZoom(false) // 设置支持缩放

        settings.builtInZoomControls = false
        settings.cacheMode = WebSettings.LOAD_DEFAULT // 设置默认缓存模式，根据cache-control决定是否从网络上取数据。

        settings.databaseEnabled = true // 启用数据库缓存

        settings.domStorageEnabled = true // 启用DOM缓存

        settings.setAppCacheEnabled(true) // 启用应用缓存

        settings.savePassword = false // 关闭webview的自动保存密码

        settings.allowContentAccess = true
        settings.allowFileAccess = false // 启用WebView访问文件数据

        settings.allowUniversalAccessFromFileURLs = false //允许跨域

        settings.textZoom = 100 //设置字体大小不变

        webView.setVerticalScrollBarEnabled(false)
        webView.setHorizontalScrollBarEnabled(false)
        webView.webChromeClient = mWebChromeClient
        jsRouterInject = JSRouterInject.Builder(this, webView).setUrl("").build()

        webView.loadUrl("file:///android_asset/test.html")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    val mWebChromeClient = object: WebChromeClient() {
        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            jsRouterInject?.injectJs()
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            jsRouterInject?.injectJs()
        }
    }
}