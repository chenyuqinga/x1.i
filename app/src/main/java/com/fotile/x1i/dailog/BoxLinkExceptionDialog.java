package com.fotile.x1i.dailog;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fotile.common.util.Tool;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseDialog;
import com.fotile.x1i.server.screensaver.ScreenTool;

import butterknife.BindView;

public class BoxLinkExceptionDialog extends BaseDialog implements View.OnClickListener {

    private static final String TAG = BoxLinkExceptionDialog.class.getSimpleName();

    @BindView(R.id.tv_link_exception_title)
    TextView mTitleTv;

    @BindView(R.id.tv_link_exception_content)
    TextView mContentTv;

    @BindView(R.id.tv_top_button)
    TextView mTopBtn;

    @BindView(R.id.tv_bottom_button)
    TextView mBottomBtn;

    @BindView(R.id.tv_guide_title_exception)
    TextView mGuideTitleTv;

    private Builder mBuilder;
    private Context mContext;
    private BtnClickListener mListener;

    private BoxLinkExceptionDialog(Builder builder, @NonNull Context context, BtnClickListener listener) {
//        super(context, R.style.WiFiDialog);
        super(context, R.style.FullScreenDialog);
        Log.e("BoxLinkExceptionDialog", "constructor");
        mBuilder = builder;
        mContext = context;
        mListener = listener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_box_link_exception;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_box_link_exception);
        initView();
    }

    private void initView() {
        Log.e("BoxLinkExceptionDialog", "initView");
        mTopBtn.setOnClickListener(this);
        mBottomBtn.setOnClickListener(this);
        mGuideTitleTv.setVisibility(mBuilder.isNeedGuideTitle ? View.VISIBLE : View.INVISIBLE);
        if (!TextUtils.isEmpty(mBuilder.title)) {
            mTitleTv.setVisibility(View.VISIBLE);
            mTitleTv.setText(mBuilder.title);
        }

        if (!TextUtils.isEmpty(mBuilder.content)) {
            mContentTv.setVisibility(View.VISIBLE);
            if (mBuilder.content.contains("您还可以：")) {
                SpannableString spannableString = new SpannableString(mBuilder.content);
                ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.parseColor("#C8AF70"));
                spannableString.setSpan(colorSpan1, 0, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                mContentTv.setText(spannableString);
            } else {
                mContentTv.setText(mBuilder.content);
            }
        }

        if (!TextUtils.isEmpty(mBuilder.topBtnText)) {
            mTopBtn.setVisibility(View.VISIBLE);
            mTopBtn.setText(mBuilder.topBtnText);
        }

        if (mBuilder.topBtnBorder) {
//            mTopBtn.setBackground(
//                    mContext.getResources().getDrawable(R.drawable.box_button_selector));
        }

        if (!TextUtils.isEmpty(mBuilder.bottomBtnText)) {
            mBottomBtn.setVisibility(View.VISIBLE);
            mBottomBtn.setText(mBuilder.bottomBtnText);
        }

        if (mBuilder.bottomBtnBorder) {
//            mBottomBtn.setBackground(
//                    mContext.getResources().getDrawable(R.drawable.box_button_selector));
        }
    }

    @Override
    public void show() {
        super.show();
        Log.e(TAG, "dismiss icon");
        dismissSpeechIcon(mContext);
        Tool.lightScreen(mContext);
    }

    @Override
    public void dismiss() {
        Log.e("BoxLinkExceptionDialog", "dismiss");
        super.dismiss();
        showSpeechIcon(mContext);
        mTitleTv.setVisibility(View.GONE);
        mContentTv.setVisibility(View.GONE);
        mTopBtn.setVisibility(View.GONE);
        mTopBtn.setBackground(null);
        mBottomBtn.setVisibility(View.GONE);
        mBottomBtn.setBackground(null);
        ScreenTool.getInstance().addResetData("box_link_exception_dismiss");//重置屏保计时器
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_top_button:
                dismiss();
                mListener.onTopBtnClick();
                break;

            case R.id.tv_bottom_button:
                dismiss();
                mListener.onBottomBtnClick();
                break;

            default:
                break;
        }
    }

    public interface BtnClickListener {
        void onTopBtnClick();

        void onBottomBtnClick();
    }

    private void showSpeechIcon(Context mContext) {
//        if (SpeechManager.getInstance().isConnected() && !DiffuseView.getInstance(mContext)
//                .isShown()) {
//            DiffuseView.getInstance(mContext).show();
//        }
    }

    private void dismissSpeechIcon(Context context) {
//        DiffuseView.getInstance(context).dismiss();
    }

    public static class Builder {
        private String title;
        private String content;
        private String topBtnText;
        private String bottomBtnText;
        private boolean topBtnBorder;
        private boolean bottomBtnBorder;
        private boolean isNeedGuideTitle;

        public Builder() {
        }

        public Builder setGuideTitle(boolean isNeedGuideTitle) {
            this.isNeedGuideTitle = isNeedGuideTitle;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setTopBtnText(String text) {
            this.topBtnText = text;
            return this;
        }

        public Builder setBottomBtnText(String text) {
            this.bottomBtnText = text;
            return this;
        }

        public Builder setTopBtnBorder(boolean isShow) {
            this.topBtnBorder = isShow;
            return this;
        }

        public Builder setBottomBtnBorder(boolean isShow) {
            this.bottomBtnBorder = isShow;
            return this;
        }

        public BoxLinkExceptionDialog build(@NonNull Context context, BtnClickListener listener) {
            Log.e("BoxLinkExceptionDialog", "build: " + this.toString());
            return new BoxLinkExceptionDialog(this, context, listener);
        }

        @Override
        public String toString() {
            return "Builder{" + "title='" + title + '\'' + ", content='" + content + '\'' +
                   ", topBtnText='" + topBtnText + '\'' + ", bottomBtnText='" + bottomBtnText +
                   '\'' + ", topBtnBorder=" + topBtnBorder + ", bottomBtnBorder=" +
                   bottomBtnBorder + ", isNeedGuideTitle=" + isNeedGuideTitle + '}';
        }
    }
}
