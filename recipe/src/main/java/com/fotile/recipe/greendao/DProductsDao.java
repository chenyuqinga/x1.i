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

import com.fotile.recipe.bean.recipe.DProducts;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DPRODUCTS".
*/
public class DProductsDao extends AbstractDao<DProducts, Long> {

    public static final String TABLENAME = "DPRODUCTS";

    /**
     * Properties of entity DProducts.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_id");
        public final static Property RecipeId = new Property(1, String.class, "recipeId", false, "RECIPE_ID");
        public final static Property DeviceId = new Property(2, Long.class, "deviceId", false, "DEVICE_ID");
        public final static Property Id = new Property(3, String.class, "id", false, "ID");
        public final static Property Name = new Property(4, String.class, "name", false, "NAME");
    }

    private Query<DProducts> dDevice_ProductsesQuery;

    public DProductsDao(DaoConfig config) {
        super(config);
    }
    
    public DProductsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DPRODUCTS\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"RECIPE_ID\" TEXT," + // 1: recipeId
                "\"DEVICE_ID\" INTEGER," + // 2: deviceId
                "\"ID\" TEXT," + // 3: id
                "\"NAME\" TEXT);"); // 4: name
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DPRODUCTS\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DProducts entity) {
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
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(4, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DProducts entity) {
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
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(4, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public DProducts readEntity(Cursor cursor, int offset) {
        DProducts entity = new DProducts( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // recipeId
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // deviceId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // id
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // name
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DProducts entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setRecipeId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDeviceId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setId(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(DProducts entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(DProducts entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(DProducts entity) {
        return entity.get_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "productses" to-many relationship of DDevice. */
    public List<DProducts> _queryDDevice_Productses(Long deviceId) {
        synchronized (this) {
            if (dDevice_ProductsesQuery == null) {
                QueryBuilder<DProducts> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.DeviceId.eq(null));
                dDevice_ProductsesQuery = queryBuilder.build();
            }
        }
        Query<DProducts> query = dDevice_ProductsesQuery.forCurrentThread();
        query.setParameter(0, deviceId);
        return query.list();
    }

}
