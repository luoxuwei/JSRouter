package com.luoxuwei.jsrouter.demo;

import android.app.Activity;
import android.widget.Toast;

import com.luoxuwei.jsrouter.annotation.JSRoute;
import com.luoxuwei.jsrouter.base.BaseJavaScriptInterface;

import org.json.JSONObject;

/**
 * Created by 罗旭维 on 2021/8/13.
 */
@JSRoute(path = "MyRoute2")
public class MyRoute2 extends BaseJavaScriptInterface {
    @Override
    public void onCall(Activity activity, JSONObject param) {
        Toast.makeText(activity, "MyRoute2", Toast.LENGTH_SHORT).show();
    }
}
