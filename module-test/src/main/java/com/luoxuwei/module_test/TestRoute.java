package com.luoxuwei.module_test;

import android.app.Activity;
import android.widget.Toast;

import com.luoxuwei.jsrouter.annotation.JSRoute;
import com.luoxuwei.jsrouter.base.BaseJavaScriptInterface;

import org.json.JSONObject;

/**
 * Created by 罗旭维 on 2021/8/14.
 */
@JSRoute(path = "TestToute")
public class TestRoute extends BaseJavaScriptInterface {
    @Override
    public void onCall(Activity activity, JSONObject param) {
        Toast.makeText(activity, "TestRoute", Toast.LENGTH_SHORT).show();
    }
}
