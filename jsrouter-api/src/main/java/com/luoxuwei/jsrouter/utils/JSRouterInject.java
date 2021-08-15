package com.luoxuwei.jsrouter.utils;

import android.app.Activity;
import android.os.Build;
import android.os.Looper;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.luoxuwei.jsrouter.JSRouter;

import org.json.JSONObject;

/**
 * Created by 罗旭维 on 2021/8/15.
 */
public class JSRouterInject {
    private static final String INJECT_JS = "function getJsBridge(){return{call:function(e,c,a){var b='';if(typeof c=='function'){a=c;c={}}if(typeof a=='function'){window.dscb=window.dscb||0;var d='dscb'+window.dscb++;window[d]=a;c._dscbstub=d}c=JSON.stringify(c||{});if(window._dswk){b=prompt(window._dswk+e,c)}else{if(typeof _dsbridge=='function'){b=_dsbridge(e,c)}else{b=_dsbridge.call(e,c)}}return b}}};";

    private static final String BRIDGE_NAME = "_dsbridge";
    private static final String DSCBSTUB = "_dscbstub";
    private Activity mActivity;
    private WebView mWebView;
    private String mUrl;

    private JSRouterInject(Builder builder) {
        mActivity = builder.mActivity;
        mWebView = builder.mWebView;
        mUrl = builder.mUrl;
        mWebView.addJavascriptInterface(new JSObject(), BRIDGE_NAME);
    }

    public void injectJs() {
        evaluateJavascript(INJECT_JS);
    }

    public void evaluateJavascript(final String script) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            _evaluateJavascript(script);
        } else {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    _evaluateJavascript(script);
                }
            });
        }
    }

    private void _evaluateJavascript(String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript(script, null);
        } else {
            mWebView.loadUrl("javascript:" + script);
        }
    }


    private class JSObject {

        @JavascriptInterface
        public String call(String methodName, String args) {
            String error = "Js bridge method called, but there is " +
                    "not a JavascriptInterface object, please set JavascriptInterface object first!";

            try {
                JSONObject arg = new JSONObject(args);
                String callback = "";

                try {
                    callback = arg.optString(DSCBSTUB);
                    arg.remove(DSCBSTUB);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                JSRouter.getInstance().navigation(methodName).onCall(mActivity, arg, new ReturnCallback(callback, mWebView));

                return "";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
    }

    public static class Builder {
        private Activity mActivity;
        private WebView mWebView;
        private String mUrl;

        public Builder(Activity activity, WebView webView) {
            mActivity = activity;
            mWebView = webView;
        }

        public Builder setUrl(String url) {
            mUrl = url;
            return this;
        }

        public JSRouterInject build() {
            return new JSRouterInject(this);
        }
    }
}
