package com.fotile.x1i.activity.setting.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseFragment;

/**
 * 文件名称：AboutInstructionsFragment
 * 创建时间：2019/4/29 16:35
 * 文件作者：yaohx
 * 功能描述：关于本机-电子说明书
 */
public class AboutInstructionsFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_about_instructions;
    }
}
