package com.fotile.x1i.server.wifilink;

import android.content.Context;

import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;

/**
 * 文件名称：StoveControl
 * 创建时间：2019/7/18 14:57
 * 文件作者：yaohx
 * 功能描述：灶具控制指令类
 */
public class StoveControl {

    /**
     * 控制灶具关火
     *
     * @param left  true表示执行关火
     * @param right true表示执行关火
     */
    public static void closeFire(boolean left, boolean right, Context context) {
        if (left == false && right == false) {
            return;
        }
        final int HEADER = 0xF4F5;
        //表示灶具
        final int TYPE = 0x1000;
        //表示发送请求-（1B）
        final int CMD = 0x31;
        final int STAT = 0x01;
        final int FLAG = 0x0000;

        //因为不知道长度，所以先构建一个128的字节数组，保证长度足够
        byte[] bytes = new byte[128];
        int index = 0;
        bytes[index++] = (byte) (HEADER >> 8 & 0xFF);//F4
        bytes[index++] = (byte) (HEADER & 0xFF);//F5
        bytes[index++] = (byte) 0x00;//len高四位
        bytes[index++] = (byte) 0x00;//len低四位
        bytes[index++] = (byte) (TYPE >> 8 & 0xFF);//type高四位
        bytes[index++] = (byte) (TYPE & 0xFF);//type低四位
        bytes[index++] = (byte) CMD;//cmd 0x31
        bytes[index++] = (byte) STAT;//stat 0x01
        bytes[index++] = (byte) (FLAG >> 8 & 0xFF);//flag高四位
        bytes[index++] = (byte) (FLAG & 0xFF);//flag低四位

        //---------------------------------控制灶具关火byte↓---------------------------------//
        //Bit15--Bit8 （attr_flags 高八位）
        int attr = 0b00000000;
        //左灶开关火控制-Bit11 （0标识无效，1标识有效）
        if (left) {
            attr = (attr | 0b00001000) & 0xFF;
        }
        //右灶开关火控制-Bit12  （0标识无效，1标识有效）
        if (right) {
            attr = (attr | 0b00010000) & 0xFF;
        }
        bytes[index++] = (byte) (attr);
        //Bit7-Bit0  （attr_flags 低八位补0）
        bytes[index++] = 0b00000000 & 0b11111111;


        //组attr_vals 30个字节
        byte[] attr_vals = new byte[30];
        //左灶关火
        if (left) {
            attr_vals[28] = 0x00;
        }
        if (right) {
            attr_vals[29] = 0x00;
        }
        //将attr_vals拼接到主字节数组
        for (int k = 0; k < attr_vals.length; k++) {
            bytes[index++] = attr_vals[k];
        }
        //---------------------------------控制灶具关火byte↑---------------------------------//

        //先默认设置CRC占位，计算完了len之后再赋值
        //CRC
        bytes[index++] = (byte) 0x00;//CRC高八位
        bytes[index++] = (byte) 0x00;//CRC低八位
        int max_index = index; //最大的索引值

        //到此bytes的长度完成
        //赋值完毕-计算长度
        int byte_length = max_index;

        //计算length的值，去除header和len
        int length = byte_length - 4;
        byte b1 = 0x00; // len高八位
        byte b2 = (byte) (length & 0xFF);//len低八位
        //赋值len字节
        bytes[2] = b1;
        bytes[3] = b2;

        //CRC
        bytes[byte_length - 2] = (byte) (0x00);//CRC高八位
        bytes[byte_length - 1] = getCheck(bytes);//CRC低八位

        //截取字节数组协议部分
        byte[] result = new byte[byte_length];
        for (int k = 0; k < byte_length; k++) {
            result[k] = bytes[k];
        }

        LinkAction.getInstance(context).addData(result);
        String str = left + " " + right;
        LogUtil.LOG_STOVE_LINK("灶具关火指令-" + str, Tool.getHexBinString(result));
    }

    /**
     * 各字节相异或-用与计算CRC
     * 相同则为0，不相同则为1
     * CRC高八位默认为00，即CRC=0x00##；（计算F4转义之后的CRC，从Header开始到payload结束）
     *
     * @param bytes
     * @return
     */
    private static byte getCheck(byte[] bytes) {
        byte result = bytes[0];
        for (int i = 1; i <= bytes.length - 1; i++) {
            result ^= bytes[i];
        }
        return result;
    }
}
