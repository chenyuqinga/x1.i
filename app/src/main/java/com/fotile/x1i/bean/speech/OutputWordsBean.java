package com.fotile.x1i.bean.speech;

public class OutputWordsBean extends SpeechContent {

    private String mOutputWords;

    public OutputWordsBean(String outputWords) {
        mType = TYPE_OUTPUT_WORDS;
        mOutputWords = outputWords;
    }

    public String getmOutputWords() {
        return mOutputWords;
    }

    public void setmOutputWords(String mOutputWords) {
        this.mOutputWords = mOutputWords;
    }
}
