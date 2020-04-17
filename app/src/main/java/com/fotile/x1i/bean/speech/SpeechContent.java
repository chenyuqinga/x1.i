package com.fotile.x1i.bean.speech;

public abstract class SpeechContent {

    public static final int TYPE_RECEIVED_WORDS = 21001;
    public static final int TYPE_OUTPUT_WORDS = 21002;
    public static final int TYPE_OUTPUT_MUSIC = 21003;
    public static final int TYPE_OUTPUT_VIDEO = 21004;
    public static final int TYPE_OUTPUT_SETTING = 21005;
    public static final int TYPE_OUTPUT_RECIPE = 21006;

    int mType;

    public int getType() {
        return mType;
    }
}
