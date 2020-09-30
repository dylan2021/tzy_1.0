package com.android.tongzhiyuan.act_0;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.tongzhiyuan.act_1.ChooseEmplyeeActivity;
import com.android.tongzhiyuan.act_other.CommonBaseActivity;
import com.android.tongzhiyuan.act_other.PictBean;
import com.android.tongzhiyuan.bean.DeptInfo;
import com.android.tongzhiyuan.bean.FileInfo;
import com.android.tongzhiyuan.bean.GroupInfo;
import com.android.tongzhiyuan.bean.MsgInfo;
import com.android.tongzhiyuan.bean.PurchaseInfo;
import com.android.tongzhiyuan.bean.ReimburseInfo;
import com.android.tongzhiyuan.bean.TripItemInfo;
import com.android.tongzhiyuan.util.DialogUtils;
import com.android.tongzhiyuan.util.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.tongzhiyuan.App;
import com.android.tongzhiyuan.R;
import com.android.tongzhiyuan.adapter.FileListAdapter;
import com.android.tongzhiyuan.bean.FileListInfo;
import com.android.tongzhiyuan.bean.HistoryInfo;
import com.android.tongzhiyuan.core.net.GsonRequest;
import com.android.tongzhiyuan.core.utils.Constant;
import com.android.tongzhiyuan.core.utils.DialogHelper;
import com.android.tongzhiyuan.core.utils.ImageUtil;
import com.android.tongzhiyuan.core.utils.KeyConst;
import com.android.tongzhiyuan.core.utils.NetUtil;
import com.android.tongzhiyuan.core.utils.RetrofitUtil;
import com.android.tongzhiyuan.core.utils.TextUtil;
import com.android.tongzhiyuan.util.FileTypeUtil;
import com.android.tongzhiyuan.util.ToastUtil;
import com.android.tongzhiyuan.view.ScrollListView;
import com.android.tongzhiyuan.widget.mulpicture.MulPictureActivity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gool Lee
 * @Date 申请详情
 */
public class MsgDetailActivity extends CommonBaseActivity {

