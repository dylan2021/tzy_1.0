package com.android.tongzhiyuan.core.utils;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.tongzhiyuan.act_other.BaseFgActivity;
import com.android.tongzhiyuan.R;
import com.android.tongzhiyuan.util.DialogUtils;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具类
 * Gool
 */
public class TextUtil {

    public static boolean setInputOneDot;

    /**
     * @param str 被判的字符串
     * @return 如果任何一个字符串为null, 则返回true
     */
    public static boolean isAnyEmpty(String... str) {

        for (String s : str) {
            if (s == null || s.length() <= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串是否为空
     *
     * @return 如果为空则返回 true
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.trim().length() <= 0) {
            return true;
        }
        return false;
    }


    /**
     * 是否是合法字符串
     *
     * @param str 被校验的字符串
     * @param reg 正则表达式
     * @return
     */
    public static boolean isLegal(String str, String reg) {

        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);

        return matcher.matches();
    }

    /**
     * 检测是否是合法的手机号
     *
     * @param phone
     * @return
     */
    public static boolean isMobile(String phone) {

        return isLegal(phone, "^1[3|4|5|7|8]\\d{9}$");
    }

    //把String转化为double
    public static double convertToDouble(String number, double defaultValue) {
        if (TextUtils.isEmpty(number)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(number);
        } catch (Exception e) {
            return defaultValue;
        }

    }

    /**
     * 格式化下载数值
     *
     * @param count 数值
     * @return 格式化后的字符串
     */
    public static String formatCount(long count) {

        String countStr;

        if (count > 1000) {
            countStr = Math.round(count / 1000) + "千";
        } else if (count > 10000) {
            countStr = Math.round(count / 10000) + "万";
        } else if (count > 100000) {
            countStr = Math.round(count / 100000) + "十万";
        } else if (count > 1000000) {
            countStr = Math.round(count / 1000000) + "百万";
        } else {
            countStr = count + "";
        }
        return countStr;
    }

    /**
     * 描述：是否是邮箱.
     *
     * @param str 指定的字符串
     * @return 是否是邮箱:是为true，否则false
     */
    public static Boolean isEmail(String str) {
        Boolean isEmail = false;
        String expr = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        if (str.matches(expr)) {
            isEmail = true;
        }
        return isEmail;
    }

