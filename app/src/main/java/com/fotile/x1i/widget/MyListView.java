package com.fotile.x1i.widget;

import android.content.Context;
import android.widget.ListView;

/**
 * 文件名称：MyListView
 * 创建时间：2017/10/20
 * 文件作者：zhangqin
 * 功能描述：在ScrollView中可滑动的ListView
 */


public class MyListView extends ListView
{

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, android.util.AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }

}
