/*
 * 	Flan.Zeng 2011-2016	http://git.oschina.net/signup?inviter=flan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.tongzhiyuan.act_3;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.android.tongzhiyuan.R;
import com.android.tongzhiyuan.act_0.MsgDetailActivity;
import com.android.tongzhiyuan.act_0.NoticeDetailUrlActivity;
import com.android.tongzhiyuan.act_1.WageMonthAddActivity;
import com.android.tongzhiyuan.act_1.Work024567911_12Activity;
import com.android.tongzhiyuan.act_1.Work13Activity;
import com.android.tongzhiyuan.act_1.Work1TripActivity;
import com.android.tongzhiyuan.act_1.Work8Activity;
import com.android.tongzhiyuan.bean.MsgInfo;
import com.android.tongzhiyuan.core.utils.Constant;
import com.android.tongzhiyuan.core.utils.KeyConst;
import com.android.tongzhiyuan.core.utils.TextUtil;
import com.android.tongzhiyuan.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * @author Gool Lee
 * @since
 */
public class MeApplyListAdapter extends BaseAdapter {

    private List<MsgInfo> msgInfos;

    private MeApplyListActivity context;
    private int meListPositon = 0;

    public MeApplyListAdapter(MeApplyListActivity context, List<MsgInfo> msgInfos) {
        super();
        this.context = context;
        this.msgInfos = msgInfos;
    }

    /**
     * 设置ListView中的数据
     */

