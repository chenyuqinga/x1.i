package com.fotile.x1i.activity.setting;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fotile.bind.mvp.presenter.DeviceBindingPresenter;
import com.fotile.bind.mvp.view.DeviceBindingView;
import com.fotile.bind.util.QRCodeUtil;
import com.fotile.common.util.PreferenceUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.guide.introduce.GuideAgreementActivity;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.dailog.CommonDialog;
import com.fotile.x1i.listener.OnDialogListener;
import com.fotile.x1i.manager.DeviceReportManager;
import com.fotile.x1i.widget.RotationLoadingView;
import com.fotile.x1i.widget.SnakeBar;
import com.fotile.x1i.widget.TopBar;

import butterknife.BindView;
import fotile.device.cookerprotocollib.helper.DeviceControl;
import fotile.device.cookerprotocollib.helper.bean.DeviceMessage;
import rx.functions.Action1;

import static com.fotile.common.util.PreferenceUtil.DEVICE_BIND_STATE;
import static com.fotile.common.util.PreferenceUtil.TELE_CONTROL;
import static com.fotile.x1i.util.Constant.PRIDUCT_ID_TEST;

/**
 * 文件名称：DeviceBindActivity
 * 创建时间：2019/5/14 14:31
 * 文件作者：yaohx
 * 功能描述：设备绑定--和引导页面公用
 */
