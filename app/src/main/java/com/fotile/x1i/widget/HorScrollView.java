package com.fotile.x1i.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件名称：HorScrollView
 * 创建时间：2019/4/29 9:50
 * 文件作者：yaohx
 * 功能描述：水平切换滚动条
 */
public class HorScrollView extends LinearLayout {

    private Context context;
    private int color_normal;
    private int color_pressed;

    private int size_pressed = 36;
    private int size_normal = 30;

    private int width;
    private int height;
    /**
     * 当前显示的子item位置
     */
    private int position;
    /**
     * 每一个文本的宽度
     */
    private int item_width = 200;
    private int item_height = 50;
    private LayoutInflater inflater;

    private List<TextView> listTxt = new ArrayList<TextView>();
    private List<ImageView> listPoint = new ArrayList<ImageView>();

    private LinearLayout lineContent;

    private final int view_width = 1280;

    public HorScrollView(Context context) {
        super(context);
        init(context);
    }

    public HorScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 设置文本的宽度
     *
     * @param item_width
     */
    public void setItemWidth(int item_width) {
        this.item_width = item_width;
    }

    /**
     * 设置文本字体大小
     *
     * @param size_pressed
     * @param size_normal
     */
    public void setTextSize(int size_pressed, int size_normal) {
        this.size_pressed = size_pressed;
        this.size_normal = size_normal;
    }

    private void init(Context context) {
        this.context = context;
        color_pressed = Color.parseColor("#C8AF70");
        color_normal = Color.parseColor("#9A9A9A");
        inflater = LayoutInflater.from(context);

        lineContent = new LinearLayout(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams
                .WRAP_CONTENT);
        lineContent.setLayoutParams(lp);
        addView(lineContent);
    }

    public void setSelect(int position) {
        this.position = position;
        if (position >= 0 && position <= listTxt.size() - 1) {

            int start = view_width / 2 - (item_width / 2 + item_width * position);
            scrollTo(-start, 0);

            LogUtil.LOGE("=============setSelect",getScrollX());

            LogUtil.LOGE("=============getWinth",getWidth());

            for (int k = 0; k <= listTxt.size() - 1; k++) {
                TextView textView = listTxt.get(k);
                if (k == position) {
                    textView.setTextColor(color_pressed);
                    textView.setTextSize(size_pressed);
                } else {
                    textView.setTextColor(color_normal);
                    textView.setTextSize(size_normal);
                }
            }
        }
    }

    /**
     * 添加某一个文本元素
     *
     * @param text
     * @param index
     */
    public void addItemView(String text, final int index) {
        View view = inflater.inflate(R.layout.item_hor_txt, null);
        View parent = view.findViewById(R.id.parent);
        TextView txtView = (TextView) view.findViewById(R.id.txt);
        ImageView point = (ImageView) view.findViewById(R.id.point);
        point.setVisibility(View.GONE);

        txtView.setText(text);
        txtView.setTextSize(size_normal);
        txtView.setTextColor(color_normal);

        //设置整个item的宽度高度
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) parent.getLayoutParams();
        lp.width = item_width;
        lp.height = item_height;
        parent.setLayoutParams(lp);

        //更改content的宽度
        LinearLayout.LayoutParams content_lp = (LayoutParams) lineContent.getLayoutParams();
        content_lp.width = content_lp.width + item_width;
        lineContent.setLayoutParams(content_lp);

        lineContent.addView(view);
        listTxt.add(txtView);
        listPoint.add(point);

//        parent.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setSelect(index);
//                if (null != onHorItemClickListener) {
//                    onHorItemClickListener.onHorItemClick(index);
//                }
//            }
//        });
    }

    public void showRedPoint(int index) {

        if (index >= 0 && index <= listPoint.size() - 1) {
            ImageView imgPoint = listPoint.get(index);
            imgPoint.setVisibility(View.VISIBLE);
        }
    }

    public void hideRedPoint(int index) {
        if (index >= 0 && index <= listPoint.size() - 1) {
            ImageView imgPoint = listPoint.get(index);
            imgPoint.setVisibility(View.GONE);
        }


    }

    private int xDown;
    private int xDown_;
    private int xUp_;
    private int xMove;
    private long time_down;
    private long time_up;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = (int) event.getX();
                xDown_ = (int) event.getX();
                time_down = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = (int) event.getX();
                int dx = xMove - xDown;
                if (Math.abs(dx) > 15) {
                    handScroll(dx);
                    xDown = xMove;
                }
                break;
            case MotionEvent.ACTION_UP:
                xUp_ = (int) event.getX();
                time_up = System.currentTimeMillis();
                if (Math.abs(xUp_ - xDown_) < 10) {
                    if (time_up - time_down < 800) {
                        handClick();
                    }
                }
                break;
        }
        return true;
    }

    private void handClick() {

        int scroll_x = getScrollX();
        int length = 0;
        if (scroll_x < 0) {
            length = xUp_ - Math.abs(scroll_x);
        } else {
            length = xUp_ + Math.abs(scroll_x);
        }

        //计算index
        int index = length / item_width;
        setSelect(index);
        if (null != onHorItemClickListener) {
            onHorItemClickListener.onHorItemClick(index);
        }
    }

    private void handScroll(int dx) {
        int scroll_x = getScrollX();
        //item 总数叠加的view宽度
        int line_content_width = listTxt.size() * item_width;
        //已经滑动到最左边
        if (scroll_x < -(view_width / 2 - item_width / 2)) {
            return;
        }

        //已经滑动到最右边
        if (scroll_x > (line_content_width - item_width / 2 - view_width / 2)) {
            return;
        }

        //手指向左滑动为负，向右滑动为正
        float f = view_width * 1.0f / line_content_width * 1.0f;
        int view_move = (int) (dx * 1.0 / f);

        //最左边坐标
        int left_position = -(view_width / 2 - item_width / 2);
        //即将要滑动到最左边（手指向右）
        if (scroll_x + (-view_move) < left_position) {
            view_move = -(left_position - scroll_x);
        }

        //最右边坐标
        int right_position = line_content_width - item_width / 2 - view_width / 2;
        //即将要滑动到最右边（手指向左）
        if (scroll_x + (-view_move) > right_position) {
            view_move = -(right_position - scroll_x);
        }

        scrollBy(-view_move, 0);
    }


    public interface OnHorItemClickListener {
        void onHorItemClick(int index);
    }

    private OnHorItemClickListener onHorItemClickListener;

    public void setOnHorItemClickListener(OnHorItemClickListener onHorItemClickListener) {
        this.onHorItemClickListener = onHorItemClickListener;
    }
}
