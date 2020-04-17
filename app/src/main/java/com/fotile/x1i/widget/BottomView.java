package com.fotile.x1i.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fotile.common.util.AppManagerUtil;
import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.Tool;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.MainActivity;
import com.fotile.x1i.activity.control.CruiseActivity;
import com.fotile.x1i.activity.control.LampActivity;
import com.fotile.x1i.activity.control.WindControlActivity;
import com.fotile.x1i.activity.ota.OtaMainActivity;
import com.fotile.x1i.activity.setting.DeviceLinkageActivity;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.dailog.DelayCloseDialog;
import com.fotile.x1i.dailog.FullScreenDialog;
import com.fotile.x1i.listener.OnDialogListener;
import com.fotile.x1i.manager.DeviceReportManager;
import com.fotile.x1i.manager.SpeechManager;
import com.fotile.x1i.server.screensaver.ScreenSaverService;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.util.AppUtil;

import butterknife.ButterKnife;
import fotile.device.cookerprotocollib.helper.DeviceControl;
import fotile.device.cookerprotocollib.helper.bean.WorkBean;
import rx.functions.Action1;

import static com.fotile.x1i.activity.control.WindControlActivity.DEFAULT_WIND_VALUE;
import static com.fotile.x1i.manager.DeviceReportManager.WORK_AUTO;
import static com.fotile.x1i.manager.DeviceReportManager.WORK_FORCEWORK;
import static com.fotile.x1i.manager.DeviceReportManager.WORK_WIND_CONTROL;


/**
 * 文件名称：BottomView
 * 创建时间：2019/5/10 17:41
 * 文件作者：yaohx
 * 功能描述：BottomView
 */
public class BottomView extends LinearLayout implements View.OnClickListener {
    private static final String TAG = BottomView.class.getSimpleName();

    private BaseActivity context;

    private static BottomView instance;

    private int color_normal;
    private int color_pressed;

    /**
     * 语音
     */
    private ImageView imgVoice;
    private TextView txtVoice;
    /**
     * 照明
     */
    private ImageView imgLamp;
    private TextView txtLamp;
    /**
     * 自动挡
     */
    private ImageView imgAuto;
    private TextView txtAuto;
    /**
     * 风量
     */
    private ImageView imgWind;
    private TextView txtWind;
    /**
     * 烟机关闭
     */
    private ImageView imgClose;
    private TextView txtClose;

    private View main;

    private ImageView imgBack;
    private ImageView imgHome;

    /**
     * 增大触控区域
     */
    private View layout_bottom_voice;
    private View layout_bottom_lamp;
    private View layout_bottom_auto;
    private View layout_bottom_wind;
    private View layout_bottom_close;

    private final int STATE_NORMAL = 1001;
    private final int STATE_PRESSED = 1002;
    private final int BTN_LAMP = 1003;
    private final int BTN_AUTO = 1004;
    private final int BTN_WIND = 1005;
    private boolean mIsSpeechEnabel;

    private BottomView(Context context) {
        super(context);
    }

    public synchronized static BottomView getInstance(Context context) {
        if (null == instance) {
            instance = new BottomView(context);
        }
        return instance;
    }

    public void initContext(MainActivity context) {
        if (context instanceof MainActivity) {
            this.context = context;
            init();
        } else {
            throw new IllegalArgumentException("context must be MainActivity");
        }
    }

    private void init() {
        ButterKnife.bind(this);
        color_normal = Color.parseColor("#FFFFFF");
        color_pressed = Color.parseColor("#C8AF70");

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.item_bottom, this, true);

