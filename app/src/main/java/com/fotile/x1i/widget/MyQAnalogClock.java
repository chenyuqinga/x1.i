package com.fotile.x1i.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;


import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.R;

import java.util.Calendar;
import java.util.TimeZone;

public class MyQAnalogClock extends View {

    Bitmap mBmpDial;
    Bitmap mBmpHour;
    Bitmap mBmpMinute;
    Bitmap mBmpSecond;
    Bitmap mBmpCenter;

    BitmapDrawable bmdHour;
    BitmapDrawable bmdMinute;
    BitmapDrawable bmdSecond;
    BitmapDrawable bmdDial;
    BitmapDrawable bmdCenter;

    Paint mPaint;

    Handler tickHandler;

    int mWidth;
    int mHeight;
    int mTempWidth;
    int mTempHeight;
    int centerX;
    int centerY;

    private int mIndex;

    private String sTimeZoneString;

    public MyQAnalogClock(Context context, int index) {
        this(context, "GMT+8:00", index);

    }

    public MyQAnalogClock(Context context, String sTime_Zone, int index) {
        super(context);
        LogUtil.LOGD("", "index==" + index);
        mIndex = index;
        selectClock(index);

        sTimeZoneString = sTime_Zone;

        bmdHour = new BitmapDrawable(mBmpHour);

        bmdMinute = new BitmapDrawable(mBmpMinute);

        bmdSecond = new BitmapDrawable(mBmpSecond);

        mTempWidth = bmdSecond.getIntrinsicWidth();// 得到拉伸后的宽度
//        mTempHeight = bmdSecond.getIntrinsicHeight();

        bmdDial = new BitmapDrawable(mBmpDial);
        bmdCenter = new BitmapDrawable(mBmpCenter);
        mWidth = mBmpDial.getWidth();
        mHeight = mBmpDial.getHeight();
        Log.d("MyClock", "width==" + mWidth + "    height==" + mHeight);
        /*if (CookerProtocolManager.getInstance().isMicrowaveMachine()) {

			int availableWidth = 720;
			int availableHeight = 1280;
			centerX = availableWidth / 2;
			centerY = availableHeight / 2;
		} else {*/

        int availableWidth = 1280;
        int availableHeight = 800;
        centerX = availableWidth / 2;
        centerY = availableHeight / 2;
        //}

        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        run();
    }

