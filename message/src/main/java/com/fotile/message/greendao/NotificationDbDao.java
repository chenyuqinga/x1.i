package com.fotile.message.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.fotile.message.bean.NotificationDb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "NOTIFICATION_DB".
*/
public class NotificationDbDao extends AbstractDao<NotificationDb, Long> {

    public static final String TABLENAME = "NOTIFICATION_DB";

    /**
     * Properties of entity NotificationDb.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_id");
        public final static Property Date = new Property(1, String.class, "date", false, "DATE");
        public final static Property Time = new Property(2, String.class, "time", false, "TIME");
        public final static Property From_id = new Property(3, int.class, "from_id", false, "FROM_ID");
        public final static Property Content = new Property(4, String.class, "content", false, "CONTENT");
        public final static Property Type = new Property(5, String.class, "type", false, "TYPE");
        public final static Property Avatar = new Property(6, String.class, "avatar", false, "AVATAR");
        public final static Property Recipe_name = new Property(7, String.class, "recipe_name", false, "RECIPE_NAME");
        public final static Property From_name = new Property(8, String.class, "from_name", false, "FROM_NAME");
    }


    public NotificationDbDao(DaoConfig config) {
        super(config);
    }
    
    public NotificationDbDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"NOTIFICATION_DB\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"DATE\" TEXT," + // 1: date
                "\"TIME\" TEXT," + // 2: time
                "\"FROM_ID\" INTEGER NOT NULL ," + // 3: from_id
                "\"CONTENT\" TEXT," + // 4: content
                "\"TYPE\" TEXT," + // 5: type
                "\"AVATAR\" TEXT," + // 6: avatar
                "\"RECIPE_NAME\" TEXT," + // 7: recipe_name
                "\"FROM_NAME\" TEXT);"); // 8: from_name
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NOTIFICATION_DB\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, NotificationDb entity) {
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
        stmt.bindLong(4, entity.getFrom_id());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(5, content);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(6, type);
        }
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(7, avatar);
        }
 
        String recipe_name = entity.getRecipe_name();
        if (recipe_name != null) {
            stmt.bindString(8, recipe_name);
        }
 
        String from_name = entity.getFrom_name();
        if (from_name != null) {
            stmt.bindString(9, from_name);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, NotificationDb entity) {
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
        stmt.bindLong(4, entity.getFrom_id());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(5, content);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(6, type);
        }
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(7, avatar);
        }
 
        String recipe_name = entity.getRecipe_name();
        if (recipe_name != null) {
            stmt.bindString(8, recipe_name);
        }
 
        String from_name = entity.getFrom_name();
        if (from_name != null) {
            stmt.bindString(9, from_name);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public NotificationDb readEntity(Cursor cursor, int offset) {
        NotificationDb entity = new NotificationDb( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // date
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // time
            cursor.getInt(offset + 3), // from_id
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // content
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // type
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // avatar
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // recipe_name
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8) // from_name
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, NotificationDb entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDate(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTime(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setFrom_id(cursor.getInt(offset + 3));
        entity.setContent(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setType(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setAvatar(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setRecipe_name(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setFrom_name(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(NotificationDb entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(NotificationDb entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(NotificationDb entity) {
        return entity.get_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
