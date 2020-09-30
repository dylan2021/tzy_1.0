package com.android.tongzhiyuan.core.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.tongzhiyuan.App;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import com.android.tongzhiyuan.bean.JsonResult;
import com.android.tongzhiyuan.bean.User;
import com.android.tongzhiyuan.core.net.GsonRequest;

/**
 * 用户登录辅助类
 * Created by zeng on 2016/7/26.
 */
public class LoginHelper {

    public static final String TAG = "777";

    private Context context;
    private SharedPreferences preferences;
    private String userName;
    private String passWord;

    public LoginHelper(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(Constant.CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        userName = preferences.getString(KeyConst.username, "");
        passWord = preferences.getString(Constant.sp_pwd, "");
    }

    /**
     * 重新登录
     */
    public void reLogin() {

    }
}
