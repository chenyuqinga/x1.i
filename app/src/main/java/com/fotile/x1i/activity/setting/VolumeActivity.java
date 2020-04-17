package com.fotile.x1i.activity.setting;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;

import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.widget.SeekBar;
import com.fotile.x1i.widget.TopBar;

import butterknife.BindView;

/**
 * @author： yaohx
 * @data： 2019/4/24 16:43
 * @company： 杭州方太智能科技有限公司
 * @description： 调节音量
 * <p>
 * Copyright (c) 2018, FOTILE GROUP.
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * - Neither the name of the FOTILE GROUP nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL FOTILE GROUP BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class VolumeActivity extends BaseActivity {

    @BindView(R.id.seekbar)
    SeekBar seekbar;
    /**
     * 音量manager
     */
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //x1.i的板子是15最大值
        final int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekbar.setMax(maxVolume);

        int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //设置seekbar 上一次的媒体音量
        seekbar.setProgress(volume);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(int progress) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                TopBar.getInstance(context).setVolmeState();
            }

            @Override
            public void onProgressStop(int progress) {

            }
        });


    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting_volume;
    }

    @Override
    public boolean showBottom() {
        return true;
    }
}
