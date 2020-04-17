package com.fotile.x1i.activity.setting.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fotile.common.util.Tool;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.factory.FactoryPWDActivity;
import com.fotile.x1i.base.BaseFragment;
import com.fotile.x1i.server.ProductionLineServer;

import java.io.File;

import butterknife.BindView;

/**
 * 文件名称：AboutProductFragment
 * 创建时间：2019/4/29 16:32
 * 文件作者：yaohx
 * 功能描述：关于本机-产品信息
 */
public class AboutProductFragment extends BaseFragment {
    @BindView(R.id.txt_model)
    TextView txtModel;

    @BindView(R.id.txt_mac)
    TextView txtMac;

    @BindView(R.id.txt_capacity_all)
    TextView txtCapacityAll;

    @BindView(R.id.txt_capacity_available)
    TextView txtCapacityAvailable;

    @BindView(R.id.txt_sn)
    TextView txtSn;

    @BindView(R.id.txt_version)
    TextView txtVersion;

    /**
     * 总容量
     */
    private String totalMemory;
    /**
     * 可用容量
     */
    private String availableMemory;
    /**
     * 序列号
     */
    @BindView(R.id.relate_sn)
    RelativeLayout relateSn;
    @BindView(R.id.relate_available)
    RelativeLayout relateAvailable;

    /**
     * 点击次数
     */
    final static int COUNTS = 4;
    /**
     * 规定有效时间
     */
    final static long DURATION = 4 * 1000;
    long[] serialHits = new long[COUNTS];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getMemoryInfo(Environment.getDataDirectory(), context);
        initView();
        return view;
    }

    private void initView() {
        //本机型号
        txtModel.setText("X1.i");
        //MAC地址
        txtMac.setText(Tool.getLocalMacAddress());
        //总容量
        txtCapacityAll.setText(totalMemory);
        //可用容量
        txtCapacityAvailable.setText(availableMemory);
        //序列号
        txtSn.setText(android.os.Build.SERIAL);
        //版本
        txtVersion.setText(Tool.getVersionName(context));

        relateSn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(serialHits, 1, serialHits, 0, serialHits.length - 1);
                //实现左移，然后最后一个位置更新距离开机的时间，如果最后一个时间和最开始时间小于DURATION，即连续5次点击
                serialHits[serialHits.length - 1] = SystemClock.uptimeMillis();

                if (serialHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
                    context.launchActivity(FactoryPWDActivity.class);
                }
            }
        });
        //可用容量
        relateAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(serialHits, 1, serialHits, 0, serialHits.length - 1);
                //实现左移，然后最后一个位置更新距离开机的时间，如果最后一个时间和最开始时间小于DURATION，即连续5次点击
                serialHits[serialHits.length - 1] = SystemClock.uptimeMillis();

                if (serialHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
                    Intent intent = new Intent(context, ProductionLineServer.class);
                    context.startService(intent);
                    context.launch2Home();
                }
            }
        });
    }

    private void getMemoryInfo(File path, Context context) {
        // 获得一个磁盘状态对象
        StatFs stat = new StatFs(path.getPath());

        long blockSize = stat.getBlockSize();    // 获得一个扇区的大小

        long totalBlocks = stat.getBlockCount();    // 获得扇区的总数

        long availableBlocks = stat.getAvailableBlocks();    // 获得可用的扇区数量

        // 总空间
        totalMemory = Formatter.formatFileSize(context, totalBlocks * blockSize);
        // 可用空间
        availableMemory = Formatter.formatFileSize(context, availableBlocks * blockSize);

    }

    @Override
    public int getLayoutId() {
        return R.layout.item_about_product;
    }
}
