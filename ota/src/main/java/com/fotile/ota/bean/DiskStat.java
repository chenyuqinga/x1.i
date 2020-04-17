package com.fotile.ota.bean;

/**
 * @author ： panyw .
 * @date ：2018/1/29 17:41
 * @COMPANY ： Fotile智能厨电研究院
 * @description ：
 */

public class DiskStat {
    public long free;
    public long total;

    public DiskStat(long free, long total) {
        this.free = free;
        this.total = total;
    }

    public long getFree() {
        return free;
    }

}
