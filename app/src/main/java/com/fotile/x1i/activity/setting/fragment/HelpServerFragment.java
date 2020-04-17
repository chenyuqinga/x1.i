package com.fotile.x1i.activity.setting.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseFragment;

/**
  * 文件名称：HelpServerFragment
  * 创建时间：2019/5/10 19:30
  * 文件作者：yaohx
  * 功能描述：帮助--服务热线
  */
public class HelpServerFragment extends BaseFragment implements OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        initView();
        return view;
    }

    private void initView() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.item_help_server;
    }

    @Override
    public void onClick(View v) {

    }
}
