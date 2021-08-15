package com.luoxuwei.jsrouter.demo;

import android.app.Activity;
import android.widget.Toast;

import com.luoxuwei.jsrouter.annotation.JSRoute;
import com.luoxuwei.jsrouter.base.BaseJavaScriptInterface;
import com.luoxuwei.jsrouter.utils.ReturnCallback;

import org.json.JSONObject;

/**
 * Created by 罗旭维 on 2021/8/15.
 */
@JSRoute(path = "TestRoute1")
public class TestRout1 extends BaseJavaScriptInterface {
    @Override
    public void onCall(Activity activity, JSONObject param, ReturnCallback returnCallback) {
        Toast.makeText(activity, "TestRout1", Toast.LENGTH_SHORT).show();
    }
}
