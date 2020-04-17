package com.fotile.x1i.dailog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseDialog;

import butterknife.BindView;

/**
 * 文件名称：FullScreenDialog
 * 创建时间：2019/7/25 14:06
 * 文件作者：yaohx
 * 功能描述：满屏提示dialog
 */
public class FullScreenDialog extends BaseDialog implements View.OnClickListener {

    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_message)
    TextView txtMessage;
    @BindView(R.id.view_margin)
    View viewMargin;
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_right)
    TextView txtRight;

    FullScreenTip fullScreenTip;

    /**
     * 只显示一个按钮时，显示右边的按钮
     * @param context
     * @param fullScreenTip
     */
    public FullScreenDialog(Context context, FullScreenTip fullScreenTip) {
        super(context, R.style.FullScreenDialog);
        this.context = context;
        this.fullScreenTip = fullScreenTip;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        txtLeft.setOnClickListener(this);
        txtRight.setOnClickListener(this);

        //只显示确定按钮
        if (fullScreenTip == FullScreenTip.ONE_BTN) {
            txtLeft.setVisibility(View.GONE);
            viewMargin.setVisibility(View.GONE);
        }
//        //显示两个按钮
//        else{
//            txtLeft.setVisibility(View.VISIBLE);
//            viewMargin.setVisibility(View.VISIBLE);
//        }
    }

    /**
     * 设置提示消息
     *
     * @param msg
     */
    public void setMessage(String msg) {
        txtMessage.setText(msg);
    }

    /**
     *
     */
    public void setTitle(String msg) {
        txtTitle.setText(msg);
    }

    public void setBtnTxt(String left, String right) {
        txtLeft.setText(left);
        txtRight.setText(right);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_left:
                dismiss();
                if (null != onDialogListener) {
                    onDialogListener.onLeftClick();
                }
                break;
            case R.id.txt_right:
                dismiss();
                if (null != onDialogListener) {
                    onDialogListener.onRightClick();
                }
                break;

        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_fullscreen_tip;
    }

    //显示一个按钮（右边按钮）或者两个按钮
    public enum FullScreenTip {
        ONE_BTN, TWO_BTN
    }

}