    private int processId = 1;
    private String createName;
    public MsgDetailActivity context;
    private LinearLayout planItemLayout;
    private ScrollListView listView;
    private FileListAdapter fileListAdapter;
    private List<FileListInfo> fileData = new ArrayList<>();
    private LinearLayout copyerItemsLayout, itemsLayout1, itemsLayout2;
    private RelativeLayout auditLayout;
    private Button auditBt1, auditBt2, auditBt3;
    private MsgInfo info;
    private ImageView statusTagIv;
    private String TYPE;
    private JsonObject infoObj = new JsonObject();
    private View itemView;
    private View itemView2;
    private int agreeReject_recall;
    private List<GroupInfo> parentList = new ArrayList<>();
    private ImageView informPeopleAddBt;
    private boolean IS_NEW_TASK = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_msg_detail);
        Intent intent = getIntent();
        processId = intent.getIntExtra(KeyConst.id, 0);
        agreeReject_recall = intent.getIntExtra(KeyConst.agreeReject_recall, 0);
        IS_NEW_TASK = intent.getBooleanExtra(KeyConst.is_new_task, false);

        initTitleBackBt(intent.getStringExtra(KeyConst.title));
        context = this;

        initView();

        getAuditLogData();//审批日志

        getData();

        getDeptData();
    }

    private void addTopItems(String title, String value) {
        itemView = View.inflate(context, R.layout.item_key_value, null);
        TextView keyTv = (TextView) itemView.findViewById(R.id.item_key_tv);
        TextView valueTv = (TextView) itemView.findViewById(R.id.item_value_tv);//工期

        if (title.equals("出差天数")) {
            value = value.replace(".0", "");
        }
        if ("关联报销".equals(title) || "关联出差".equals(title)) {
            valueTv.setTextColor(ContextCompat.getColor(context, R.color.mainColor));

            if ("关联报销".equals(title)) {
                //出差 --> 关联多个报销
                JsonElement objArr = infoObj.get(KeyConst.bizProcessList);
                if (objArr != null && !objArr.isJsonNull()) {
                    JsonArray processArr = infoObj.getAsJsonArray(KeyConst.bizProcessList);
                    if (processArr != null && processArr.size() != 0 && !processArr.isJsonNull()) {
                        for (int i = 0; i < processArr.size(); i++) {
                            itemView = View.inflate(context, R.layout.item_key_value, null);
                            keyTv = (TextView) itemView.findViewById(R.id.item_key_tv);
                            valueTv = (TextView) itemView.findViewById(R.id.item_value_tv);


                            JsonObject processObj = processArr.get(i).getAsJsonObject();
                            String reformId = Utils.getObjStr(processObj, KeyConst.id);
                            String headline = Utils.getObjStr(processObj, KeyConst.headline);
                            String procNum = Utils.getObjStr(processObj, KeyConst.procNum);

                            final Intent intent = new Intent(context, MsgDetailActivity.class);
                            intent.putExtra(KeyConst.id, Integer.valueOf(reformId));
                            intent.putExtra(KeyConst.title, headline);
                            intent.putExtra(KeyConst.is_new_task, true);
                            intent.putExtra(KeyConst.agreeReject_recall, 0);

                            if (!IS_NEW_TASK) {
                                valueTv.setTextColor(ContextCompat.getColor(context, R.color.mainColor));

                                valueTv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        context.startActivity(intent);
                                    }
                                });
                            }
                            if (i == 0) {
                                keyTv.setText(title);
                            }
                            valueTv.setText(procNum);
                            //产值
                            itemsLayout1.addView(itemView);
                        }
                        return;
                    }
                }
                valueTv.setTextColor(ContextCompat.getColor(context, R.color.color212121));
                value = "无";
            } else {
                String reformId = Utils.getObjStr(infoObj, KeyConst.businessTripId);
                String headline = Utils.getObjStr(infoObj, KeyConst.businessTripHeadline);
                if (!TextUtil.isEmpty(reformId)) {
                    final Intent intent = new Intent(context, MsgDetailActivity.class);
                    intent.putExtra(KeyConst.id, Integer.valueOf(reformId));
                    intent.putExtra(KeyConst.title, headline);
                    intent.putExtra(KeyConst.is_new_task, true);
                    intent.putExtra(KeyConst.agreeReject_recall, 0);
                    valueTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (IS_NEW_TASK) {
                                finish();
                            } else {
                                context.startActivity(intent);
                            }
                        }
                    });
                } else {
                    valueTv.setTextColor(ContextCompat.getColor(context, R.color.color212121));
                    value = "无";
                }
            }

        }
        keyTv.setText(title);
        valueTv.setText(value);
        //产值
        itemsLayout1.addView(itemView);
    }

    private void addTopRedItems(String title, String value) {
        itemView = View.inflate(context, R.layout.item_key_value, null);
        TextView keyTv = (TextView) itemView.findViewById(R.id.item_key_tv);
        TextView valueTv = (TextView) itemView.findViewById(R.id.item_value_tv);//工期
        keyTv.setTextColor(ContextCompat.getColor(context, R.color.red));
        valueTv.setTextColor(ContextCompat.getColor(context, R.color.red));
        keyTv.setText(title);
        valueTv.setText(value);
        //产值
        itemsLayout1.addView(itemView);
    }

    //出差
    private void setTripsItemsLayout() {
        itemsLayout2 = (LinearLayout) findViewById(R.id.items_layout_2);
        JsonElement objArr = infoObj.get(KeyConst.tripList);
        if (objArr == null || objArr.isJsonNull()) {
            return;
        }
        JsonArray tripArr = infoObj.getAsJsonArray(KeyConst.tripList);

        if (tripArr == null || tripArr.size() == 0 || tripArr.isJsonNull()) {
            return;
        }
        List<TripItemInfo> infoList = new Gson().fromJson(tripArr, new TypeToken<List<TripItemInfo>>() {
        }.getType());

        for (int i = 0; i < infoList.size(); i++) {
            TripItemInfo itemInfo = infoList.get(i);
            if (itemInfo == null) {
                return;
            }
            itemView2 = View.inflate(context, R.layout.item_go_out_item, null);
            TextView titleTv = (TextView) itemView2.findViewById(R.id.go_out_title_tv);
            titleTv.setText("行程" + (i + 1));

            addItemLayout2("交通工具", itemInfo.getVehicle(), R.id.keyTv0, R.id.valueTv0);
            addItemLayout2("单程往返", itemInfo.getType(), R.id.keyTv1, R.id.valueTv1);
            addItemLayout2("往返城市", itemInfo.getDeparture() + "-"
                    + itemInfo.getDestination(), R.id.keyTv2, R.id.valueTv2);
            addItemLayout2("开始时间", TextUtil.subTimeYmdHm(itemInfo.getStartTime()),
                    R.id.keyTv3, R.id.valueTv3);
            addItemLayout2("结束时间", TextUtil.subTimeYmdHm(itemInfo.getEndTime()),
                    R.id.keyTv4, R.id.valueTv4);
            addItemLayout2("时长(天)", TextUtil.remove_0(itemInfo.getPeriod() + ""),
                    R.id.keyTv5, R.id.valueTv5);
            //产值
            itemsLayout2.addView(itemView2);

        }
    }

    //报销
    private void setReimbursesItemsLayout() {
        itemsLayout2 = (LinearLayout) findViewById(R.id.items_layout_2);
        JsonElement objArr = infoObj.get(KeyConst.reimburseDetailList);
        if (objArr == null || objArr.isJsonNull()) {
            return;
        }
        JsonArray tripArr = infoObj.getAsJsonArray(KeyConst.reimburseDetailList);
        if (tripArr == null || tripArr.size() == 0 || tripArr.isJsonNull()) {
            return;
        }
        List<ReimburseInfo> infoList = new Gson().fromJson(tripArr, new TypeToken<List<ReimburseInfo>>() {
        }.getType());

        for (int i = 0; i < infoList.size(); i++) {
            ReimburseInfo itemInfo = infoList.get(i);
            if (itemInfo == null) {
                return;
            }
            itemView2 = View.inflate(context, R.layout.item_go_out_item, null);
            TextView titleTv = (TextView) itemView2.findViewById(R.id.go_out_title_tv);
            titleTv.setText("报销明细" + (i + 1));

            addItemLayout2("报销金额(元)", itemInfo.getAmount(), R.id.keyTv0, R.id.valueTv0);
            addItemLayout2("报销类别", itemInfo.getType(), R.id.keyTv1, R.id.valueTv1);
            addItemLayout2("费用明细", itemInfo.getRemark(), R.id.keyTv2, R.id.valueTv2);
            itemView2.findViewById(R.id.detail_layout2_item3).setVisibility(View.GONE);
            itemView2.findViewById(R.id.detail_layout2_item4).setVisibility(View.GONE);
            itemView2.findViewById(R.id.detail_layout2_item5).setVisibility(View.GONE);
            //产值
            itemsLayout2.addView(itemView2);
        }

    }

    //请购单
    private void setPurchaseItemsLayout() {
        itemsLayout2 = (LinearLayout) findViewById(R.id.items_layout_2);
        JsonElement obj = infoObj.get(KeyConst.purchaseDetail);
        if (obj == null || obj.isJsonNull()) {
            return;
        }
        JsonArray objArr = infoObj.getAsJsonArray(KeyConst.purchaseDetail);
        if (objArr == null || objArr.size() == 0 || objArr.isJsonNull()) {
            return;
        }
        List<PurchaseInfo> infoList = new Gson().fromJson(
                objArr, new TypeToken<List<PurchaseInfo>>() {
                }.getType());

        for (int i = 0; i < infoList.size(); i++) {
            PurchaseInfo itemInfo = infoList.get(i);
            if (itemInfo == null) {
                return;
            }
            itemView2 = View.inflate(context, R.layout.item_go_out_item, null);
            TextView titleTv = (TextView) itemView2.findViewById(R.id.go_out_title_tv);
            titleTv.setText("采购明细" + (i + 1));

            addItemLayout2("名称", itemInfo.getName(), R.id.keyTv0, R.id.valueTv0);
            addItemLayout2("规格", itemInfo.getSpec(), R.id.keyTv1, R.id.valueTv1);
            String numUnit = itemInfo.getNumber().replace(".0", "") + itemInfo.getUnit();
            addItemLayout2("数量", numUnit, R.id.keyTv2, R.id.valueTv2);
            addItemLayout2("价格(元)", itemInfo.getPrice(), R.id.keyTv3, R.id.valueTv3);
            itemView2.findViewById(R.id.detail_layout2_item4).setVisibility(View.GONE);
            itemView2.findViewById(R.id.detail_layout2_item5).setVisibility(View.GONE);
            //产值
            itemsLayout2.addView(itemView2);
        }

    }

    private void addItemLayout2(String key, String value, int keyViewId, int valueViewId) {
        ((TextView) itemView2.findViewById(keyViewId)).setText(key);
        ((TextView) itemView2.findViewById(valueViewId)).setText(value);
    }

    //抄送人列表
    private void initCopyerLayout(String employeeName) {
        if (TextUtil.isEmpty(employeeName) || copyerItemsLayout == null) {
            return;
        }
        copyerItemsLayout.removeAllViews();
        String[] nameArr = new String[1];
        if (!employeeName.contains(",")) {
            nameArr[0] = employeeName;
        } else {
            nameArr = employeeName.split(",");
        }
        for (String name : nameArr) {
            View itemView = View.inflate(context, R.layout.item_tv_iv, null);
            TextView copyerIv = (TextView) itemView.findViewById(R.id.copyer_iv);
            TextView copyerNameTv = (TextView) itemView.findViewById(R.id.copyer_tv);
            copyerNameTv.setText(name);
            copyerIv.setText(TextUtil.getLast2(name));
            //产值
            copyerItemsLayout.addView(itemView);
        }
    }

    private List<String> payTypeArr = new ArrayList();
    private String payType;

    private void initView() {
        initData();
        itemsLayout1 = (LinearLayout) findViewById(R.id.items_layout_1);
        statusTagIv = (ImageView) findViewById(R.id.status_tag_iv);
        informPeopleAddBt = (ImageView) findViewById(R.id.inform_people_add_bt);
        auditLayout = findViewById(R.id.audit_layout);
        auditBt1 = (Button) findViewById(R.id.audit_bt_1);
        auditBt2 = (Button) findViewById(R.id.audit_bt_2);
        auditBt3 = (Button) findViewById(R.id.audit_bt_3);

        if (agreeReject_recall == 1) {//同意,驳回
            auditLayout.setVisibility(View.VISIBLE);
            auditBt3.setVisibility(View.GONE);
            final Map<String, Object> map = new HashMap<>();
            map.put(KeyConst.processId, processId);

            //同意
            auditBt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    agreeReject_recall = 0;

                    if (info != null && "1".equals(info.lastNode) && Constant.PURCHASE.equals(TYPE)) {
                        View inflate = LayoutInflater.from(context).inflate(R.layout.
                                layout_purchase_agress, null);
                        final TextView payTypeTv = inflate.findViewById(R.id.pay_type_tv);
                        final EditText totalAmountEt = inflate.findViewById(R.id.total_amount_tv);
                        if (null != infoObj && !infoObj.isJsonNull()) {
                            String totalMoney = Utils.getObjStr(infoObj, KeyConst.totalAmount);
                            totalAmountEt.setText(totalMoney);
                            totalAmountEt.setSelection(totalMoney.length());
                        }
                        final EditText remarkEt = inflate.findViewById(R.id.dialog_remark_tv);

                        new MaterialDialog.Builder(context)
                                .customView(inflate, false)
                                .positiveColorRes(R.color.mainColor)
                                .positiveText(R.string.sure)
                                .autoDismiss(false)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        String totalAmount = totalAmountEt.getText().toString();
                                        if (ToastUtil.showCannotEmpty(context, totalAmount, "采购总价格")) {
                                            return;
                                        }
                                        if (ToastUtil.showCannotEmpty(context, payType, "支付方式")) {
                                            return;
                                        }
                                        String remark = remarkEt.getText().toString();
                                        if (employeeIdArr != null && employeeIdArr.size() > 0 && !employeeIdArr.isJsonNull()) {
                                            JSONArray informList = new JSONArray();
                                            for (JsonElement element : employeeIdArr) {
                                                informList.put(element.getAsInt());
                                            }
                                            map.put(KeyConst.informList, informList);
                                        }
                                        map.put(KeyConst.totalAmount, totalAmount);
                                        map.put(KeyConst.payType, payType);
                                        map.put(KeyConst.remark, remark);
                                        map.put(KeyConst.auditResult, true);
                                        bottomAuditBtPost(new JSONObject(map));
                                        dialog.dismiss();
                                    }
                                })
                                .negativeColorRes(R.color.mainColor)
                                .negativeText(R.string.cancel)
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();

                        DialogUtils.showKeyBorad(totalAmountEt, context);

                        payTypeTv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String url = Constant.WEB_SITE + "/dict/dicts/cached/PAY_TYPE";

                                Response.Listener<JsonArray> successListener = new Response
                                        .Listener<JsonArray>() {
                                    @Override
                                    public void onResponse(JsonArray result) {

                                        if (result != null || result.size() == 0) {
                                            payTypeArr.clear();
                                            for (int i = 0; i < result.size(); i++) {
                                                JsonObject object = result.get(i).getAsJsonObject();
                                                String status = object.get(KeyConst.status).getAsString();
                                                if (Constant.DICT_STATUS_USED.equals(status)) {
                                                    payTypeArr.add(object.get(KeyConst.name).getAsString());
                                                }
                                            }
                                            new MaterialDialog.Builder(context)
                                                    .items(payTypeArr)// 列表数据
                                                    .itemsCallback(new MaterialDialog.ListCallback() {
                                                        @Override
                                                        public void onSelection(MaterialDialog dialog, View itemView,
                                                                                int position, CharSequence text) {
                                                            payType = payTypeArr.get(position);
                                                            payTypeTv.setText(payType);
                                                        }
                                                    }).show();

                                        } else {
                                            ToastUtil.show(context, "获取支付方式失败");
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
                        });


                    } else {
                        MaterialDialog.Builder inputDialog = DialogUtils.getInputDialog(context, "请输入备注信息");
                        inputDialog.negativeText(R.string.commit);
                        inputDialog.onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                String remark = ((EditText) dialog.getCustomView()).getText().toString();
                                if (employeeIdArr != null && employeeIdArr.size() > 0 && !employeeIdArr.isJsonNull()) {
                                    JSONArray informList = new JSONArray();
                                    for (JsonElement element : employeeIdArr) {
                                        informList.put(element.getAsInt());
                                    }
                                    map.put(KeyConst.informList, informList);
                                }
                                map.put(KeyConst.remark, remark);
                                map.put(KeyConst.auditResult, true);
                                bottomAuditBtPost(new JSONObject(map));

                            }
                        }).show();
                    }
                }
            });
            //驳回
            auditBt2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    agreeReject_recall = 0;
                    MaterialDialog.Builder inputDialog =
                            DialogUtils.getInputDialog(context, "请输入备注信息");
                    inputDialog.negativeText("提交");
                    inputDialog.onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            String remark = ((EditText) dialog.getCustomView()).getText().toString();
                            if (employeeIdArr != null && employeeIdArr.size() > 0 && !employeeIdArr.isJsonNull()) {
                                JSONArray informList = new JSONArray();
                                for (JsonElement element : employeeIdArr) {
                                    informList.put(element.getAsInt());
                                }
                                map.put(KeyConst.informList, informList);
                            }
                            map.put(KeyConst.remark, remark + "");
                            map.put(KeyConst.auditResult, false);
                            bottomAuditBtPost(new JSONObject(map));
                        }
                    }).show();

                }
            });
        } else if (agreeReject_recall == 2) {//撤销
            auditLayout.setVisibility(View.VISIBLE);
            auditBt3.setVisibility(View.VISIBLE);
            //撤销
            auditBt3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    agreeReject_recall = 0;

                    bottomAuditBtPost(null);
                }
            });
        }


    }

    private void bottomAuditBtPost(JSONObject jsonObject) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }

        String url = Constant.WEB_SITE;
        if (jsonObject == null) {
            url = url + "/biz/process/launch/" + processId;
        } else {
            url = url + "/biz/process/needDo";
        }
        DialogHelper.showWaiting(getSupportFragmentManager(), getString(R.string.loading));
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                if (null != context && !context.isFinishing()) {
                    DialogHelper.hideWaiting(getSupportFragmentManager());
                }
                if (result != null && result.toString().contains("200")) {
                    context.finish();
                } else {
                    ToastUtil.show(context, R.string.commit_faild);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != context && !context.isFinishing()) {
                    DialogHelper.hideWaiting(getSupportFragmentManager());
                }
                if (error != null && error.networkResponse != null &&
                        400 == error.networkResponse.statusCode) {
                    DialogUtils.showTipDialog(context, getString(R.string.recall_failed));
                } else {
                    ToastUtil.show(context, getString(R.string.server_exception));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConst.Content_Type, Constant.application_json);
                params.put(KeyConst.Authorization, KeyConst.Bearer + App.token);

                return params;
            }
        };
        App.requestQueue.add(jsonRequest);
    }


    //设置数据
    private void setView() {
        createName = info.createName;
        ((TextView) findViewById(R.id.msg_name_tv)).setText(createName);
        ((TextView) findViewById(R.id.msg_name_tag_tv)).setText(TextUtil.getLast2(createName));
        TextView statusTv = (TextView) findViewById(R.id.work_start_status_tv);
        int status = info.status;
        statusTv.setText(Utils.getStatusText(status));
        statusTv.setTextColor(Utils.getStatusColor(context, status));

        statusTagIv.setImageResource(Utils.getStatusDrawable(status));
    }


    private void setAuditLogView(List<HistoryInfo> planInfos) {
        copyerItemsLayout = (LinearLayout) findViewById(R.id.copyer_item_layout);
        planItemLayout.removeAllViews();
        planItemLayout.setVisibility(View.VISIBLE);
        int paddingTop = getResources().getDimensionPixelSize(R.dimen.dm023);
        for (int i = 0; i < planInfos.size(); i++) {
            final HistoryInfo itemInfo = planInfos.get(i);
            if (itemInfo == null) {
                return;
            }
            if ("已抄送".equals(itemInfo.getAuditStaus())) {
                initCopyerLayout(itemInfo.getEmployeeName());
                continue;
            }

            View itemView = View.inflate(context, R.layout.item_work_hostory, null);
            TextView ciycleNameTv = (TextView) itemView.findViewById(R.id.name_ciycle_tv);
            TextView nameTv = (TextView) itemView.findViewById(R.id.name_tv);
            TextView timeTv = (TextView) itemView.findViewById(R.id.time_tv);
            TextView remarkTv = (TextView) itemView.findViewById(R.id.remark_tv);
            TextView actionTv = (TextView) itemView.findViewById(R.id.action_tv);

            timeTv.setText(TextUtil.subTimeMDHm(itemInfo.getAuditTime()));//时间
            // "";
            if (TextUtil.isEmpty(itemInfo.getEmployeeName())) {
                Drawable drawablePassed = ContextCompat.getDrawable(context, R.drawable.ic_circle_passed);
                ciycleNameTv.setBackground(null);

                ciycleNameTv.setPadding(0, paddingTop, 0, 0);
                ciycleNameTv.setCompoundDrawablesWithIntrinsicBounds(null, drawablePassed, null, null);
                actionTv.setPadding(0, 0, 0, 0);
                actionTv.setText(Html.fromHtml("<font color='#4db1fc' >未找到审批人</font>，已自动通过"));
                planItemLayout.addView(itemView);
                continue;
            }
            String employeeName = TextUtil.remove_N(itemInfo.getEmployeeName());
            nameTv.setText(employeeName);
            ciycleNameTv.setText(TextUtil.getLast2(employeeName));//操作人
            actionTv.setText(0 == i ? "发起申请" : itemInfo.getAuditStaus());
            remarkTv.setText(itemInfo.getRemark());


            planItemLayout.addView(itemView);

        }
    }

    private void choisePicture() {
        int choose = 9 - pictures.size();
        Intent intent = new Intent(context, MulPictureActivity.class);
        bundle = setBundle();
        bundle.putInt("imageNum", choose);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);
        startActivityForResult(intent, 101);
    }

    //选择文件
    private void choiseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //同行人员
        if (requestCode == 1 && resultCode == 2) {
            JsonArray choosedEmployeeIds = new JsonArray();
            String nameStr = "";
            parentList = (List<GroupInfo>) data.
                    getSerializableExtra(KeyConst.OBJ_INFO);

            for (GroupInfo groupInfo : parentList) {
                if (groupInfo.isAllChecked()) {

                    for (GroupInfo.ChildrenBean employeeInfo : groupInfo.getChildren()) {
                        if (employeeInfo.isChildChecked()) {
                            choosedEmployeeIds.add(employeeInfo.getId());
                            String name = employeeInfo.getEmployeeName();
                            if (choosedEmployeeIds.size() == 1) {
                                nameStr = name;
                            } else {
                                nameStr = nameStr + "," + name;
                            }
                        }

                    }

                }
            }

            initCopyerLayout(nameStr);
            employeeIdArr = choosedEmployeeIds;
        }
        //上传附件
        String fileType = "";
        String path = "";
        if (data != null && data.getData() != null) {
            path = FileTypeUtil.getPath(context, data.getData());
            //不是合格的类型
            if (!FileTypeUtil.isFileType(path) && !ImageUtil.isImageSuffix(path)) {
                ToastUtil.show(context, "暂不支持该文件类型");
                return;
            }
            fileType = ImageUtil.isImageSuffix(path) ? Constant.FILE_TYPE_IMG : Constant.FILE_TYPE_DOC;
        }
        //上传图片
        if (requestCode == 101 && data != null) {
            setIntent(data);
            getBundleP();
            if (pictures != null && pictures.size() > 0) {
                fileType = Constant.FILE_TYPE_IMG;
                for (int i = 0; i < pictures.size(); i++) {
                    path = pictures.get(i).getLocalURL();
                    fileType = Constant.FILE_TYPE_IMG;
                }
            }
        }
        if (TextUtil.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        uploadPictureThread(file, fileType);
    }

    public void getBundleP() {
        if (getIntent() != null) {
            bundle = getIntent().getExtras();
            if (bundle != null) {
                pictures = (List<PictBean>) bundle.getSerializable("pictures") != null ?
                        (List<PictBean>) bundle.getSerializable("pictures") : new
                        ArrayList<PictBean>();
            }
        }
    }

    private void uploadPictureThread(final File file, final String fileType) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        DialogHelper.showWaiting(getSupportFragmentManager(), getString(R.string.uploading));
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put(KeyConst.fileType, fileType);
        final String url = Constant.WEB_FILE_UPLOAD;
        new Thread() {
            @Override
            public void run() {
                try {
                    RetrofitUtil.upLoadByCommonPost(url, file, map,
                            new RetrofitUtil.FileUploadListener() {
                                @Override
                                public void onProgress(long pro, double precent) {
                                }

                                @Override
                                public void onFinish(int code, final String responseUrl, Map<String, List<String>> headers) {
                                    DialogHelper.hideWaiting(getSupportFragmentManager());
                                    if (200 == code && responseUrl != null) {
                                        final String finalResponseUrl = responseUrl;
                                        context.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                fileData.add(new FileListInfo(
                                                        file.getName(), finalResponseUrl, file.length(), finalResponseUrl));
                                                fileListAdapter.setDate(fileData);
                                                ImageUtil.reSetLVHeight(context, listView);
                                            }
                                        });
                                    }
                                }
                            });
                } catch (IOException e) {
                    DialogHelper.hideWaiting(getSupportFragmentManager());
                }

            }
        }.start();
    }

    private void setFileListData() {
        TextView linkTv = (TextView) findViewById(R.id.file_link_iv);
        TextView fileTitleTv = (TextView) findViewById(R.id.card_detail_file_title);
        fileTitleTv.setText(R.string.file_link_list);
        fileTitleTv.setTextColor(ContextCompat.getColor(this, R.color.color999));
        ScrollListView listView = (ScrollListView) findViewById(R.id.horizontal_gridview);
        JsonObject object = info.object;
        if (object == null) {
            return;
        }
        JsonArray jsonArray = object.getAsJsonArray(KeyConst.attachList);
        List<FileInfo> attList = new Gson().fromJson(jsonArray, new TypeToken<List<FileInfo>>() {
        }.getType());
        if (attList != null) {
            for (FileInfo att : attList) {
                fileData.add(new FileListInfo(att.name, att.url, Constant.TYPE_SEE));
            }
        }
        if (fileData == null || fileData.size() == 0) {
            findViewById(R.id.card_detail_file_layout).setVisibility(View.GONE);
        } else {
            linkTv.setVisibility(View.GONE);
        }
        FileListAdapter fileListAdapter = new FileListAdapter(this, fileData);
        listView.setAdapter(fileListAdapter);
    }

    //查询流程数据
    private void getData() {
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        DialogHelper.showWaiting(getSupportFragmentManager(), "加载中...");
        String url = Constant.WEB_SITE + "/biz/process/" + processId;
        Response.Listener<MsgInfo> successListener = new Response
                .Listener<MsgInfo>() {
            @Override
            public void onResponse(MsgInfo result) {
                DialogHelper.hideWaiting(getSupportFragmentManager());
                if (result == null) {
                    ToastUtil.show(context, R.string.no_data);
                    return;
                }
                info = result;
                setView();

                setTypeView13();
                setFileListData();//附件
            }
        };

        Request<MsgInfo> versionRequest = new
                GsonRequest<MsgInfo>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ToastUtil.show(context, R.string.server_exception);
                        DialogHelper.hideWaiting(getSupportFragmentManager());
                    }
                }, new TypeToken<MsgInfo>() {
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

    private void getAuditLogData() {
        planItemLayout = (LinearLayout) findViewById(R.id.history_item_layout);
        String url = Constant.WEB_SITE + "/biz/process/" + processId + "/log";
        Response.Listener<List<HistoryInfo>> successListener = new Response.
                Listener<List<HistoryInfo>>() {
            @Override
            public void onResponse(List<HistoryInfo> result) {
                if (result == null || result.size() == 0) {
                    return;
                }
                setAuditLogView(result);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        };

        Request<List<HistoryInfo>> request = new
                GsonRequest<List<HistoryInfo>>(Request.Method.GET,
                        url, successListener, errorListener, new TypeToken<List<HistoryInfo>>() {
                }.getType()) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConst.Authorization, KeyConst.Bearer + App.token);
                        return params;
                    }
                };
        App.requestQueue.add(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context = null;
    }


    JsonArray employeeIdArr = new JsonArray();

    //不同的申请类型  不同字段
    private void setTypeView13() {
        TYPE = info.type;
        infoObj = info.object;
        String procNum = info.procNum;//审批编号;
        String deptName = info.createDeptName;//所在部门;
        addTopItems("审批编号", procNum);
        if (null != infoObj && !infoObj.isJsonNull()) {
            addTopItems(Constant.WAGE_AUDIT.equals(TYPE) ? "申请部门" : "所在部门", deptName);
            if (Constant.LEAVE.equals(TYPE)) {//请假
                Log.d(TAG, "" + infoObj);
                setTypeData(0);
            } else if (Constant.BUSINESS_TRIP.equals(TYPE)) {//出差
                setTypeData(1);

                setTripsItemsLayout();//行程列表
            } else if (Constant.OVERTIME.equals(TYPE)) {//加班
                setTypeData(2);
            } else if (Constant.REGULAR_WORKER.equals(TYPE)) {//转正
                addTopItems("实际申请人", info.applicantName);
                addTopItems("所属部门", info.applicantDeptName);
                setTypeData(3);
            } else if (Constant.RECRUIT.equals(TYPE)) {//招聘
                setTypeData(4);
            } else if (Constant.DIMISSION.equals(TYPE)) {//离职
                addTopItems("实际申请人", info.applicantName);
                addTopItems("所属部门", info.applicantDeptName);
                setTypeData(5);
            } else if (Constant.OFFICIAL_DOCUMENT.equals(TYPE)) {//公文
                setTypeData(6);

                //审批权限  设置抄送人
                if (agreeReject_recall == 1) {
                    setInformPeopleBt();
                }
            } else if (TYPE.equals(Constant.REIMBURSE)) {//报销
                setTypeData(7);
                setReimbursesItemsLayout();
            } else if (Constant.PETTY_CASH.equals(TYPE)) {//备用金
                setTypeData(8);
            } else if (Constant.PAY.equals(TYPE)) {//付款
                setTypeData(9);
            } else if (Constant.PURCHASE.equals(TYPE)) {//申购采购
                setTypeData(10);
                setPurchaseItemsLayout();
            } else if (Constant.WAGE_AUDIT.equals(TYPE)) {//工资审核
                addTopItems("日期", Utils.getObjStr(infoObj, KeyConst.wageYear) + "年"
                        + Utils.getObjStr(infoObj, KeyConst.wageMonth) + "月");

                String objStr = Utils.getObjStr(infoObj, KeyConst.totalHourNum);
                addTopItems("总计时", objStr.replace(".0", "") + "小时");

                String objStr0 = Utils.getObjStr(infoObj, KeyConst.totalPieceMonthNum);
                addTopItems("小料包计件", TextUtil.isEmpty(objStr0) ? "0包" : objStr0 + "包");
                String objStr02 = Utils.getObjStr(infoObj, KeyConst.totalPieceMonthPrice);
                addTopItems("小料包计件工资", TextUtil.isEmpty(objStr02) ? "0元" : objStr02 + "元");

                String objStr1 = Utils.getObjStr(infoObj, KeyConst.totalPieceNum);
                addTopItems("其他计件", objStr1.replace(".0", ""));
              /*  String objStr12 = Utils.getObjStr(infoObj, KeyConst.pieceWage);
                addTopItems("其他计件工资", TextUtil.isEmpty(objStr12) ? "0元" : objStr12 + "元");*/

                String objStr2 = Utils.getObjStr(infoObj, KeyConst.totalDeduction);
                addTopItems("总扣款", TextUtil.isEmpty(objStr2) ? "0元" : objStr2.replace(".00", "") + "元");
                String objStr3 = Utils.getObjStr(infoObj, KeyConst.totalAttendReward);
                addTopItems("奖金", TextUtil.isEmpty(objStr3) ? "0元" : objStr3.replace(".00", "") + "元");
                String personNum = Utils.getObjStr(infoObj, KeyConst.personNum);
                addTopItems("参与人员", TextUtil.isEmpty(personNum) ? "0人" : personNum + "人");
                String totalWage = Utils.getObjStr(infoObj, KeyConst.totalWage);
                addTopRedItems("合计", TextUtil.isEmpty(totalWage) ? "0元" : totalWage + "元");

                //审批权限  设置抄送人
                if (agreeReject_recall == 1) {
                    setInformPeopleBt();
                }
            } else if (TYPE.equals(Constant.NOTICE)) {//公告

            }
        }
    }

    //添加抄送人
    private void setInformPeopleBt() {
        informPeopleAddBt.setVisibility(View.VISIBLE);
        informPeopleAddBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (parentList == null || parentList.size() == 0) {
                    getDeptData();
                } else {
                    //设置进入选择抄送人页面被选中状态
                    setInformSeletedStatus();

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(KeyConst.OBJ_INFO, (Serializable) parentList);//序列化,要注意转化(Serializable)
                    Intent intent = new Intent(context, ChooseEmplyeeActivity.class);
                    intent.putExtras(bundle);
                    context.startActivityForResult(intent, 1);
                }
            }
        });

        if (info.informList != null && !info.informList.isJsonNull()) {
            employeeIdArr = info.informList.getAsJsonArray();
        }
        //抄送人
        String informListName = info.informNameList;
        initCopyerLayout(informListName);//公文抄送人
    }

    String[] titleArr0 = {"请假类型", "时长(小时)", "请假事由"};
    String[] valueArr0 = {KeyConst.leaveType, KeyConst.period, KeyConst.remark};

    String[] titleArr1 = {"出差事由", "关联报销", "出差天数", "同行人员", "出差备注"};
    String[] valueArr1 = {KeyConst.reason, KeyConst.reimburseProcessNum, KeyConst.period,
            KeyConst.peers, KeyConst.remark};

    String[] titleArr2 = {"开始时间", "结束时间", "时长(小时)", "加班原因"};
    String[] valueArr2 = {KeyConst.startTime, KeyConst.endTime, KeyConst.period, KeyConst.remark};

    String[] titleArr3 = {"入职日期", "试用期(月)", "转正日期", "工作期间表现"};//转正
    String[] valueArr3 = {KeyConst.employmentDate,
            KeyConst.probationPeriod, KeyConst.regularDate, KeyConst.remark};

    String[] titleArr4 = {"需求岗位", "需求人数(人)", "期望到岗日期", "岗位职责需求"};//招聘
    String[] valueArr4 = {KeyConst.recruitPost, KeyConst.recruitNum, KeyConst.arrivalDate, KeyConst.remark};

    String[] titleArr5 = {"入职日期", "最后工作日", "离职原因"};//离职
    String[] valueArr5 = {KeyConst.employmentDate, KeyConst.lastWorkDate, KeyConst.remark};

    String[] titleArr6 = {"公文内容"};
    String[] valueArr6 = {KeyConst.remark};

    String[] titleArr7 = {"关联出差", "报销总金额(元)"};
    String[] valueArr7 = {KeyConst.businessTripNum, KeyConst.totalAmount};

    String[] titleArr8 = {"申请金额(元)", "使用日期", "归还日期", "申请事由"};
    String[] valueArr8 = {KeyConst.applyAmount, KeyConst.useDate, KeyConst.returnDate, KeyConst.remark};

    String[] titleArr9 = {"付款金额(元)", "付款方式", "支付日期", "支付对象", "开户行", "银行账户", "付款事由"};
    String[] valueArr9 = {KeyConst.amount, KeyConst.payType, KeyConst.payDate,
            KeyConst.payObject, KeyConst.bankName, KeyConst.bankAccount, KeyConst.remark
    };

    String[] titleArr10 = {"申请事由", "采购类型", "期望交付日期", "总价格(元)", "支付方式", "备注"};//申购
    String[] valueArr10 = {KeyConst.reason, KeyConst.purchaseType, KeyConst.deliverDate,
            KeyConst.totalAmount, KeyConst.payType,
            KeyConst.remark};


    List<String[]> titleArrList = new ArrayList<>();
    List<String[]> valueArrList = new ArrayList<>();

    private void initData() {
        titleArrList.add(titleArr0);
        titleArrList.add(titleArr1);
        titleArrList.add(titleArr2);
        titleArrList.add(titleArr3);
        titleArrList.add(titleArr4);
        titleArrList.add(titleArr5);
        titleArrList.add(titleArr6);
        titleArrList.add(titleArr7);
        titleArrList.add(titleArr8);
        titleArrList.add(titleArr9);
        titleArrList.add(titleArr10);

        valueArrList.add(valueArr0);
        valueArrList.add(valueArr1);
        valueArrList.add(valueArr2);
        valueArrList.add(valueArr3);
        valueArrList.add(valueArr4);
        valueArrList.add(valueArr5);
        valueArrList.add(valueArr6);
        valueArrList.add(valueArr7);
        valueArrList.add(valueArr8);
        valueArrList.add(valueArr9);
        valueArrList.add(valueArr10);
    }

    private void setTypeData(int type) {
        String[] titleArr = titleArrList.get(type);
        String[] valueArr = valueArrList.get(type);
        for (int i = 0; i < titleArr.length; i++) {
            String titleStr = titleArr[i];
            String key = valueArr[i];

            if ("请假类型".equals(titleStr)) {//请假  去掉时间的秒
                addTopItems(titleStr, Utils.getDictTypeName(context,
                        KeyConst.LEAVE_TYPE, Utils.getObjStr(infoObj, key)));
                addTopItems("开始时间", TextUtil.subTimeYmdHm(
                        Utils.getObjStr(infoObj, KeyConst.startTime)));
                addTopItems("结束时间", TextUtil.subTimeYmdHm(
                        Utils.getObjStr(infoObj, KeyConst.endTime)));
            } else if ("试用期(月)".equals(titleStr)) {
                addTopItems(titleStr, Utils.getObjStr(infoObj, key).replace(".00", ""));
            } else {
                addTopItems(titleStr, Utils.getObjStr(infoObj, key));
            }

        }
    }


    //获取同行人员
    private void getDeptData() {
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        String url = Constant.WEB_SITE + "/upms/departments/tree";
        Response.Listener<List<DeptInfo>> successListener = new Response
                .Listener<List<DeptInfo>>() {
            @Override
            public void onResponse(List<DeptInfo> result) {
                if (result == null || result.size() == 0) {
                    return;
                }
                int size = result.size();
                for (int i = 0; i < size; i++) {
                    List<DeptInfo.ChildrenBeanX> deptList = result.get(i).getChildren();
                    int size1 = deptList.size();
                    for (int j = 0; j < size1; j++) {
                        DeptInfo.ChildrenBeanX child = deptList.get(j);
                        int id = child.getId();
                        String title = child.getTitle();
                        //最后一个,更新数据
                        boolean lastIndex = i == size - 1 && j == size1 - 1;
                        int orderBy = child.getOrderBy();
                        getItemData(id, title, lastIndex, orderBy);
                    }
                }
            }
        };

        Request<List<DeptInfo>> versionRequest = new
                GsonRequest<List<DeptInfo>>(Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }, new TypeToken<List<DeptInfo>>() {
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

    private void getItemData(final int deptId, final String title, final boolean lastIndex, final int orderBy) {
        if (parentList == null) {
            parentList = new ArrayList<>();
        }
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        String url = Constant.WEB_SITE + "/upms/departments/" + deptId + "/employees/all?included=0";
        Response.Listener<List<GroupInfo.ChildrenBean>> successListener = new Response
                .Listener<List<GroupInfo.ChildrenBean>>() {
            @Override
            public void onResponse(List<GroupInfo.ChildrenBean> result) {
                parentList.add(new GroupInfo(deptId, title, false, result, orderBy));

            }
        };

        Request<List<GroupInfo.ChildrenBean>> versionRequest = new
                GsonRequest<List<GroupInfo.ChildrenBean>>(Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }, new TypeToken<List<GroupInfo.ChildrenBean>>() {
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

    private void setInformSeletedStatus() {
        if (employeeIdArr != null && employeeIdArr.size() > 0 && !employeeIdArr.isJsonNull()) {
            int size = parentList.size();
            for (JsonElement element : employeeIdArr) {
                for (int j = 0; j < size; j++) {
                    GroupInfo groupInfo = parentList.get(j);
                    List<GroupInfo.ChildrenBean> children = groupInfo.getChildren();
                    if (children == null) {
                        continue;
                    }
                    for (int i = 0; i < children.size(); i++) {
                        GroupInfo.ChildrenBean childrenBean = children.get(i);
                        String asString = element.getAsString();
                        if (asString.equals(childrenBean.getId() + "")) {
                            parentList.get(j).setAllChecked(true);
                            parentList.get(j).getChildren().get(i).setChildChecked(true);
                            break;//结束这个for
                        }
                    }
                }
            }
        }
    }

}
