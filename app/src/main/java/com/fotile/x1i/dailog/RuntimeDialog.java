package com.fotile.x1i.dailog;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 文件名称： FaultDialog
 * 创建时间： 2019/6/18
 * 文件作者： chenyqi
 * 功能描述：风机2000小时保养\故障
 */
public class RuntimeDialog extends BaseDialog implements View.OnClickListener {

    @BindView(R.id.tv_tip_content)
    TextView tvTipContent;

    @BindView(R.id.txt_later)
    TextView txtLater;

    @BindView(R.id.txt_completed)
    TextView txtCompleted;


    public RuntimeDialog(Context context) {
        super(context, R.style.FullScreenDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setCareTip();
        txtLater.setOnClickListener(this);
        txtCompleted.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //稍后提示
            case R.id.txt_later:
                PreferenceUtil.setRunTimeTip(context,PreferenceUtil.RUN_CLEAN_TIP_NEXT);
                dismiss();
                break;
            //已完成保养（不再提示）
            case R.id.txt_completed:
                PreferenceUtil.setRunTimeTip(context,PreferenceUtil.RUN_CLEAN_TIP_NEVER);
                dismiss();
                break;
        }
    }

    /**
     * 保养提示
     */
    private void setCareTip() {
        String tip = "风机运行2000多小时 清洗服务电话 952315";
        SpannableString spannableString = new SpannableString(tip);
        int index_start = tip.indexOf("4000");
        int index_end = tip.length();
        int color = getContext().getResources().getColor(R.color.select_item);
        spannableString.setSpan(new ForegroundColorSpan(color), index_start, index_end, Spanned
                .SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTipContent.setText(spannableString);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_runtime;
    }
}