    public static String getTxtString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "gbk");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String subTimeYmd(String time) {
        if (time == null || time.length() < 10) {
            return "";
        } else {
            return time.substring(0, 10);
        }

    }

    public static String subTimeYmdHm(String time) {
        if (time == null || time.length() < 16) {
            return "";
        } else {
            return time.substring(0, 16);
        }

    }

    public static String subTimeMDHm(String time) {
        if (time == null || time.length() < 16) {
            return "";
        } else {
            return time.substring(5, 16);
        }

    }

    /**
     * 某时间距离现在
     *
     * @return
     */
    public static int differentDaysByMillisecond(Date date2) {
        Date date = new Date();
        int days = (int) ((date2.getTime() - date.getTime()) / (1000 * 3600 * 24));
        return days + 1;
    }

    /**
     * 两个时间的间隔
     *
     * @return
     */
    public static int differentDaysByMillisecond2(Date endDate, Date startDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        int days = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 3600 * 24));
        return days;
    }

    public static String differentDayOfTime(Date endDate, Date startDate) {
        if (startDate == null || endDate == null) {
            return "";
        }

        BigDecimal b1 = new BigDecimal(Double.toString(endDate.getTime() - startDate.getTime()));
        BigDecimal b2 = new BigDecimal(Double.toString(1000 * 3600 * 24));
        String dayStr = b1.divide(b2, 1, BigDecimal.ROUND_HALF_UP).doubleValue() + "";

        return dayStr.replace(".0", "");
    }

    /**
     * 半角转全角
     *
     * @param input String.
     * @return 全角字符串.
     */
    public static String toAllSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    public static String remove_N(String str) {
        return str == null ? "" : str;
    }

    public static String remove_0(String str) {
        if (str != null) {
            return str.replace(".00", "").replace(".0", "");
        }
        return "";
    }

    /**
     * 身份证号校验
     */
    public static boolean isIdCardNum(String idCard) {
        String reg = "^\\d{15}$|^\\d{17}[0-9Xx]$";
        if (!idCard.matches(reg)) {
            return false;
        }
        return true;
    }

    public static Integer getAgeFromIDCard(String idCardNo) {

        int length = idCardNo.length();

        String dates = "";

        if (length > 9) {
            dates = idCardNo.substring(6, 10);

            SimpleDateFormat df = new SimpleDateFormat("yyyy");

            String year = df.format(new Date());

            int u = Integer.parseInt(year) - Integer.parseInt(dates);

            return u > 150 ? 0 : u < 0 ? 0 : u;

        } else {
            return 0;
        }

    }

    public static String parseArrayToString(CharSequence[] arr) {
        if (arr == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int offset = arr.length - 1;
        for (int i = 0; i < offset; i++) {
            sb.append(arr[i]).append(", ");
        }
        sb.append(arr[offset]);

        return sb.toString();
    }

    public static String parseArrayToString(String[] arr) {
        if (arr == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int offset = arr.length - 1;
        for (int i = 0; i < offset; i++) {
            sb.append(arr[i]).append(", ");
        }
        sb.append(arr[offset]).append("]");

        return sb.toString();
    }

    public static void initEmptyTv(BaseFgActivity context, TextView emptyTv) {
        emptyTv.setText(!NetUtil.isNetworkConnected(context) ? context.getString(R.string.no_network) : "");
        emptyTv.setVisibility(!NetUtil.isNetworkConnected(context) ? View.VISIBLE : View.GONE);
        Drawable noNetWork = context.getResources().getDrawable(!NetUtil.isNetworkConnected(context) ?
                R.drawable.ic_bg_no_network : R.drawable.ic_bg_no_data);
        emptyTv.setCompoundDrawablesWithIntrinsicBounds(null, noNetWork, null, null);
    }

    public static String getLast2(String text) {
        if (isEmpty(text)) {
            return "";
        }
        int length = text.length();
        return length > 2 ? text.substring(length - 2) : text;
    }

    //设置EditText禁止输入
    public static void setEtNoFocusable(EditText hourTv) {
        hourTv.setEnabled(false);
        hourTv.setFocusable(false);
        hourTv.setKeyListener(null);
    }

    public static int getArrIndex(String[] arr, String value) {
        if (arr == null) {
            return 0;
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(value)) {
                return i;
            }
        }
        return 0;
    }

    //接口返回的内容
    public static String getErrorMsg(VolleyError error) {
        if (null != error && error.networkResponse != null && error.networkResponse.data != null) {
            byte[] htmlBodyBytes = error.networkResponse.data;
            return new String(htmlBodyBytes);
        } else {
            return null;
        }

    }

    public static boolean setInputOneDot(EditText et, CharSequence s) {
        //------------只能输入1位小数--------------
        String numStr = s.toString();
        if (numStr.contains(".")) {
            if (s.length() - 1 - numStr.indexOf(".") > 1) {
                s = numStr.subSequence(0, numStr.indexOf(".") + 2);
                et.setText(s);
                et.setSelection(s.length());
            }
        }
        if (numStr.trim().substring(0).equals(".")) {
            s = "0" + s;
            et.setText(s);
            et.setSelection(2);
        }
        if (numStr.startsWith("0") && numStr.trim().length() > 1) {
            if (!numStr.substring(1, 2).equals(".")) {
                et.setText(s.subSequence(0, 1));
                et.setSelection(1);
                return true;
            }
        }
        //--------------只能输入1位小数---------------------
        return false;
    }
}
