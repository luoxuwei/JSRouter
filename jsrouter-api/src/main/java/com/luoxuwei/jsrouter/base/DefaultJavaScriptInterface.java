package com.luoxuwei.jsrouter.base;

import android.app.Activity;
import android.widget.Toast;

import com.luoxuwei.jsrouter.utils.ReturnCallback;

import org.json.JSONObject;

/**
 * Created by 罗旭维 on 2021/8/13.
 */
public class DefaultJavaScriptInterface extends BaseJavaScriptInterface {
    @Override
    public void onCall(Activity activity, JSONObject param, ReturnCallback returnCallback) {
        Toast.makeText(activity, "DefaultJavaScriptInterface", Toast.LENGTH_SHORT).show();
    }
}
