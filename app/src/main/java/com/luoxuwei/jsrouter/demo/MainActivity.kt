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

        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.setSupportZoom(false) // 设置支持缩放
        settings.builtInZoomControls = false
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
            jsRouterInject?.injectJs()
            super.onReceivedTitle(view, title)
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            jsRouterInject?.injectJs()
            super.onProgressChanged(view, newProgress)
        }
    }
}