public class DeviceBindActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 刷新二维码按钮
     */
    @BindView(R.id.img_qrcode_refresh)
    ImageView imgQrcodeRefresh;
    /**
     * 二维码图片
     */
    @BindView(R.id.img_qrcode_bind)
    ImageView imgQrcodeBind;
    /**
     * 旋转动态
     */
    @BindView(R.id.img_qrcode_rotation)
    RotationLoadingView imgQrcodeRotation;

    DeviceBindingPresenter bindingPresenter;
    /**
     * 页面跳转是否来自引导页
     */
    boolean fromGuide = false;
    /**
     * 设置页面标题
     */
    @BindView(R.id.txt_setting_titie)
    TextView txtSettingTitie;
    @BindView(R.id.txt_guide_titie)
    TextView txtGuideTitie;
    @BindView(R.id.txt_guide_tip)
    TextView txtGuideTip;
    /**
     * 下一步按钮--用于引导页
     */
    @BindView(R.id.txt_next)
    TextView txtNext;

    /**
     * 未绑定view
     */
    @BindView(R.id.layout_unbind)
    View layoutUnbind;
    /**
     * 已绑定view（设置页面显示）
     */
    @BindView(R.id.layout_binded)
    View layoutBindedSetting;
    /**
     * 已绑定view（引导页面显示）
     */
    @BindView(R.id.layout_binded_guide)
    View layoutBindedGuide;

    /**
     * 扫码绑定view
     */
    @BindView(R.id.layout_code)
    View layoutCode;

    /**
     * 远程控制开关
     */
    @BindView(R.id.btn_switch)
    Switch btnSwitch;
    /**
     * 解除绑定
     */
    @BindView(R.id.txt_unbind)
    TextView txtUnbind;

    /**
     * 旋转动图
     */
    @BindView(R.id.img_rotation)
    RotationLoadingView rotationLoadingView;

    //请求失败
    final int QR_CODE_ERROR = 1001;
    //请求中
    final int QR_CODE_REQUEST = 1002;
    //请求完成
    final int QR_CODE_SUCCESS = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initApp();
        super.onCreate(savedInstanceState);
        initView();
        initData();
        getBindQrCode();
    }

    private void initApp() {
        if (getIntent().hasExtra("guide")) {
            fromGuide = true;
        }
    }

    @Override
    public void createAction() {
        super.createAction();
        Action1<DeviceMessage> action1 = new Action1<DeviceMessage>() {
            @Override
            public void call(DeviceMessage deviceMessage) {
                //数据处理已经在DeviceReportManager中处理
                switch (deviceMessage.code){
                    case 6:
                        showBindView();
                        break;
                    case 20:
                        String result=deviceMessage.params;
                        if(TextUtils.isEmpty(result)||result.contains("error")||!result.contains("qrcode")){
                            deviceBindingView.updateFailQR();
                        }else {
                            deviceBindingView.updateSuccessQR(QRCodeUtil.createQRCodeBitmap(result,250));
                        }
                        break;
                        default:
                            break;
                }

            }
        };
        DeviceReportManager.getInstance().addMessageAction(action1);
    }

    private void initView() {
        //入口来自引导页
        if (fromGuide) {
            txtSettingTitie.setVisibility(View.GONE);
            txtGuideTitie.setVisibility(View.VISIBLE);
            txtGuideTip.setVisibility(View.VISIBLE);
            txtNext.setVisibility(View.VISIBLE);

            String tip = "您也可以在  设置 > 远程控制  中设置该功能";
            SpannableString spannableString = new SpannableString(tip);
            int index_start = tip.indexOf("设置 >");
            int index_end = tip.indexOf("中设置");
            int color = Color.parseColor("#C8AF70");
            spannableString.setSpan(new ForegroundColorSpan(color), index_start, index_end, Spanned
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
            txtGuideTip.setText(spannableString);
        } else {
            txtSettingTitie.setVisibility(View.VISIBLE);
            txtGuideTitie.setVisibility(View.GONE);
            txtGuideTip.setVisibility(View.GONE);
            txtNext.setVisibility(View.GONE);
        }

        txtNext.setOnClickListener(this);
        txtUnbind.setOnClickListener(this);
        //远程控制开关
        String control = (String) PreferenceUtil.getPreferenceValue(context, TELE_CONTROL, "on");
        boolean tele_control = "on".equals(control) ? true : false;
        btnSwitch.setChecked(tele_control);
        imgQrcodeRefresh.setOnClickListener(this);

        //远程控制开关
        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    DeviceControl.getInstance().setTelecontrol("on");
                    SnakeBar.makeDeviceLinkMsgSnake(context, "远程控制已打开").show();
                } else {
                    DeviceControl.getInstance().setTelecontrol("off");
                    SnakeBar.makeDeviceLinkMsgSnake(context, "远程控制已关闭").show();
                }
            }
        });

        showBindView();
    }

    /**
     * 根据绑定状态来显示view
     */
    private void showBindView() {
        boolean isbind = (boolean) PreferenceUtil.getPreferenceValue(this, DEVICE_BIND_STATE, false);
        //已绑定
        if (isbind) {
            if (fromGuide) {
                txtNext.setText("下一步");
                //显示已绑定view（引导页）
                layoutBindedGuide.setVisibility(View.VISIBLE);
                layoutBindedSetting.setVisibility(View.GONE);
                //隐藏二维码view
                layoutCode.setVisibility(View.GONE);
                //显示未绑定页面，需要显示“添加成功”
                layoutUnbind.setVisibility(View.VISIBLE);

                txtUnbind.setVisibility(View.GONE);
                rotationLoadingView.setVisibility(View.GONE);
            } else {
                TopBar.getInstance(context).updateBindState(true);
                //显示已绑定view（设置）
                layoutBindedGuide.setVisibility(View.GONE);
                layoutBindedSetting.setVisibility(View.VISIBLE);
                //隐藏二维码view
                layoutCode.setVisibility(View.GONE);
                //隐藏未绑定页面
                layoutUnbind.setVisibility(View.GONE);

                txtUnbind.setVisibility(View.VISIBLE);
                rotationLoadingView.setVisibility(View.GONE);
            }
        }
        //未绑定状态
        else {
            TopBar.getInstance(context).updateBindState(false);
            txtNext.setText("跳过");
            //隐藏绑定view
            layoutBindedGuide.setVisibility(View.GONE);
            layoutBindedSetting.setVisibility(View.GONE);
            //显示二维码view
            layoutCode.setVisibility(View.VISIBLE);
            //显示未绑定页面
            layoutUnbind.setVisibility(View.VISIBLE);

            txtUnbind.setVisibility(View.GONE);
            rotationLoadingView.setVisibility(View.VISIBLE);
        }

    }

    private void initData() {
        bindingPresenter = new DeviceBindingPresenter(context, deviceBindingView);
        getData();
    }

    private void getData() {
        String authorizeCode = DeviceControl.getAuthCode();
        String deviceId = DeviceControl.getDeviceID();
        bindingPresenter.setData(PRIDUCT_ID_TEST, authorizeCode, deviceId);
    }

    /**
     * 获取扫码绑定二维码
     */
    public void getBindQrCode() {
        boolean isRegister = bindingPresenter.registerWifiReceiver();
        if (isRegister) {
            bindingPresenter.getDeviceSubQR();
        }
        updateQrcodeView(QR_CODE_REQUEST);
    }

    DeviceBindingView deviceBindingView = new DeviceBindingView() {
        @Override
        public void updateSuccessQR(Bitmap bitmap) {
            updateQrcodeView(QR_CODE_SUCCESS);
            imgQrcodeBind.setImageBitmap(bitmap);
        }

        @Override
        public void updateFailQR() {
            updateQrcodeView(QR_CODE_ERROR);
            imgQrcodeBind.setImageBitmap(null);
        }
    };

    private void updateQrcodeView(int state) {
        switch (state) {
            //请求失败
            case QR_CODE_ERROR:
                imgQrcodeRefresh.setVisibility(View.VISIBLE);
                imgQrcodeBind.setVisibility(View.GONE);
                imgQrcodeRotation.setVisibility(View.GONE);
                imgQrcodeRotation.stopRotationAnimation();
                break;
            //请求成功
            case QR_CODE_SUCCESS:
                imgQrcodeRefresh.setVisibility(View.GONE);
                imgQrcodeBind.setVisibility(View.VISIBLE);
                imgQrcodeRotation.setVisibility(View.GONE);

                imgQrcodeRotation.stopRotationAnimation();
                break;
            //请求中
            case QR_CODE_REQUEST:
                imgQrcodeRefresh.setVisibility(View.GONE);
                imgQrcodeBind.setVisibility(View.GONE);
                imgQrcodeRotation.setVisibility(View.VISIBLE);

                imgQrcodeRotation.startRotationAnimation();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting_bind;
    }

    @Override
    public boolean showBottom() {
        if (fromGuide) {
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bindingPresenter.stop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //刷新按钮
            case R.id.img_qrcode_refresh:
                getData();
                getBindQrCode();
                break;
            //点击引导页入口的下一步--跳转到用户协议
            case R.id.txt_next:
                launchActivity(GuideAgreementActivity.class);
                break;
            //解除绑定
            case R.id.txt_unbind:
                boolean isOpen = btnSwitch.isChecked();
                if (!isOpen) {
                    Toast.makeText(context, "请先打开远程控制开关", Toast.LENGTH_SHORT).show();
                } else {
                    CommonDialog dialog = new CommonDialog(context, CommonDialog.CommonTip.TWO_BTN);
                    dialog.setOnDialogListener(listener);
                    dialog.show();
                    dialog.setMessage("是否确定解除该设备与所有已绑定手机的连接？");
                }
                break;
        }
    }

    OnDialogListener listener = new OnDialogListener() {
        @Override
        public void onLeftClick(Object... objects) {

        }

        //点击开始解绑
        @Override
        public void onRightClick(Object... objects) {
            rotationLoadingView.setVisibility(View.VISIBLE);
            rotationLoadingView.startRotationAnimation();

            txtUnbind.setVisibility(View.GONE);
            DeviceControl.getInstance().setXlinkServer(2);
        }
    };

}
