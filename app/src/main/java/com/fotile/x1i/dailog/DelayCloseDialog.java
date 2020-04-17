package com.fotile.x1i.dailog;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.Tool;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.base.BaseDialog;
import com.fotile.x1i.bean.event.FinishActivityMessage;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.server.smokelink.SmokeStoveLinkAction;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.widget.BottomView;
import com.fotile.x1i.widget.ProgressCircle;
import com.fotile.x1i.widget.TopBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

import static com.fotile.x1i.server.smokelink.SmokeStoveLinkAction.LINK_STATE_NORMAL;
import static com.fotile.x1i.server.smokelink.SmokeStoveLinkAction.LINK_STATE_TIMER_DOWN;


/**
 * 文件名称：DelayCloseDialog
 * 创建时间：2019/6/27 20:20
 * 文件作者：yaohx
 * 功能描述：延时关机dialog
 */
public class DelayCloseDialog extends BaseDialog implements View.OnClickListener {

    @BindView(R.id.progress_circle)
    ProgressCircle progress_circle;
    /**
     * 倒计时文本
     */
    @BindView(R.id.txt_tick)
    TextView txt_tick;

    /**
     * 立即关闭
     */
    @BindView(R.id.txt_close)
    TextView txt_close;
    /**
     * 退出
     */
    @BindView(R.id.txt_exit)
    TextView txt_exit;
    private DELAY_TYPE type;

    private static final int WHAT_DELAY_ENTER_SCREEN = 1001;

    CountDownTimer countDownTimer;

    public DelayCloseDialog(Context context, DELAY_TYPE type) {
        //因为延时关机dialog需要系统显示，所以需要applicationContext
        super(context.getApplicationContext(), R.style.FullScreenDialog);
        this.context = context;
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        txt_close.setOnClickListener(this);
        txt_exit.setOnClickListener(this);

        int delayTime = PreferenceUtil.getDelayTime(context) * 60 * 1000;
        progress_circle.setDuration2(0, delayTime, "", new ProgressCircle.OnFinishListener() {
            @Override
            public void onFinish() {
                handleClose();
            }
        });

        countDownTimer = new TimerCountDownTimer(delayTime, 1000);
        countDownTimer.start();
    }

    //延时关机时，灶具点火的监听
    @Subscribe
    public void onActivityFinish(FinishActivityMessage message) {
        if (message.to_class.getSimpleName().contains("DelayCloseDialog")) {
            dismiss();
        }
    }

    class TimerCountDownTimer extends CountDownTimer {

        public TimerCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //计算剩余分钟
            int min = (int) Tool.divide(millisUntilFinished, 60 * 1000);
            //计算剩余秒
            int second = (int) (millisUntilFinished / 1000 % 60);
            //回调到view的string
            String minStr = String.valueOf(min);
            //回调到view的string
            String secStr = second < 10 ? "0" + second : String.valueOf(second);

            String result = minStr + "分" + secStr + "秒";
            txt_tick.setText(result + "后关闭油烟机主功能");
            progress_circle.setCenterTxt(result);
        }

        @Override
        public void onFinish() {

        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_delay_close;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //立即关闭
            case R.id.txt_close:
                handleClose();
                break;
            //退出
            case R.id.txt_exit:
                break;
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //延时进入屏保
                case WHAT_DELAY_ENTER_SCREEN:
                    ScreenTool.getInstance().addResstNowData();
                    break;
            }
            return false;
        }
    });

    /**
     * 关闭所有功能并回到主页
     */
    private void handleClose() {
        //执行电源键延时关机
        if (type == DELAY_TYPE.DELAY_TYPE_POWER) {
            dismiss();
//            //定时结束，进入屏保
//            AppUtil.closeAllFunction(context);
            //修改为和岛式机逻辑一致（不关闭音乐播放）
            AppUtil.closeAllFunctionButMusic(context);
            handler.sendEmptyMessageDelayed(WHAT_DELAY_ENTER_SCREEN, 1000);
        }
        //执行dock栏关闭功能
        if (type == DELAY_TYPE.DELAY_TYPE_CLOSE) {
            SmokeStoveLinkAction.getInstance().closeSmokeLinkNow();
            dismiss();
        }
        //执行灶具关火
        if (type == DELAY_TYPE.DELAY_TYPE_STOVE) {
            SmokeStoveLinkAction.getInstance().closeSmokeLinkNow();
            dismiss();
        }
        //设置状态为正常,延时关机流程走完
        SmokeStoveLinkAction.getInstance().setLinkState(LINK_STATE_NORMAL);
    }

    /**
     * 延时关机dialog，由于涉及到爱奇艺，所以需要单独做处理
     */
    public void show() {
        //使用系统dialog显示延时关机，因为爱奇艺打开时可以使用延时关机
        showSystemDialog();
        //延时关机界面，停止屏保
        ScreenTool.getInstance().addPause();
        //弹出延时关机对话框，更新状态为倒计时中
        SmokeStoveLinkAction.getInstance().setLinkState(LINK_STATE_TIMER_DOWN);

        //系统dialog可以不隐藏底栏
//        BottomView.getInstance(context).hide();
//        TopBar.getInstance(context).hide();

        //告诉父容器有dialog显示
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            activity.dialogShowing = true;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 定义延时关机dialog的触发源
     */
    public enum DELAY_TYPE {
        DELAY_TYPE_POWER, //电源键延时关机
        DELAY_TYPE_CLOSE, //关闭功能延时关机
        DELAY_TYPE_STOVE; //灶具关火延时关机
    }
}
