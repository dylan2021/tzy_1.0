package com.android.tongzhiyuan.act_1;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.tongzhiyuan.R;
import com.android.tongzhiyuan.bean.WageDailyInfo;
import com.android.tongzhiyuan.core.utils.Constant;
import com.android.tongzhiyuan.core.utils.KeyConst;

import java.util.ArrayList;
import java.util.List;

/**
 * Gool Lee
 */

public class WageDailyListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<WageDailyInfo> progressList = new ArrayList<>();
    private WageDailyListActivity context;
    private int type;

    public WageDailyListAdapter(WageDailyListActivity context, int type) {
        this.context = context;
        this.type = type;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return progressList == null ? 0 : progressList.size();
    }

    @Override
    public Object getItem(int i) {
        return progressList == null ? null : progressList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final WageDailyInfo itemInfo = progressList.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new WageDailyListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.item_daily_wage, viewGroup, false);
            holder.nameTv = convertView.findViewById(R.id.name_tv);
            holder.timeTv = convertView.findViewById(R.id.dialy_wage_item_time_tv);

            holder.totalWageTv = convertView.findViewById(R.id.total_wage_tv);
            holder.everyHoursWageTv = convertView.findViewById(R.id.every_hours_wage_tv);
            holder.everyNumberWageTv = convertView.findViewById(R.id.every_number_wage_tv);
            holder.moneyDeductTv = convertView.findViewById(R.id.status_tv);

            holder.employeeNumberTv = convertView.findViewById(R.id.employee_number_tv);
            holder.totalSumHoursTv = convertView.findViewById(R.id.total_hours_tv);
            holder.totalSumNumberTv = convertView.findViewById(R.id.total_sum_number_tv);
            convertView.setTag(holder);
        } else {
            holder = (WageDailyListAdapter.ViewHolder) convertView.getTag();
        }
        if (null != itemInfo) {
            int pieceWageId = itemInfo.getPieceWageId();
            int deptId = itemInfo.getDeptId();
            String workDate = itemInfo.getWorkDate();

            String url_str = "workDate=" + workDate +
                    "&pieceWageId=" + pieceWageId + "&deptId=" + deptId;
            if (type == -1) {
                url_str = "workDate=" + workDate +
                        "&pieceMonthCategoryId=" + Constant.xiaoliaobao_proj_id + "&deptId=" + deptId;
            }
            final String url_param = url_str;
            String s = itemInfo.getPieceMonthCategoryName() + "(" + workDate + ")";
            holder.nameTv.setText(type > 0 ? itemInfo.getProjectName() : s);
            holder.employeeNumberTv.setText("参与人数：" + itemInfo.getPeopleNum() + "人");
            if (type > 0) {
                holder.timeTv.setText(workDate);
                holder.totalWageTv.setText("合计工资：" + itemInfo.getTotalWage() + "元");

                holder.everyHoursWageTv.setText("计时工资：" + itemInfo.getTotalHourlyWage() + "元");

                holder.everyNumberWageTv.setText("计件工资：" + itemInfo.getTotalPieceWage() + "元");
                holder.moneyDeductTv.setText("扣款：" + itemInfo.getTotalDeduction() + "元");

                holder.totalSumHoursTv.setText("总计时：" + itemInfo.getTotalHourNum() + "小时");
                holder.totalSumNumberTv.setText("总件数：" + itemInfo.getTotalPieceNum() + itemInfo.getUnit());
            } else {
                holder.timeTv.setPadding(0, 3, 0, 0);
                holder.everyHoursWageTv.setText("件数：" + itemInfo.getTotalPieceMonthNum() + itemInfo.getUnit());
                holder.totalWageTv.setVisibility(View.GONE);
                holder.everyNumberWageTv.setVisibility(View.GONE);
                holder.moneyDeductTv.setVisibility(View.GONE);
                holder.totalSumHoursTv.setVisibility(View.GONE);
                holder.totalSumNumberTv.setVisibility(View.GONE);

            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WageEmplyeeListActivity.class);
                    intent.putExtra(KeyConst.id, url_param);
                    intent.putExtra(KeyConst.type, type);
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }

    public void setData(List<WageDailyInfo> processorList) {
        progressList = processorList;
        notifyDataSetChanged();
    }


    public class ViewHolder {
        private TextView nameTv, timeTv, totalSumHoursTv,
                totalSumNumberTv, employeeNumberTv,
                totalWageTv, moneyDeductTv, everyHoursWageTv, everyNumberWageTv;

    }

}
