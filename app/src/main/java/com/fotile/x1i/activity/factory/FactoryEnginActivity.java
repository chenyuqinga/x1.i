package com.fotile.x1i.activity.factory;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fotile.common.bean.EnginBean;
import com.fotile.common.bean.EnginType;
import com.fotile.common.util.EnginUtil;
import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.dailog.ResetFactoryDialog;
import com.fotile.x1i.util.Constant;
import com.fotile.x1i.widget.SnakeBar;

import butterknife.BindView;
import fotile.device.cookerprotocollib.helper.DeviceControl;

/**
 * 文件名称：FactoryEnginActivity
 * 创建时间：2019/3/5 16:44
 * 文件作者：chenyqi
 * 功能描述：工程模式
 */
public class FactoryEnginActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.back_button)
    Button backbtn;
    @BindView(R.id.apply_button)
    Button applybtn;
    /**
     * ota环境
     */
    @BindView(R.id.ota_txt)
    TextView ota_button;
    @BindView(R.id.ota_group)
    RadioGroup ota_group;
    @BindView(R.id.ota_test)
    RadioButton ota_test;
    @BindView(R.id.ota_online)
    RadioButton ota_online;
    @BindView(R.id.ota_develop)
    RadioButton ota_develop;
    /**
     * 物联网云平台
     */
    @BindView(R.id.cloud_txt)
    TextView cloud_button;
    @BindView(R.id.cloud_group)
    RadioGroup cloud_group;
    @BindView(R.id.cloud_online)
    RadioButton cloud_online;
    @BindView(R.id.cloud_test)
    RadioButton cloud_test;
    @BindView(R.id.cloud_develop)
    RadioButton cloud_develop;
    /**
     * 菜谱平台
     */
    @BindView(R.id.recipe_txt)
    TextView recipe_button;
    @BindView(R.id.recipe_group)
    RadioGroup recipe_group;
    @BindView(R.id.recipe_test)
    RadioButton recipe_test;
    @BindView(R.id.recipe_develop)
    RadioButton recipe_develop;
    @BindView(R.id.recipe_online)
    RadioButton recipe_online;

    /**
     * relase debug
     */
    @BindView(R.id.relaeseAndDebug_group)
    RadioGroup releaseAndDebug_group;
    // release
    @BindView(R.id.release_mode)
    RadioButton release_mode;
    //debug
    @BindView(R.id.debug_mode)
    RadioButton debug_mode;
    // ota平台标识符
    public EnginType ota_url;
    // 菜谱平台标识符
    public EnginType recipe_url;
    // debug模式标识符
    public EnginType pack_mode;
    //物联网云平台标识符
    public int number;
    EnginBean enginBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查版本
        EnginUtil.resetEnginBean(Constant.FACTORY_VERSION_CODE);
        setRadioEnable();
        getData();
        LogUtil.LOG_ENGIN("进入工程模式", enginBean, number);

        updateButtonStatus();
        ota_group.setOnCheckedChangeListener(new OnCheckedChangeListener());
        cloud_group.setOnCheckedChangeListener(new OnCheckedChangeListener());
        recipe_group.setOnCheckedChangeListener(new OnCheckedChangeListener());
        releaseAndDebug_group.setOnCheckedChangeListener(new OnCheckedChangeListener());
        backbtn.setOnClickListener(this);
        applybtn.setOnClickListener(this);
    }

    private void setRadioEnable() {
        //ota没有开发平台
        ota_develop.setEnabled(false);
    }

    /**
     * 获取EnginBean对象数据
     */
    private void getData() {
        enginBean = EnginUtil.getEnginBean();
        ota_url = enginBean.ota_url;
        recipe_url = enginBean.recipe_url;
        pack_mode = enginBean.pack_mode;
        number = DeviceControl.getInstance().getHostNameOrIp();
    }

    private void updateButtonStatus() {
        //release模式，相应radioButton置灰
        if (pack_mode == EnginType.ENGIN_PACK_RELASE) {
            release_mode.setEnabled(false);
            release_mode.setChecked(true);
            debug_mode.setEnabled(true);
        } else {
            //debug模式
            debug_mode.setEnabled(false);
            debug_mode.setChecked(true);
            release_mode.setEnabled(true);
        }
        //菜谱线上模式
        if (recipe_url == EnginType.ENGIN_URL_ONLINE) {
            recipe_online.setEnabled(false);
            recipe_online.setChecked(true);
            recipe_develop.setEnabled(true);
            recipe_test.setEnabled(true);
        }
        //菜谱测试模式
        else if (recipe_url == EnginType.ENGIN_URL_TEST) {
            recipe_test.setEnabled(false);
            recipe_test.setChecked(true);
            recipe_develop.setEnabled(true);
            recipe_online.setEnabled(true);
        }
        //菜谱开发模式
        else if (recipe_url == EnginType.ENGIN_URL_DEVELOP) {
            recipe_develop.setEnabled(false);
            recipe_develop.setChecked(true);
            recipe_test.setEnabled(true);
            recipe_online.setEnabled(true);
        }
        //ota线上模式
        if (ota_url == EnginType.ENGIN_URL_ONLINE) {
            ota_online.setEnabled(false);
            ota_online.setChecked(true);
            ota_test.setEnabled(true);
        }
        //ota测试模式
        else if (ota_url == EnginType.ENGIN_URL_TEST) {
            ota_test.setEnabled(false);
            ota_test.setChecked(true);
            ota_online.setEnabled(true);
        }
        //物联网云平台线上环境
        if (number == 0) {
            cloud_online.setEnabled(false);
            cloud_test.setEnabled(true);
            cloud_develop.setEnabled(true);
            cloud_online.setChecked(true);
        }
        //物联网云平台测试环境
        if (number == 1) {
            cloud_online.setEnabled(true);
            cloud_test.setEnabled(false);
            cloud_develop.setEnabled(true);
            cloud_test.setChecked(true);
        }
        //物联网云平台开发环境
        if (number == 2) {
            cloud_online.setEnabled(true);
            cloud_test.setEnabled(true);
            cloud_develop.setEnabled(false);
            cloud_develop.setChecked(true);
        }

    }

    private class OnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id = group.getCheckedRadioButtonId();
            switch (id) {
                //菜谱测试
                case R.id.recipe_test:
                    enginBean.recipe_url = EnginType.ENGIN_URL_TEST;
                    break;
                //菜谱开发
                case R.id.recipe_develop:
                    enginBean.recipe_url = EnginType.ENGIN_URL_DEVELOP;
                    break;
                //菜谱线上
                case R.id.recipe_online:
                    enginBean.recipe_url = EnginType.ENGIN_URL_ONLINE;
                    break;
                //OTA测试
                case R.id.ota_test:
                    enginBean.ota_url = EnginType.ENGIN_URL_TEST;
                    break;
                //OTA线上
                case R.id.ota_online:
                    enginBean.ota_url = EnginType.ENGIN_URL_ONLINE;
                    break;
                //物联网云平台线上
                case R.id.cloud_online:
                    number = 0;
                    break;
                //物联网云平台测试
                case R.id.cloud_test:
                    number = 1;
                    break;
                //物联网云平台开发
                case R.id.cloud_develop:
                    number = 2;
                    //release模式
                case R.id.release_mode:
                    enginBean.pack_mode = EnginType.ENGIN_PACK_RELASE;
                    break;
                //debug模式
                case R.id.debug_mode:
                    enginBean.pack_mode = EnginType.ENGIN_PACK_DEBUG;
                    break;
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.apply_button:
                EnginUtil.updateEnginBean(enginBean);
                DeviceControl.getInstance().setHostNameOrIp(number);
                LogUtil.LOG_ENGIN("工程模式提交", enginBean, number);

//                ResetFactoryDialog dialog = new ResetFactoryDialog(context);
//                dialog.show();
                //恢复出厂会重置sdcard
                SnakeBar.makeMsgSnake(context,"请长按重启设备").show();
                break;

        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_factory_engin;
    }

    @Override
    public boolean showBottom() {
        return false;
    }

}
