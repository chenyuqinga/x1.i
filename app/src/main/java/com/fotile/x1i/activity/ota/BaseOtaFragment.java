package com.fotile.x1i.activity.ota;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fotile.ota.R;
import com.fotile.ota.bean.UpgradeInfo;

/**
 * 文件名称：BaseOtaFragment
 * 创建时间：2018/7/4 12:00
 * 文件作者：yaohx
 * 功能描述：BaseOtaFragment
 */
public abstract class BaseOtaFragment extends Fragment {

    protected View view;

    public AppCompatActivity context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(getLayoutId(), container, false);
        context = (AppCompatActivity) getActivity();
        return view;
    }

    /**
     * 跳转到网络错误fragment
     */
    public void change2NetError() {
        boolean isForeground = ((OtaMainActivity) context).isForeground;
        //如果页面在前台才更去更新
        if (isForeground) {
            FragmentManager ft = context.getSupportFragmentManager();
            FragmentTransaction transaction = ft.beginTransaction();
            transaction.replace(R.id.frame, new OtaNetErrorFragment());
            //transaction.commit();
            //commit()方法在Activity的onSaveInstanceState()之后调用会报错，故改成commitAllowingStateLoss()
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * 跳转到下载fragment
     *
     * @param upgradeInfo
     * @param state
     */
    public void change2Download(UpgradeInfo upgradeInfo, int state) {
        boolean isForeground = ((OtaMainActivity) context).isForeground;
        //如果页面在前台才更去更新
        if (isForeground) {
            FragmentManager ft = context.getSupportFragmentManager();
            FragmentTransaction transaction = ft.beginTransaction();
            transaction.replace(R.id.frame, new OtaDownloadFragment(upgradeInfo, state));
            transaction.commitAllowingStateLoss();
        }
    }

    public void change2Check() {
        boolean isForeground = ((OtaMainActivity) context).isForeground;
        //如果页面在前台才更去更新
        if (isForeground) {
            FragmentManager ft = context.getSupportFragmentManager();
            FragmentTransaction transaction = ft.beginTransaction();
            transaction.replace(R.id.frame, new OtaCheckFragment());
            transaction.commitAllowingStateLoss();
        }
    }

    public abstract int getLayoutId();

}
