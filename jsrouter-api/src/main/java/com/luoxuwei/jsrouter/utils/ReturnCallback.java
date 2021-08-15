package com.luoxuwei.jsrouter.utils;

import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.WebView;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * Created by 罗旭维 on 2021/8/15.
 */
public class ReturnCallback {
    private String callbackFun;
    private WebView webview;

    public ReturnCallback(String callbackFun, WebView webview) {
        this.webview = webview;
        if (TextUtils.isEmpty(callbackFun)) {
            this.callbackFun = "void";
        } else {
            this.callbackFun = callbackFun;
        }
    }

    public void complete(JSONObject params) {
        if (params == null) {
            params = new JSONObject();
        }

        try {
            call( params.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void complete(String json) {
        try {
            call(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void call(String retValue) {
        try {
            if (retValue == null) retValue = "";
            retValue = URLEncoder.encode(retValue, "UTF-8").replaceAll("\\+", "%20");
            String script = String.format("%s(decodeURIComponent(\"%s\"));delete window.%s", callbackFun, retValue, callbackFun);
            evaluateJavascript(script);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public WebView getWebview() {
        return webview;
    }

    //如果当前在主线程，不要直接调用post,这可能会延迟js执行
    public void evaluateJavascript(final String script) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            _evaluateJavascript(script);
        } else {
            webview.post(new Runnable() {
                @Override
                public void run() {
                    _evaluateJavascript(script);
                }
            });
        }
    }

    private void _evaluateJavascript(String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webview.evaluateJavascript(script, null);
        } else {
            webview.loadUrl("javascript:" + script);
        }
    }
}