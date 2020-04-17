package com.fotile.x1i.widget.setting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * 文件名称：WifiListView
 * 创建时间：2017-12-14 13:02
 * 文件作者：shihuijuan
 * 功能描述：自定义wifi列表
 */

public class WifiListView extends ListView{

    /**
     * 是否跳转至第一项
     */
    private boolean isToFirst;
    /**
     * 是否可以刷新列表
     */
    private boolean canRefresh = true;

    public WifiListView(Context context) {
        super(context);
    }

    public WifiListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WifiListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void handleDataChanged() {
        super.handleDataChanged();
        if (isToFirst){
            setSelection(0);
            isToFirst = false;
        }
    }

    /**
     * 更新列表选中项
     * @param isToFirst
     */
    public void updateSelection(boolean isToFirst){
        this.isToFirst = isToFirst;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                canRefresh = false;
                break;
            case MotionEvent.ACTION_UP:
                canRefresh = true;
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 获取列表是否可以刷新
     * @return
     */
    public boolean isCanRefresh() {
        return canRefresh || isToFirst;
    }
}
