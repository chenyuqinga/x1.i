package com.fotile.x1i.activity.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fotile.common.util.Tool;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.setting.dialog.DateSetDialog;
import com.fotile.x1i.activity.setting.dialog.TimeSetDialog;
import com.fotile.x1i.base.BaseActivity;

import java.util.Calendar;

import butterknife.BindView;

/**
 * @author： yaohx
 * @data： 2019/4/23 13:22
 * @company： 杭州方太智能科技有限公司
 * @description： 时间设置界面
 * <p>
 * Copyright (c) 2018, FOTILE GROUP.
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * - Neither the name of the FOTILE GROUP nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL FOTILE GROUP BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class TimeSettingActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.layout_net_time)
    View layoutNetTime;
    @BindView(R.id.layout_local_time)
    View layoutLocalTime;


    /**
     * 网络时间
     */
    @BindView(R.id.txt_net_time)
    TextView txtNetTime;
    @BindView(R.id.txt_am_pm)
    TextView txtAmpm;

    @BindView(R.id.relate_set_date)
    RelativeLayout relateSetDate;
    @BindView(R.id.relate_set_time)
    RelativeLayout relateSetTime;

    /**
     * 无网设置日期
     */
    @BindView(R.id.txt_date)
    TextView txtLocalDate;
    /**
     * 无网设置时间
     */
    @BindView(R.id.txt_time)
    TextView txtLocalTime;

    /**
     * 更新时间msg
     */
    private static final int MSG_UPDATE_NET = 1;
    private static final int MSG_UPDATE_LOCAL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        registerWifiReceiver();
    }

    private void initView() {
        relateSetDate.setOnClickListener(this);
        relateSetTime.setOnClickListener(this);
    }

    /**
     * 初始化有网络的情况下时间
     */
    private void setTime(boolean net) {
        if (net) {
            long time = System.currentTimeMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            int am_pm = calendar.get(Calendar.AM_PM);
            txtAmpm.setText((am_pm == 0) ? "AM" : "PM");

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            String timeNet = (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);

            txtNetTime.setText(timeNet);
        }
    }

    /**
     * 注册wifi连接上广播监听
     */
    private void registerWifiReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        //设置接收广播的类型
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        //调用Context的registerReceiver（）方法进行动态注册
        context.registerReceiver(wifiReceiver, intentFilter);
    }

    //注册网络监听
    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {//wifi连接上与否
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                //有网络
                if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    handler.removeMessages(MSG_UPDATE_NET);
                    handler.removeMessages(MSG_UPDATE_LOCAL);

                    handler.sendEmptyMessage(MSG_UPDATE_NET);
                }
                //没有网络
                else {
                    handler.removeMessages(MSG_UPDATE_NET);
                    handler.removeMessages(MSG_UPDATE_LOCAL);

                    handler.sendEmptyMessage(MSG_UPDATE_LOCAL);
                }
            }
        }
    };

    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //有网
                case MSG_UPDATE_NET:
                    setTime(true);
                    //每隔1秒更新一次
                    handler.sendEmptyMessageDelayed(MSG_UPDATE_NET, 1000);

                    layoutLocalTime.setVisibility(View.GONE);
                    layoutNetTime.setVisibility(View.VISIBLE);
                    break;
                //无网络
                case MSG_UPDATE_LOCAL:

                    layoutLocalTime.setVisibility(View.VISIBLE);
                    layoutNetTime.setVisibility(View.GONE);
                    setLocalDateTime();
                    break;
            }
            return false;
        }
    });

    /**
     * 设置无网络显示日期和时间
     */
    private void setLocalDateTime() {
        Time time = new Time();
        time.setToNow();
        //年
        int year = time.year;
        //月
        int month = time.month;
        //日
        int day = time.monthDay;
        //时
        int hour = time.hour;
        //分
        int minute = time.minute;

        String dateStr = year + "年" + (month + 1) + "月" + day + "日";
        String timeStr = hour + "时" + minute + "分";
        txtLocalDate.setText(dateStr);
        txtLocalTime.setText(timeStr);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting_time;
    }

    @Override
    public boolean showBottom() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //设置日期
            case R.id.relate_set_date:
                if (Tool.fastclick()) {
                    DateSetDialog dialog = new DateSetDialog(this);
                    dialog.setOnDateTimeSetListener(listener);
                    dialog.show();
                }
                break;
            //设置时间
            case R.id.relate_set_time:
                if (Tool.fastclick()) {
                    TimeSetDialog dialog = new TimeSetDialog(this);
                    dialog.setOnDateTimeSetListener(listener);
                    dialog.show();
                }
                break;
        }
    }

    OnDateTimeSetListener listener = new OnDateTimeSetListener() {
        @Override
        public void onDateSet(String date) {
            txtLocalDate.setText(date);
        }

        @Override
        public void onTimeSet(String time) {
            txtLocalTime.setText(time);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(MSG_UPDATE_NET);
        handler.removeMessages(MSG_UPDATE_LOCAL);
        context.unregisterReceiver(wifiReceiver);
    }


    public interface OnDateTimeSetListener {
        void onDateSet(String date);

        void onTimeSet(String time);
    }

}
