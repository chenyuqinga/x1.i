package com.fotile.x1i.dailog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 文件名称：CommonDialog
 * 创建时间：2019/4/25 14:27
 * 文件作者：yaohx
 * 功能描述：普通提示信息dialog（非全屏）
 */
public class CommonDialog extends BaseDialog implements View.OnClickListener {

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
    TextView txtKind;

    CommonTip commonTip;

    public CommonDialog(Context context, CommonTip commonTip) {
        super(context, R.style.CommonDialog);
        this.commonTip = commonTip;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_common_tip;
    }

    public void setBtnTxt(String left, String right){
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

    /**
     *
     */
    public void setTitle(String msg) {
        txtKind.setText(msg);
    }

    private void initView() {
        txtLeft.setOnClickListener(this);
        txtRight.setOnClickListener(this);

        //只显示确定按钮
        if (commonTip == CommonTip.ONE_BTN) {
            txtLeft.setVisibility(View.GONE);
            imgLine.setVisibility(View.GONE);
        }
//        //显示两个按钮
//        else{
//            txtLeft.setVisibility(View.VISIBLE);
//            imgLine.setVisibility(View.VISIBLE);
//        }
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

    //显示一个按钮或者两个按钮
    public enum CommonTip {
        ONE_BTN, TWO_BTN
    }

}
