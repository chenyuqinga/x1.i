package com.fotile.x1i.dailog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.quicktool.QuickToolActivity;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.base.BaseDialog;
import com.fotile.x1i.bean.event.FinishActivityMessage;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.server.smokelink.SmokeStoveLinkAction;
import com.fotile.x1i.widget.BottomView;
import com.fotile.x1i.widget.TopBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

import static com.fotile.x1i.server.smokelink.SmokeStoveLinkAction.LINK_STATE_TIMER_DOWN;

/**
 * Created by chenyqi on 2019/8/2.
 */

public class LockScreenDialog extends BaseDialog {
    @BindView(R.id.lock_screen_layout)
    LinearLayout layoutLock;
    @BindView(R.id.img_lock_screen)
    ImageView imgLock;
    /**
     * 锁屏状态监听
     */
    private QuickToolActivity.LockScreenListener listener;

    /**
     * 初始值imageview左侧距离
     */
    private int imgLeft;
    /**
     * 初始值imageview右侧距离
     */
    private int imgRight;

    /**
     * 锁频时长
     */
    final int time_delay = 1 * 60 * 60 * 1000;

    private final int WHAT_DELAY_REMOVE = 1001;

    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录手指按下时View上的横坐标的值
     */
    private float xInView;

    public LockScreenDialog(Context context) {
        super(context, R.style.FullScreenDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onDialogDismiss(FinishActivityMessage message) {
        if(message.to_class.getSimpleName().contains("LockScreenDialog")){
            dismiss();
        }
    }

    private void initView() {
        imgLock.setBackgroundResource(R.drawable.bg_lock_screen);
        AnimationDrawable anim = (AnimationDrawable) imgLock.getBackground();
        anim.start();
        imgLeft = 400;
        imgRight = 800;
        //        handler.sendEmptyMessageDelayed(WHAT_DELAY_REMOVE, time_delay);
    }

    //    Handler handler = new Handler(new Handler.Callback() {
    //        @Override
    //        public boolean handleMessage(Message msg) {
    //            if (msg.what == WHAT_DELAY_REMOVE) {
    //                if (null != listener) {
    //                    try {
    //                         dismiss();
    //                        if (listener != null) {
    //                            listener.onLockScreenChanged(false);
    //                        }
    //                        PreferenceUtil.setPreferenceValue(context, PreferenceUtil.IS_SCREEN_LOCK,
    //                                false);
    //                        Log.e("lock", "set lock screen false");
    //                    } catch (Exception e) {
    //                        e.printStackTrace();
    //                    }
    //                }
    //            }
    //            return false;
    //        }
    //    });
    public void show() {
        //使用系统dialog显示延时关机，因为爱奇艺打开时可以使用延时关机
        showSystemDialog();
        //延时关机界面，停止屏保
        ScreenTool.getInstance().addKeepDark();

        BottomView.getInstance(context).hide();
        TopBar.getInstance(context).hide();

        //告诉父容器有dialog显示
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            activity.dialogShowing = true;
        }
        PreferenceUtil.setPreferenceValue(context, PreferenceUtil.IS_SCREEN_LOCK, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xInView = event.getX();
                LogUtil.LOGE("LOCK-xInView", xInView);
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                LogUtil.LOGE("LOCK-xInScreen", xInScreen);
                //手指移动的时候更新imageView的位置
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                // 如果手指滑动大于100，解锁
                if ((xInScreen - xInView) > 200) {
                    LogUtil.LOGE("LOCK-xInScreen", xInView - xInScreen);
                    dismiss();
                    if (listener != null) {
                        listener.onLockScreenChanged(false);
                    }
                    //                    //移除handler
                    //                    handler.removeMessages(WHAT_DELAY_REMOVE);
                } else {
                    //layout(0, getTop(), getRight(), getBottom());
                    imgLock.layout(imgLeft, imgLock.getTop(), imgRight, imgLock.getBottom());
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        EventBus.getDefault().unregister(this);
        PreferenceUtil.setPreferenceValue(context, PreferenceUtil.IS_SCREEN_LOCK, false);
    }

    private void updateViewPosition() {
        if (xInScreen - xInView > 0 && xInScreen - xInView < 200) {
            imgLock.layout((int) (xInScreen - xInView) + imgLeft, imgLock.getTop(), (int) (xInScreen - xInView) + imgRight, imgLock.getBottom());
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_lock_screen;
    }

}
