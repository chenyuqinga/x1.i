package com.fotile.x1i.activity.setting.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.fotile.x1i.R;
import com.fotile.x1i.activity.setting.dialog.LegalDialog;
import com.fotile.x1i.base.BaseFragment;

import butterknife.BindView;

/**
 * 文件名称：AboutInstructionsFragment
 * 创建时间：2019/4/29 16:35
 * 文件作者：yaohx
 * 功能描述：关于本机-法律信息
 */
public class AboutLegalFragment extends BaseFragment implements OnClickListener {

    @BindView(R.id.layout_xieyi)
    View layoutXieyi;
    @BindView(R.id.layout_yinsi)
    View layoutYinsi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        initView();
        return view;
    }

    private void initView() {
        layoutXieyi.setOnClickListener(this);
        layoutYinsi.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_about_legal;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //用户协议
            case R.id.layout_xieyi:
                LegalDialog dialog = new LegalDialog(context, 1);
                dialog.show();
                break;
            //隐私政策
            case R.id.layout_yinsi:
                dialog = new LegalDialog(context, 2);
                dialog.show();
                break;
        }
    }
}
