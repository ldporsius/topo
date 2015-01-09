package porsius.nl.topo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by linda on 28/10/14.
 */
public class SqliteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "topo.db";

    public static final String TABLE_MAP = "map";
    public static final String COLUMN_MAP_ID = "map_id";
    public static final String COLUMN_MAP_NAME = "map_name";
    public static final String COLUMN_MAP_AUTHOR = "map_author";

    public static final String TABLE_MAP_LOC = "map_loc";
    public static final String COLUMN_MAP_LOC_ID = "map_loc_id";
    public static final String COLUMN_MAP_LOC_MAP_ID = "map_loc_map_id";
    public static final String COLUMN_MAP_LOC_NAME = "map_loc_name";
    public static final String COLUMN_MAP_LOC_LAT = "map_loc_lat";
    public static final String COLUMN_MAP_LOC_LNG = "map_loc_lng";


    private static final String CREATE_MAP =
            "create table " + TABLE_MAP +
            "(" + COLUMN_MAP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
             COLUMN_MAP_NAME +" TEXT NOT NULL, " +
             COLUMN_MAP_AUTHOR + " TEXT" +
              ");";

    private static final String CREATE_MAP_LOC =
            "create table " + TABLE_MAP_LOC +
                    "(" + COLUMN_MAP_LOC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_MAP_LOC_MAP_ID +" INTEGER NOT NULL," +
                    COLUMN_MAP_LOC_NAME + " TEXT NOT NULL, " +
                    COLUMN_MAP_LOC_LAT + " REAL NOT NULL," +
                    COLUMN_MAP_LOC_LNG + " REAL NOT NULL" +
                    ");";


    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_MAP);
        db.execSQL(CREATE_MAP_LOC);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SqliteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAP_LOC);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAP_LOC);
        onCreate(db);
    }
}
