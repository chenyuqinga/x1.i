package com.fotile.message.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import com.fotile.message.bean.MemorandumDb;
import com.fotile.message.bean.NotificationDb;
import com.fotile.message.greendao.DaoMaster;
import com.fotile.message.greendao.DaoSession;
import com.fotile.message.greendao.MemorandumDbDao;
import com.fotile.message.greendao.NotificationDbDao;
import com.fotile.message.util.MessageConstant;

import org.greenrobot.greendao.rx.RxQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件名称：RecipeNetDaoManager
 * 创建时间：17-8-29 上午9:47
 * 文件作者：yujiaying
 * 功能描述：管理数据库
 */

public class MessageDaoManager {
    private Context context;
    private DaoMaster.DevOpenHelper devOpenHelper;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private MemorandumDbDao memorandumDbDao;
    private NotificationDbDao notificationDbDao;

    public MessageDaoManager(Context context) {
        this.context = context;
        initDatabase();
    }

    /**
     * 初始化数据库
     */
    private void initDatabase() {
        devOpenHelper = new DaoMaster.DevOpenHelper(context, MessageConstant.DB_MESSAGE, null);
        db = devOpenHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        memorandumDbDao = daoSession.getMemorandumDbDao();
        notificationDbDao = daoSession.getNotificationDbDao();
    }

    /**
     * 搜索家庭备忘录
     *
     * @return
     */
    public RxQuery<MemorandumDb> queryAllMemorandumDb() {
        return memorandumDbDao.queryBuilder().rx();
    }

    /**
     * 搜索消息通知
     *
     * @return
     */
    public RxQuery<NotificationDb> queryAllNotificationDb() {
        return notificationDbDao.queryBuilder().rx();
    }

    public List<MemorandumDb> queryAllMemorandum() {
        return memorandumDbDao.loadAll();
    }

    public List<NotificationDb> queryAllNotification() {
        return notificationDbDao.loadAll();
    }

    /**
     * @param dateList
     */
    public void deleteByNotifiactionDateList(List<String> dateList) {
        daoSession.getDatabase().beginTransaction();
        try {
            for (String date : dateList) {
                notificationDbDao.queryBuilder().where(NotificationDbDao.Properties.Date.eq(date)).buildDelete().executeDeleteWithoutDetachingEntities();
            }
            daoSession.getDatabase().setTransactionSuccessful();
        } finally {
            daoSession.getDatabase().endTransaction();
        }
    }

    public List<String> queryAllMemorandumDate() {
        List<String> strList = new ArrayList<>();
        memorandumDbDao.queryBuilder().rx();
        List<MemorandumDb> dbs = memorandumDbDao.queryBuilder().list();
        for (MemorandumDb db : dbs) {
            if (db != null) {
                String date = db.getDate();
                if (!strList.contains(date)) {
                    strList.add(date);
                }
            }

        }
        return strList;
    }

    public List<String> queryAllNotificationDate() {
        List<String> strList = new ArrayList<>();
        notificationDbDao.queryBuilder().rx();
        List<NotificationDb> dbs = notificationDbDao.queryBuilder().list();
        for (NotificationDb db : dbs) {
            if (db != null) {
                String date = db.getDate();
                if (!strList.contains(date)) {
                    strList.add(date);
                }
            }

        }
        return strList;
    }

    /**
     * @param dateList
     */
    public void deleteByMemorandumDateList(List<String> dateList) {
        daoSession.getDatabase().beginTransaction();
        try {
            for (String date : dateList) {
                memorandumDbDao.queryBuilder().where(MemorandumDbDao.Properties.Date.eq(date)).buildDelete().executeDeleteWithoutDetachingEntities();
            }
            daoSession.getDatabase().setTransactionSuccessful();
        } finally {
            daoSession.getDatabase().endTransaction();
        }
    }

    /**
     * @param dateList
     */
    public void deleteByNotificationDateList(List<String> dateList) {
        daoSession.getDatabase().beginTransaction();
        try {
            for (String date : dateList) {
                notificationDbDao.queryBuilder().where(NotificationDbDao.Properties.Date.eq(date)).buildDelete().executeDeleteWithoutDetachingEntities();
            }
            daoSession.getDatabase().setTransactionSuccessful();
        } finally {
            daoSession.getDatabase().endTransaction();
        }
    }

    public List<String> queryAllNotifiacationDate() {
        List<String> strList = new ArrayList<>();
        notificationDbDao.queryBuilder().rx();
        List<NotificationDb> dbs = notificationDbDao.queryBuilder().list();
        for (NotificationDb db : dbs) {
            if (db != null) {
                String date = db.getDate();
                if (!strList.contains(date)) {
                    strList.add(date);
                }
            }

        }
        return strList;
    }

    /**
     * 插入家庭备忘
     *
     * @param memorandumDb
     * @return
     */
    public long insertMemorandum(MemorandumDb memorandumDb) {
        if (null != memorandumDb && null != memorandumDbDao) {
            return memorandumDbDao.insertOrReplace(memorandumDb);
        } else {
            return -1;
        }
    }

    /**
     * 插入通知
     *
     * @param notificationDb
     * @return
     */
    public long insertNotification(NotificationDb notificationDb) {
        if (null != notificationDb && null != notificationDbDao) {
            return notificationDbDao.insertOrReplace(notificationDb);
        } else {
            return -1;
        }
    }

    /**
     * 删除所有通知
     */
    public void deleteAllNotification() {
        daoSession.getDatabase().beginTransaction();
        try {
            notificationDbDao.deleteAll();
            daoSession.getDatabase().setTransactionSuccessful();
        } finally {
            daoSession.getDatabase().endTransaction();
        }
    }

    /**
     * 删除所有家庭备忘录
     */
    public void deleteAllMemorandum() {
        daoSession.getDatabase().beginTransaction();
        try {
            memorandumDbDao.deleteAll();
            daoSession.getDatabase().setTransactionSuccessful();
        } finally {
            daoSession.getDatabase().endTransaction();
        }
    }

    /**
     * 删除通知记录
     *
     * @return
     */
    public void deletebyInfoID(NotificationDb info) {
        if(info!=null && info.get_id()!=0){
            notificationDbDao.deleteByKey(info.get_id());
        }
    }

    /**
     * 删除备忘录记录
     *
     * @return
     */
    public void deletebyInfoID(MemorandumDb info) {
        if(info!=null && info.get_id()!=0){
            memorandumDbDao.deleteByKey(info.get_id());
        }
    }
}
