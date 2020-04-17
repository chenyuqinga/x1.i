package com.fotile.x1i.dailog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.base.BaseDialog;
import com.fotile.x1i.server.screensaver.ScreenTool;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 文件名称： FaultDialog
 * 创建时间： 2019/6/18
 * 文件作者： chenyqi
 * 功能描述：风机2000小时保养\故障
 */
public class FaultDialog extends BaseDialog implements View.OnClickListener {

    @BindView(R.id.tv_tip_content)
    TextView tvTipContent;
    @BindView(R.id.btn_later)
    TextView tvBtnLater;


    public FaultDialog(Context context) {
        //使用全局提示框
        super(context.getApplicationContext(), R.style.FullScreenDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvBtnLater.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //稍后提示
            case R.id.btn_later:
                PreferenceUtil.setPreferenceValue(context,PreferenceUtil.FAULT_TIP,false);
                dismiss();
                break;
        }
    }

    /**
     * 推杆电击故障
     *
     * @param errornum
     */
    public void setPushTip(String errornum) {
        String tip = "出现故障（F" + errornum + "），已停止工作 请拨打售后电话：952315";
        SpannableString spannableString = new SpannableString(tip);
        int index_start = tip.indexOf("952315");
        int index_end = tip.length();
        int color = getContext().getResources().getColor(R.color.select_item);
        spannableString.setSpan(new ForegroundColorSpan(color), index_start, index_end, Spanned
                .SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTipContent.setText(spannableString);
        tvBtnLater.setVisibility(View.VISIBLE);
    }

    /**
     * 关闭灶具提示
     *
     * @param errornum
     */
    public void setCloseStoveTip(String errornum) {
        String tip = "出现故障（F" + errornum + "），设备已经通过烟灶联动关闭灶具\n 请拨打售后电话：952315";
        SpannableString spannableString = new SpannableString(tip);
        int index_start = tip.indexOf("4000");
        int index_end = tip.length();
        int color = getContext().getResources().getColor(R.color.select_item);
        spannableString.setSpan(new ForegroundColorSpan(color), index_start, index_end, Spanned
                .SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTipContent.setText(spannableString);
        tvBtnLater.setVisibility(View.VISIBLE);
    }


    /**
     * 故障dialog使用系统dialog显示
     */
    public void show() {
        //使用系统dialog显示
        showSystemDialog();
        //故障界面，停止屏保
        ScreenTool.getInstance().addPause();

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
    public int getLayoutId() {
        return R.layout.dialog_fault;
    }
}
