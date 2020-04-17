package com.fotile.x1i.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;


public class ProgressCircle extends View {

    /**
     * 进度条最大值，值越大越平滑
     */
    private int maxValue = 10 * 1000;
    /**
     * 圆环初始进度值，比如说圆环从10开始绘制
     */
    private int preValue = 0;
    /**
     * 当前进度値 （preValue + progress）
     */
    private int currentValue = 0;
    /**
     * 角速度   alphaAngle=(currentValue/maxValue)*360
     */
    private float alphaAngle;

    private Paint textPaint = new Paint();
    private Paint circlePaint = new Paint();
    /**
     * 动画运行时长
     */
    private int duration;
    private String txtMinute = "";

    private OnFinishListener listener;
    /**
     * 外环圆环颜色
     */
    private int colorOuter = Color.parseColor("#C8AF70");
    /**
     * 内环圆环颜色
     */
    private int colorInner = Color.parseColor("#44FFFFFF");

    /**
     * 内环宽度
     */
    private int innerWidth;
    /**
     * 外环宽度
     */
    private int outerWidth;
    /**
     * 圆环半径
     */
    private int radius;
    /**
     * 中心店坐标
     */
    private int center;

    public ProgressCircle(Context context) {
        super(context);
        init();
    }

    public ProgressCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        innerWidth = 4;
        outerWidth = 8;

        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);

        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (center == 0) {
            center = this.getWidth() / 2;
            radius = center - outerWidth / 2;
        }

        //绘制进度圆弧
        drawCircle(canvas, center, radius);
        drawText(canvas, center);
    }

    /**
     * 绘制进度圆弧
     *
     * @param center 圆心的x和y坐标
     */
    private void drawCircle(Canvas canvas, int center, int radius) {
        //先画内环
        circlePaint.setShader(null);
        circlePaint.setColor(colorInner);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(innerWidth);
        canvas.drawCircle(center, center, radius, circlePaint);

        //再画外环
        circlePaint.setStrokeWidth(outerWidth);
        RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius); // 圆的外接正方形
       // circlePaint.setShadowLayer(10, 10, 10, colorOuter);
        circlePaint.setColor(colorOuter);//设置圆弧的颜色
        circlePaint.setStrokeCap(Paint.Cap.ROUND);//把每段圆弧改成圆角
        alphaAngle = (currentValue + preValue) * 360.0f / maxValue * 1.0f;
        // 计算每次画圆弧时扫过的角度，这里计算要注意分母要转为float类型，否则alphaAngle永远为0
        canvas.drawArc(oval, -90, alphaAngle, false, circlePaint);
    }

    private void drawText(Canvas canvas, int center) {
        String percent = txtMinute;
        textPaint.setTextSize(30);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStrokeWidth(0);// 注意此处一定要重新设置宽度为0,否则绘制的文字会重叠
        Rect bounds = new Rect(); //文字边框
        textPaint.getTextBounds(percent, 0, percent.length(), bounds);//获得绘制文字的边界矩形
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = center + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        canvas.drawText(percent, center, baseline, textPaint);
        if (maxValue == (currentValue + preValue)) {
            if (listener != null) {
                listener.onFinish();
            }
        }
    }

    public interface OnFinishListener {
        void onFinish();
    }

    public void setCenterTxt(String txtMinute){
        this.txtMinute = txtMinute;
    }

    /**
     * 动画从initValue处开始绘制
     *
     * @param preValue   动画已经走完的时间 ms
     * @param maxValue   动画全部时间 ms
     * @param txtMinute
     * @param listener
     */
    public void setDuration2(int preValue, final int maxValue, String txtMinute, OnFinishListener listener) {
        this.listener = listener;
        this.txtMinute = txtMinute;

        this.preValue = preValue;
        this.maxValue = maxValue;
        currentValue = 0;

        this.duration =  (maxValue) + 500;
        ValueAnimator animator = ValueAnimator.ofInt(0, this.maxValue);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentValue = (int) animation.getAnimatedValue();
                //一般只是希望在View发生改变时对UI进行重绘。invalidate()方法系统会自动调用 View的onDraw()方法。
                invalidate();
            }
        });
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);
        animator.start();
    }


}
