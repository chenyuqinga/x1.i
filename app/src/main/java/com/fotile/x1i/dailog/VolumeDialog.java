package com.fotile.x1i.dailog;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.fotile.x1i.R;
import com.fotile.x1i.widget.TopBar;

/**
 * 文件名称：VolumeDialog
 * 创建时间：2018/11/5 16:18
 * 文件作者：chenyqi
 * 功能描述：
 */

public class VolumeDialog extends Dialog {
    private static final String TAG = VolumeDialog.class.getSimpleName();
    /**
     * 加大音量
     */
    ImageView soundPlus;
    /**
     * 减小音量
     */
    ImageView soundMinus;
    /**
     * 音量显示
     */
    //    public ThinBoldTextView txtVolume;
    /**
     * 音量调节
     */
    SeekBar mVolumeControlSb;
    /**
     * 音量manager
     */
    private AudioManager audioManager;

    private Context context;
    private int mMaxVolume;
    private int mCurrentVolume;

    public VolumeDialog(Context context) {
        super(context, R.style.CommonDialog);
        Log.e(TAG, "constructor");
        this.context = context;
    }

//    @Override
//    public int getLayoutId() {
//        Log.e(TAG, "get layout id");
//        return R.layout.dialog_sound_layout;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        init();
        initView();
    }

    private void init() {
//        EventBus.getDefault().register(this);
        Log.e(TAG, "init");
        setContentView(R.layout.dialog_sound_layout);
        //        Window win = getWindow();
        //        WindowManager.LayoutParams lp = win.getAttributes();
        //        lp.height = 190;
        //        lp.width = 848;
        //        lp.gravity = Gravity.CENTER;
        //        win.setAttributes(lp);
    }

    private void initView() {
        Log.e(TAG, "init view start");
        soundPlus = (ImageView) findViewById(R.id.img_sound_plus);
        soundMinus = (ImageView) findViewById(R.id.img_sound_minus);
        //        txtVolume = (ThinBoldTextView) findViewById(R.id.img_volume);
        mVolumeControlSb = (SeekBar) findViewById(R.id.sb_volume_dialog);
        findViewById(R.id.iv_volume_dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //消费掉整个布局的点击退出对话框事件
        soundMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        soundPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mVolumeControlSb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mCurrentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (mCurrentVolume * 10 == 0) {
            soundMinus.setImageResource(R.mipmap.img_sound_zero);
        } else {
            soundMinus.setImageResource(R.mipmap.img_sound_plus);
        }
        mVolumeControlSb.setOnSeekBarChangeListener(onVolumeChangeListener);
        mVolumeControlSb.setMax(mMaxVolume);
        //设置seekbar 上一次的媒体音量
        mVolumeControlSb.setProgress(mCurrentVolume);
        Log.e(TAG, "init view end");
    }

    private SeekBar.OnSeekBarChangeListener onVolumeChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress * 100 == 0) {
                soundMinus.setImageResource(R.mipmap.img_sound_zero);
            } else {
                soundMinus.setImageResource(R.mipmap.img_sound_minus);
            }
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            TopBar.getInstance(context).setVolmeState();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    //    @Override
    //    public void show() {
    //        super.show();
    //        EventBus.getDefault().post(new DockMessage(DockMessage.DOCK_STATE_BAR_HIDE));
    //        EventBus.getDefault().post(new DockMessage(DockMessage.DOCK_BOTTOM_HIDE));
    //        //设置全屏
    //        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
    //        getWindow().setWindowAnimations(R.style.dialog_anim_style);
    //        getWindow().setAttributes(layoutParams);
    //    }
    //
    //    @Override
    //    public void dismiss() {
    //        super.dismiss();
    //        EventBus.getDefault().post(new DockMessage(DockMessage.DOCK_STATE_BAR_SHOW));
    //        EventBus.getDefault().post(new DockMessage(DockMessage.DOCK_BOTTOM_SHOW));
    //    }
}
