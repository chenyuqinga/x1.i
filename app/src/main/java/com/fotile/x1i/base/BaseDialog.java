package com.fotile.x1i.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.fotile.x1i.R;
import com.fotile.x1i.listener.OnDialogListener;
import com.fotile.x1i.manager.DeviceReportManager;
import com.fotile.x1i.manager.DialogManager;
import com.fotile.x1i.server.screensaver.ScreenSaverService;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.widget.BottomView;
import com.fotile.x1i.widget.TopBar;

import butterknife.ButterKnife;

import static com.fotile.x1i.util.Constant.SCREEN_HEIGHT;
import static com.fotile.x1i.util.Constant.SCREEN_WIDTH;

/**
 * @author： yaohx
 * @data： 2019/4/15 16:21
 * @company： 杭州方太智能科技有限公司
 * @description： dialog继承自BaseDialog
 */
public abstract class BaseDialog extends Dialog {
    public Context context;
    /**
     * dialog回调
     */
    public OnDialogListener onDialogListener;

    public BaseDialog(Context context, int type) {
        super(context, type);
        this.context = context;
        setContentView(getLayoutId());
        ButterKnife.bind(this);
    }

    public abstract int getLayoutId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ScreenSaverService.getCurrentState() == ScreenSaverService.SCREEN_DARK) {
            ScreenTool.getInstance().addResetData("BaseDialog的onResume或者手势");
            return true;
        } else {
            ScreenTool.getInstance().addResetData("BaseDialog的onResume或者手势");
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public void show() {
        //这行代码不能删除，防止bottom hide被执两次
        if (!isShowing()) {
            super.show();
            BottomView.getInstance(context).hide();
            TopBar.getInstance(context).hide();
        }
        //告诉父容器有dialog显示
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            activity.dialogShowing = true;
        }
        //dailog显示时，重启屏保
        ScreenTool.getInstance().addResetData("BaseDialog show");
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (context instanceof BaseActivity) {
            if (!DeviceReportManager.getInstance().is_video_show) {
                BaseActivity activity = (BaseActivity) context;
                if (activity.showBottom() && !DialogManager.getInstance().isExceptionDialogShow()) {
                    BottomView.getInstance(context).show();
                }
                //如果容器类显示顶栏
                if (activity.showTopBar()) {
                    TopBar.getInstance(context).show();
                }

                //告诉父容器dialog隐藏
                activity.dialogShowing = false;
            }
        }
        //dialog取消时，重启屏保
        ScreenTool.getInstance().addResetData("dialog dismiss");
    }

    //设置屏幕缩进边距
    @Override
    public void onStart() {
        super.onStart();
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        if (null != attributes) {
            int screen_margin = (int) context.getResources().getDimension(R.dimen.screen_margin);

            //提示dialog为全屏dialog所以不需要预留顶栏和底栏
            attributes.x = screen_margin;
            attributes.y = screen_margin;
            attributes.width = SCREEN_WIDTH - screen_margin * 2;
            attributes.height = SCREEN_HEIGHT - screen_margin * 2;
            attributes.gravity = Gravity.LEFT | Gravity.TOP;
            getWindow().setAttributes(attributes);
        }
    }

    /**
     * 隐藏对话框，在隐藏后不显示 BottomView
     */
    public void dismissOutBottom() {
        super.dismiss();
    }

    /**
     * 全局弹框,dialog会覆盖在windowmanager之上
     */
    public void showSystemDialog() {
        Window dialogWindow = getWindow();
        if (null != dialogWindow) {
            dialogWindow.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        super.show();
    }

    public void setOnDialogListener(OnDialogListener onDialogListener) {
        this.onDialogListener = onDialogListener;
    }
}
