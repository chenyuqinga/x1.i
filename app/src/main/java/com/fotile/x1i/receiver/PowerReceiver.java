package com.fotile.x1i.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

import com.fotile.common.util.AppManagerUtil;
import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.activity.MainActivity;
import com.fotile.x1i.dailog.CommonDialog;
import com.fotile.x1i.dailog.DelayCloseDialog;
import com.fotile.x1i.listener.OnDialogListener;
import com.fotile.x1i.manager.DeviceReportManager;
import com.fotile.x1i.server.screensaver.ScreenSaverService;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.server.smokelink.SmokeStoveLinkAction;
import com.fotile.x1i.util.AppUtil;

import fotile.device.cookerprotocollib.helper.DeviceControl;

import static com.fotile.common.util.PreferenceUtil.IS_SCREEN_LOCK;
import static com.fotile.x1i.server.smokelink.SmokeStoveLinkAction.LINK_STATE_TIMER_DOWN;


/**
 * 文件名称：PowerReceiver
 * 创建时间：2019/7/4 13:59
 * 文件作者：yaohx
 * 功能描述：电源键上报receiver
 */
public class PowerReceiver extends BroadcastReceiver {

    CommonDialog commonDialog;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("com.android.action.POWERKEY_TOUCH".equals(action)) {
            if (Tool.fastclick()) {
                shortPress(context);
            }
        }
    }

    public void shortPress(Context context) {
        //暗屏中，点击电源键点亮屏幕
        if (ScreenSaverService.getCurrentState() == ScreenSaverService.SCREEN_DARK || AppUtil.isAwaitView()) {
            ScreenTool.getInstance().addResetData("电源键短按");
            return;
        }

        LogUtil.LOG_POWER("短按电源键", "处理短按逻辑");

        //如果爱奇艺打开
        if (DeviceReportManager.getInstance().is_video_show) {
            AppUtil.setVideoState(-1, context);

            AppUtil.closeAllFunction(context);
            ScreenTool.getInstance().addResstNowData();
            return;
        }
        //如果乐投打开
        if (DeviceReportManager.getInstance().is_lt_show) {
//            //关闭乐投即回到首页
//            Activity activity = AppManagerUtil.getInstance().currentActivity();
//            if (null != activity) {
//                Intent intent = new Intent(context, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                activity.startActivity(intent);
//            }
//
//            AppUtil.closeAllFunction(context);
//            ScreenTool.getInstance().addResstNowData();
            return;
        }

        //如果是在锁屏中
        boolean lock = (boolean) PreferenceUtil.getPreferenceValue(context, IS_SCREEN_LOCK, false);
        if (lock) {
            return;
        }
        //如果是在延时关机倒计时
        if (SmokeStoveLinkAction.getInstance().getLinkState() == LINK_STATE_TIMER_DOWN) {
            return;
        }

        //设备联动中
        if (SmokeStoveLinkAction.getInstance().isDeviceLinking()) {
            showFinishLinkDialog();
            return;
        }

        /*************************************处理业务逻辑****************************************/
        DeviceControl.getInstance().setBuzzer(DeviceControl.BUZZER_SHORT);

        //其他情况
        //点击物理电源键，延时关闭所有功能
        AppUtil.showDelayCloseDialog(context, DelayCloseDialog.DELAY_TYPE.DELAY_TYPE_POWER);
    }

    /**
     * 显示结束烹饪对话框
     */
    private void showFinishLinkDialog() {
        Activity activity = AppManagerUtil.getInstance().currentActivity();
        if (null != activity) {
            if (commonDialog == null) {
                 commonDialog = new CommonDialog(activity, CommonDialog.CommonTip.TWO_BTN);
                 LogUtil.LOGE("---LINK",hashCode());
            }
            commonDialog.setOnDialogListener(new OnDialogListener() {
                //取消
                @Override
                public void onLeftClick(Object... objects) {

                }

                //确认
                @Override
                public void onRightClick(Object... objects) {
                    //结束烟灶联动
                    SmokeStoveLinkAction.getInstance().closeSmokeLinkNow();
                }
            });
            commonDialog.setMessage("是否结束设备联动");

            if (!commonDialog.isShowing()) {
                commonDialog.show();
            }
        }

    }
}

