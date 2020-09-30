package com.android.tongzhiyuan.act_1;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.tongzhiyuan.bean.DeptInfo;
import com.android.tongzhiyuan.bean.GroupInfo;
import com.android.tongzhiyuan.bean.ProjDeptInfo;
import com.android.tongzhiyuan.bean.EmployeeInfo;
import com.android.tongzhiyuan.core.net.GsonRequest;
import com.android.tongzhiyuan.util.DialogUtils;
import com.android.tongzhiyuan.util.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.tongzhiyuan.App;
import com.android.tongzhiyuan.R;
import com.android.tongzhiyuan.act_other.BaseFgActivity;
import com.android.tongzhiyuan.core.utils.Constant;
import com.android.tongzhiyuan.core.utils.DialogHelper;
import com.android.tongzhiyuan.core.utils.KeyConst;
import com.android.tongzhiyuan.core.utils.NetUtil;
import com.android.tongzhiyuan.core.utils.TextUtil;
import com.android.tongzhiyuan.util.TimeUtils;
import com.android.tongzhiyuan.util.ToastUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *  @author Gool Lee
 */
public class WageDailyAddActivity extends BaseFgActivity {
    private String chooseId;
    private WageDailyAddActivity context;
    private TextView timeTv, belongDeptTv;
    private TextView hoursSumTv, numbersSumTv, remarkTv;
    private TextView moneyDeductTv, formalEmployeeTv, temporaryEmployeeTv;
    private TextView projectNameTv;
    private String workDate, deptId;
    private List<ProjDeptInfo> projInfoList = new ArrayList<>();
    private int pieceWageId;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_daily_wage_add);
        context = this;
        type = getIntent().getIntExtra(KeyConst.type, 0);
        initTitleBackBt(type > 0 ? "工时录入" : "小料包计件录入");
        chooseId = getIntent().getStringExtra(KeyConst.id);

        initView();
        //getEmplyeeList(1);//正式
        getDeptData();
        getEmplyeeList(2);//临时
    }

    private List<EmployeeInfo> employeeList = new ArrayList<>();
    private List<EmployeeInfo> tempEmployeeList = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //同行人员
        if (requestCode == 1 && resultCode == 2) {
            JSONArray choosedEmployeeIds = new JSONArray();
            String nameStr = "";
            parentList = (List<GroupInfo>) data.
                    getSerializableExtra(KeyConst.OBJ_INFO);

            for (GroupInfo groupInfo : parentList) {
                if (groupInfo.isAllChecked()) {

                    for (GroupInfo.ChildrenBean employeeInfo : groupInfo.getChildren()) {
                        if (employeeInfo.isChildChecked()) {

                            choosedEmployeeIds.put(employeeInfo.getId());
                            String name = employeeInfo.getEmployeeName();
                            if (choosedEmployeeIds.length() == 1) {
                                nameStr = name;
                            } else if (choosedEmployeeIds.length() < Constant.EMPLYEE_SHOW_NUMBER) {
                                nameStr = name + "、" + nameStr;
                            }
                        }

                    }

                }
            }
            if (choosedEmployeeIds.length() >= Constant.EMPLYEE_SHOW_NUMBER) {
                nameStr = nameStr + "等" + choosedEmployeeIds.length() + "人";
            }
            formalEmployeeTv.setText(nameStr);
            employeeIdList = choosedEmployeeIds;
        }
    }

    private void getEmplyeeList(final int type) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE + (type == 1 ? "/upms/employees/all?status=1"
                : "/biz/wage/wageOnDay/tempEmployee");
        Response.Listener<List<EmployeeInfo>> successListener = new Response
                .Listener<List<EmployeeInfo>>() {
            @Override
            public void onResponse(List<EmployeeInfo> result) {
                if (type == 1) {
                    employeeList = result;
                } else {
                    tempEmployeeList = result;
                }
            }
        };

        Request<List<EmployeeInfo>> versionRequest = new
                GsonRequest<List<EmployeeInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ToastUtil.show(context, getString(R.string.get_data_faild));
                    }
                }, new TypeToken<List<EmployeeInfo>>() {
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

    String[] projItems = {};
    private List<GroupInfo> parentList = new ArrayList<>();

    private void initView() {
        projectNameTv = (TextView) findViewById(R.id.hours_in_name_tv);
        if (type > 0) {
            findViewById(R.id.must_inout_iv).setVisibility(View.GONE);
            projectNameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getProjsList();
                }
            });
        } else {
            findViewById(R.id.hours_add_layout).setVisibility(View.GONE);
            findViewById(R.id.deduct_layout).setVisibility(View.GONE);
            projectNameTv.setCompoundDrawables(null, null, null, null);
            projectNameTv.setText(Constant.xiaoliaobao_proj_name);
        }
        timeTv = (TextView) findViewById(R.id.hours_add_time_tv);

        //所属部门
        belongDeptTv = (TextView) findViewById(R.id.belong_department_tv);
        belongDeptTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeptTreeList();
            }
        });
        //计时,计件
        hoursSumTv = (TextView) findViewById(R.id.hours_in_hours_sum_tv);
        numbersSumTv = (TextView) findViewById(R.id.hours_in_numbers_sum_tv);
        moneyDeductTv = (TextView) findViewById(R.id.hours_in_money_deduct_tv);
        remarkTv = (TextView) findViewById(R.id.remark_tv);
        long todayTime = TimeUtils.getTodayZeroTime();
        workDate = TimeUtils.getTimeYmd(todayTime);
        timeTv.setText(workDate);
        timeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.Builder timePickerDialog = DialogUtils.getTimePicker(context);
                timePickerDialog.setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        if (millseconds > System.currentTimeMillis()) {
                            ToastUtil.show(context, "时间不可以大于今日");
                            return;
                        }
                        workDate = TimeUtils.getTimeYmd(millseconds);
                        timeTv.setText(workDate);
                    }
                });

                timePickerDialog.build().show(context.getSupportFragmentManager(), "");
            }
        });

        formalEmployeeTv = (TextView) findViewById(R.id.employee_formal_tv);
        temporaryEmployeeTv = (TextView) findViewById(R.id.employee_temporary_tv);


        formalEmployeeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showEmplyeeListDialog(employeeList, 1);
                if (parentList == null || parentList.size() == 0) {
                    getDeptData();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(KeyConst.OBJ_INFO, (Serializable) parentList);//序列化,要注意转化(Serializable)
                    Intent intent = new Intent(context, ChooseEmplyeeActivity.class);
                    intent.putExtras(bundle);
                    context.startActivityForResult(intent, 1);
                }

            }
        });
        temporaryEmployeeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEmplyeeListDialog(tempEmployeeList, 2);
            }
        });
    }

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
                final String deptName = Utils.getObjStr(jsonObj, KeyConst.title);
                nameTv.setText(deptName);

                nameTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deptId = id;
                        belongDeptTv.setText(deptName);

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
                if (result == null || result.size() == 0) {
                    ToastUtil.show(context, "数据为空");
                    return;
                }
                projItems = new String[result.size()];
                for (int i = 0; i < projItems.length; i++) {
                    ProjDeptInfo projDeptInfo = result.get(i);
                    projItems[i] = projDeptInfo.getProjectName();
                }

                new MaterialDialog.Builder(context)
                        .items(projItems)// 列表数据
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView,
                                                    int position, CharSequence text) {
                                ProjDeptInfo projDeptInfo = projInfoList.get(position);
                                projectNameTv.setText(projDeptInfo.getProjectName());
                                pieceWageId = projDeptInfo.getId();
                            }
                        })
                        .show();

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

    JSONArray employeeIdList = new JSONArray();
    JSONArray tempEmployeeIdList = new JSONArray();

    private void showEmplyeeListDialog(final List<EmployeeInfo> list, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style
                .dialog_appcompat_theme_fullscreen);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_dialog_emplyee_choose, null);
        LinearLayout itemsLayout = (LinearLayout) v.findViewById(R.id.emplyee_seleted_items_layout);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                final EmployeeInfo itemInfo = list.get(i);
                View itemView = View.inflate(context, R.layout.layout_dialog_emplyee_item, null);
                final TextView nameTv = (TextView) itemView.findViewById(R.id.emplyee_choosed_name_tv);
                nameTv.setText(itemInfo.getEmployeeName());
                nameTv.setSelected(itemInfo.getSeleted());
                nameTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean selected = nameTv.isSelected();
                        nameTv.setSelected(!selected);

                        itemInfo.setSeleted(!selected);
                    }
                });
                itemsLayout.addView(itemView);
            }

        } else {
            ToastUtil.show(context, "人员为空");
        }
        final Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(v);
        v.findViewById(R.id.dialog_btn_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        v.findViewById(R.id.emplyee_seleted_save_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String nameStr = "";
                JSONArray employeeIds = new JSONArray();
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    EmployeeInfo employeeInfo = list.get(i);
                    boolean seleted = employeeInfo.getSeleted();
                    if (type == 1) {
                        employeeList.get(i).setSeleted(seleted);
                    } else {
                        tempEmployeeList.get(i).setSeleted(seleted);
                    }
                    if (seleted) {
                        employeeIds.put(employeeInfo.getId());
                        String name = employeeInfo.getEmployeeName();


                        if (employeeIds.length() == 1) {
                            nameStr = name;
                        } else if (employeeIds.length() < Constant.EMPLYEE_SHOW_NUMBER) {
                            nameStr = name + "、" + nameStr;
                        }

                    }
                }
                if (employeeIds.length() >= Constant.EMPLYEE_SHOW_NUMBER) {
                    nameStr = nameStr + "等" + employeeIds.length() + "人";
                }
                //正式
                if (type == 1) {
                    employeeIdList = employeeIds;
                    formalEmployeeTv.setText(nameStr);
                } else {
                    tempEmployeeIdList = employeeIds;
                    temporaryEmployeeTv.setText(nameStr);
                }
                if (employeeIds.length() == 0) {
                    ToastUtil.show(context, "请至少选择一个人员");
                    return;
                }

                dialog.dismiss();
            }
        });
    }

    private void addPost() {
        String hoursNum = hoursSumTv.getText().toString();
        String pieceNum = numbersSumTv.getText().toString();
        String deduction = moneyDeductTv.getText().toString();//扣款
        String remark = remarkTv.getText().toString();
        if (type == -1) {
            pieceWageId = Constant.xiaoliaobao_proj_id;
        }
        if (pieceWageId == 0) {
            ToastUtil.show(context, "请选择项目");
            return;
        }
        if (TextUtil.isEmpty(workDate)) {
            ToastUtil.show(context, "请选择日期");
            return;
        }
        if (TextUtil.isEmpty(deptId)) {
            ToastUtil.show(context, "请选择所属部门");
            return;
        }
        if ((employeeIdList == null || employeeIdList.length() == 0) &&
                (tempEmployeeIdList == null || tempEmployeeIdList.length() == 0)) {
            ToastUtil.show(context, "至少选择一个参与人员");
            return;
        }

        if (type > 0) {
            if (TextUtil.isEmpty(hoursNum)) {
                hoursNum = "0";
            }
            if (TextUtil.isEmpty(pieceNum)) {
                pieceNum = "0";
            }
            if (TextUtil.isEmpty(deduction)) {
                deduction = "0";
            }
        } else {
            if (ToastUtil.showCannotEmpty(context, pieceNum, "计件数量")) {
                return;
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put(KeyConst.employeeIdList, employeeIdList);
        map.put(KeyConst.tempEmployeeIdList, tempEmployeeIdList);
        map.put(KeyConst.deptId, deptId);
        map.put(KeyConst.workDate, workDate);
        map.put(KeyConst.pieceWageId, pieceWageId);

        if (type > 0) {
            map.put(KeyConst.hourNum, hoursNum);//计时
            map.put(KeyConst.deduction, deduction);//扣款
        }
        map.put(KeyConst.pieceNum, pieceNum);//计件
        map.put(KeyConst.remark, remark);//扣款

        //添加
        final FragmentManager fm = getSupportFragmentManager();
        DialogHelper.showWaiting(fm, "加载中...");
        String url;
        if (type > 0) {
            url = Constant.WEB_SITE + "/biz/wage/wageOnDay";
        } else {
            url = Constant.WEB_SITE + "/biz/pieceMonthRecord/pieceMonthPerDayVO";//小料包
        }
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        if (result == null) {
                            DialogHelper.hideWaiting(fm);
                            ToastUtil.show(context, "工时录入失败,稍后重试");
                            return;
                        }
                        ToastUtil.show(context, "工时录入成功");
                        DialogHelper.hideWaiting(fm);
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "数据" + TextUtil.getErrorMsg(error));
                DialogHelper.hideWaiting(fm);
                ToastUtil.show(context, getString(R.string.server_exception));
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

    public void onReportCommitClick(View view) {
        addPost();
    }
}
