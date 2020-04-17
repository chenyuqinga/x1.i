package com.fotile.message.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.fotile.message.bean.MemorandumDb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MEMORANDUM_DB".
*/
public class MemorandumDbDao extends AbstractDao<MemorandumDb, Long> {

    public static final String TABLENAME = "MEMORANDUM_DB";

    /**
     * Properties of entity MemorandumDb.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_id");
        public final static Property Date = new Property(1, String.class, "date", false, "DATE");
        public final static Property Time = new Property(2, String.class, "time", false, "TIME");
        public final static Property Content = new Property(3, String.class, "content", false, "CONTENT");
        public final static Property From_id = new Property(4, int.class, "from_id", false, "FROM_ID");
        public final static Property From_name = new Property(5, String.class, "from_name", false, "FROM_NAME");
        public final static Property Type = new Property(6, String.class, "type", false, "TYPE");
        public final static Property Title = new Property(7, String.class, "title", false, "TITLE");
        public final static Property Home_name = new Property(8, String.class, "home_name", false, "HOME_NAME");
        public final static Property Home_id = new Property(9, String.class, "home_id", false, "HOME_ID");
        public final static Property Avatar = new Property(10, String.class, "avatar", false, "AVATAR");
    }


    public MemorandumDbDao(DaoConfig config) {
        super(config);
    }
    
    public MemorandumDbDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MEMORANDUM_DB\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"DATE\" TEXT," + // 1: date
                "\"TIME\" TEXT," + // 2: time
                "\"CONTENT\" TEXT," + // 3: content
                "\"FROM_ID\" INTEGER NOT NULL ," + // 4: from_id
                "\"FROM_NAME\" TEXT," + // 5: from_name
                "\"TYPE\" TEXT," + // 6: type
                "\"TITLE\" TEXT," + // 7: title
                "\"HOME_NAME\" TEXT," + // 8: home_name
                "\"HOME_ID\" TEXT," + // 9: home_id
                "\"AVATAR\" TEXT);"); // 10: avatar
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MEMORANDUM_DB\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MemorandumDb entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(2, date);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(3, time);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(4, content);
        }
        stmt.bindLong(5, entity.getFrom_id());
 
        String from_name = entity.getFrom_name();
        if (from_name != null) {
            stmt.bindString(6, from_name);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(7, type);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(8, title);
        }
 
        String home_name = entity.getHome_name();
        if (home_name != null) {
            stmt.bindString(9, home_name);
        }
 
        String home_id = entity.getHome_id();
        if (home_id != null) {
            stmt.bindString(10, home_id);
        }
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(11, avatar);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MemorandumDb entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(2, date);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(3, time);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(4, content);
        }
        stmt.bindLong(5, entity.getFrom_id());
 
        String from_name = entity.getFrom_name();
        if (from_name != null) {
            stmt.bindString(6, from_name);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(7, type);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(8, title);
        }
 
        String home_name = entity.getHome_name();
        if (home_name != null) {
            stmt.bindString(9, home_name);
        }
 
        String home_id = entity.getHome_id();
        if (home_id != null) {
            stmt.bindString(10, home_id);
        }
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(11, avatar);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public MemorandumDb readEntity(Cursor cursor, int offset) {
        MemorandumDb entity = new MemorandumDb( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // date
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // time
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // content
            cursor.getInt(offset + 4), // from_id
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // from_name
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // type
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // title
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // home_name
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // home_id
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10) // avatar
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MemorandumDb entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDate(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTime(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setContent(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setFrom_id(cursor.getInt(offset + 4));
        entity.setFrom_name(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setType(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setTitle(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setHome_name(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setHome_id(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setAvatar(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(MemorandumDb entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(MemorandumDb entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(MemorandumDb entity) {
        return entity.get_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}