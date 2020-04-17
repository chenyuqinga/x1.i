package com.fotile.x1i.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * 文件名称：MarqueeTextView
 * 创建时间：18-4-2 上午11:00
 * 文件作者：zhangqiang
 * 功能描述：设置跑马灯
 */
public class MarqueeTextView extends AppCompatTextView {

    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isFocused() {
        return true; //返回值为true,使得所有的TextView都能够获取到焦点.
    }

}
