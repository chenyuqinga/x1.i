package com.fotile.x1i.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fotile.x1i.R;

/**
 * 文件名称：BottomViewArrowUp
 * 创建时间：2019/7/23 14:41
 * 文件作者：yaohx
 * 功能描述：底栏向上箭头
 */
public class BottomViewArrowUp extends LinearLayout implements View.OnClickListener {

    private Context context;
    private static BottomViewArrowUp instance;
    private ImageView imgUp;
    private View main;

    private BottomViewArrowUp(Context context) {
        super(context);
        init(context);
    }

    public static BottomViewArrowUp getInstance(Context context) {
        if (null == instance) {
            instance = new BottomViewArrowUp(context);
        }
        return instance;
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_bottom_arrow_up, this, true);
        main = view.findViewById(R.id.main);
        imgUp = (ImageView) view.findViewById(R.id.img_arrow_up);
        imgUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //向上箭头
            case R.id.img_arrow_up:
                hide();
                BottomView.getInstance(context).show();
                BottomViewArrowDown.getInstance(context).show();
                break;
        }
    }

    public void hide(){
        if(null != main){
            main.setVisibility(View.GONE);
        }
        setVisibility(View.GONE);
    }

    public void show(){
        if(null != main){
            main.setVisibility(View.VISIBLE);
        }
        setVisibility(View.VISIBLE);
    }
}