    private void selectClock(int index) {
        switch (index) {
            //英伦
            case 0:
                mBmpHour = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_england_shi);
                mBmpMinute = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_england_fen);
                mBmpSecond = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_england_miao);
                mBmpDial = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_england);
                break;
            //商务
            case 1:
                mBmpHour = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_business_shi);
                mBmpMinute = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_business_fen);
                mBmpDial = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_business);
                break;
            //古典
            case 2:
                mBmpHour = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_classical_shi);
                mBmpMinute = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_classical_fen);
                //mBmpCenter = BitmapFactory.decodeResource(getResources(), R.mipmap.classical_centre);
                mBmpDial = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_classical);
                break;
            //现代
            case 3:
                mBmpHour = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_modern_shi);
                mBmpMinute = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_modern_fen);
                mBmpSecond = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_modern_miao);
                mBmpDial = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_modern);
                break;
            //罗马
            case 4:
                mBmpHour = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_roman_shi);
                mBmpMinute = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_roman_fen);
                mBmpSecond = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_roman_miao);
                mBmpDial = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_roman);
                break;
            //怀旧
            case 5:
                mBmpHour = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_fashionl_shi);
                mBmpMinute = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_fashionl_fen);
                mBmpSecond = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_fashionl_miao);
                mBmpDial = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_fashionl);
                break;
            //电子
            case 6:
                mBmpHour = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_luxurious_shi);
                mBmpMinute = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_luxurious_fen);
                mBmpSecond = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_luxurious_miao);
                mBmpDial = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_luxurious);
                //mBmpCenter = BitmapFactory.decodeResource(getResources(), R.mipmap.luxurious_centre);
                break;
            //无
            case 7:
                mBmpHour = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_fun_shi);
                mBmpMinute = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_fun_fen);
                mBmpSecond = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_fun_miao);
                mBmpDial = BitmapFactory.decodeResource(getResources(), R.mipmap.await_clock_fun);

                break;
        }

    }

    public void run() {
        tickHandler = new Handler();
        tickHandler.post(tickRunnable);
    }

    private Runnable tickRunnable = new Runnable() {
        public void run() {
            postInvalidate();
            tickHandler.postDelayed(tickRunnable, 1000);
        }
    };

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mIndex) {
            case 0:
                drawEngland(canvas);
                break;
            case 1:
                drawBusiness(canvas);
                break;
            case 2:
                drawClassial(canvas);
                break;
            case 3:
                drawModern(canvas);
                break;
            case 4:
                drawRoman(canvas);
                break;
            case 5:
                drawFashionl(canvas);
                break;
            case 6:
                drawLuxurious(canvas);
                break;
            case 7:
//                drawFun(canvas);

                break;
        }
    }

    private void drawFashionl(Canvas canvas) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(sTimeZoneString));
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        float hourRotate = hour * 30.0f + minute / 60.0f * 30.0f + 120f - 120f;
        float minuteRotate = minute * 6.0f - 11 * 6.0f + 66f;
        float secondRotate = second * 6.0f - 30 * 6.0f;

        bmdDial.setBounds(centerX - (mWidth / 2), centerY - (mHeight / 2), centerX + (mWidth / 2), centerY + (mHeight
                / 2));
        bmdDial.draw(canvas);
        mTempWidth = bmdHour.getIntrinsicWidth();
        mTempHeight = bmdHour.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(hourRotate, centerX, centerY);
        bmdHour.setBounds(centerX - (mTempWidth / 2), centerY - (mTempHeight * 2 / 3), centerX + (mTempWidth / 2),
                centerY + (mTempHeight / 3));
        bmdHour.draw(canvas);

        canvas.restore();

        mTempWidth = bmdMinute.getIntrinsicWidth();
        mTempHeight = bmdMinute.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(minuteRotate, centerX, centerY);
        bmdMinute.setBounds(centerX - (mTempWidth / 2), centerY - (mTempHeight * 2 / 3 + 20), centerX + (mTempWidth /
                2), centerY + (mTempHeight / 3 - 20));
        bmdMinute.draw(canvas);

        canvas.restore();

        mTempWidth = bmdSecond.getIntrinsicWidth();
        mTempHeight = bmdSecond.getIntrinsicHeight();
        canvas.rotate(secondRotate, centerX, centerY);
        bmdSecond.setBounds(centerX - (mTempWidth / 2), centerY - (mTempHeight / 4) + 11, centerX + (mTempWidth / 2),
                centerY + (mTempHeight * 3 / 4) + 11);
        bmdSecond.draw(canvas);

    }

    private void drawBusiness(Canvas canvas) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(sTimeZoneString));
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        float hourRotate = hour * 30.0f + minute / 60.0f * 30.0f + 120f - 120f;
        float minuteRotate = minute * 6.0f - 11 * 6.0f + 66f;

        bmdDial.setBounds(centerX - (mWidth / 2), centerY - (mHeight / 2), centerX + (mWidth / 2), centerY + (mHeight
                / 2));
        bmdDial.draw(canvas);
        mTempWidth = bmdHour.getIntrinsicWidth();
        mTempHeight = bmdHour.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(hourRotate, centerX, centerY);
        bmdHour.setBounds(centerX - (mTempWidth / 2), centerY - (mTempHeight + 20), centerX + (mTempWidth / 2),
                centerY + 30);
        bmdHour.draw(canvas);

        canvas.restore();

        mTempWidth = bmdMinute.getIntrinsicWidth();
        mTempHeight = bmdMinute.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(minuteRotate, centerX, centerY);
        bmdMinute.setBounds(centerX - (mTempWidth / 2), centerY - (mTempHeight + 20), centerX + (mTempWidth / 2),
                centerY + 30);
        bmdMinute.draw(canvas);

        canvas.restore();

    }

    private void drawClassial(Canvas canvas) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(sTimeZoneString));
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        float hourRotate = hour * 30.0f + minute / 60.0f * 30.0f + 120f - 120f;
        float minuteRotate = minute * 6.0f - 11 * 6.0f + 66f;

        bmdDial.setBounds(centerX - (mWidth / 2), centerY - (mHeight / 2), centerX + (mWidth / 2), centerY + (mHeight
                / 2));
        bmdDial.draw(canvas);

        mTempWidth = bmdHour.getIntrinsicWidth();
        mTempHeight = bmdHour.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(hourRotate, centerX, centerY);
        bmdHour.setBounds(centerX - (mTempWidth / 2), centerY - mTempHeight + 25, centerX + (mTempWidth / 2), centerY
                + 25);
        bmdHour.draw(canvas);

        canvas.restore();

        mTempWidth = bmdMinute.getIntrinsicWidth();
        mTempHeight = bmdMinute.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(minuteRotate, centerX, centerY);
        bmdMinute.setBounds(centerX - (mTempWidth / 2), centerY - mTempHeight + 25, centerX + (mTempWidth / 2),
                centerY + 25);
        bmdMinute.draw(canvas);

        canvas.restore();

        mTempWidth = bmdCenter.getIntrinsicWidth();
        mTempHeight = bmdCenter.getIntrinsicHeight();

        bmdCenter.setBounds(centerX - (mTempWidth / 2), centerY - (mTempHeight / 2), centerX + (mTempWidth / 2),
                centerY + (mTempHeight / 2));
        bmdCenter.draw(canvas);

    }

    private void drawEngland(Canvas canvas) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(sTimeZoneString));
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        float hourRotate = hour * 30.0f + minute / 60.0f * 30.0f + 120f - 120f;
        float minuteRotate = minute * 6.0f - 11 * 6.0f + 66f;
        float secondRotate = second * 6.0f - 30 * 6.0f;

        bmdDial.setBounds(centerX - (mWidth / 2), centerY - (mHeight / 2), centerX + (mWidth / 2), centerY + (mHeight
                / 2));
        bmdDial.draw(canvas);
        mTempWidth = bmdHour.getIntrinsicWidth();
        mTempHeight = bmdHour.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(hourRotate, centerX, centerY);
        bmdHour.setBounds(centerX - (mTempWidth / 2), centerY - (mTempHeight) - 40, centerX + (mTempWidth / 2),
                centerY + (mTempHeight / 2 - 40));
        bmdHour.draw(canvas);

        canvas.restore();

        mTempWidth = bmdMinute.getIntrinsicWidth();
        mTempHeight = bmdMinute.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(minuteRotate, centerX, centerY);
        bmdMinute.setBounds(centerX - 10, centerY - mTempHeight - 30, centerX + (mTempWidth - 10), centerY + 10);
        bmdMinute.draw(canvas);

        canvas.restore();

        mTempWidth = bmdSecond.getIntrinsicWidth();
        mTempHeight = bmdSecond.getIntrinsicHeight();
        canvas.rotate(secondRotate, centerX, centerY);
        bmdSecond.setBounds(centerX - (mTempWidth / 2), centerY - (mTempHeight / 4) - 17, centerX + (mTempWidth / 2),
                centerY + (mTempHeight));
        bmdSecond.draw(canvas);

    }

    private void drawFun(Canvas canvas) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(sTimeZoneString));
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        float hourRotate = hour * 30.0f + minute / 60.0f * 30.0f + 120f - 120f;
        float minuteRotate = minute * 6.0f - 11 * 6.0f + 66f;
        float secondRotate = second * 6.0f - 30 * 6.0f - 180f;

        bmdDial.setBounds(centerX - (mWidth / 2), centerY - (mHeight / 2), centerX + (mWidth / 2), centerY + (mHeight
                / 2));
        bmdDial.draw(canvas);
        mTempWidth = bmdHour.getIntrinsicWidth();
        mTempHeight = bmdHour.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(hourRotate, centerX, centerY);
        bmdHour.setBounds(centerX - (mTempWidth / 2), centerY - (mTempHeight + 50), centerX + (mTempWidth / 2),
                centerY - 40);
        bmdHour.draw(canvas);

        canvas.restore();

        mTempWidth = bmdMinute.getIntrinsicWidth();
        mTempHeight = bmdMinute.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(minuteRotate, centerX, centerY);
        bmdMinute.setBounds(centerX - (mTempWidth / 2), centerY - (mTempHeight + 80), centerX + (mTempWidth / 2),
                centerY - 40);
        bmdMinute.draw(canvas);

        canvas.restore();

        mTempWidth = bmdSecond.getIntrinsicWidth();
        mTempHeight = bmdSecond.getIntrinsicHeight();
        canvas.rotate(secondRotate, centerX, centerY);
        bmdSecond.setBounds(centerX - (mTempWidth / 2), centerY - (mTempHeight + 90), centerX + (mTempWidth / 2),
                centerY - 40);
        bmdSecond.draw(canvas);

    }

    private void drawLuxurious(Canvas canvas) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(sTimeZoneString));
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        float hourRotate = hour * 30.0f + minute / 60.0f * 30.0f + 120f - 120f;
        float minuteRotate = minute * 6.0f - 11 * 6.0f + 67f;
        float secondRotate = second * 6.0f - 30 * 6.0f - 180f;

        bmdDial.setBounds(centerX - (mWidth / 2), centerY - (mHeight / 2), centerX + (mWidth / 2), centerY + (mHeight
                / 2));
        bmdDial.draw(canvas);

        mTempWidth = bmdHour.getIntrinsicWidth();
        mTempHeight = bmdHour.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(hourRotate, centerX, centerY);
        bmdHour.setBounds(centerX - (mTempWidth / 2 + 3), centerY - (mTempHeight - 10), centerX + (mTempWidth / 2 +
                3), centerY + 30);
        bmdHour.draw(canvas);

        canvas.restore();

        mTempWidth = bmdMinute.getIntrinsicWidth();
        mTempHeight = bmdMinute.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(minuteRotate, centerX, centerY);
        bmdMinute.setBounds(centerX - (mTempWidth / 2 + 3), centerY - (mTempHeight + 27), centerX + (mTempWidth / 2 +
                3), centerY + 32);
        bmdMinute.draw(canvas);

        canvas.restore();

        mTempWidth = bmdSecond.getIntrinsicWidth();
        mTempHeight = bmdSecond.getIntrinsicHeight();
        canvas.rotate(secondRotate, centerX, centerY);
        bmdSecond.setBounds(centerX - (mTempWidth / 2 + 3), centerY - (mTempHeight + 23), centerX + (mTempWidth / 2 +
                3), centerY + 65);
        bmdSecond.draw(canvas);

        mTempWidth = bmdCenter.getIntrinsicWidth();
        mTempHeight = bmdCenter.getIntrinsicHeight();
        canvas.save();
        bmdCenter.setBounds(centerX - (mTempWidth / 2 + 6), centerY - (mTempHeight / 2 + 6), centerX + (mTempWidth /
                2 + 6), centerY + (mTempHeight / 2 + 6));
        bmdCenter.draw(canvas);
        canvas.restore();
    }

    private void drawRoman(Canvas canvas) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(sTimeZoneString));
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        float hourRotate = hour * 30.0f + minute / 60.0f * 30.0f + 120f - 120f;
        float minuteRotate = minute * 6.0f - 11 * 6.0f + 66f;
        float secondRotate = second * 6.0f - 30 * 6.0f;

        bmdDial.setBounds(centerX - (mWidth / 2), centerY - (mHeight / 2), centerX + (mWidth / 2), centerY + (mHeight
                / 2));
        bmdDial.draw(canvas);
        mTempWidth = bmdHour.getIntrinsicWidth();
        mTempHeight = bmdHour.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(hourRotate, centerX, centerY);
        bmdHour.setBounds(centerX - (mTempWidth / 2 + 5), centerY - (mTempHeight + 10), centerX + (mTempWidth / 2 +
                5), centerY + 23);
        bmdHour.draw(canvas);

        canvas.restore();

        mTempWidth = bmdMinute.getIntrinsicWidth();
        mTempHeight = bmdMinute.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(minuteRotate, centerX, centerY);
        bmdMinute.setBounds(centerX - (mTempWidth / 2 + 5), centerY - (mTempHeight + 30), centerX + (mTempWidth / 2 +
                5), centerY + 23);
        bmdMinute.draw(canvas);

        canvas.restore();

        mTempWidth = bmdSecond.getIntrinsicWidth();
        mTempHeight = bmdSecond.getIntrinsicHeight();
        canvas.rotate(secondRotate, centerX, centerY);
        bmdSecond.setBounds(centerX - (mTempWidth / 2 + 3), centerY - mTempHeight - 10, centerX + (mTempWidth / 2 +
                3), centerY + 50);
        bmdSecond.draw(canvas);

    }

    private void drawModern(Canvas canvas) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(sTimeZoneString));
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        float hourRotate = hour * 30.0f + minute / 60.0f * 30.0f + 300f - 120f;
        float minuteRotate = minute * 6.0f - 11 * 6.0f - 114f;
        float secondRotate = second * 6.0f - 30 * 6.0f - 180f;

        bmdDial.setBounds(centerX - (mWidth / 2), centerY - (mHeight / 2), centerX + (mWidth / 2), centerY + (mHeight
                / 2));
        bmdDial.draw(canvas);
        mTempWidth = bmdHour.getIntrinsicWidth();
        mTempHeight = bmdHour.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(hourRotate, centerX, centerY);
        bmdHour.setBounds(centerX - (mTempWidth), centerY - (30), centerX + (mTempWidth / 2), centerY + mTempHeight +
                10);
        bmdHour.draw(canvas);

        canvas.restore();

        mTempWidth = bmdMinute.getIntrinsicWidth();
        mTempHeight = bmdMinute.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(minuteRotate, centerX, centerY);
        bmdMinute.setBounds(centerX - (mTempWidth), centerY - (30), centerX + (mTempWidth / 2), centerY + mTempHeight
                + 10);
        bmdMinute.draw(canvas);

        canvas.restore();

        mTempWidth = bmdSecond.getIntrinsicWidth();
        mTempHeight = bmdSecond.getIntrinsicHeight();
        canvas.rotate(secondRotate, centerX, centerY);
        bmdSecond.setBounds(centerX - (mTempWidth / 2), centerY - mTempHeight + 10, centerX + (mTempWidth / 2),
                centerY + 30);
        bmdSecond.draw(canvas);

    }

    private void drawGirl(Canvas canvas) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(sTimeZoneString));
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        float hourRotate = hour * 30.0f + minute / 60.0f * 30.0f + 171f - 120f;
        float minuteRotate = minute * 6.0f - 11 * 6.0f + 16f;
        float secondRotate = second * 6.0f - 30 * 6.0f;

        bmdDial.setBounds(centerX - (mWidth / 2), centerY - (mHeight / 2), centerX + (mWidth / 2), centerY + (mHeight
                / 2));
        bmdDial.draw(canvas);
        mTempWidth = bmdHour.getIntrinsicWidth();
        mTempHeight = bmdHour.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(hourRotate, centerX, centerY);
        bmdHour.setBounds(centerX - (mTempWidth), centerY - (mTempHeight), centerX + (mTempWidth / 2), centerY +
                (mTempHeight / 2));
        bmdHour.draw(canvas);

        canvas.restore();

        mTempWidth = bmdMinute.getIntrinsicWidth();
        mTempHeight = bmdMinute.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(minuteRotate, centerX, centerY);
        canvas.translate(100, -20);
        bmdMinute.setBounds(centerX - (mTempWidth), centerY - (mTempHeight), centerX + (mTempWidth / 2), centerY +
                (mTempHeight / 2));
        bmdMinute.draw(canvas);

        canvas.restore();

        mTempWidth = bmdSecond.getIntrinsicWidth();
        mTempHeight = bmdSecond.getIntrinsicHeight();
        canvas.rotate(secondRotate, centerX, centerY);
        bmdSecond.setBounds(centerX - (mTempWidth / 2), centerY - (mTempHeight / 4), centerX + (mTempWidth / 2),
                centerY + (mTempHeight));
        bmdSecond.draw(canvas);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBmpDial != null) {
            mBmpDial.recycle();
            mBmpDial = null;
        }
        if (mBmpHour != null) {
            mBmpHour.recycle();
            mBmpHour = null;
        }
        if (mBmpMinute != null) {
            mBmpMinute.recycle();
            mBmpMinute = null;
        }
        if (mBmpSecond != null) {
            mBmpSecond.recycle();
            mBmpSecond = null;
        }
        if (mBmpCenter != null) {
            mBmpCenter.recycle();
            mBmpCenter = null;
        }
    }

}
