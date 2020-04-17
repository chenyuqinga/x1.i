package com.fotile.x1i.activity.setting;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.bean.event.UpdateAwaitText;
import com.fotile.x1i.widget.ItemAwaitView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

import static com.fotile.x1i.util.Constant.SCREEN_FOTILE_SAVER;

/**
 * 文件名称：AwaitActivity
 * 创建时间：2019/4/24 18:48
 * 文件作者：yaohx
 * 功能描述：待机画面
 */
public class AwaitActivity extends BaseActivity {

    @BindView(R.id.linear_content)
    LinearLayout linearContent;

    @BindView(R.id.current_txt)
    TextView current_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        initView();
    }

    @Subscribe
    public void onUpdateCurrentAwait(UpdateAwaitText updateAwaitText) {
        if (updateAwaitText.to_class.getSimpleName().contains("AwaitActivity")) {
            LogUtil.LOGE("11", 123);
            current_txt.setText("待机画面： " + getCurrentTypeString(updateAwaitText.type));
        }
    }

    private static String getCurrentTypeString(int type) {
        String s ;
        switch (type) {
            case 0:
                s = "英伦";
                break;
            case 1:
                s = "商务";
                break;
            case 2:
                s = "古典";
                break;
            case 3:
                s = "现代";
                break;
            case 4:
                s = "罗马";
                break;
            case 5:
                s = "怀旧";
                break;
            case 6:
                s = "电子";
                break;
            case 7:
                s = "无";
                break;
            default:
                s = "方太画报";
                break;
        }
        return s;
    }

    /**
     * 逐个添加屏保时钟页面
     */
    private void initView() {
        int default_index = PreferenceUtil.getScreenSaverType(context);
        current_txt.setText("待机画面： " + getCurrentTypeString(default_index));

        ItemAwaitView itemView = new ItemAwaitView(context);
        //index == -1 方太画报
        itemView.setData(R.mipmap.pic_await_fangtai, "方太画报", SCREEN_FOTILE_SAVER);
        linearContent.addView(itemView);

        //索引和MyQAnalogClock类中的对应
        int index = 0;
        itemView = new ItemAwaitView(context);
        itemView.setData(R.mipmap.pic_await_yinlun, "英伦", index++);
        linearContent.addView(itemView);


        itemView = new ItemAwaitView(context);
        itemView.setData(R.mipmap.pic_await_shangwu, "商务", index++);
        linearContent.addView(itemView);

        itemView = new ItemAwaitView(context);
        itemView.setData(R.mipmap.pic_await_gudian, "古典", index++);
        linearContent.addView(itemView);

        itemView = new ItemAwaitView(context);
        itemView.setData(R.mipmap.pic_await_xiandai, "现代", index++);
        linearContent.addView(itemView);

        itemView = new ItemAwaitView(context);
        itemView.setData(R.mipmap.pic_await_luoma, "罗马", index++);
        linearContent.addView(itemView);

        itemView = new ItemAwaitView(context);
        itemView.setData(R.mipmap.pic_await_huaijiu, "怀旧", index++);
        linearContent.addView(itemView);

        itemView = new ItemAwaitView(context);
        itemView.setData(R.mipmap.pic_await_dianzi, "电子", index++);
        linearContent.addView(itemView);

        //index == 7 无屏保
        itemView = new ItemAwaitView(context);
        itemView.setData(R.mipmap.pic_await_wu, "无", index++);
        linearContent.addView(itemView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting_await;
    }

    @Override
    public boolean showBottom() {
        return true;
    }
}
