package porsius.nl.topo.data;

/**
 * Created by linda on 29/10/14.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class MapDatasource {

    // Database fields
    private SQLiteDatabase database;
    private SqliteHelper dbHelper;
    private String[] allColumns = { SqliteHelper.COLUMN_MAP_ID,
            SqliteHelper.COLUMN_MAP_NAME, SqliteHelper.COLUMN_MAP_AUTHOR };

    public MapDatasource(Context context) {
        dbHelper = new SqliteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public MMap createMap(String name, String author) {
        ContentValues values = new ContentValues();
        values.put(SqliteHelper.COLUMN_MAP_NAME, name);
        values.put(SqliteHelper.COLUMN_MAP_AUTHOR, author);
        long insertId = database.insert(SqliteHelper.TABLE_MAP, null,
                values);
        Cursor cursor = database.query(SqliteHelper.TABLE_MAP,
                allColumns, SqliteHelper.COLUMN_MAP_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        MMap newMap = cursorToMap(cursor);
        cursor.close();
        return newMap;
    }

    public void deleteMap(MMap map) {
        long id = map.getId();
        System.out.println("Map deleted with id: " + id);
        database.delete(SqliteHelper.TABLE_MAP, SqliteHelper.COLUMN_MAP_ID
                + " = " + id, null);
    }

    public List<MMap> getAllMaps() {
        List<MMap> maps = new ArrayList<MMap>();

        Cursor cursor = database.query(SqliteHelper.TABLE_MAP,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MMap map = cursorToMap(cursor);
            maps.add(map);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return maps;
    }

    public MMap getLastMap()
    {
        MMap map = null;
        Cursor cursor = database.query(SqliteHelper.TABLE_MAP,
                allColumns, null, null, null, null, null);

        cursor.moveToLast();
        while (!cursor.isAfterLast()) {
            map = cursorToMap(cursor);

            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return map;
    }

    public MMap getMap(int map_id)
    {
        MMap map = null;
        Cursor cursor = database.query(SqliteHelper.TABLE_MAP,
                allColumns, SqliteHelper.COLUMN_MAP_ID
                        + " = " + map_id, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            map = cursorToMap(cursor);

            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return map;
    }
    private MMap cursorToMap(Cursor cursor) {
        MMap map = new MMap();
        map.setId(cursor.getInt(0));
        map.setName(cursor.getString(1));
        map.setAuthor(cursor.getString(2));
        return map;
    }

    public int fromJson(JSONObject object)
    {
        String name = null;
        String author = null;
        int id = -1;
        try {
            if(object.has("map_name"))
                name = object.getString("map_name");

            if(object.has("author"))
                author = object.getString("author");

        } catch (JSONException e) {
            e.printStackTrace();
            return id;
        }

        createMap(name, author);

        return getLastMap().getId();
    }
}

