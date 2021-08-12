package com.luoxuwei.jsrouter.base;

import android.app.Activity;
import android.content.Intent;
import android.webkit.WebView;

import org.json.JSONObject;

/**
 * Created by 罗旭维 on 2021/8/12.
 */
public abstract class BaseJavaScriptInterface {
    public abstract void onCall(JSONObject param);
    public void onActivityResult(Activity webActivity, WebView webView, int requestCode, int resultCode, Intent data) {}
}
