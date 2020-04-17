package com.fotile.x1i.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fotile.x1i.R;


/**
 * 文件名称：BottomViewArrowDown
 * 创建时间：2019/7/23 14:41
 * 文件作者：yaohx
 * 功能描述：底栏向下箭头
 */
public class BottomViewArrowDown extends LinearLayout implements View.OnClickListener {

    private Context context;
    private static BottomViewArrowDown instance;
    private ImageView imgDown;
    private View main;
    /**
     * 延时10秒隐藏
     */
    private final int WHAT_DELAY_HIDE = 1001;

    private BottomViewArrowDown(Context context) {
        super(context);
        init(context);
    }

    public static BottomViewArrowDown getInstance(Context context) {
        if (null == instance) {
            instance = new BottomViewArrowDown(context);
        }
        return instance;
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_bottom_arrow_down, this, true);
        main = findViewById(R.id.main);
        imgDown = (ImageView) view.findViewById(R.id.img_arrow_down);
        imgDown.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //向下箭头
            case R.id.img_arrow_down:
                hide();
                BottomView.getInstance(context).hide();
                BottomViewArrowUp.getInstance(context).show();
                break;
        }
    }

    public void hide(){
        if(null != main){
            main.setVisibility(View.GONE);
        }
        setVisibility(View.GONE);
        handler.removeMessages(WHAT_DELAY_HIDE);
    }

    public void show(){
        if(null != main){
            main.setVisibility(View.VISIBLE);
        }
        setVisibility(View.VISIBLE);
        delayHide();
    }

    public void delayHide(){
        if(getVisibility() == View.VISIBLE){
            //移除之前的handler
            handler.removeMessages(WHAT_DELAY_HIDE);
            handler.sendEmptyMessageDelayed(WHAT_DELAY_HIDE , 10 * 1000);
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_DELAY_HIDE:
                    hide();
                    BottomView.getInstance(context).hide();
                    BottomViewArrowUp.getInstance(context).show();
                    break;
            }
            return false;
        }
    });
}