        imgVoice = (ImageView) findViewById(R.id.img_voice);
        txtVoice = (TextView) findViewById(R.id.txt_voice);
        imgLamp = (ImageView) findViewById(R.id.img_lamp);
        txtLamp = (TextView) findViewById(R.id.txt_lamp);
        imgAuto = (ImageView) findViewById(R.id.img_auto);
        txtAuto = (TextView) findViewById(R.id.txt_auto);
        imgWind = (ImageView) findViewById(R.id.img_wind);
        txtWind = (TextView) findViewById(R.id.txt_wind);
        imgClose = (ImageView) findViewById(R.id.img_close);
        txtClose = (TextView) findViewById(R.id.txt_close);
        imgBack = (ImageView) findViewById(R.id.img_back);
        imgHome = (ImageView) findViewById(R.id.img_home);
        main = findViewById(R.id.main);
        layout_bottom_voice = findViewById(R.id.layout_bottom_voice);
        layout_bottom_lamp = findViewById(R.id.layout_bottom_lamp);
        layout_bottom_auto = findViewById(R.id.layout_bottom_auto);
        layout_bottom_wind = findViewById(R.id.layout_bottom_wind);
        layout_bottom_close = findViewById(R.id.layout_bottom_close);

        imgVoice.setOnClickListener(this);
        imgLamp.setOnClickListener(this);
        imgAuto.setOnClickListener(this);
        imgWind.setOnClickListener(this);
        imgClose.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgHome.setOnClickListener(this);
        layout_bottom_voice.setOnClickListener(this);
        layout_bottom_lamp.setOnClickListener(this);
        layout_bottom_auto.setOnClickListener(this);
        layout_bottom_wind.setOnClickListener(this);
        layout_bottom_close.setOnClickListener(this);

