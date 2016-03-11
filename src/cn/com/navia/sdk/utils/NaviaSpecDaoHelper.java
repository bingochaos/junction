package cn.com.navia.sdk.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.com.navia.sdk.bean.RetVal_UpdateItem;
import cn.com.navia.sdk.bean.SpectrumInfo;


/**
 * sqlite的dao操作帮助类
 *
 * @author weisir
 */

public class NaviaSpecDaoHelper implements ISQLiteHelper {

    private static final Logger logger = LoggerFactory.getLogger(NaviaSpecDaoHelper.class);
    private static final String tableName = "NAVIA_SPECS"; // 表名

    private static final String[] cols = {"ID", "BUILDING_ID", "NAME", "VERSION", "FILE", "AVAILABLE"}; // 列名
    private static final int version = 3;

    private DBOpenHelper dbHelper;

    public NaviaSpecDaoHelper(Context context) {
        dbHelper = new DBOpenHelper(context, tableName, null, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see cn.com.navia.sdk.utils.ISQLiteHelper#query(java.lang.String)
     */
    @Override
    public List<SpectrumInfo> query(String selection) {
        return query(selection, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see cn.com.navia.sdk.utils.ISQLiteHelper#query(java.lang.String,
     * java.lang.String[])
     */
    @Override
    public List<SpectrumInfo> query(String selection, String[] args) {
        List<SpectrumInfo> list = new LinkedList<SpectrumInfo>();
        Cursor cursor = null;
        try {
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            cursor = database.query(tableName, cols, selection, args, null, null, null);
            while (cursor.moveToNext()) {

                int id = cursor.getInt(cursor.getColumnIndex(cols[0]));
                int building = cursor.getInt(cursor.getColumnIndex(cols[1]));
                String name = cursor.getString(cursor.getColumnIndex(cols[2]));
                int ver = cursor.getInt(cursor.getColumnIndex(cols[3]));
                String file = cursor.getString(cursor.getColumnIndex(cols[4]));
                int available = cursor.getInt(cursor.getColumnIndex(cols[5]));

                RetVal_UpdateItem updateItem = new RetVal_UpdateItem(id, building, name, ver, available);
                SpectrumInfo spectrumInfo = new SpectrumInfo(updateItem);
                spectrumInfo.setFile(file);
                list.add(spectrumInfo);
            }
        } catch (Exception e) {
            logger.error("query selection:{} args:{}", selection, args, e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return list;
    }

    public long insertOrUpdate(ContentValues values){
        int buildingId = values.getAsInteger(cols[1]);

        List<SpectrumInfo> _infos =  query("BUILDING_ID = ?", new String[]{ buildingId + "" } );
        for(SpectrumInfo info: _infos ){
            int _buildingId = info.getUpdateItem().getBuilding_id();
            if(buildingId == _buildingId){
                delete("BUILDING_ID = "+ buildingId );
            }
        }

       return insert(values);
    }

    /*
     * (non-Javadoc)
     *
     * @see cn.com.navia.sdk.utils.ISQLiteHelper#insert(java.util.List)
     */
    @Override
    public long insert(ContentValues values) {
        long i = 0;
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            // 打开数据库
            database.beginTransaction();

            i = database.insert(tableName, null, values);
            if (values != null) {
                values.clear();
                values = null;
            }

            // 设置事务成功.
            database.setTransactionSuccessful();

        } catch (Exception e) {
            logger.error("insert", e);
        } finally {
            database.endTransaction();
        }
        return i;
    }

    @Override
    public long insert(SpectrumInfo specInfo) {
        ContentValues row = new ContentValues();

        RetVal_UpdateItem updateItem = specInfo.getUpdateItem();

        row.put(cols[0], updateItem.getId());
        row.put(cols[1], updateItem.getBuilding_id());
        row.put(cols[2], updateItem.getName());
        row.put(cols[3], updateItem.getVersion());
        row.put(cols[4], specInfo.getFile());
        row.put(cols[5], updateItem.getAvailable());

        return insertOrUpdate(row);
    }

    /*
     * (non-Javadoc)
     *
     * @see cn.com.navia.sdk.utils.ISQLiteHelper#delete(java.lang.String)
     */
    @Override
    public int delete(String whereClause) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            // 打开数据库
            database.beginTransaction();
            // 设置事务成功.
            int rowCount = database.delete(tableName, whereClause, null);
            database.setTransactionSuccessful();
            return rowCount;
        } catch (Exception e) {
            logger.error("delete selection:{}", whereClause, e);
            return -1;
        } finally {
            database.endTransaction();
        }
    }

    static public void clear(List<Map<String, String>> list) {
        if (null == list) {
            return;
        }
        for (Map<String, String> map : list) {
            if (null != map) {
                map.clear();
            }
        }
        list.clear();
    }

    /*
     * (non-Javadoc)
     *
     * @see cn.com.navia.sdk.utils.ISQLiteHelper#query(java.lang.String,
     * java.lang.String[], cn.com.navia.sdk.utils.SQLiteDaoHelper.RowProcessor)
     */
    @Override
    public <T> List<T> query(String sql, String[] selectionArgs, RowProcessor<T> rp) {
        List<T> list = new LinkedList<T>();
        Cursor c = null;
        try {
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            c = database.rawQuery(sql, selectionArgs);
            while (c.moveToNext()) {
                list.add(rp.process(c));
            }
        } catch (Exception e) {
            logger.error("query sql:{} args:{}", sql, selectionArgs);
        } finally {
            if (null != c) {
                c.close();
            }
        }
        return list;
    }

    // 行处理接口
    public interface RowProcessor<T> {
        T process(Cursor c);
    }

    public class DBOpenHelper extends SQLiteOpenHelper {

        private String tableName;
        private String createSQL;

        public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
            this.tableName = name;
            StringBuilder sql = new StringBuilder();
            sql.append("CREATE TABLE ").append(tableName).append(" (");
            sql.append(cols[0]).append(" integer primary key autoincrement, ");
            sql.append(cols[1]).append(" integer,   ");
            sql.append(cols[2]).append(" text,      ");
            sql.append(cols[3]).append(" integer,   ");
            sql.append(cols[4]).append(" text,      ");
            sql.append(cols[5]).append(" integer    ");
            sql.append(")");
            this.createSQL = sql.toString();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(createSQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
            onCreate(db);
        }
    }

    @Override
    public void close() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        database.close();
    }

}