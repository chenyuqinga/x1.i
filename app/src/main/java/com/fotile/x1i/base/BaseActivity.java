package com.fotile.x1i.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.fotile.common.util.AppManagerUtil;
import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.MainActivity;
import com.fotile.x1i.server.wifilink.LinkObserverable;
import com.fotile.x1i.server.wifilink.WifiSearchObserverable;
import com.fotile.x1i.server.wifilink.StoveWifiDevice;
import com.fotile.x1i.manager.DeviceReportManager;
import com.fotile.x1i.server.screensaver.ScreenSaverService;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.widget.BottomView;
import com.fotile.x1i.widget.TopBar;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import fotile.device.cookerprotocollib.helper.bean.WorkBean;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;


/**
 * @author： yaohx
 * @data： 2019/4/15 16:21
 * @company： 杭州方太智能科技有限公司
 * @description： BaseActivity
 */
public abstract class BaseActivity extends AppCompatActivity {
    /**
     * Activity根布局
     */
    public ViewGroup rootView;

    public int screen_width;

    public int screen_height;

    /**
     * 管理RxJava的订阅关系
     * CompositeSubscription.unsubscribe()，这个CompositeSubscription对象就不可用了
     * 如果要继续使用CompositeSubscription，就必须再创建一个新的对象了
     */
    public CompositeSubscription compositeSubscription;
    /**
     * 设备控制action
     */
    public Action1<WorkBean> actionWorkBean = null;
    /**
     * Context
     */
    public Context context;
    /**
     * 搜索灶具
     */
    public Action1<StoveWifiDevice> actionSearchStove = null;
    /**
     * 连接灶具
     */
    public Action1<StoveWifiDevice> actionLinkStove = null;
    /**
     * 表示是否有dialog显示中
     */
    public boolean dialogShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        createAction();
        createSearchAction();
        createLinkAction();
        initData();
        setScreenMargin();
    }

    private void initData() {
        context = this;
        //创建订阅关系
        compositeSubscription = new CompositeSubscription();

        screen_width = getWindowManager().getDefaultDisplay().getWidth();
        screen_height = getWindowManager().getDefaultDisplay().getHeight();

        //添加action
        if (null != actionWorkBean) {
            DeviceReportManager.getInstance().addWorkBeanAction(actionWorkBean);
        }
        if (null != actionSearchStove) {
            WifiSearchObserverable.getInstance(this).addSearchAction(actionSearchStove);
        }
        if (null != actionLinkStove) {
            LinkObserverable.getInstance(this).addLinkAction(actionLinkStove);
        }
    }

    public abstract int getLayoutId();

    /**
     * 在子类中将action1变量赋值
     */
    public void createAction() {

    }

    public void createSearchAction() {

    }

    public void createLinkAction() {

    }

    /**
     * 在finish中移除action，finish之后，上报的指令就不让执行action
     * 由于finish之后才会执行onDestory，所以移除action动作放在finish()中执行
     */
    @Override
    public void finish() {
        super.finish();
        //移除action
        DeviceReportManager.getInstance().removeWorkBeanAction(actionWorkBean);
        WifiSearchObserverable.getInstance(this).removeSearchAction(actionSearchStove);
        LinkObserverable.getInstance(this).removeLinkAction(actionLinkStove);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        //解除订阅关系，释放网络资源
        if (null != compositeSubscription && compositeSubscription.hasSubscriptions()) {
            compositeSubscription.unsubscribe();
        }
    }

    /**
     * 是否显示顶栏
     * 基本上每个页面都有顶栏，如果没有的话需要重写并且return false
     *
     * @return
     */
    public  boolean showTopBar() {
        //每一个页面都有顶栏，所以顶栏设计为覆盖上去，activity空间不用预留顶栏位置
        return true;
    }

    /**
     * 是否显示底栏
     * true，显示底栏
     * false，不显示底栏
     *
     * @return
     */
    public abstract boolean showBottom();

    @Override
    protected void onResume() {
        super.onResume();
        //添加当前activity到栈
        AppManagerUtil.getInstance().addActivity(this);

        //只有在dialog非显示时才执行
        //场景：从屏保时钟回到actiivity页面（dialog显示中），如果执行下述操作会将底栏盖在dailog上面
        if(!dialogShowing){
            //是否显示底栏
            if (showBottom()) {
                BottomView.getInstance(this).show();
            } else {
                BottomView.getInstance(this).hide();
            }
            //是否显示顶部状态栏
            if (showTopBar()) {
                TopBar.getInstance(this).show();
            } else {
                TopBar.getInstance(this).hide();
            }
        }
        else{
            BottomView.getInstance(this).hide();
            TopBar.getInstance(this).hide();
        }


        //主页面使用不同的背景
        if (this instanceof MainActivity&&!dialogShowing) {
            if (showBottom()) {
                BottomView.getInstance(this).setBottomMainBg(R.mipmap.bg_main_bottom);
                BottomView.getInstance(this).setBackBtnShow(false);
                BottomView.getInstance(this).setHomeBtnShow(false);
            }
        } else {
            if (showBottom()&&!dialogShowing) {
                BottomView.getInstance(this).setBottomMainBg(R.mipmap.bg_bottom);
                BottomView.getInstance(this).setBackBtnShow(true);
                BottomView.getInstance(this).setHomeBtnShow(true);
            }
        }

        //重置屏幕状态
        int screen_state = ScreenSaverService.getCurrentState();
        boolean isAwaitView = AppUtil.isAwaitView();
        LogUtil.LOG_SCREEN("screen_state:" + screen_state + "] iaAwaitView:" + isAwaitView);
        if (screen_state != ScreenSaverService.SCREEN_SLEEP && !isAwaitView) {
            //重置屏幕状态,在点击屏幕,重新回到该界面都需要重置屏幕亮度及重新计时
            ScreenTool.getInstance().addResetData(getClass().getSimpleName() + "的onResume或者手势");
            //当从系统的Launcher回到页面时候，设置电源键不可点，事件上传给f3
//            PowerUtil.powerProvideApp();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ScreenSaverService.getCurrentState() == ScreenSaverService.SCREEN_DARK) {
            ScreenTool.getInstance().addResetData(getClass().getSimpleName() + "的onResume或者手势");
            return true;
        } else {
            ScreenTool.getInstance().addResetData(getClass().getSimpleName() + "的onResume或者手势");
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //当MainActivity进入屏保时钟时，不能移除该activity
        if (!(this instanceof MainActivity)) {
            //移除当前Activity
            AppManagerUtil.getInstance().removeActivity(this);
        }
    }

    /**
     * 设置屏幕边距缩进
     */
    private void setScreenMargin() {
        rootView = (ViewGroup) findViewById(android.R.id.content);
        //屏幕缩进距离
        int screen_margin = (int) getResources().getDimension(R.dimen.screen_margin);
        //顶部状态栏高度
        int state_bar_height = (int) getResources().getDimension(R.dimen.state_bar_height);
        //bottom栏高度
        int bottom_height = (int) getResources().getDimension(R.dimen.bottom_height);

        int margin_top = screen_margin;
        int margin_bottom = screen_margin;

        //底栏和顶栏都是覆盖上去的
        rootView.setPadding(screen_margin, margin_top, screen_margin, margin_bottom);
    }

    public void launchActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    public void launchActivity(Intent intent) {
        startActivity(intent);
    }

    public void launch2Home() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

}
