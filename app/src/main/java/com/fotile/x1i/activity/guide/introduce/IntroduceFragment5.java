package com.fotile.x1i.activity.guide.introduce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseFragment;

public class IntroduceFragment5 extends BaseFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        TextView txt_home = (TextView) view.findViewById(R.id.txt_home);
        txt_home.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                context.launch2Home();
            }
        });

        return view;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_introduce_page_5;
    }
}
