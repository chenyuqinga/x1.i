package com.fotile.x1i.dailog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 文件名称：TipCloseDialog
 * 创建时间：2019/7/23 16:57
 * 文件作者：yaohx
 * 功能描述：关闭爱奇艺视频提示dialog
 */
public class TipCloseDialog extends BaseDialog implements View.OnClickListener {
    @BindView(R.id.txt_left)
    TextView txtLeft;
    @BindView(R.id.txt_right)
    TextView txtRight;
    @BindView(R.id.line)
    ImageView imgLine;


    /**
     * 提示消息
     */
    @BindView(R.id.txt_message)
    TextView txtMessage;
    /**
     * 提示类型
     */
    @BindView(R.id.tip_kind)
    TextView txtTitle;

    public TipCloseDialog(Context context) {
        super(context, R.style.CommonDialog);
    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_common_tip);
        initView();
    }

    public void setBtnTxt(String left, String right) {
        txtLeft.setText(left);
        txtRight.setText(right);
    }

    /**
     * 设置提示消息
     *
     * @param msg
     */
    public void setMessage(String msg) {
        txtMessage.setText(msg);
    }

    public void setTitle(String msg) {
        txtTitle.setText(msg);
    }

    @Override
    public void show() {
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        super.show();
    }

    private void initView() {
        txtLeft.setOnClickListener(this);
        txtRight.setOnClickListener(this);

        txtLeft.setVisibility(View.VISIBLE);
        imgLine.setVisibility(View.VISIBLE);
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

}
