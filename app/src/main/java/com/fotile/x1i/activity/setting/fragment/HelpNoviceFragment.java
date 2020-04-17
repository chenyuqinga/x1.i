package com.fotile.x1i.activity.setting.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.fotile.x1i.R;
import com.fotile.x1i.activity.guide.introduce.GuideIntroduceActivity;
import com.fotile.x1i.activity.guide.introduce.GuideIntroducePageActivity;
import com.fotile.x1i.base.BaseFragment;

import butterknife.BindView;

/**
  * 文件名称：HelpNoviceFragment
  * 创建时间：2019/5/10 19:30
  * 文件作者：yaohx
  * 功能描述：帮助--新手帮助
  */
public class HelpNoviceFragment extends BaseFragment implements OnClickListener {

    @BindView(R.id.txt_confirm)
    View txtConfirm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        initView();
        return view;
    }

    private void initView() {
        txtConfirm.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_help_novice;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //查看功能介绍
            case R.id.txt_confirm:
                Intent intent = new Intent(context, GuideIntroducePageActivity.class);
                context.startActivity(intent);
                break;
        }
    }
}
