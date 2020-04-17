package com.fotile.x1i.base;

import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.support.multidex.MultiDexApplication;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.manager.DeviceReportManager;
import com.fotile.x1i.server.InitServer;
import com.fotile.x1i.server.screensaver.ScreenSaverService;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.util.AppFrontBackHelper;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.util.Constant;
import com.fotile.x1i.widget.BottomView;
import com.fotile.x1i.widget.BottomViewArrowDown;
import com.fotile.x1i.widget.BottomViewArrowUp;
import com.fotile.x1i.widget.SpeechFloatingLayer;
import com.fotile.x1i.widget.TopBar;

/**
 * @author： yaohx
 * @data： 2019/4/15 16:21
 * @company： 杭州方太智能科技有限公司
 * @description： BaseApplication
 */
public class BaseApplication extends MultiDexApplication {

    /**
     * 常驻栏
     */
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    @Override
    public void onCreate() {
        super.onCreate();
        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("name");
        kl.disableKeyguard();
        String pidName = Tool.pidName(this);
        if (pidName.equals("com.fotile.x1i")) {
            InitServer initServer = new InitServer();
            initServer.init(this);
            addWindowManager();
            handLt();
        }
    }

    /**
     * 处理乐投进程切换前后台切换逻辑
     */
    private void handLt() {
        //乐投相关
        AppFrontBackHelper helper = new AppFrontBackHelper();
        helper.register(BaseApplication.this, new AppFrontBackHelper.OnAppStatusListener() {
            //app应用切换到前台
            @Override
            public void onFront() {
                LogUtil.LOGE("==================进程切换", "app切换到前台");
                //隐藏向上\向下箭头
                BottomViewArrowDown.getInstance(BaseApplication.this).hide();
                BottomViewArrowUp.getInstance(BaseApplication.this).hide();
                DeviceReportManager.getInstance().is_lt_show = false;
            }

            //app应用切到后台处理逻辑
            @Override
            public void onBack() {
                //乐投(爱奇艺)打开时，当前app会切换到后台，此处只处理乐投
                if (!DeviceReportManager.getInstance().is_video_show) {
                    //由于息屏导致的黑屏也会回调此方法
                    if (ScreenSaverService.getCurrentState() != ScreenSaverService.SCREEN_SLEEP) {
                        LogUtil.LOGE("==================进程切换", "app切换到后台，乐投打开");
                        //乐投打开时关闭音乐播放
                        AppUtil.stopPlayMusic(BaseApplication.this);

                        //显示底栏和向下箭头
                        BottomView.getInstance(BaseApplication.this).show();
                        BottomView.getInstance(BaseApplication.this).setBottomMainBg(R.mipmap.bg_bottom);
                        BottomViewArrowDown.getInstance(BaseApplication.this).show();
                        BottomViewArrowUp.getInstance(BaseApplication.this).hide();
                        //显示底栏返回键和home键（由于打开可能是在首页，首页没有显示返回按钮和home按钮）
                        BottomView.getInstance(BaseApplication.this).setHomeBtnShow(true);
                        BottomView.getInstance(BaseApplication.this).setBackBtnShow(true);
                        //暂停屏保
                        ScreenTool.getInstance().addPause();
                        DeviceReportManager.getInstance().is_lt_show = true;
                    }
                }
            }
        });

    }

    private void addWindowManager() {
        int screen_width = 1280;
        int screen_height = 800;

        int bottomHeight = (int) getResources().getDimension(R.dimen.bottom_height);
        int stateHeight = (int) getResources().getDimension(R.dimen.state_bar_height);
        int arrowHeight = (int) getResources().getDimension(R.dimen.arrow_height);
        int arrowWidth = (int) getResources().getDimension(R.dimen.arrow_width);

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        // 获取LayoutParams对象
        layoutParams = new WindowManager.LayoutParams();
        //设置window type
        layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        layoutParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams
                .FLAG_ALT_FOCUSABLE_IM;
        //调整悬浮窗显示的停靠位置为左侧置顶
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;

        int margin_all = (int) getResources().getDimension(R.dimen.screen_margin);
        //底栏
        layoutParams.x = margin_all;
        layoutParams.y = screen_height - bottomHeight - margin_all;
        layoutParams.width = screen_width - margin_all * 2;
        layoutParams.height = bottomHeight;
        BottomView bottomView = BottomView.getInstance(this);
        if (null != bottomView.getParent()) {
            windowManager.removeViewImmediate(bottomView);
        }
        windowManager.addView(bottomView, layoutParams);
        //顶栏
        layoutParams.x = margin_all;
        layoutParams.y = margin_all;
        layoutParams.width = screen_width - margin_all * 2;
        layoutParams.height = stateHeight;
        TopBar topBar = TopBar.getInstance(this);
        if (null != topBar.getParent()) {
            windowManager.removeViewImmediate(topBar);
        }
        windowManager.addView(topBar, layoutParams);
        //语音浮层
        setVoiceFloatingLayer();
        //底栏向上箭头
        layoutParams.x = (screen_width-margin_all*2)/2-arrowWidth/2;
        layoutParams.y = screen_height - arrowHeight - margin_all;
        layoutParams.width = arrowWidth;
        layoutParams.height = arrowHeight;
        BottomViewArrowUp arrowUp = BottomViewArrowUp.getInstance(this);
        if (null != arrowUp.getParent()) {
            windowManager.removeViewImmediate(arrowUp);
        }
        windowManager.addView(arrowUp, layoutParams);
        BottomViewArrowUp.getInstance(this).hide();
        //底栏向下箭头
        layoutParams.x = (screen_width-margin_all*2)/2-arrowWidth/2;
        layoutParams.y = screen_height - bottomHeight - arrowHeight - margin_all;
        layoutParams.width = arrowWidth;
        layoutParams.height = arrowHeight;
        BottomViewArrowDown arrowDown = BottomViewArrowDown.getInstance(this);
        if (null != arrowDown.getParent()) {
            windowManager.removeViewImmediate(arrowDown);
        }
        windowManager.addView(arrowDown, layoutParams);
        BottomViewArrowDown.getInstance(this).hide();

    }

    private void setVoiceFloatingLayer() {
        //语音浮层
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width = Constant.SCREEN_WIDTH;
        layoutParams.height = Constant.SCREEN_HEIGHT;
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;     // 系统提示类型,重要
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点
        if (null != SpeechFloatingLayer.get(this).getParent()) {
            windowManager.removeViewImmediate(SpeechFloatingLayer.get(this));
        }
        SpeechFloatingLayer.get(this).setVisibility(View.GONE);
        windowManager.addView(SpeechFloatingLayer.get(this), layoutParams);
//        SpeechFloatingLayer.get(this).dismiss(false);
    }
}
