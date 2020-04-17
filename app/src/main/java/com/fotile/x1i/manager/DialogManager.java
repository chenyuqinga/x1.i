/*
 * ************************************************************
 * 文件：DialogManager.java  模块：app  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.x1i.manager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.fotile.x1i.dailog.BoxLinkExceptionDialog;
import com.fotile.x1i.util.AppUtil;

public class DialogManager {
    private static final String TAG = DialogManager.class.getSimpleName();
    private static volatile DialogManager mDialogManager = null;
    private BoxLinkExceptionDialog mBoxErrorDialog = null;
    private Context mTempContext;

    public static DialogManager getInstance() {
        if (mDialogManager == null) {
            synchronized (DialogManager.class) {
                if (mDialogManager == null) {
                    mDialogManager = new DialogManager();
                }
            }
        }
        return mDialogManager;
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 10001:
                    Log.e(TAG, "show dialog");
                    mBoxErrorDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            Log.e(TAG, "dialog on show");
                            //若不在引导页则显示时隐藏下边栏
//                            if (mTempContext != null && !PreferenceUtil.isInGuide(mTempContext)) {
//                                BottomView.getInstance(mTempContext).hide();
//                                TopBar.getInstance(mTempContext).hide();
//                            }
                        }
                    });
                    mBoxErrorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Log.e(TAG, "dialog on dismiss");
//                            ScreenTool.getInstance().addResetData(
//                                    "box_link_exception_dismiss");//重置屏保计时器
//                            //若不在引导页则消失时显示下边栏
//                            if (mTempContext != null && !PreferenceUtil.isInGuide(mTempContext)) {
//                                BottomView.getInstance(mTempContext).show();
//                                TopBar.getInstance(mTempContext).show();
//                                mTempContext = null;
//                            }
                        }
                    });
                    mBoxErrorDialog.show();
                    mBoxErrorDialog.setOwnerActivity(AppUtil.getCurrentActivity(null));
                    Log.e(TAG, "dialog shown at: " + mBoxErrorDialog.getOwnerActivity());
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    public void showExceptionDialog(@NonNull final Context context, final String title,
            final String content, final String topBtnText, final boolean topBtnBorder,
            final String bottomBtnText, final boolean bottomBtnBorder,
            final boolean isNeedGuideTitle,
            final BoxLinkExceptionDialog.BtnClickListener listener) {
        mTempContext = context;
        //        dismissExceptionDialog();
        if (mBoxErrorDialog != null && mBoxErrorDialog.isShowing()) {
            Log.e(TAG, "other exception dialog is showing");
            return;
        }
        //亮屏
//        Tool.lightScreen(context);
        //暂停屏保
//        ScreenTool.getInstance().addPause();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "build dialog: " + AppUtil.getCurrentActivityName(context));
                mBoxErrorDialog = new BoxLinkExceptionDialog.Builder().setGuideTitle(
                        isNeedGuideTitle).setTitle(title).setContent(content).setTopBtnText(
                        topBtnText).setTopBtnBorder(topBtnBorder).setBottomBtnText(bottomBtnText)
                        .setBottomBtnBorder(bottomBtnBorder).build(
                                AppUtil.getCurrentActivity(context), listener);
            }
        }, 500);
        mHandler.sendEmptyMessageDelayed(10001, 510);
    }

    public void dismissExceptionDialog() {
        Log.e(TAG, "dismiss Exception Dialog: " +
                   (mBoxErrorDialog == null ? null : mBoxErrorDialog.getOwnerActivity()));
        if (mBoxErrorDialog != null) {
            Log.e(TAG, "dialog is showing: " + mBoxErrorDialog.isShowing() + ", owner activity: " +
                       (mBoxErrorDialog.getOwnerActivity() == null
                        ? null
                        : mBoxErrorDialog.getOwnerActivity().isFinishing()));
            if (mBoxErrorDialog.isShowing() && mBoxErrorDialog.getOwnerActivity() != null &&
                !mBoxErrorDialog.getOwnerActivity().isFinishing()) {
                mBoxErrorDialog.dismiss();
            }
            mBoxErrorDialog = null;
        }
    }

    public BoxLinkExceptionDialog getAttachExceptionDialog(Activity activity) {
        if (mBoxErrorDialog != null && mBoxErrorDialog.isShowing() &&
            mBoxErrorDialog.getOwnerActivity() != null && activity != null && TextUtils.equals(
                mBoxErrorDialog.getOwnerActivity().getLocalClassName(),
                activity.getLocalClassName())) {
            return mBoxErrorDialog;
        }
        return null;
    }

    public boolean isExceptionDialogShow() {
        return mBoxErrorDialog != null && mBoxErrorDialog.isShowing();
    }
}
