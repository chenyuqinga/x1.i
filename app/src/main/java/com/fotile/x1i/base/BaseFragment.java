package com.fotile.x1i.base;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * 文件名称：BaseMusicFragment
 * 创建时间：2018/7/4 12:00
 * 文件作者：yaohx
 * 功能描述：
 */
public abstract class BaseFragment extends Fragment {

    public BaseActivity context;

    protected View view;
    /**
     * 取自BaseActivity
     */
    public CompositeSubscription compositeSubscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(getLayoutId(), container, false);
        createAction();
        ButterKnife.bind(this, view);
        initApp();
        return view;
    }

    public void createAction(){

    }

    private void initApp() {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            this.context = (BaseActivity) activity;
            compositeSubscription = ((BaseActivity) activity).compositeSubscription;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public abstract int getLayoutId();


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
