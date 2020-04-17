package fotile.device.cookerprotocollib.helper.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 文件名称：WorkBean
 * 创建时间：2017/10/27 16:02
 * 文件作者：yaohx
 * 功能描述：设备回传数据
 */
public class WorkBean implements Parcelable {

    public WorkBean(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String bodyStr = jsonObject.getString("body");
            JSONObject bodyObj = new JSONObject(bodyStr);
            if (bodyObj.has("devstate")) {
                devstate = bodyObj.getString("devstate");
            }
            if (bodyObj.has("lamp")) {
                try{
                    lamp = bodyObj.getInt("lamp");
                }catch (Exception e){

                }
            }
            if (bodyObj.has("workmode")) {
                workmode = bodyObj.getString("workmode");
            }
            if (bodyObj.has("motorgear")) {
                motorgear = bodyObj.getInt("motorgear");
            }
            if (bodyObj.has("motornumber")) {
                motornumber = bodyObj.getInt("motornumber");
            }
            if (bodyObj.has("motortype")) {
                motortype = bodyObj.getString("motortype");
            }
            if (bodyObj.has("motorspeed")) {
                motorspeed = bodyObj.getString("motorspeed");
            }
            if (bodyObj.has("loadconditions")) {
                loadconditions = bodyObj.getString("loadconditions");
            }
            if (bodyObj.has("runtime")) {
                runtime = bodyObj.getInt("runtime");
            }
            if (bodyObj.has("sensor")) {
                sensor = new Sensor(bodyObj.getString("sensor"));
            }
            if (bodyObj.has("errornum")) {
                errornum = bodyObj.getInt("errornum");
            }
            if (bodyObj.has("delaytime")) {
                delaytime = bodyObj.getInt("delaytime");
            }
            if (bodyObj.has("ota")) {
                ota = bodyObj.getString("ota");
            }
            if (bodyObj.has("autocleanstate")) {
                autocleanstate = bodyObj.getString("autocleanstate");
            }
            if (bodyObj.has("autoclean")) {
                autoclean = bodyObj.getString("autoclean");
            }
            if (bodyObj.has("soundtype")) {
                soundtype = bodyObj.getString("soundtype");
            }
            if (bodyObj.has("mechanism")) {
                mechanism = bodyObj.getString("mechanism");
            }
            if (bodyObj.has("anion")) {
                anion = bodyObj.getString("anion");
            }
            if (bodyObj.has("remindtime")) {
                remindtime = bodyObj.getString("remindtime");
            }
            if (bodyObj.has("resistance")) {
                resistance = bodyObj.getString("resistance");
            }
            if (bodyObj.has("linkage")) {
                String linkage_str = bodyObj.getString("linkage");
                linkage = new Linkage(linkage_str);
            }
            if (bodyObj.has("ambientlight")) {
                ambientlight = bodyObj.getString("ambientlight");
            }
            if (bodyObj.has("red")) {
                red = bodyObj.getInt("red");
            }
            if (bodyObj.has("green")) {
                green = bodyObj.getInt("green");
            }
            if (bodyObj.has("blue")) {
                blue = bodyObj.getInt("blue");
            }

            if (bodyObj.has("motorcurrent")) {
                motorcurrent = bodyObj.getString("motorcurrent");
            }
            if (bodyObj.has("motorfault")) {
                motorfault = bodyObj.getInt("motorfault");
            }
            if (bodyObj.has("linearactuatorlocation")) {
                linearactuatorlocation = bodyObj.getInt("linearactuatorlocation");
            }
            if (bodyObj.has("linearactuatorelectricity")) {
                linearactuatorelectricity = bodyObj.getInt("linearactuatorelectricity");
            }
            if (bodyObj.has("specialfunction")) {
                specialfunction = bodyObj.getString("specialfunction");
            }
            if (bodyObj.has("autocruise")) {
                temo_auto = bodyObj.getInt("autocruise");
                //求烟道阻力位 1-阻力大 0-无
                int byte0 = temo_auto & 0x01;
                //求油烟浓度位  2-油烟浓度大 0-无
                int byte1 = temo_auto & 0x02;

                //烟道阻力大
                if (byte0 == 0x01) {
                    autocruise_pressure = true;
                } else {
                    autocruise_pressure = false;
                }
                //油烟浓度大
                if (byte1 == 0x02) {
                    autocruise_smoke = true;
                } else {
                    autocruise_smoke = false;
                }
            }
            if (bodyObj.has("telecontrol")) {
                telecontrol = bodyObj.getString("telecontrol");
            }
            if(bodyObj.has("NTC")){
                NTC = bodyObj.getInt("NTC");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * NTC 传感器温度
     */
    public int NTC;
    /**
     * 远程开关
     */
    public String telecontrol;

    /**
     * 油烟是否大
     */
    public boolean autocruise_smoke;
    /**
     * 烟道阻力是否大
     */
    public boolean autocruise_pressure;

    private int temo_auto;

    /**
     * 关机/待机/开机
     * off：设备关机
     * wait：设备待机
     * on：设备开机
     */
    public String devstate;
    /**
     * 照明灯
     * off：照明灯关闭
     * on：照明灯开启
     */
    public int lamp;
    /**
     * 工作模式
     * auto1：智能巡航
     * auto2：空气管家
     * manual：风量控制
     */
    public String workmode;
    /**
     * 电机档位
     * 0~9：电机档数
     */
    public int motorgear;
    /**
     * 电机数量
     * 1~2：电机数量
     */
    private int motornumber;

    /**
     * 电机类型
     * ac：交流
     * dc：直流
     */
    private String motortype;

    /***********************************工厂自检*******************************/
    /**
     * 电机转速
     * 0-5000
     */
    public String motorspeed;
    /**
     * 电机电流
     */
    public String motorcurrent;
    /**
     * 油烟机总运行时间（小时）
     */
    public int runtime;
    /**
     * 电机故障
     */
    public int motorfault;
    /**
     * 电动推杆位置
     * 0x01:最上  0x02:最下   0x03:中间  0x04：上半区   0x05：下半区
     */
    public int linearactuatorlocation;
    /**
     * 电动推杆电流
     */
    public int linearactuatorelectricity;
    /**
     * 故障号
     * 0~255：故障号，详见附件《Z1.I油烟机消毒柜故障类型说明》
     * 0x01:电机
     * 0x02:电动推杆
     * 0x03:净化装置
     * 0x04:油烟传感器
     * 0x05:空气传感器
     * 0x06:声音传感器
     * 0x07:烟灶联动
     * 0x11:通讯故障（屏端和电源板握手错误）
     * 0b11:着火（油烟机着火）
     */
    public int errornum = -1;
    /**
     * 特殊功能
     */
    public String specialfunction;
    /***********************************工厂自检*******************************/
    /**
     * 负载状况
     * 0~255：适用于维护保养，报警值，具体值待定
     */
    private String loadconditions;
    /**
     * 传感器部分
     */
    public Sensor sensor;

    /**
     * 延时关机
     * 0~255：烟机设定时间延迟关机，暂定2分钟
     */
    private int delaytime;

    /**
     * ota升级
     * yes：支持（预留）
     * no：不支持（预留）
     */
    private String ota;
    /**
     * 自动清洗状态
     * off：关闭（预留）
     * on：清洗中（预留）
     */
    private String autocleanstate;
    /**
     * 自动清洗功能操作
     * yes：支持
     * no：不支持
     */
    private String autoclean;
    /**
     * 音频输出类型
     * buzzer：蜂鸣器
     * horn：喇叭
     */
    private String soundtype;
    /**
     * 自动机构状态
     * off：关闭
     * on：开启
     */
    private String mechanism;

    /**
     * 负离子
     * off：关闭
     * on：开启
     */
    private String anion;
    /**
     * 定时提醒时间
     * 0~255：定时提醒时间（单位：分钟）
     */
    private String remindtime;
    /**
     * 烟道阻力大标志
     * big：检测到烟道阻力大
     * small：未检测到烟道阻力大
     */
    public String resistance;
    /**
     * 烟灶联动状态
     * off：未进行烟灶联动
     * on：烟灶联动中
     */
    public Linkage linkage;
    /**
     * 氛围灯
     * off：关闭
     * on：开启
     */
    private String ambientlight;
    /**
     * 氛围灯R的PWM值	0~255
     */
    private int red;
    /**
     * 氛围灯G的PWM值	0~255
     */
    private int green;
    /**
     * 氛围灯B的PWM值	0~255
     */
    private int blue;

    /**
     * 用于比较两个workBean对象是否数据一致
     * @return
     */
    public String equalParam() {
        String s0 = "【当前开关机状态devstate：" + devstate + "】\n";
        String s1 = "【当前灯lamp：" + lamp + "】\n";
        String s2 = "【当前风机档位motorgear：" + motorgear + "】\n";
        String s3 = "【当前工作模式workmode：" + workmode + "】\n";
        String s6 = "【当前智能巡航状态【烟道阻力：" + autocruise_pressure + "；油烟浓度：" + autocruise_smoke + "】\n";
        String s9 = "【当前远程控制开关telecontrol：" + telecontrol + "】\n";
        String s10 = "【设备故障号errornum：" + errornum + "】\n";
        String s11 = "【设备NTC：" + NTC + "】\n";
        String log = s0 + s1 + s2 + s3 + s6 + s9 + s10 + s11;
        return log;
    }

    @Override
    public String toString() {
        String start = "start----------------------------------------------------------start\n";
        String s0 = "【当前开关机状态devstate：" + devstate + "】\n";
        String s1 = "【当前灯lamp：" + lamp + "】\n";
        String s2 = "【当前风机档位motorgear：" + motorgear + "】\n";
        String s3 = "【当前工作模式workmode：" + workmode + "】\n";
        String s6 = "【当前智能巡航状态【烟道阻力：" + autocruise_pressure + "；油烟浓度：" + autocruise_smoke + "】\n";
        String s7 = "【当前烟机运行时间runtime：" + runtime + "】\n";
        String s8 = "【当前烟机电机转速motorspeed：" + motorspeed + "】\n";
        String s9 = "【当前远程控制开关telecontrol：" + telecontrol + "】\n";
        String s10 = "【设备故障号errornum：" + errornum + "】\n";
        String s11 = "【设备NTC：" + NTC + "】\n";
        String end = "end----------------------------------------------------------end";
        String log = start + s0 + s1 + s2 + s3 + s6 + s7 + s8 + s9 + s10 + s11 + end;
        return log;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.telecontrol);
        dest.writeByte(this.autocruise_smoke ? (byte) 1 : (byte) 0);
        dest.writeByte(this.autocruise_pressure ? (byte) 1 : (byte) 0);
        dest.writeInt(this.temo_auto);
        dest.writeString(this.devstate);
        dest.writeInt(this.lamp);
        dest.writeString(this.workmode);
        dest.writeInt(this.motorgear);
        dest.writeInt(this.motornumber);
        dest.writeString(this.motortype);
        dest.writeString(this.motorspeed);
        dest.writeString(this.motorcurrent);
        dest.writeInt(this.runtime);
        dest.writeInt(this.motorfault);
        dest.writeInt(this.linearactuatorlocation);
        dest.writeInt(this.linearactuatorelectricity);
        dest.writeInt(this.errornum);
        dest.writeString(this.specialfunction);
        dest.writeString(this.loadconditions);
        dest.writeParcelable(this.sensor, flags);
        dest.writeInt(this.delaytime);
        dest.writeString(this.ota);
        dest.writeString(this.autocleanstate);
        dest.writeString(this.autoclean);
        dest.writeString(this.soundtype);
        dest.writeString(this.mechanism);
        dest.writeString(this.anion);
        dest.writeString(this.remindtime);
        dest.writeString(this.resistance);
        dest.writeParcelable(this.linkage, flags);
        dest.writeString(this.ambientlight);
        dest.writeInt(this.red);
        dest.writeInt(this.green);
        dest.writeInt(this.blue);
    }

    protected WorkBean(Parcel in) {
        this.telecontrol = in.readString();
        this.autocruise_smoke = in.readByte() != 0;
        this.autocruise_pressure = in.readByte() != 0;
        this.temo_auto = in.readInt();
        this.devstate = in.readString();
        this.lamp = in.readInt();
        this.workmode = in.readString();
        this.motorgear = in.readInt();
        this.motornumber = in.readInt();
        this.motortype = in.readString();
        this.motorspeed = in.readString();
        this.motorcurrent = in.readString();
        this.runtime = in.readInt();
        this.motorfault = in.readInt();
        this.linearactuatorlocation = in.readInt();
        this.linearactuatorelectricity = in.readInt();
        this.errornum = in.readInt();
        this.specialfunction = in.readString();
        this.loadconditions = in.readString();
        this.sensor = in.readParcelable(Sensor.class.getClassLoader());
        this.delaytime = in.readInt();
        this.ota = in.readString();
        this.autocleanstate = in.readString();
        this.autoclean = in.readString();
        this.soundtype = in.readString();
        this.mechanism = in.readString();
        this.anion = in.readString();
        this.remindtime = in.readString();
        this.resistance = in.readString();
        this.linkage = in.readParcelable(Linkage.class.getClassLoader());
        this.ambientlight = in.readString();
        this.red = in.readInt();
        this.green = in.readInt();
        this.blue = in.readInt();
    }

    public static final Creator<WorkBean> CREATOR = new Creator<WorkBean>() {
        @Override
        public WorkBean createFromParcel(Parcel source) {
            return new WorkBean(source);
        }

        @Override
        public WorkBean[] newArray(int size) {
            return new WorkBean[size];
        }
    };
}