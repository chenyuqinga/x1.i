package com.fotile.x1i.widget;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;


import java.util.ArrayList;

/**
 * Description
 * Created by chenqiao on 2016/8/26.
 */
public class WaveSmartView extends View {

    private Paint mPaint;
    private Path mPath;

    private int width, height;
    private float max_amplitude = 45f;

    private ArrayList<SinWave> waves1;
    private ArrayList<SinWave> waves2;

    private ObjectAnimator scrollAnimator;

    private int first_Color = Color.parseColor("#C8AF70");
    private int second_Color = Color.WHITE;

    public WaveSmartView(Context context) {
        this(context, null);
    }

    public WaveSmartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveSmartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPath = new Path();
        waves1 = new ArrayList<>();
        waves2 = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        generateLines();
    }

    private void generateLines() {
        int padding = 20;
        waves1.clear();
        waves2.clear();
        for (int i = 0; i < 5; i++) {
            SinWave wave = new SinWave();
            wave.center_X = padding + (width - padding * 2) / 6 * (1 + i);
            wave.length = (int) (width * 1.5f / 7);
            wave.max_amplitude = max_amplitude;
            wave.amplitude = (float) (max_amplitude * Math.random() + 5);
            wave.isUp = Math.random() >= 0.5f;
            waves1.add(wave);
        }
        padding = 40;
        for (int i = 0; i < 4; i++) {
            SinWave wave = new SinWave();
            wave.center_X = padding + (width - padding * 2) / 5 * (1 + i);
            wave.length = (int) (width * 1.5f / 6);
            wave.max_amplitude = max_amplitude;
            wave.isUp = Math.random() >= 0.5f;
            wave.amplitude = (float) (max_amplitude * Math.random() + 5);
            waves2.add(wave);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (scrollAnimator == null) {
            scrollAnimator = ObjectAnimator.ofInt(this, "scrollX", width, 0).setDuration(5000);
            scrollAnimator.start();
        }
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(first_Color);
        mPaint.setAlpha(80);
        mPaint.setStrokeWidth(2.0f);
        mPath.reset();
        for (SinWave sinWave : waves1) {
            sinWave.base_Y = height / 2 - sinWave.amplitude;
            sinWave.wave();
            for (int i = 0; i < sinWave.length; i++) {
                float x, y;
                x = sinWave.center_X - sinWave.length / 2 + i;
                y = (float) (sinWave.base_Y + sinWave.amplitude * Math.sin(0.5f * Math.PI + Math.PI * 2 * i / (1.0f *
                        sinWave.length)));
                if (i == 0) {
                    mPath.moveTo(x, y);
                } else {
                    mPath.lineTo(x, y);
                }
            }
            sinWave.base_Y = height / 2 + sinWave.amplitude;
            for (int i = 0; i < sinWave.length; i++) {
                float x, y;
                x = sinWave.center_X - sinWave.length / 2 + i;
                y = (float) (sinWave.base_Y + sinWave.amplitude * Math.sin(-0.5f * Math.PI + Math.PI * 2 * i / (1.0f
                        * sinWave.length)));
                if (i == 0) {
                    mPath.moveTo(x, y);
                } else {
                    mPath.lineTo(x, y);
                }
            }
        }
        canvas.drawPath(mPath, mPaint);

        mPath.reset();
        mPaint.setColor(second_Color);
        mPaint.setAlpha(80);
        for (SinWave sinWave : waves2) {
            sinWave.base_Y = height / 2 - sinWave.amplitude;
            sinWave.wave();
            for (int i = 0; i < sinWave.length; i++) {
                float x, y;
                x = sinWave.center_X - sinWave.length / 2 + i;
                y = (float) (sinWave.base_Y + sinWave.amplitude * Math.sin(0.5f * Math.PI + Math.PI * 2 * i / (1.0f *
                        sinWave.length)));
                if (i == 0) {
                    mPath.moveTo(x, y);
                } else {
                    mPath.lineTo(x, y);
                }
            }

            sinWave.base_Y = height / 2 + sinWave.amplitude;
            for (int i = 0; i < sinWave.length; i++) {
                float x, y;
                x = sinWave.center_X - sinWave.length / 2 + i;
                y = (float) (sinWave.base_Y + sinWave.amplitude * Math.sin(-0.5f * Math.PI + Math.PI * 2 * i / (1.0f
                        * sinWave.length)));
                if (i == 0) {
                    mPath.moveTo(x, y);
                } else {
                    mPath.lineTo(x, y);
                }
            }
        }
        canvas.drawPath(mPath, mPaint);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(4.0f);
        mPaint.setAlpha(100);
        canvas.drawLine(0, height / 2, width * 2, height / 2, mPaint);

        postInvalidateDelayed(15);
    }

    public int getFirst_Color() {
        return first_Color;
    }

    public void setFirst_Color(int first_Color) {
        this.first_Color = first_Color;
    }

    public int getSecond_Color() {
        return second_Color;
    }

    public void setSecond_Color(int second_Color) {
        this.second_Color = second_Color;
    }

    /**
     * 正常运行
     */
    public void translateToSecond() {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "first_Color", first_Color, first_Color).setDuration(500);
        animator.setEvaluator(new ArgbEvaluator());
        ObjectAnimator animator2 = ObjectAnimator.ofInt(this, "second_Color", second_Color, second_Color).setDuration
                (500);
        animator2.setEvaluator(new ArgbEvaluator());
        animator.start();
        animator2.start();
    }

    /**
     * 烟灶阻力大
     */
    public void translateToFirst() {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "second_Color", second_Color, second_Color).setDuration
                (500);
        animator.setEvaluator(new ArgbEvaluator());
        ObjectAnimator animator2 = ObjectAnimator.ofInt(this, "first_Color", first_Color, first_Color).setDuration(500);
        animator2.setEvaluator(new ArgbEvaluator());
        animator.start();
        animator2.start();
    }

    public static class SinWave {
        int length;
        float base_Y;
        float amplitude;
        float max_amplitude;
        float center_X;
        boolean isUp;
        WaveAnimator animator;

        public SinWave() {
        }

        public class WaveAnimator extends ValueAnimator {
            AccelerateDecelerateInterpolator acce;

            public WaveAnimator() {
                acce = new AccelerateDecelerateInterpolator();
                setFloatValues(0f, amplitude, 0f);
                setDuration((long) (500 * Math.random() + 1200));
                setInterpolator(acce);
                addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        amplitude = (float) animation.getAnimatedValue();
                    }
                });
                addListener(new AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        amplitude = (float) (max_amplitude * Math.random() + 5);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        }

        public void wave() {
            if (animator == null || !animator.isRunning()) {
                animator = new WaveAnimator();
                animator.start();
            }
        }
    }
}
