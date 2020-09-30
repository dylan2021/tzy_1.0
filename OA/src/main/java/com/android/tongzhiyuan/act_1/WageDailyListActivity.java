package com.android.tongzhiyuan.act_1;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.tongzhiyuan.bean.ProjDeptInfo;
import com.android.tongzhiyuan.core.utils.ImageUtil;
import com.android.tongzhiyuan.util.DialogUtils;
import com.android.tongzhiyuan.util.TimeUtils;
import com.android.tongzhiyuan.util.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.tongzhiyuan.App;
import com.android.tongzhiyuan.R;
import com.android.tongzhiyuan.act_other.BaseFgActivity;
import com.android.tongzhiyuan.bean.WageDailyInfo;
import com.android.tongzhiyuan.core.net.GsonRequest;
import com.android.tongzhiyuan.core.utils.Constant;
import com.android.tongzhiyuan.core.utils.KeyConst;
import com.android.tongzhiyuan.core.utils.NetUtil;
import com.android.tongzhiyuan.core.utils.TextUtil;
import com.android.tongzhiyuan.util.ToastUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gool Lee
 * 每日工资详情列表
 */
public class WageDailyListActivity extends BaseFgActivity {
    private WageDailyListActivity context;
    private WageDailyListAdapter mAdapter;
    private ListView mListView;
    private RefreshLayout mRefreshLayout;
    private List<ProjDeptInfo> projInfoList = new ArrayList<>();
    private String[] projItems;
    private String workDate;
    private TextView deptTv;
    private String projName, deptName;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wage_daily);
        context = this;

        type = getIntent().getIntExtra(KeyConst.type, 0);
        initStatusBar();
        initTitleBackBt(type > 0 ? "每日工资" : "小料包计件");
        initView();
        getProjsList();
    }

    private int pieceWageId, deptId;

    private void showFilterDialog() {
        final Dialog dialog = new Dialog(context, R.style.dialog_appcompat_theme);
        View inflate = LayoutInflater.from(context).inflate(R.layout.
                layout_wage_daily_filter, null);
        final TextView projTv = inflate.findViewById(R.id.filter_proj_tv);
        deptTv = inflate.findViewById(R.id.pay_type_tv);
        final TextView timeTv = inflate.findViewById(R.id.filter_time_bt_1);
        projTv.setText(type > 0 ? projName : (Constant.xiaoliaobao_proj_name + "  "));
        timeTv.setText(workDate);
        deptTv.setText(deptName);
        View.OnClickListener mDialogClickLstener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                switch (v.getId()) {
                    case R.id.menu_ok_bt:
                        String searchDate = "searchDate=" + workDate.replace("-", "");
                        if (pieceWageId != 0) {
                            pieceWageId = type > 0 ? pieceWageId : Constant.xiaoliaobao_proj_id;
                            searchDate = searchDate + "&pieceWageId=" + pieceWageId;
                        }
                        if (deptId != 0) {
                            searchDate = searchDate + "&deptId=" + deptId;
                        }
                        getListData(searchDate);
                        dialog.dismiss();
                        break;
                    case R.id.filter_proj_tv://项目
                        if (projInfoList == null || projInfoList.size() == 0) {
                            getProjsList();
                            ToastUtil.show(context, "项目数据为空");
                        } else {
                            projItems = new String[projInfoList.size()];
                            for (int i = 0; i < projItems.length; i++) {
                                ProjDeptInfo projDeptInfo = projInfoList.get(i);
                                projItems[i] = projDeptInfo.getProjectName();
                            }

                            new MaterialDialog.Builder(context)
                                    .items(projItems)// 列表数据
                                    .itemsCallback(new MaterialDialog.ListCallback() {
                                        @Override
                                        public void onSelection(MaterialDialog dialog, View itemView,
                                                                int position, CharSequence text) {
                                            ProjDeptInfo projDeptInfo = projInfoList.get(position);
                                            pieceWageId = projDeptInfo.getId();
                                            projName = projDeptInfo.getProjectName();
                                            projTv.setText(projName);
                                        }
                                    })
                                    .show();
                        }

                        break;

                    case R.id.pay_type_tv://部门
                        getDeptTreeList();
                        break;
                    case R.id.filter_time_bt_1:
                        TimePickerDialog.Builder build = DialogUtils.getTimePicker(context);
                        build.setCallBack(new OnDateSetListener() {
                            @Override
                            public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                                workDate = TimeUtils.getTimeYmd(millseconds);
                                timeTv.setText(workDate);
                            }
                        });

                        build.build().show(getSupportFragmentManager(), "");

                        break;
                    case R.id.menu_cancel_bt:
                        //选择
                        dialog.dismiss();
                        break;
                }
            }
        };
        inflate.findViewById(R.id.menu_ok_bt).setOnClickListener(mDialogClickLstener);
        inflate.findViewById(R.id.menu_cancel_bt).setOnClickListener(mDialogClickLstener);

        if (type > 0) {
            projTv.setOnClickListener(mDialogClickLstener);
        } else {
            projTv.setCompoundDrawables(null, null, null, null);
        }
        deptTv.setOnClickListener(mDialogClickLstener);
        timeTv.setOnClickListener(mDialogClickLstener);
        dialog.setContentView(inflate);


        Window dialogWindow = dialog.getWindow();
        dialogWindow.setBackgroundDrawable(ContextCompat.
                getDrawable(context, R.drawable.shape_f5f5f5_20px));
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);

        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = ImageUtil.getScreenWidth(context) - 200;
        dialogWindow.setAttributes(params);
        dialog.show();

    }

    private void getDeptTreeList() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE + "/upms/departments/tree";
        Response.Listener<JsonArray> successListener = new Response
                .Listener<JsonArray>() {
            @Override
            public void onResponse(JsonArray result) {
                if (result == null || result.isJsonNull() || result.size() == 0) {
                    ToastUtil.show(context, "部门数据为空");
                    return;
                }
                JsonObject companyObj = result.get(0).getAsJsonObject();
                if (companyObj != null && !companyObj.isJsonNull()) {//公司下面
                    JsonArray childrenJsonArr = companyObj.getAsJsonArray(KeyConst.children);
                    showDeptTreeList(childrenJsonArr, false);
                }
            }
        };

        Request<JsonArray> versionRequest = new
                GsonRequest<JsonArray>(Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d(TAG, "解析:" + volleyError.toString());
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

    List<AlertDialog> dialogList = new ArrayList<>();

    private void showDeptTreeList(JsonArray jsonArray, boolean showBack) {
        if (jsonArray == null || jsonArray.isJsonNull() || jsonArray.size() == 0) {
            ToastUtil.show(context, "部门数据为空");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style
                .dialog_appcompat_theme_fullscreen);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_dialog_emplyee_choose, null);
        LinearLayout itemsLayout = (LinearLayout) v.findViewById(R.id.emplyee_seleted_items_layout);
        itemsLayout.removeAllViews();


        final AlertDialog deptDialog = builder.create();
        dialogList.add(deptDialog);
        deptDialog.show();
        deptDialog.getWindow().setContentView(v);
        int size = jsonArray.size();
        for (int i = 0; i < size; i++) {
            JsonObject jsonObj = jsonArray.get(i).getAsJsonObject();
            View itemView = View.inflate(context, R.layout.item_dept_next, null);
            TextView nameTv = (TextView) itemView.findViewById(R.id.dept_next_name_tv);
            nameTv.setPadding(40, 0, 0, 0);
            TextView nextTv = (TextView) itemView.findViewById(R.id.dept_next_bt);

            if (jsonObj != null && !jsonObj.isJsonNull()) {
                final JsonArray childrenJsonArr = jsonObj.getAsJsonArray(KeyConst.children);
                if (childrenJsonArr != null && !childrenJsonArr.isJsonNull() && childrenJsonArr.size() != 0) {
                    nextTv.setVisibility(View.VISIBLE);
                    nextTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDeptTreeList(childrenJsonArr, true);
                        }
                    });
                }

                final String id = Utils.getObjStr(jsonObj, KeyConst.id);
                final String deptNameDialog = Utils.getObjStr(jsonObj, KeyConst.title);
                nameTv.setText(deptNameDialog);

                nameTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deptName = deptNameDialog;
                        deptId = Integer.valueOf(id);
                        deptTv.setText(deptName);
                        for (AlertDialog alertDialog : dialogList) {
                            alertDialog.dismiss();
                        }

                    }
                });

                //产值
                itemsLayout.addView(itemView);
            }
        }

        TextView backTv = v.findViewById(R.id.dialog_btn_cancel);
        backTv.setText(showBack ? "返回" : "取消");
        backTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                deptDialog.dismiss();
            }
        });
        v.findViewById(R.id.emplyee_seleted_save_bt).setVisibility(View.GONE);
    }

    //选择项目
    private void getProjsList() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE + "/biz/wage/projectName";
        Response.Listener<List<ProjDeptInfo>> successListener = new Response
                .Listener<List<ProjDeptInfo>>() {
            @Override
            public void onResponse(List<ProjDeptInfo> result) {
                projInfoList = result;
            }
        };

        Request<List<ProjDeptInfo>> versionRequest = new
                GsonRequest<List<ProjDeptInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ToastUtil.show(context, getString(R.string.get_data_faild));
                        Log.d(TAG, "数据异常" + volleyError.toString());
                    }
                }, new TypeToken<List<ProjDeptInfo>>() {
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

    private void initView() {
        Button titleRightBt = getTitleRightBt("");
        Drawable filterDrawable = context.getResources().getDrawable(R.drawable.ic_filter);
        titleRightBt.setCompoundDrawablesWithIntrinsicBounds(null, null, filterDrawable, null);
        titleRightBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });
        workDate = TimeUtils.getTimeYmd(TimeUtils.getTodayZeroTime());
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.autoRefresh();
        mListView = (ListView) findViewById(R.id.circle_lv);
        emptyTv = findViewById(R.id.empty_tv);
        mAdapter = new WageDailyListAdapter(context, type);
        mListView.setAdapter(mAdapter);

        Utils.setLoadHeaderFooter(context, mRefreshLayout);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                pieceWageId = type > 0 ? 0 : Constant.xiaoliaobao_proj_id;
                projName = "";

                deptId = 0;
                deptName = "";
                workDate = TimeUtils.getTimeYmd(TimeUtils.getTodayZeroTime());
                getListData("");
            }
        });
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore();
                ToastUtil.show(context, getString(R.string.no_more_data));
            }
        });
    }

    private void getListData(final String date) {
        TextUtil.initEmptyTv(context, emptyTv);
        if (!NetUtil.isNetworkConnected(context)) {
            mRefreshLayout.finishRefresh(0);
            return;
        }
        String url;
        if (type > 0) {
            url = Constant.WEB_SITE + "/biz/wage/wageOnDay/all?" + date;
        } else {
            url = Constant.WEB_SITE + "/biz/pieceMonthRecord/pieceMonthPerDayVO/list?" + date;//小料包
        }
        Response.Listener<List<WageDailyInfo>> successListener = new Response
                .Listener<List<WageDailyInfo>>() {
            @Override
            public void onResponse(List<WageDailyInfo> result) {
                mRefreshLayout.finishRefresh(0);
                if (result == null || result.size() == 0) {
                    emptyTv.setText(context.getString(R.string.no_data));
                    emptyTv.setVisibility(View.VISIBLE);
                    mAdapter.setData(null);
                    return;
                }
                mAdapter.setData(result);
            }
        };

        Request<List<WageDailyInfo>> versionRequest = new
                GsonRequest<List<WageDailyInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mRefreshLayout.finishRefresh(0);
                        mAdapter.setData(null);
                        emptyTv.setText(context.getString(R.string.server_exception));
                        emptyTv.setVisibility(View.VISIBLE);
                    }
                }, new TypeToken<List<WageDailyInfo>>() {
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