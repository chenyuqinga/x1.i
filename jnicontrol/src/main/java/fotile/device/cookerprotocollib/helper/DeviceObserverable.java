package fotile.device.cookerprotocollib.helper;


import java.util.concurrent.CopyOnWriteArrayList;

import fotile.device.cookerprotocollib.helper.bean.DeviceMessage;
import fotile.device.cookerprotocollib.helper.bean.WorkBean;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * 文件名称：DeviceObserverable
 * 创建时间：2017/9/26 18:04
 * 文件作者：yaohx
 * 功能描述：设备上报被观察者
 */
public class DeviceObserverable implements OnDeviceListener {
    //设备控制list
    CopyOnWriteArrayList<Action1<WorkBean>> listWorkBean = new CopyOnWriteArrayList<Action1<WorkBean>>();
    //消息list
    CopyOnWriteArrayList<Action1<DeviceMessage>> listMessage = new CopyOnWriteArrayList<Action1<DeviceMessage>>();
    //绑定消息list
    CopyOnWriteArrayList<Action1> listBind = new CopyOnWriteArrayList<Action1>();
    /**
     * 上一次接受到的workBean
     */
    private WorkBean oldWorkBean;

    private IDeviceReportListener iDeviceReportListener;

    private static DeviceObserverable deviceObserverable;

    private DeviceObserverable() {

    }

    public static DeviceObserverable getInstance() {
        if (null == deviceObserverable) {
            deviceObserverable = new DeviceObserverable();
        }
        return deviceObserverable;
    }

    /**
     * 初始化
     *
     * @param macAddress
     */
    public void initJNI(String macAddress, IDeviceReportListener iDeviceReportListener) {
        CookerProtocol.init(0x0403, macAddress);
        CookerProtocol.setCallListener(this);
        this.iDeviceReportListener = iDeviceReportListener;
    }


    //添加设备控制Action
    public void addWorkBeanAction(Action1<WorkBean> action1) {
        if (null != listWorkBean && !listWorkBean.contains(action1) && null != action1) {
            listWorkBean.add(action1);
        }
    }

    public void addMessageAction(Action1<DeviceMessage> action1) {
        if (null != listMessage && !listMessage.contains(action1) && null != action1) {
            listMessage.add(action1);
        }
    }

    //添加设备绑定Action
    public void addBindAction(Action1<Integer> action1) {
        if (null != listBind && !listBind.contains(action1) && null != action1) {
            listBind.add(action1);
        }
    }

    //移除设备控制Action
    public void removeWorkBeanAction(Action1<WorkBean> action1) {
        if (null != listWorkBean && listWorkBean.contains(action1) && null != action1) {
            listWorkBean.remove(action1);
        }
    }

    //移除消息Action
    public void removeMessageAction(Action1<DeviceMessage> action1) {
        if (null != listMessage && listMessage.contains(action1) && null != action1) {
            listMessage.remove(action1);
        }
    }

    //移除绑定Action
    public void removeBindAction(Action1<Integer> action1) {
        if (null != listBind && listBind.contains(action1) && null != action1) {
            listBind.remove(action1);
        }
    }

    //Jni设备控制上报
    @Override
    public synchronized void onReportStatus(String params) {
        if (null == params) {
            return;
        }
//        String result = new String(params);
        WorkBean workBean = new WorkBean(params);
        if (equalsWorkBean(workBean)) {
            return;
        }

        //将界面跳转和设备状态更新逻辑移交给应用层处理
        Observable observable = Observable.just(workBean);
        Action1<WorkBean> beanAction = new Action1<WorkBean>() {
            @Override
            public void call(WorkBean workBean) {
                if (null != iDeviceReportListener) {
                    iDeviceReportListener.onDeviceReport(workBean);
                }
            }
        };
        observable.subscribeOn(Schedulers.immediate())//指定被观察者执行的线程
                .observeOn(AndroidSchedulers.mainThread())//指定观察者的执行线程
                .subscribe(beanAction);

        //将上报的信息传递给订阅activity，在activity中处理页面刷新
        if (null != listWorkBean && !listWorkBean.isEmpty()) {
            for (Action1 action : listWorkBean) {
                observable.subscribeOn(Schedulers.immediate())//指定被观察者执行的线程
                        .observeOn(AndroidSchedulers.mainThread())//指定观察者的执行线程
                        .subscribe(action);
            }
        }
        oldWorkBean = workBean;
    }

    private boolean equalsWorkBean(WorkBean workBean) {
        if (null != oldWorkBean && null != workBean) {
            return oldWorkBean.equalParam().equals(workBean.equalParam());
        }
        return false;
    }

    //Jni消息上报
    @Override
    public void onMsgNotify(int code, String params) {
        DeviceMessage deviceMessage = new DeviceMessage(code, params);
        //创建一个被观察者对象
        Observable observable = Observable.just(deviceMessage);
        if (null != listMessage && !listMessage.isEmpty()) {
            for (Action1 action : listMessage) {
                observable.subscribeOn(Schedulers.immediate())//指定被观察者执行的线程
                        .observeOn(AndroidSchedulers.mainThread())//指定观察者的执行线程
                        .subscribe(action);

            }
        }
    }

    //Jni 0表示未被订阅，1表示已被订阅
    @Override
    public void onSetSubscribeFlag(int state) {
        //创建一个被观察者对象
        Observable observable = Observable.just(state);

        if (null != listBind && !listBind.isEmpty()) {
            for (Action1 action : listBind) {
                observable.subscribeOn(Schedulers.immediate())//指定被观察者执行的线程
                        .observeOn(AndroidSchedulers.mainThread())//指定观察者的执行线程
                        .subscribe(action);
            }
        }
    }

    @Override
    public void onReportRecordMenuData(byte[] params, int count) {

    }


    @Override
    public void onAuthorCode(String authorcode) {

    }

    @Override
    public void onStartRecord(String params) {

    }
}