    public void setData(List<MsgInfo> msgInfoList, int type) {
        msgInfos = msgInfoList;
        meListPositon = type;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (msgInfos != null) {
            return msgInfos.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (msgInfos != null) {
            return msgInfos.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_me_apply_lv, parent, false);
            holder.titleTv = (TextView) convertView.findViewById(R.id.apply_item_title_tv);
            holder.tv1 = (TextView) convertView.findViewById(R.id.apply_item_type_tv);
            holder.timeTv = (TextView) convertView.findViewById(R.id.apply_item_time_tv);
            holder.tv2 = (TextView) convertView.findViewById(R.id.apply_item_start_time_tv);
            holder.tv3 = (TextView) convertView.findViewById(R.id.apply_item_end_time_tv);
            holder.statusTv = (TextView) convertView.findViewById(R.id.apply_item_status_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MsgInfo info = msgInfos.get(position);
        if (info != null) {

            holder.titleTv.setText(info.headline);

            holder.tv2.setVisibility(View.VISIBLE);
            holder.tv3.setVisibility(View.VISIBLE);
            final int status = info.status;
            holder.statusTv.setText(Utils.getStatusText(status));
            holder.statusTv.setTextColor(
                    Utils.getStatusColor(context, status));

            String type = info.type;
            JsonObject object = info.object;
            final Intent intent = new Intent();
            intent.putExtra(KeyConst.id, info.id);
            //我的草稿 ==3  直接到修改界面 +显示"提交"/"删除"按钮
            if (meListPositon == 3) {
                intent.setClass(context, Work024567911_12Activity.class);
                intent.putExtra(KeyConst.type, 0);
            } else {
                intent.setClass(context, MsgDetailActivity.class);
                if (meListPositon == 0 && status == 2 && "2".equals(info.isAudit)) {//我发起的   正在审核状态的->撤销
                    intent.putExtra(KeyConst.agreeReject_recall, 2);
                } else {
                    intent.putExtra(KeyConst.agreeReject_recall, 0);
                }

                intent.putExtra(KeyConst.title, info.headline);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    context.startActivity(intent);
                }
            });
            if (null != object) {
                if (Constant.LEAVE.equals(type)) {//请假
                    String typeValue = Utils.getObjStr(object, KeyConst.leaveType);
                    holder.tv1.setText("请假类型：" + Utils.getDictTypeName(context, KeyConst.LEAVE_TYPE, typeValue));
                    holder.tv2.setText("开始时间：" + TextUtil.subTimeYmdHm(
                            Utils.getObjStr(object, KeyConst.startTime)));
                    holder.tv3.setText("结束时间：" + TextUtil.subTimeYmdHm(
                            Utils.getObjStr(object, KeyConst.endTime)));
                } else if (Constant.BUSINESS_TRIP.equals(type)) {//出差
                    holder.tv1.setText("出差天数：" + Utils.getObjStr(object, KeyConst.period).
                            replace(".0", "") + "天");
                    String peers = Utils.getObjStr(object, KeyConst.peers);
                    holder.tv2.setText("同行人员：" + (TextUtil.isEmpty(peers) ? "无" : peers));
                    holder.tv3.setVisibility(View.GONE);

                    if (meListPositon == 3) {
                        intent.setClass(context, Work1TripActivity.class);
                    }
                } else if (Constant.OVERTIME.equals(type)) {//加班
                    holder.tv1.setText("开始时间：" + Utils.getObjStr(object, KeyConst.startTime));
                    holder.tv2.setText("结束时间：" + Utils.getObjStr(object, KeyConst.endTime));
                    holder.tv3.setText("时长：" + Utils.getObjStr(object, KeyConst.period) + "小时");
                    intent.putExtra(KeyConst.type, 2);
                } else if (Constant.REGULAR_WORKER.equals(type)) {//转正
                    holder.tv1.setText("入职日期：" + Utils.getObjStr(object, KeyConst.employmentDate));
                    holder.tv2.setText("试用期：" + Utils.getObjStr(object, KeyConst.probationPeriod).replace(".00", "个月"));
                    holder.tv3.setText("转正日期：" + Utils.getObjStr(object, KeyConst.regularDate));
                    intent.putExtra(KeyConst.type, 4);
                } else if (Constant.RECRUIT.equals(type)) {//招聘
                    holder.tv1.setText("需求岗位：" + Utils.getObjStr(object, KeyConst.recruitPost));
                    holder.tv2.setText("需求人数：" + Utils.getObjStr(object, KeyConst.recruitNum) + "人");
                    holder.tv3.setVisibility(View.GONE);
                    intent.putExtra(KeyConst.type, 5);
                } else if (Constant.DIMISSION.equals(type)) {//离职
                    holder.tv1.setText("入职日期：" + Utils.getObjStr(object, KeyConst.employmentDate));
                    holder.tv2.setText("最后工作日：" + Utils.getObjStr(object, KeyConst.lastWorkDate));
                    holder.tv3.setVisibility(View.GONE);
                    intent.putExtra(KeyConst.type, 6);
                } else if (Constant.OFFICIAL_DOCUMENT.equals(type)) {//公文
                    holder.tv1.setText("审批部门：" + Utils.getObjStr(object, KeyConst.auditDept));
                    holder.tv2.setVisibility(View.GONE);
                    holder.tv3.setVisibility(View.GONE);
                    intent.putExtra(KeyConst.type, 7);
                } else if (Constant.REIMBURSE.equals(type)) {//报销
                    holder.tv1.setText("报销总金额：" + Utils.getObjStr(object, KeyConst.totalAmount) + "元");
                    holder.tv2.setVisibility(View.GONE);
                    holder.tv3.setVisibility(View.GONE);
                    if (meListPositon == 3) {
                        intent.setClass(context, Work8Activity.class);
                    }
                } else if (Constant.PURCHASE.equals(type)) {//采购
                    holder.tv1.setText("采购类型：" + Utils.getObjStr(object, KeyConst.purchaseType));
                    holder.tv2.setText("申请事由：" + Utils.getObjStr(object, KeyConst.reason));
                    holder.tv3.setText("总价格：" + Utils.getObjStr(object, KeyConst.totalAmount) + "元");
                    if (meListPositon == 3) {
                        intent.setClass(context, Work13Activity.class);
                    }
                } else if (Constant.PAY.equals(type)) {//付款
                    holder.tv1.setText("付款金额：" + Utils.getObjStr(object, KeyConst.amount) + "元");
                    holder.tv2.setText("付款方式：" + Utils.getObjStr(object, KeyConst.payType));
                    holder.tv3.setVisibility(View.GONE);
                    intent.putExtra(KeyConst.type, 12);
                } else if (Constant.PETTY_CASH.equals(type)) {//备用金
                    holder.tv1.setText("申请金额：" + Utils.getObjStr(object, KeyConst.applyAmount) + "元");
                    holder.tv2.setText("使用日期：" + Utils.getObjStr(object, KeyConst.useDate));
                    holder.tv3.setText("归还日期：" + Utils.getObjStr(object, KeyConst.returnDate));
                    intent.putExtra(KeyConst.type, 11);
                } else if (Constant.WAGE_AUDIT.equals(type)) {//工资审核
                    holder.tv1.setText("合计工资：" + Utils.getObjStr(object, KeyConst.totalWage) + "元");
                    holder.tv2.setText("参与人数：" + Utils.getObjStr(object, KeyConst.personNum) + "人");
                    holder.tv3.setVisibility(View.GONE);
                    //工资审核
                    if (meListPositon == 3) {
                        intent.setClass(context, WageMonthAddActivity.class);
                    }
                } else if (type.contains(Constant.NOTICE)) {//公告
                    holder.tv1.setText("摘要：" + Utils.getObjStr(object, KeyConst.summary));
                    holder.tv2.setText("发布人：" + info.applicantName);
                    holder.tv3.setVisibility(View.GONE);

                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            intent.setClass(context, NoticeDetailUrlActivity.class);
                            intent.putExtra(KeyConst.id, info.id);
                            intent.putExtra(KeyConst.title, info.headline);
                            if (meListPositon == 0 && status == 2 && "2".equals(info.isAudit)) {
                                //我发起的   未审核的->撤销
                                intent.putExtra(KeyConst.agreeReject_recall, 2);
                            } else if (meListPositon == 3) {
                                intent.putExtra(KeyConst.agreeReject_recall, 3);//删除
                            }
                            context.startActivity(intent);
                        }
                    });
                }
            }

            holder.timeTv.setText(info.createTime);

        }
        return convertView;
    }

    private class ViewHolder {
        private TextView tv1, tv2, tv3, timeTv, titleTv, statusTv;
    }

}














