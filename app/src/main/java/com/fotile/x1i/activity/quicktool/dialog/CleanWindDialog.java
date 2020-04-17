package com.fotile.x1i.activity.quicktool.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.view.View;
import android.widget.TextView;

import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseDialog;
import com.fotile.x1i.manager.DeviceReportManager;

import butterknife.BindView;
import fotile.device.cookerprotocollib.helper.DeviceControl;
import fotile.device.cookerprotocollib.helper.bean.WorkBean;
import rx.functions.Action1;

/**
 * 文件名称： CleanWindDialog
 * 创建时间： 2019/6/12
 * 文件作者： chenyqi
 * 功能描述： 清洁保养
 */
public class CleanWindDialog extends BaseDialog  {
    @BindView(R.id.tv_close)
    TextView tvClose;
    Action1<WorkBean> action1;


    public CleanWindDialog(Context context){
        super(context, R.style.FullScreenDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DeviceControl.getInstance().startClean();
        createAction();

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceControl.getInstance().closeDevice(true);
                dismiss();
            }
        });
    }

    private void createAction(){
        action1 = new Action1<WorkBean>() {
            @Override
            public void call(WorkBean workBean) {
                int work_state = DeviceReportManager.work_state;
                 if(work_state != DeviceReportManager.WORK_CLEAN){
                     dismiss();
                 }
            }
        };
        DeviceReportManager.getInstance().addWorkBeanAction(action1);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        DeviceReportManager.getInstance().removeWorkBeanAction(action1);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_care_instruction;
    }
}
