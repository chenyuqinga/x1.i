package com.fotile.x1i.activity.ota;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fotile.common.util.Tool;
import com.fotile.ota.R;
import com.fotile.x1i.activity.setting.WifiListActivity;

/**
 * 项目名称：Ota
 * 创建时间：2018/10/11 16:50
 * 文件作者：yaohx
 * 功能描述：网络异常页面
 */
public class OtaNetErrorFragment extends BaseOtaFragment {
    /**
     * 设置网络
     */
    private TextView txt_set_net;
    /**
     * 本地固件版本
     */
    private TextView txt_version_local;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        initView();
        return view;
    }

    private void initView() {
        txt_set_net = (TextView) view.findViewById(R.id.txt_set_net);
        txt_version_local = (TextView) view.findViewById(R.id.txt_version_local);

        txt_set_net.setOnClickListener(set_Listener);
        txt_version_local.setText("方太智慧厨房" + Tool.getVersionName(context));
    }

    View.OnClickListener set_Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //网络不好点击设置进入wife页面
            OtaMainActivity otaMainActivity = (OtaMainActivity) getActivity();
            otaMainActivity.resume_from_wife = true;

            Intent intent = new Intent();
            intent.setClass(context, WifiListActivity.class);
            context.startActivity(intent);

        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.fragment_ota_net_error;
    }


}
