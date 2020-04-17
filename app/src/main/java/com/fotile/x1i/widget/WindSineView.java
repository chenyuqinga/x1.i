package com.fotile.x1i.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by caixd on 2016/8/27.
 */
public class WindSineView extends View {
    private int sine_color = Color.parseColor("#F4C869");//颜色
    private int line_number = 4;

    private Path path;
    private Paint paint;
    private int viewWidth;
    private int viewHeight;
    private float[] yPos;
    private ArrayList<SineLine> lists;
    private float[] tempAmplitude;
    private float[] tempPeriods;
    private float[] tempOffset;
    private int level;

    public WindSineView(Context context) {
        this(context, null);
    }

    public WindSineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WindSineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();
        initView();
    }

    private void initView() {
        tempAmplitude = new float[line_number];
        tempPeriods = new float[line_number];
        tempOffset = new float[line_number];
        yPos = new float[viewWidth];

        lists = new ArrayList<>();
        for (int i = 0; i < line_number; i++) {
            SineLine line = new SineLine();
            line.amplitude = (float) (Math.random() * 1 + 1);//(1-2)*level
            line.periods = (float) (Math.random() * 2 + 1);//(1-3)*level+700
            line.width = (int) (Math.random() * 3) + 2;//2-5
            line.speed = (float) (Math.random() * 8 + 5);//10-20
            line.alpha = (int) (Math.random() * 200 + 50);//50-250
            lists.add(line);
            tempAmplitude[i] = line.amplitude;
            tempPeriods[i] = line.periods;
            tempOffset[i] = line.offset;
        }
        path = new Path();
        paint = new Paint();
    }

    /**
     * 更新振幅
     *
     * @param level 当前值
     * @param max   最大值
     */
    public void updateSine(int level, int max) {
        if (level <= 1) {
            level = 1;
        }
        this.level = (int) (100.f * level / max);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < line_number; i++) {
            lists.get(i).amplitude = tempAmplitude[i] * level;
            lists.get(i).periods = tempPeriods[i] * level + 700;

            paint.reset();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(lists.get(i).width);
            paint.setColor(sine_color);
            paint.setAntiAlias(true);
            for (int j = 0; j < viewWidth; j++) {
                yPos[j] = viewHeight / 2f - (float) (lists.get(i).amplitude * Math.sin((j - lists.get(i).offset) * 2 * Math.PI / lists.get(i).periods));
                if (j == 0) {
                    path.moveTo(j, yPos[j]);
                } else {
                    path.lineTo(j, yPos[j]);
                }
            }
            lists.get(i).offset = (int) ((lists.get(i).offset + lists.get(i).speed) % lists.get(i).periods);
            canvas.drawPath(path, paint);
            path.reset();
        }
        postInvalidateDelayed(15);
    }

    public static class SineLine {
        float amplitude;//振幅
        float periods;//周期
        int width;//曲线宽度
        float speed;//速度
        float offset;
        int alpha;
    }
}
