package com.fotile.x1i.server.wifilink;

import com.fotile.common.util.Tool;

/**
 * 文件名称：StoveBean
 * 创建时间：2019/7/18 13:56
 * 文件作者：yaohx
 * 功能描述：灶蒸烤设备状态
 */
public class StoveBean {

    public int cmd;

    public byte[] payload;
    /**
     * 左灶是否点火
     */
    public boolean left_firing;
    /**
     * 右灶是否点火
     */
    public boolean right_firing;

    /**
     * 将wifi模组上传数据解析为Bean
     *
     * @param data wifi模组上报的F4 F5 字节数组
     */
    public StoveBean(byte[] data) {
        if (null != data) {
            if (data.length > 6) {
                cmd = data[6] & 0xff;
            }
            getPayload(data);
            if (null != payload) {
                //左灶
                if (payload.length > 42) {
                    int left_fire_data = payload[42] & 0xff;
                    if (left_fire_data == 0) {
                        left_firing = false;
                    }
                    if (left_fire_data == 1) {
                        left_firing = true;
                    }
                }
                //右灶
                if (payload.length > 43) {
                    int right_fire_data = payload[43] & 0xff;
                    if (right_fire_data == 0) {
                        right_firing = false;
                    }
                    if (right_fire_data == 1) {
                        right_firing = true;
                    }
                }
            }
        }
    }

    private void getPayload(byte[] data) {
        //获取payload
        //计算type之后的字节长度
        byte b1 = data[2];
        byte b2 = data[3];
        //从type开始到最后一个字节的长度
        int length = Tool.plus(b1, b2);
        // 减去固定的字节位 (type 2;cmd 1;start 1;flags 2;crc 2)
        int payload_length = length - 8;

        //dev_status(xxB) -剩余长度为设备状态的字节标识长度
        if (payload_length > 0) {
            payload = new byte[payload_length];
            //从索引第10个字节开始是设备控制的数据
            int index_start = 10;
            for (int k = index_start; k < index_start + payload_length; k++) {
                payload[k - index_start] = data[k];
            }
        }
    }

    @Override
    public String toString() {
        String start = "\nstart----------------------------------------------------------start\n";
        String s0 = "【灶具设备控制字节bb：" + Tool.getHexBinString(payload) + "】\n";
        String s1 = "【灶具cmd：" + (String.format("%02x", (byte) cmd) + "】\n");
        String s2 = "【灶具是否有火：" + left_firing + ":" + right_firing + "】\n";
        String end = "end----------------------------------------------------------end";
        String log = start + s0 + s1 + s2 + end;
        return log;
    }
}
