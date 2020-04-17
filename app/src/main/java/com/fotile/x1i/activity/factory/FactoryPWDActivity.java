package com.fotile.x1i.activity.factory;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.fotile.common.util.PowerUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.control.FactoryCheckActivity;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.server.ProductionLineServer;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.util.CmdUtil;
import com.fotile.x1i.util.StatusBarUtils;

import butterknife.BindView;
import fotile.device.cookerprotocollib.helper.DeviceControl;

/**
 * 文件名称：FactoryPWDActivity
 * 创建时间：2019/2/26 11:16
 * 文件作者：yaohx
 * 功能描述：管理员密码
 */
public class FactoryPWDActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_factory_pwd_back)
    Button btnBack;

    @BindView(R.id.btn_factory_pwd_sure)
    Button btnSure;

    @BindView(R.id.factory_pwd_edit)
    EditText editTextPwd;


    private BaseActivity context;
    private InputMethodManager inputManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        initView();
        //        //进入该界面，关闭乐投，不影响界面跳转以及侧栏底栏显示
        //        Tool.closeLt(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //从系统设置页回到该页面，隐藏statusBar
        StatusBarUtils.hideStatusBar(context);
        ScreenTool.getInstance().addPause();
        DeviceControl.getInstance().closeDevice(true);
    }

    private void initView() {
        inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        btnBack.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        editTextPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    doNext();
                    inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return false;
            }
        });
    }

    private void doNext() {
        String factorypwd = editTextPwd.getText().toString();
        if (factorypwd.equals("")) {
            Toast.makeText(context, "密码不能为空！", Toast.LENGTH_LONG).show();
            return;
        }

        //老化测试
        else if (factorypwd.equals("*#19960118#*")) {

        }
        //工程模式
        else if (factorypwd.equals("*#20180101#*")) {
            Intent intent = new Intent();
            intent.setClass(FactoryPWDActivity.this, FactoryEnginActivity.class);
            startActivity(intent);
        }
        //安卓系统
        else if (factorypwd.equals("*#100#")) {
          //  defaultLauncher();
            CmdUtil.startSystemUI();
            StatusBarUtils.showStatusBar(context);
            AppUtil.startOtherApp(context, "com.android.settings");
        }
        //方太工厂自检
        else if (factorypwd.equals("*#120#")) {
            Intent intent = new Intent();
            intent.setClass(FactoryPWDActivity.this, FactoryCheckActivity.class);
            startActivity(intent);
            finish();

        }
        //视源工厂测试
        else if (factorypwd.equals("*#110#")) {
//            go2FactoryTest(context);
        }
        //产线功能自检-开启
        else if (factorypwd.equals("*#130#") || factorypwd.equals("*#1000#*")) {
            Intent intent = new Intent(context, ProductionLineServer.class);
            context.startService(intent);
            context.launch2Home();
        }

        /****************************************************/

    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_factory_pwd;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_factory_pwd_back:
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                finish();
                break;
            case R.id.btn_factory_pwd_sure:
                doNext();
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                break;
        }
    }

    @Override
    public boolean showBottom() {
        return false;
    }
}
