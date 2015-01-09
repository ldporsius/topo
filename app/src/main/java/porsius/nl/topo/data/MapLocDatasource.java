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
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapLocDatasource {

    // Database fields
    private SQLiteDatabase database;
    private SqliteHelper dbHelper;
    private String[] allColumns = { SqliteHelper.COLUMN_MAP_LOC_ID,
            SqliteHelper.COLUMN_MAP_LOC_MAP_ID, SqliteHelper.COLUMN_MAP_LOC_NAME, SqliteHelper.COLUMN_MAP_LOC_LAT, SqliteHelper.COLUMN_MAP_LOC_LNG };

    public MapLocDatasource(Context context) {
        dbHelper = new SqliteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public MMapLoc createLoc(int map_id, String name, float lat, float lng) {
        ContentValues values = new ContentValues();
        values.put(SqliteHelper.COLUMN_MAP_LOC_MAP_ID, map_id);
        values.put(SqliteHelper.COLUMN_MAP_LOC_NAME, name);
        values.put(SqliteHelper.COLUMN_MAP_LOC_LAT, lat);
        values.put(SqliteHelper.COLUMN_MAP_LOC_LNG, lng);
        long insertId = database.insert(SqliteHelper.TABLE_MAP_LOC, null,
                values);
        Cursor cursor = database.query(SqliteHelper.TABLE_MAP_LOC,
                allColumns, SqliteHelper.COLUMN_MAP_LOC_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        MMapLoc newMapLoc = cursorToMap(cursor);
        cursor.close();

        System.out.println("xxx Loc created with map_id: " + map_id);
        return newMapLoc;
    }

    public void deleteMapLoc(MMapLoc loc) {
        long id = loc.getId();
        System.out.println("xxx Loc deleted with id: " + id);
        database.delete(SqliteHelper.TABLE_MAP_LOC, SqliteHelper.COLUMN_MAP_LOC_ID
                + " = " + id, null);
    }

    public void deleteAllLocsForMap(MMap map) {
        long id = map.getId();

        database.delete(SqliteHelper.TABLE_MAP_LOC, SqliteHelper.COLUMN_MAP_LOC_MAP_ID
                + " = " + id, null);
    }

    public List<MMapLoc> getAllLocs(int map_id) {
        List<MMapLoc> maps = new ArrayList<MMapLoc>();

        Cursor cursor = database.query(SqliteHelper.TABLE_MAP_LOC,
                allColumns,SqliteHelper.COLUMN_MAP_LOC_MAP_ID + " = " + map_id, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MMapLoc map = cursorToMap(cursor);
            maps.add(map);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return maps;
    }

    private MMapLoc cursorToMap(Cursor cursor) {
        MMapLoc map = new MMapLoc();
        map.setId(cursor.getInt(0));
        map.setMap_id(cursor.getInt(1));
        map.setName(cursor.getString(2));
        map.setLat(cursor.getDouble(3));
        map.setLng(cursor.getDouble(4));
        return map;
    }

    public String toJson(MMap map, List<MMapLoc> list, String author)
    {

        JSONObject mapObject = new JSONObject();

        JSONArray array = new JSONArray();

        try
        {
            for(MMapLoc loc: list)
            {
                JSONObject object = new JSONObject();
                object.put("id", loc.getId());
                object.put("map_id", loc.getMap_id());
                object.put("name", loc.getName());
                object.put("lat", loc.getLat());
                object.put("lng", loc.getLng());

                array.put(object);
            }
            mapObject.put("author", author);
            mapObject.put("map_id", map.getId());
            mapObject.put("map_name", map.getName());
            mapObject.put("locations", array);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Log.i(" json xxx: ", mapObject.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mapObject.toString();
    }

    public void fromJson(int map_id, JSONObject object)
    {

        String name = null;
        float lat = 0;
        float lng = 0;

        try {

            if(object.has("name"))
                name = object.getString("name");

            if(object.has("lat"))
                lat = (float) object.getDouble("lat");

            if(object.has("lng"))
                lng = (float) object.getDouble("lng");

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        createLoc(map_id,name,lat,lng);

    }
}

