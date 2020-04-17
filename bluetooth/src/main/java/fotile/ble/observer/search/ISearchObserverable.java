package fotile.ble.observer.search;

import java.util.concurrent.CopyOnWriteArrayList;

import fotile.ble.bean.BleDevice;
import rx.functions.Action1;

/**
 * 项目名称：BleClient
 * 创建时间：2018/9/3 17:34
 * 文件作者：yaohx
 * 功能描述：ble搜索Observerable
 */
public interface ISearchObserverable {
    CopyOnWriteArrayList<Action1<BleDevice>> list_search = new CopyOnWriteArrayList<Action1<BleDevice>>();

    /**
     * 添加观察者
     *
     * @param iAction
     */
    void addSearchObserver(Action1<BleDevice> iAction);

    /**
     * 清空观察者
     */
    void removeSearchObserver(Action1<BleDevice> iAction);

    void notifySearchData(BleDevice bleDevice);

}
