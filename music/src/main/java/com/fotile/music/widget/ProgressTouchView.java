package com.fotile.music.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.fotile.common.util.log.LogUtil;
import com.fotile.music.R;

import java.math.BigDecimal;

/**
 * 项目名称：X1.I
 * 创建时间：2018/8/8 10:31
 * 文件作者：yaohx
 * 功能描述：调节音量、亮度view
 */
public class ProgressTouchView extends android.support.v7.widget.AppCompatImageView {

    private Paint paint = new Paint();

    /**
     * view宽度
     */
    private int view_width;
    /**
     * view高度
     */
    private int view_height;

    private float progress = 0.2f;
    /**
     * 画布圆角数组
     */
    private float[] radiusArray = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    /**
     * 用于裁剪画布的圆角路径
     */
    private Path path;

    public ProgressTouchView(Context context) {
        super(context);
        init(context);
    }

    public ProgressTouchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProgressTouchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#c8af70"));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //将画布裁剪为圆角
        canvas.clipPath(path);
        canvas.drawColor(getResources().getColor(R.color.progressView_grey));
        //画进度内容
        int content_height = (int) (progress * view_height);
        Rect rect = new Rect();
        rect.top = view_height - content_height;
        rect.left = 0;
        rect.right = view_width;
        rect.bottom = view_height;
        canvas.drawRect(rect, paint);

        super.onDraw(canvas);
    }


    int xDown;
    int yDown;
    int xUp;
    int yUp;

    public void notifyProgress(int dy) {
        //当前高度
        int current_height = (int) (progress * view_height);
        int target_height = current_height + dy;
        setProgress(divide(target_height, view_height));
        LogUtil.LOGE("sound-noprogress", divide(target_height, view_height));
    }


    /**
     * 计算两数相除
     *
     * @return
     */
    public static float divide(float x, float y) {
        if (y <= 0) {
            return 0;
        }
        BigDecimal bigDecimal1 = new BigDecimal(x);
        BigDecimal bigDecimal2 = new BigDecimal(y);
        return bigDecimal1.divide(bigDecimal2, 4, BigDecimal.ROUND_DOWN).floatValue();
    }

    /**
     * 设置显示的进度
     *
     * @param progress 0 - 1
     */
    public void setProgress(float progress) {
        if (progress <= 0) {
            progress = 0;
        }
        if (progress >= 1.0) {
            progress = 1.0f;
        }
        this.progress = progress;
        invalidate();
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
                LogUtil.LOGE("dy", dy);
                notifyProgress(dy);
                xDown = xUp;
                yDown = yUp;
                if (null != onProgressTouchListener) {
                    onProgressTouchListener.onTouchMove(progress);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (null != onProgressTouchListener) {
                    onProgressTouchListener.onTouchUp(progress);
                }
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        view_width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        view_height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(view_width, view_height);

        measureRoundPath();
    }

    /**
     * 计算圆角区域path
     */
    private void measureRoundPath() {
        int corner = (int) getResources().getDimension(R.dimen.progress_view_corner);
        radiusArray[0] = corner;
        radiusArray[1] = corner;
        radiusArray[2] = corner;
        radiusArray[3] = corner;
        radiusArray[4] = corner;
        radiusArray[5] = corner;
        radiusArray[6] = corner;
        radiusArray[7] = corner;

        RectF rectF = new RectF();
        rectF.top = 0;
        rectF.left = 0;
        rectF.right = view_width;
        rectF.bottom = view_height;
        path = new Path();
        path.addRoundRect(rectF, radiusArray, Path.Direction.CW);
    }


    //手指抬起和移动事件
    onProgressTouchListener onProgressTouchListener;

    public interface onProgressTouchListener {
        void onTouchMove(float progress);

        void onTouchUp(float progress);
    }

    public void setOnProgressTouchListener(ProgressTouchView.onProgressTouchListener onProgressTouchListener) {
        this.onProgressTouchListener = onProgressTouchListener;
    }
}
