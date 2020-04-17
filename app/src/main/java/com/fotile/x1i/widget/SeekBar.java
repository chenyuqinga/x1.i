package com.fotile.x1i.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.fotile.x1i.R;

import java.math.BigDecimal;

/**
 * 文件名称：SeekBar
 * 创建时间：2019/8/13 15:54
 * 文件作者：yaohx
 * 功能描述：SeekBar
 */
public class SeekBar extends android.support.v7.widget.AppCompatImageView {

    private Paint paint = new Paint();

    /**
     * view宽度
     */
    private int view_width;
    /**
     * view高度
     */
    private int view_height;
    /**
     * 进度条部分高度
     */
    private int seek_height;
    private int seek_width;
    /**
     * 0-1
     */
    private float percent = 0.0f;
    /**
     * 画布圆角数组
     */
    private float[] radiusArray = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    /**
     * 用于裁剪画布的圆角路径
     */
    private Path path;

    private int color_default = Color.parseColor("#80FFFFFF");
    private int color_pressed = Color.parseColor("#FFC8AF70");

    private Bitmap bitmap = null;
    private int bitmap_width;
    private int bitmap_height;

    private int max = 100;

    public SeekBar(Context context) {
        super(context);
        init(context);
    }

    public SeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint.setAntiAlias(true);
        paint.setColor(color_pressed);
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.seekbar_thumb);
        bitmap_width = bitmap.getWidth();
        bitmap_height = bitmap.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.save();
        //将画布裁剪为圆角
        canvas.clipPath(path);
        canvas.drawColor(color_default);
        //画进度内容
        int content_width = (int) (percent * seek_width);
        Rect rect = new Rect();
        rect.top = (view_height - seek_height) / 2;
//        rect.left = 0;
//        rect.right = content_width;
        rect.left = (view_width - seek_width) / 2;
        rect.right = content_width;
        rect.bottom = (view_height - seek_height) / 2 + seek_height;
        canvas.drawRect(rect, paint);
        canvas.restore();

        drawBitmap(canvas);
        super.onDraw(canvas);
    }


    int xDown;
    int yDown;
    int xUp;
    int yUp;

    private void notifyProgress(int dx) {
        //当前高度
        int content_width = (int) (percent * seek_width);
        int target_width = content_width + dx;
        setPercent(divide(target_width, seek_width));
    }


    /**
     * 计算两数相除
     *
     * @return
     */
    private static float divide(float x, float y) {
        if (y <= 0) {
            return 0;
        }
        BigDecimal bigDecimal1 = new BigDecimal(x);
        BigDecimal bigDecimal2 = new BigDecimal(y);
        return bigDecimal1.divide(bigDecimal2, 4, BigDecimal.ROUND_DOWN).floatValue();
    }

    /**
     * 设置显示的进度百分比
     *
     * @param percent 0 - 1
     */
    public void setPercent(float percent) {
        if (percent <= 0) {
            percent = 0;
        }
        if (percent >= 1.0) {
            percent = 1.0f;
        }
        this.percent = percent;
        invalidate();
        if (null != onProgressTouchListener) {
            onProgressTouchListener.onProgressChanged((int) (percent * max));
        }
    }

    /**
     * 设置进度条显示进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        if (progress <= 0) {
            progress = 0;
        }
        if (progress >= max) {
            progress = max;
        }
        setPercent(divide(progress, max));
    }

    /**
     * 获取进度百分比
     *
     * @return
     */
    public float getPercent() {
        return percent;
    }

    /**
     * 获取progress 0-100
     *
     * @return
     */
    public int getProgress() {
        return (int) (percent * max);
    }

    public void setMax(int max){
        this.max = max;
    }

    private void drawBitmap(Canvas canvas) {
        //seekbar滑条的进度
        int content_width = (int) (percent * seek_width);

        int top = (view_height - bitmap_height) / 2;
        //bitmap距离view起点距离
        int left = content_width + (view_width - seek_width) / 2 - bitmap_width / 2;
        if (left < 0) {
            left = 0;
        }
        if (left > (view_width - bitmap_width)) {
            left = view_width - bitmap_width;
        }

        canvas.drawBitmap(bitmap, left, top, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = (int) event.getX();
                yDown = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                xUp = (int) event.getX();
                yUp = (int) event.getY();

                int dx = xUp - xDown;
                int dy = -(yUp - yDown);
                notifyProgress(dx);
                xDown = xUp;
                yDown = yUp;
                break;
            case MotionEvent.ACTION_UP:
                if (null != onProgressTouchListener) {
                    onProgressTouchListener.onProgressStop((int) (percent * max));
                }
                break;
        }
        return true;
    }

    public int getMax() {
        return max;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        view_width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        view_height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        seek_height = 16;
        seek_width = view_width - 10 * 2;
        setMeasuredDimension(view_width, view_height);

        measureRoundPath();
    }

    /**
     * 计算圆角区域path
     */
    private void measureRoundPath() {
        int corner = 8;
        radiusArray[0] = corner;
        radiusArray[1] = corner;
        radiusArray[2] = corner;
        radiusArray[3] = corner;
        radiusArray[4] = corner;
        radiusArray[5] = corner;
        radiusArray[6] = corner;
        radiusArray[7] = corner;

        RectF rectF = new RectF();
        rectF.top = (view_height - seek_height) / 2;
        rectF.left = (view_width - seek_width) / 2;
        rectF.right = view_width - ((view_width - seek_width) / 2);
        rectF.bottom = (view_height - seek_height) / 2 + seek_height;
        path = new Path();
        path.addRoundRect(rectF, radiusArray, Path.Direction.CW);
    }


    //手指抬起和移动事件
    OnSeekBarChangeListener onProgressTouchListener;

    public interface OnSeekBarChangeListener {
        //progress 0 - 100 (进度改变就会回调)
        void onProgressChanged(int progress);

        //progress 0 - 100 (手指抬起时回调)
        void onProgressStop(int progress);
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onProgressTouchListener) {
        this.onProgressTouchListener = onProgressTouchListener;
    }
}
