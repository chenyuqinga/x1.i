package com.fotile.x1i.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.setting.AwaitActivity;
import com.fotile.x1i.bean.event.EventAwaitMessage;
import com.fotile.x1i.bean.event.UpdateAwaitText;
import com.fotile.x1i.bean.event.UpdateInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
  * 文件名称：ItemAwaitView
  * 创建时间：2019/5/10 17:41
  * 文件作者：yaohx
  * 功能描述：待机画面的每一个元素item
  */
public class ItemAwaitView extends LinearLayout implements View.OnClickListener {
    private Context context;
    /**
     * 背景
     */
    ImageView imgBg;
    /**
     * 待机画面
     */
    ImageView imgSrc;
    /**
     * 待机类型
     */
    TextView txtType;
    /**
     * 待机画面索引
     */
    public int index;

    private int color_normal;
    private int color_pressed;

    public ItemAwaitView(Context context) {
        super(context);
        init(context);
    }

    public ItemAwaitView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        EventBus.getDefault().register(this);
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.item_await, this, true);

        color_normal = Color.parseColor("#ffffff");
        color_pressed = Color.parseColor("#C8AF70");

        imgBg = (ImageView) findViewById(R.id.img_item_await_bg);
        imgSrc = (ImageView) findViewById(R.id.img_item_await_src);
        txtType = (TextView) findViewById(R.id.txt_item_await_type);

        imgSrc.setOnClickListener(this);
        setChecked(false);
    }

    public void setData(int resId, String str, int index) {
        imgSrc.setImageResource(resId);
        txtType.setText(str);
        this.index = index;
        int default_index = PreferenceUtil.getScreenSaverType(context);
        if(default_index == index){
            setChecked(true);
        }
    }

    public void setChecked(boolean checked) {
        if (checked) {
            imgBg.setVisibility(View.VISIBLE);
            txtType.setTextColor(color_pressed);
        } else {
            imgBg.setVisibility(View.GONE);
            txtType.setTextColor(color_normal);
        }
    }

    //通知其他对象，setChecked(false)
    @Subscribe
    public void onEventMessage(EventAwaitMessage message) {
        int hashcode = message.hashCode;
        //自己通知自己被选中
        if (hashcode == hashCode()) {
            setChecked(true);
        } else {
            setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击背景
            case R.id.img_item_await_src:
                EventAwaitMessage message = new EventAwaitMessage(ItemAwaitView.class, hashCode());
                EventBus.getDefault().post(message);
                //保存屏保类型
                PreferenceUtil.saveScreenSaverType(context,index);
                int default_index = PreferenceUtil.getScreenSaverType(context);
                EventBus.getDefault().post(new UpdateAwaitText(default_index,AwaitActivity.class));
                break;
        }
    }
}
