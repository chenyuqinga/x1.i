package com.fotile.x1i.bean.speech;

public class ASRContentBean extends SpeechContent {

    private String mReceivedWords;

    public ASRContentBean(String receivedWords) {
        mType = TYPE_RECEIVED_WORDS;
        mReceivedWords = receivedWords;
    }

    public String getmReceivedWords() {
        return mReceivedWords;
    }

    public void setmReceivedWords(String mReceivedWords) {
        this.mReceivedWords = mReceivedWords;
    }
}
