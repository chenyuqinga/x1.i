package com.fotile.ota.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.dl7.downloaderlib.FileDownloader;
import com.fotile.common.util.FileUtil;
import com.fotile.common.util.Tool;
import com.fotile.ota.bean.OtaData;
import com.fotile.ota.service.OtaDownLoadServer;

import java.io.File;

import static com.fotile.ota.bean.OtaData.VERSION_UPDATED;


/**
 * @author chenyqi
 * @date 2019/5/16
 * @company 杭州方太智能科技有限公司
 * @description OtaTool
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


public class OtaTool {


    public static void cancalDowloadServer(Context context) {
        Intent intent = new Intent(context, OtaDownLoadServer.class);
        context.stopService(intent);
        FileDownloader.stopAll();
        deleteTmpFiles();
    }

    public static void deleteTmpFiles() {
        File file = new File(OtaConstant.FILE_ABSOLUTE_OTA + ".tmp");
        if (file.exists()) {
            file.delete();
        }

        File file1 = new File(OtaConstant.FILE_ABSOLUTE_MCU + ".tmp");
        if (file1.exists()) {
            file1.delete();
        }

        delectFile();
    }

    public static void delectFile() {
        File file = new File(OtaConstant.FILE_ABSOLUTE_OTA);
        if (file.exists()) {
            file.delete();
        }

        File file1 = new File(OtaConstant.FILE_ABSOLUTE_MCU);
        if (file1.exists()) {
            file1.delete();
        }
    }

    /**
     * 处理开机检查ota是否升级完成逻辑
     * 在开机初始化时调用
     */
    public static boolean completeInsall(Context context) {
        OtaData otaData = OtaUpgradeUtil.getOtaData();
        //文件中保存的下载固件包版本
        String fileVersion = otaData.downloaded_version;
        //当前安装固件版本
        String sysVersion = Tool.getLocalSysVersion(context);
        if (!TextUtils.isEmpty(sysVersion) && !TextUtils.isEmpty(fileVersion)) {
            //fileVersion != VERSION_UPDATED 表示未同步
            if (!sysVersion.equals("100") && !fileVersion.equals(VERSION_UPDATED)) {
                //两者相同，说明系统更新成功（下载的文件已经被系统更新）
                if (fileVersion.equals(sysVersion)) {
                    otaData.downloaded_version = VERSION_UPDATED;
                    OtaUpgradeUtil.updateOtaData(otaData);
                    //删除ota固件包
                    FileUtil.deleteFile(OtaConstant.FILE_ABSOLUTE_OTA);
                    return true;
                }
            }
        }
        return false;
    }

}
