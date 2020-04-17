package com.fotile.ota.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 文件名称：ProgressView
 * 创建时间：2018/10/12 10:07
 * 文件作者：yaohx
 * 功能描述：下载进度
 */
public class ProgressView extends ImageView {

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
     * 初始进度
     */
    private float progress = 0.005f;

    public ProgressView(Context context) {
        super(context);
        init(context);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#C8AF70"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画进度内容
        int content_width = (int) (progress * view_width);

        RectF rectF = new RectF();
        rectF.top = 0;
        rectF.left = 0;
        rectF.right = content_width;
        rectF.bottom = view_height;
        //圆角半径
        canvas.drawRoundRect(rectF, 8, 8, paint);
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        view_width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        view_height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(view_width, view_height);
    }
}
