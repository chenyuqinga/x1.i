package com.fotile.recipe.greendao;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import com.fotile.recipe.bean.recipe.StringConverter;
import java.util.List;

import com.fotile.recipe.bean.recipe.DCookingPromptsInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DCOOKING_PROMPTS_INFO".
*/
public class DCookingPromptsInfoDao extends AbstractDao<DCookingPromptsInfo, Long> {

    public static final String TABLENAME = "DCOOKING_PROMPTS_INFO";

    /**
     * Properties of entity DCookingPromptsInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_id");
        public final static Property RecipeId = new Property(1, String.class, "recipeId", false, "RECIPE_ID");
        public final static Property DeviceId = new Property(2, Long.class, "deviceId", false, "DEVICE_ID");
        public final static Property Index = new Property(3, String.class, "index", false, "INDEX");
        public final static Property Type = new Property(4, String.class, "type", false, "TYPE");
        public final static Property PromptText = new Property(5, String.class, "promptText", false, "PROMPT_TEXT");
        public final static Property ButtonType = new Property(6, String.class, "buttonType", false, "BUTTON_TYPE");
        public final static Property ButtonText = new Property(7, String.class, "buttonText", false, "BUTTON_TEXT");
        public final static Property PromptImages = new Property(8, String.class, "promptImages", false, "PROMPT_IMAGES");
        public final static Property BackgroupImages = new Property(9, String.class, "backgroupImages", false, "BACKGROUP_IMAGES");
        public final static Property Time = new Property(10, String.class, "time", false, "TIME");
        public final static Property Describe = new Property(11, String.class, "describe", false, "DESCRIBE");
    }

    private DaoSession daoSession;

    private final StringConverter promptImagesConverter = new StringConverter();
    private final StringConverter backgroupImagesConverter = new StringConverter();
    private Query<DCookingPromptsInfo> dDevice_CookingPromptsInfosQuery;

    public DCookingPromptsInfoDao(DaoConfig config) {
        super(config);
    }
    
    public DCookingPromptsInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DCOOKING_PROMPTS_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"RECIPE_ID\" TEXT," + // 1: recipeId
                "\"DEVICE_ID\" INTEGER," + // 2: deviceId
                "\"INDEX\" TEXT," + // 3: index
                "\"TYPE\" TEXT," + // 4: type
                "\"PROMPT_TEXT\" TEXT," + // 5: promptText
                "\"BUTTON_TYPE\" TEXT," + // 6: buttonType
                "\"BUTTON_TEXT\" TEXT," + // 7: buttonText
                "\"PROMPT_IMAGES\" TEXT," + // 8: promptImages
                "\"BACKGROUP_IMAGES\" TEXT," + // 9: backgroupImages
                "\"TIME\" TEXT," + // 10: time
                "\"DESCRIBE\" TEXT);"); // 11: describe
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DCOOKING_PROMPTS_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DCookingPromptsInfo entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String recipeId = entity.getRecipeId();
        if (recipeId != null) {
            stmt.bindString(2, recipeId);
        }
 
        Long deviceId = entity.getDeviceId();
        if (deviceId != null) {
            stmt.bindLong(3, deviceId);
        }
 
        String index = entity.getIndex();
        if (index != null) {
            stmt.bindString(4, index);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(5, type);
        }
 
        String promptText = entity.getPromptText();
        if (promptText != null) {
            stmt.bindString(6, promptText);
        }
 
        String buttonType = entity.getButtonType();
        if (buttonType != null) {
            stmt.bindString(7, buttonType);
        }
 
        String buttonText = entity.getButtonText();
        if (buttonText != null) {
            stmt.bindString(8, buttonText);
        }
 
        List promptImages = entity.getPromptImages();
        if (promptImages != null) {
            stmt.bindString(9, promptImagesConverter.convertToDatabaseValue(promptImages));
        }
 
        List backgroupImages = entity.getBackgroupImages();
        if (backgroupImages != null) {
            stmt.bindString(10, backgroupImagesConverter.convertToDatabaseValue(backgroupImages));
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(11, time);
        }
 
        String describe = entity.getDescribe();
        if (describe != null) {
            stmt.bindString(12, describe);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DCookingPromptsInfo entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String recipeId = entity.getRecipeId();
        if (recipeId != null) {
            stmt.bindString(2, recipeId);
        }
 
        Long deviceId = entity.getDeviceId();
        if (deviceId != null) {
            stmt.bindLong(3, deviceId);
        }
 
        String index = entity.getIndex();
        if (index != null) {
            stmt.bindString(4, index);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(5, type);
        }
 
        String promptText = entity.getPromptText();
        if (promptText != null) {
            stmt.bindString(6, promptText);
        }
 
        String buttonType = entity.getButtonType();
        if (buttonType != null) {
            stmt.bindString(7, buttonType);
        }
 
        String buttonText = entity.getButtonText();
        if (buttonText != null) {
            stmt.bindString(8, buttonText);
        }
 
        List promptImages = entity.getPromptImages();
        if (promptImages != null) {
            stmt.bindString(9, promptImagesConverter.convertToDatabaseValue(promptImages));
        }
 
        List backgroupImages = entity.getBackgroupImages();
        if (backgroupImages != null) {
            stmt.bindString(10, backgroupImagesConverter.convertToDatabaseValue(backgroupImages));
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(11, time);
        }
 
        String describe = entity.getDescribe();
        if (describe != null) {
            stmt.bindString(12, describe);
        }
    }

    @Override
    protected final void attachEntity(DCookingPromptsInfo entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public DCookingPromptsInfo readEntity(Cursor cursor, int offset) {
        DCookingPromptsInfo entity = new DCookingPromptsInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // recipeId
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // deviceId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // index
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // type
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // promptText
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // buttonType
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // buttonText
            cursor.isNull(offset + 8) ? null : promptImagesConverter.convertToEntityProperty(cursor.getString(offset + 8)), // promptImages
            cursor.isNull(offset + 9) ? null : backgroupImagesConverter.convertToEntityProperty(cursor.getString(offset + 9)), // backgroupImages
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // time
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11) // describe
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DCookingPromptsInfo entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setRecipeId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDeviceId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setIndex(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setType(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setPromptText(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setButtonType(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setButtonText(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setPromptImages(cursor.isNull(offset + 8) ? null : promptImagesConverter.convertToEntityProperty(cursor.getString(offset + 8)));
        entity.setBackgroupImages(cursor.isNull(offset + 9) ? null : backgroupImagesConverter.convertToEntityProperty(cursor.getString(offset + 9)));
        entity.setTime(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setDescribe(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(DCookingPromptsInfo entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(DCookingPromptsInfo entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(DCookingPromptsInfo entity) {
        return entity.get_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "cookingPromptsInfos" to-many relationship of DDevice. */
    public List<DCookingPromptsInfo> _queryDDevice_CookingPromptsInfos(Long deviceId) {
        synchronized (this) {
            if (dDevice_CookingPromptsInfosQuery == null) {
                QueryBuilder<DCookingPromptsInfo> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.DeviceId.eq(null));
                dDevice_CookingPromptsInfosQuery = queryBuilder.build();
            }
        }
        Query<DCookingPromptsInfo> query = dDevice_CookingPromptsInfosQuery.forCurrentThread();
        query.setParameter(0, deviceId);
        return query.list();
    }

}