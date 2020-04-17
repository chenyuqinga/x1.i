package fotile.device.cookerprotocollib.helper;


import fotile.device.cookerprotocollib.helper.bean.WorkBean;

/**
 * 文件名称：IDeviceReportListener
 * 创建时间：2018/6/27 12:27
 * 文件作者：yaohx
 * 功能描述：设备上报接口数据回调--提供给应用层处理界面
 */
public interface IDeviceReportListener {

    /**
     * 在次函数中实现应用层关于设备控制的界面跳转
     *
     * @param workBean
     */
    abstract void onDeviceReport(WorkBean workBean);
}
