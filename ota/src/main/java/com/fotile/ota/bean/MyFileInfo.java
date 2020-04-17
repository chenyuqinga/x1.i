package com.fotile.ota.bean;

import com.dl7.downloaderlib.entity.FileInfo;

public class MyFileInfo {
    public FileInfo fileInfo;
    /**
     * md5校验
     */
    public boolean md5Check = true;

    public MyFileInfo(FileInfo fileInfo, boolean md5Check) {
        this.fileInfo = fileInfo;
        this.md5Check = md5Check;
    }
}