        //强制跑模式
        imgAuto.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SnakeBar.makeMsgSnake(context, "强制跑模式").show();
                DeviceControl.getInstance().startForcework();
                return true;
            }
        });

        createAction();
    }

    public void show() {
        setVisibility(View.VISIBLE);
        if (null != main) {
            main.setVisibility(View.VISIBLE);
        }
    }

    public void hide() {
        setVisibility(View.GONE);
        if (null != main) {
            main.setVisibility(View.GONE);
        }
    }

    public void setBottomMainBg(int res) {
        if (null != main) {
            main.setBackgroundResource(res);
        }
    }

    public void setBackBtnShow(boolean show) {
        if (null != imgBack) {
            imgBack.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void setHomeBtnShow(boolean show) {
        if (null != imgHome) {
            imgHome.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void setChecked(int btn, int state) {
        //灯
        if (btn == BTN_LAMP) {
            if (state == STATE_NORMAL) {
                imgLamp.setSelected(false);
                txtLamp.setTextColor(color_normal);
            }
            if (state == STATE_PRESSED) {
                imgLamp.setSelected(true);
                txtLamp.setTextColor(color_pressed);
            }
        }
        //自动挡
        if (btn == BTN_AUTO) {
            if (state == STATE_NORMAL) {
                imgAuto.setSelected(false);
                txtAuto.setTextColor(color_normal);
            }
            if (state == STATE_PRESSED) {
                imgAuto.setSelected(true);
                txtAuto.setTextColor(color_pressed);
            }
        }
        //风量
        if (btn == BTN_WIND) {
            if (state == STATE_NORMAL) {
                imgWind.setSelected(false);
                txtWind.setTextColor(color_normal);
            }
            if (state == STATE_PRESSED) {
                imgWind.setSelected(true);
                txtWind.setTextColor(color_pressed);
            }
        }
    }

    private void createAction() {
        Action1<WorkBean> action = new Action1<WorkBean>() {
            @Override
            public void call(WorkBean workBean) {
                int work_state = DeviceReportManager.work_state;
                //灯
                setLampState(workBean);
                //自动档
                if (work_state == WORK_AUTO || work_state == WORK_FORCEWORK) {
                    setChecked(BTN_AUTO, STATE_PRESSED);
                    setChecked(BTN_WIND, STATE_NORMAL);
                }
                //风量控制
                else if (work_state == WORK_WIND_CONTROL) {
                    setChecked(BTN_AUTO, STATE_NORMAL);
                    setChecked(BTN_WIND, STATE_PRESSED);
                } else {
                    setChecked(BTN_AUTO, STATE_NORMAL);
                    setChecked(BTN_WIND, STATE_NORMAL);
                }
            }
        };
        DeviceReportManager.getInstance().addWorkBeanAction(action);
    }

    /**
     * 设置灯按钮的状态
     *
     * @param
     */
    private void setLampState(WorkBean workBean) {
        if (null == workBean) {
            return;
        }
        int lamp = workBean.lamp;
        if (lamp > 0) {
            imgLamp.setSelected(true);
            txtLamp.setTextColor(color_pressed);
        } else {
            imgLamp.setSelected(false);
            txtLamp.setTextColor(color_normal);
        }
    }

    private void launchActivity(Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        context.startActivity(intent);
    }

    /**
     * 关闭视频
     *
     * @return
     */
    private boolean closeQy() {
        if (DeviceReportManager.getInstance().is_video_show) {
            //TYPE_SYSTEM_ALERT模式下，需要使用全局的 context
            FullScreenDialog dialog = new FullScreenDialog(context.getApplicationContext(), FullScreenDialog
                    .FullScreenTip.TWO_BTN);
            dialog.showSystemDialog();
            dialog.setMessage("是否确定关闭当前播放");
            dialog.setOnDialogListener(new OnDialogListener() {
                @Override
                public void onLeftClick(Object... objects) {

                }

                //点击确认关闭爱奇艺视频
                @Override
                public void onRightClick(Object... objects) {
                    AppUtil.setVideoState(-1, context);
                }
            });
            return true;
        }
        return false;
    }

    /**
     * 关闭乐投
     */
    private boolean closeLt(final Activity activity) {
        if (DeviceReportManager.getInstance().is_lt_show && null != activity) {
            //TYPE_SYSTEM_ALERT模式下，需要使用全局的 context
            FullScreenDialog dialog = new FullScreenDialog(context.getApplicationContext(), FullScreenDialog
                    .FullScreenTip.TWO_BTN);
            dialog.showSystemDialog();
            dialog.setMessage("是否确定关闭当前播放");
            dialog.setOnDialogListener(new OnDialogListener() {
                @Override
                public void onLeftClick(Object... objects) {

                }

                //点击确认
                @Override
                public void onRightClick(Object... objects) {
                    //这里直接杀死了乐投进程，不能再次开启
                    //AppUtil.killProjectionProcess(context, Constant.PACKAGE_NAME_LT);
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                }
            });
            return true;
        }
        return false;
    }

    public void back(boolean from_voice) {
        Activity activity = AppManagerUtil.getInstance().currentActivity();
        //关闭爱奇艺
        if (closeQy()) {
            return;
        }
        //关闭乐投
        if (closeLt(activity)) {
            return;
        }

        if (from_voice) {
            if ((activity instanceof MainActivity)) {
                return;
            }
        }
        //关闭上一级页面
        if (null != activity) {
            //ota
            if (activity instanceof OtaMainActivity) {
                ((OtaMainActivity) activity).onBackClick();
                activity.finish();
            } else {
                activity.finish();
            }
        }
    }

    private void home() {
        Activity activity = AppManagerUtil.getInstance().currentActivity();
        //关闭爱奇艺
        if (closeQy()) {
            return;
        }
        //关闭乐投
        if (closeLt(activity)) {
            return;
        }

        //回到主页面
        launch2Home();
    }

    @Override
    public void onClick(View v) {
        if (ScreenSaverService.getCurrentState() == ScreenSaverService.SCREEN_DARK) {
            ScreenTool.getInstance().addResetData("BottomView");
            return;
        }
        Activity activity = AppManagerUtil.getInstance().currentActivity();
        switch (v.getId()) {
            //返回键
            case R.id.img_back:
                back(false);
                break;
            //home
            case R.id.img_home:
                home();
                break;
            //语音
            case R.id.layout_bottom_voice:
            case R.id.img_voice:
//                if (mIsSpeechEnabel) {
                if (SpeechManager.getInstance().isConnected()) {
                    SpeechManager.getInstance().startSpeech();
                } else {
                    if (context != null) {
                        context.launchActivity(DeviceLinkageActivity.class);
                    }
                }
//                } else {
//                    Toast.makeText(context, "检测到语音助手故障，请检查助手状态", Toast.LENGTH_SHORT).show();
//                }
                break;
            //灯
            case R.id.layout_bottom_lamp:
            case R.id.img_lamp:
                if (Tool.fastclick()) {
                    controlLamp(activity);
                }
                break;
            //自动挡
            case R.id.layout_bottom_auto:
            case R.id.img_auto:
                if (Tool.fastclick()) {
                    controlAuto(activity);
                }
                break;
            //风量
            case R.id.layout_bottom_wind:
            case R.id.img_wind:
                if (Tool.fastclick()) {
                    controlWind(activity);
                }
                break;
            //烟机关闭
            case R.id.layout_bottom_close:
            case R.id.img_close:
                if (Tool.fastclick()) {
                    AppUtil.showDelayCloseDialog(context, DelayCloseDialog.DELAY_TYPE.DELAY_TYPE_CLOSE);
                }
                break;
        }
    }

    private void controlLamp(Activity activity) {
        int lamp_value = PreferenceUtil.getLamp(context);
        //如果灯是关闭的,打开默认都是中间档
        if (lamp_value <= 0) {
//            //上一次用户保存的灯控值
//            int last_lamp = (int) PreferenceUtil.getPreferenceValue(context, PreferenceUtil.LAST_LAMP_VALUE, -1);
//            LogUtil.LOGE("LAMP-last_lamp",last_lamp);
//            if (last_lamp > 0) {
//                DeviceControl.getInstance().setLamp(last_lamp, false);
//            } else {
                //默认开启中档
                DeviceControl.getInstance().setLamp(DeviceControl.LAMP_CENTER, false);
//            }
        }
        //如果灯是打开的控制灯页面跳转
        else {
            if (null != activity) {
                //爱奇艺（乐投）打开只控制灯开关
                if (DeviceReportManager.getInstance().is_video_show || DeviceReportManager.getInstance().is_lt_show) {
                    DeviceControl.getInstance().setLamp(DeviceControl.LAMP_CLOSE, false);
                    return;
                }

                //关闭灯页面
                if (activity instanceof LampActivity) {
                    activity.finish();
                }
                //打开灯页面
                else {
                    launchActivity(LampActivity.class);
                }
            }
        }
    }

    private void controlAuto(Activity activity) {
        int work_state = DeviceReportManager.work_state;
        //如果当前不是自动档
        if (work_state != WORK_AUTO) {
            DeviceControl.getInstance().startCruise();
        }
        //如果当前是自动挡，则控制页面跳转
        else {
            if (null != activity) {
                //爱奇艺（乐投）打开只控制自动挡开关
                if (DeviceReportManager.getInstance().is_video_show || DeviceReportManager.getInstance().is_lt_show) {
                    DeviceControl.getInstance().closeDeviceOutLamp(true);
                    return;
                }

                //关闭自动挡页面
                if (activity instanceof CruiseActivity) {
                    activity.finish();
                }
                //打开自动挡页面
                else {
                    launchActivity(CruiseActivity.class);
                }
            }
        }
    }

    private void controlWind(Activity activity) {
        int work_state = DeviceReportManager.work_state;
        //如果当前不是自动档
        if (work_state != WORK_WIND_CONTROL) {
            //默认开启五档
            DeviceControl.getInstance().startWindControl(DEFAULT_WIND_VALUE, true);
        }
        //控制风量页面跳转
        else {
            if (null != activity) {
                //爱奇艺（乐投）打开只控制风量开关
                if (DeviceReportManager.getInstance().is_video_show || DeviceReportManager.getInstance().is_lt_show) {
                    DeviceControl.getInstance().closeDeviceOutLamp(true);
                    return;
                }

                //关闭风量页面
                if (activity instanceof WindControlActivity) {
                    activity.finish();
                }
                //打开自动挡页面
                else {
                    launchActivity(WindControlActivity.class);
                }
            }
        }
    }

    /**
     * 启动Activity
     */
    private void launch2Home() {
        context.launch2Home();
    }

    public void setSpeechBtnState(boolean isSpeechEnable) {
        mIsSpeechEnabel = isSpeechEnable;
        imgVoice.setImageResource(isSpeechEnable ? R.drawable.selector_bottom_voice : R.mipmap
                .icon_bottom_voice_disable);
    }
}
