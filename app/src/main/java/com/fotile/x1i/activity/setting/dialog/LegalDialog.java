package com.fotile.x1i.activity.setting.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fotile.common.util.Tool;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

/**
 * 文件名称：LegalDialog
 * 创建时间：2019/4/29 17:43
 * 文件作者：yaohx
 * 功能描述：法律信息-用户协议和隐私政策
 */
public class LegalDialog extends BaseDialog implements View.OnClickListener {
    @BindView(R.id.txt_title)
    TextView txt_title;
    @BindView(R.id.txt_desc)
    TextView txt_desc;

    @BindView(R.id.txt_confirm)
    TextView txt_confirm;

    /**
     * 1 -- 用户协议
     * 2 -- 隐私政策
     */
    private int type = 1;

    public LegalDialog(Context context , int type) {
        super(context, R.style.FullScreenDialog);
        this.context = context;
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_legal;
    }


    private void initView() {
        String legalJson = Tool.getFileJson("agreement", context);

        try {
            JSONArray array = new JSONArray(legalJson);
            JSONObject xieyi = (JSONObject) array.getJSONObject(0);
            JSONObject yinsi = (JSONObject) array.getJSONObject(1);

            if(type== 1){
                txt_title.setText(xieyi.getString("title"));
                txt_desc.setText(xieyi.getString("content"));
            }
            else {
                txt_title.setText(yinsi.getString("title"));
                txt_desc.setText(yinsi.getString("content"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        txt_confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //确认
            case R.id.txt_confirm:
                dismiss();
                break;
        }
    }
}
