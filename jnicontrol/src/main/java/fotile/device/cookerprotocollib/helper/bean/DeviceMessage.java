package fotile.device.cookerprotocollib.helper.bean;

/**
 * 项目名称：X1.I
 * 创建时间：2018/11/20 10:53
 * 文件作者：yaohx
 * 功能描述：设备上报的消息：绑定消息，备忘录，消息
 */
public class DeviceMessage {
    /**
     * code == 6设备绑定信息
     * code == 9家庭备忘录
     * code == 16消息中心
     * code == 20设备绑定二维码
     * code == 16烟机登录token
     */
    public int code;
    public String params;

    public DeviceMessage(int code, String params) {
        this.code = code;
        this.params = params;
    }

}
