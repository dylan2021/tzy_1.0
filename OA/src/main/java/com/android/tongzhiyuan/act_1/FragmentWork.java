package com.android.tongzhiyuan.act_1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import com.android.tongzhiyuan.App;
import com.android.tongzhiyuan.R;
import com.android.tongzhiyuan.act_0.MainActivity;
import com.android.tongzhiyuan.base.fragment.BaseSearchFragment;
import com.android.tongzhiyuan.core.net.GsonRequest;
import com.android.tongzhiyuan.core.utils.Constant;
import com.android.tongzhiyuan.core.utils.KeyConst;
import com.android.tongzhiyuan.core.utils.NetUtil;
import com.android.tongzhiyuan.core.utils.TextUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Gool Lee
 */
@SuppressLint({"WrongConstant", "ValidFragment"})
public class FragmentWork extends BaseSearchFragment {
    private int chooseId;
    private View view;
    private MainActivity context;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private TextView dailyWageBt;
    private TextView pieceAddBt, pieceListBt;

    public FragmentWork() {
    }

    public FragmentWork(int chooseId) {
        this.chooseId = chooseId;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_work;
    }

    @Override
    protected void initViewsAndEvents(View v) {
        context = (MainActivity) getActivity();
        view = v;

        sp = context.getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE);
        editor = sp.edit();

        String authArr = sp.getString(KeyConst.AUTH_ARR_STR, "");

        dailyWageBt = view.findViewById(R.id.daily_wage_bt);
        pieceAddBt = view.findViewById(R.id.work_bt_17);
        pieceListBt = view.findViewById(R.id.work_bt_16);

        if (!TextUtil.isEmpty(authArr)) {
            JsonParser parser = new JsonParser();
            JsonArray authJsonArr = parser.parse(authArr).getAsJsonArray();
            setAuthView(authJsonArr);
        }

        getAuth();

        for (int id : idArr) {
            view.findViewById(id).setOnClickListener(onWorkBtClickListener);
        }

        dailyWageBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //每日工资
                Intent intent = new Intent(context, WageDailyListActivity.class);
                intent.putExtra(KeyConst.type, 1);
                context.startActivity(intent);
            }
        });

        pieceAddBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WageDailyAddActivity.class);
                intent.putExtra(KeyConst.type, -1);
                context.startActivity(intent);
            }
        });

        pieceListBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WageDailyListActivity.class);
                intent.putExtra(KeyConst.type, -1);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getAuth();
        }
    }

    private void setAuthView(JsonArray result) {
        int length = authArr.length;
        boolean title0Show = false;
        boolean title1Show = false;
        boolean title2Show = false;
        boolean title3Show = false;
        flag:
        for (int i = 0; i < length; i++) {
            TextView authBt = view.findViewById(idArr[i]);

            for (JsonElement authObj : result) {
                if (authArr[i].equals(authObj.getAsString())) {
                    authBt.setVisibility(View.VISIBLE);
                    if (i < 3) {
                        title0Show = true;
                    }
                    if (i > 3 && i < 8) {
                        title1Show = true;
                    }
                    if (i > 7 && i < 10) {
                        title2Show = true;
                    }
                    if (i > 9 && i < 14) {
                        title3Show = true;
                    }
                    continue flag;
                }
            }
            authBt.setVisibility(View.GONE);
        }

        int dailtBtAuth = view.findViewById(R.id.work_bt_9).getVisibility();//每日工资录入
        dailyWageBt.setVisibility(dailtBtAuth);
        pieceAddBt.setVisibility(dailtBtAuth);
        pieceListBt.setVisibility(dailtBtAuth);

        view.findViewById(R.id.work_title_0).setVisibility(title0Show ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.work_title_1).setVisibility(title1Show ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.work_title_2).setVisibility(title2Show ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.work_title_3).setVisibility(title3Show ? View.VISIBLE : View.GONE);
    }

    Class classArr[] = {Work024567911_12Activity.class, Work1TripActivity.class,
            Work024567911_12Activity.class, Work024567911_12Activity.class, Work024567911_12Activity.class,
            Work024567911_12Activity.class, Work024567911_12Activity.class, Work024567911_12Activity.class
            , WageDailyAddActivity.class, WageMonthActivity.class,
            Work8Activity.class, Work024567911_12Activity.class,
            Work024567911_12Activity.class, Work13Activity.class};
    int idArr[] = {R.id.work_bt_1, R.id.work_bt_2, R.id.work_bt_3, R.id.work_bt_4, R.id.work_bt_5,
            R.id.work_bt_6, R.id.work_bt_7, R.id.work_bt_8, R.id.work_bt_9, R.id.work_bt_10,
            R.id.work_bt_11, R.id.work_bt_12, R.id.work_bt_13, R.id.work_bt_14};
    private View.OnClickListener onWorkBtClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            Intent intent = new Intent();
            intent.putExtra(KeyConst.id, chooseId);
            for (int i = 0; i < idArr.length; i++) {
                if (id == idArr[i]) {
                    intent.putExtra(KeyConst.type, i);
                    intent.setClass(context, classArr[i]);
                    context.startActivity(intent);
                }
            }
        }
    };

    @Override
    protected void onFirstUserVisible() {

    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    protected View getLoadView(View view) {
        return null;
    }

    String authArr[] = {"leaveProcess_create", "businessTrip_create", "overtime_create", "3",
            "regularWorker_create", "recruit_create", "dimission_create", "officialDocument_create",
            "wage:wageOnDay", "wage:wageOnMonth",
            "reimburse_create", "pettyCash_create", "pay_create", "purchase_create"};

    private void getAuth() {
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        String url = Constant.WEB_SITE + "/upms/accounts/authorities";
        Response.Listener<JsonArray> successListener = new Response
                .Listener<JsonArray>() {
            @Override
            public void onResponse(JsonArray result) {
                if (result == null || result.size() == 0) {
                    return;
                }
                editor.putString(KeyConst.AUTH_ARR_STR, result.toString()).commit();
                setAuthView(result);
            }
        };

        Request<JsonArray> versionRequest = new
                GsonRequest<JsonArray>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }, new TypeToken<JsonArray>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConst.Authorization, KeyConst.Bearer + App.token);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);
    }

}
