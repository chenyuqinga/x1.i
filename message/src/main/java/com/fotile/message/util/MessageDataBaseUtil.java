package com.fotile.message.util;

import android.content.Context;


import com.fotile.message.bean.Memorandum;
import com.fotile.message.bean.MemorandumDb;
import com.fotile.message.bean.NotificationDb;
import com.fotile.message.manager.DatabaseObserver;
import com.fotile.message.manager.MessageDaoManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * 文件名称：MessageDataBaseUtil
 * 创建时间：17-8-29 上午9:47
 * 文件作者：yujiaying
 * 功能描述：数据库工具类
 */

public class MessageDataBaseUtil {
    /**
     * 单例对象
     */
    private static MessageDataBaseUtil instance = null;
    private MessageDaoManager recipeDaoManager;
    /**
     * 数据观察者键值对列表,键:观察者,值:观察的数据表(标识)
     */
    private Map<DatabaseObserver, String> observerMap = new HashMap<>();

    private MessageDataBaseUtil(){

    }

    /**
     * 单例模式,获取对象实例
     *
     * @return
     */
    public static MessageDataBaseUtil getInstance() {
        if (instance == null) {
            sycInstance();
        }
        return instance;

    }

    /**
     * 同步构建实例
     */
    private synchronized static void sycInstance() {
        instance = new MessageDataBaseUtil();
    }

    /**
     * 初始化,创建数据库管理类实例
     *
     * @param context
     */
    public void init(Context context) {
        recipeDaoManager = new MessageDaoManager(context);
    }

    /**
     * 注册数据变化观察者
     *
     * @param type     表类型(菜谱,常用模式等)
     * @param observer 观察者
     */
    public void registerDatabaseObserver(String type, DatabaseObserver observer) {
        observerMap.put(observer, type);
    }

    /**
     * 取消注册数据变化观察者,从列表中移除
     *
     * @param observer
     */
    public void unregisterDatabaseObserver(DatabaseObserver observer) {
        observerMap.remove(observer);
    }

    /**
     * 得到家庭备忘消息天数
     *
     * @return
     */
    public List<String> getDateAllMemorandum() {
        return MessageDateUtil.getIntDateList(recipeDaoManager.queryAllMemorandumDate());
    }

    /**
     * 得到消息通知消息天数
     *
     * @return
     */
    public List<String> getDateAllNotification() {
        return MessageDateUtil.getIntDateList(recipeDaoManager.queryAllNotifiacationDate());
    }

    /**
     * 查询所有
     */
    public Observable<List<MemorandumDb>> queryAllMemorandumBean() {
        return recipeDaoManager.queryAllMemorandumDb().list();
    }

    public List<MemorandumDb> queryAllMemorandum() {
        return recipeDaoManager.queryAllMemorandum();
    }

    /**
     * 删除点击删除按钮当天的家庭备忘
     *
     * @param
     */
    public void deleteMemorandum(String deleteDay) {
        recipeDaoManager.deleteByMemorandumDateList(MessageDateUtil.needDeleteDateList(deleteDay, recipeDaoManager.queryAllMemorandumDate()));
    }

//    /**
//     * 删除点击删除按钮当天的通知
//     *
//     * @param
//     */
//    public void deleteNotification(String deleteDay) {
//        recipeDaoManager.deleteByNotifiactionDateList(MessageDateUtil.needDeleteDateList(deleteDay, recipeDaoManager.queryAllNotifiacationDate()));
//    }
    /**
     * 删除当前ID消息通知
     */
    public void deleteIdNotification(NotificationDb info) {
        recipeDaoManager.deletebyInfoID(info);
    }
    /**
     * 删除当前ID备忘录通知
     */
    public void deleteIdMemorandum(MemorandumDb info) {
        recipeDaoManager.deletebyInfoID(info);
    }
    /**
     * 查询所有
     */
    public Observable<List<NotificationDb>> queryAllNotificationDb() {
        return recipeDaoManager.queryAllNotificationDb().list();
    }

    public List<NotificationDb> queryAllNotification() {
        return recipeDaoManager.queryAllNotification();
    }

    /**
     * 删除超过30天的家庭备忘
     */
    public void deleteOverdayMemorandum() {
        recipeDaoManager.deleteByMemorandumDateList(MessageDateUtil.needDeleteDateList(recipeDaoManager.queryAllMemorandumDate()));
    }

    /**
     * 删除超过30天的通知
     */
    public void deleteOverdayNotification() {
        recipeDaoManager.deleteByNotifiactionDateList(MessageDateUtil.needDeleteDateList(recipeDaoManager.queryAllNotifiacationDate()));
    }

    /**
     * 删除所有家庭备忘
     */
    public void deleteAllMemorandum() {
        recipeDaoManager.deleteAllMemorandum();
    }

    /**
     * 删除所有消息通知
     */
    public void deleteAllNotification() {
        recipeDaoManager.deleteAllNotification();
    }

    /**
     * 插入消息通知
     * @param notificationDb
     * @return
     */
    public long insertNotification(NotificationDb notificationDb) {
        return recipeDaoManager.insertNotification(notificationDb);
    }

    /**
     * 插入家庭备忘
     * @param MemorandumDb
     * @return
     */
    public long insertMemorandum(MemorandumDb MemorandumDb) {
        return recipeDaoManager.insertMemorandum(MemorandumDb);
    }

    /**
     * 插入请求的家庭备忘录
     * @param memorandum
     */
    public void insertMemorandum(Memorandum memorandum) {
        MemorandumDb memorandumDb = new MemorandumDb();
        if (!memorandum.getTime().isEmpty()) {
            String time = memorandum.getTime();
            memorandumDb.setDate(time.substring(0,time.indexOf("T")).trim());
            memorandumDb.setTime(time.substring(time.indexOf("T")+1,time.indexOf(".")-1).trim());
        }
        if (!memorandum.getContent().isEmpty())
            memorandumDb.setContent(memorandum.getContent());
        if (!memorandum.getHomeId().isEmpty())
            memorandumDb.setHome_id(memorandum.getHomeId());
        if (!memorandum.getType().isEmpty())
            memorandumDb.setType(memorandum.getType());
        if (!memorandum.getTitle().isEmpty())
            memorandumDb.setTitle(memorandum.getTitle());
        insertMemorandum(memorandumDb);
    }
}
