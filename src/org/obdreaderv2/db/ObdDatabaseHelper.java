package org.obdreaderv2.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.obdreaderv2.io.ObdReader;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class ObdDatabaseHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 3;
    public static final String OBD_TABLE_NAME = "obd";
    public static final String DATABASE_NAME = "obd";
    public static final String MAP_DATA_COLUMN = "map_data";
    public static final String ID_COLUMN = "id";
    public static final String OBS_TIME_COLUMN = "obs_time";
    public static final String OBD_TABLE_CREATE =
                "CREATE TABLE " + OBD_TABLE_NAME + " (" +
                ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MAP_DATA_COLUMN + " TEXT, " +
                OBS_TIME_COLUMN + " REAL);";
    
    public static final String ENGINE_CODE_COLUMN = "engine_code";
    public static final String ENGINE_CODES_TABLE_NAME = "engine_codes";
    public static final String ENGINE_CODES_TABLE_CREATE = "CREATE TABLE " + ENGINE_CODES_TABLE_NAME + " (" + 
			ENGINE_CODE_COLUMN + " TEXT," +
			OBS_TIME_COLUMN + " REAL);";

    public ObdDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(OBD_TABLE_CREATE);
        db.execSQL(ENGINE_CODES_TABLE_CREATE);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		String sql = String.format("drop table if exists %s", OBD_TABLE_NAME);
		db.execSQL(sql);
		sql = String.format("drop table if exists %s", ENGINE_CODES_TABLE_NAME);
		db.execSQL(sql);
		onCreate(db);
	}

	public void addEngineCode(EngineCodeObject obj) {
		SQLiteDatabase db = getWritableDatabase();
		String sql = String.format("insert into %s (%s,%s) values (?,?)", ENGINE_CODES_TABLE_NAME, ENGINE_CODE_COLUMN, OBS_TIME_COLUMN);
		SQLiteStatement st = db.compileStatement(sql);
		st.bindString(1, obj.getEngineCode());
		st.bindLong(2, obj.getStartTime());
		st.execute();
		st.close();
		db.close();
	}

	public void clearEngineCode(String code) {
		SQLiteDatabase db = getWritableDatabase();
		String sql = String.format("delete from %s where %s = ?", ENGINE_CODES_TABLE_NAME, ENGINE_CODE_COLUMN);
		SQLiteStatement st = db.compileStatement(sql);
		st.bindString(1, code);
		st.execute();
		st.close();
		db.close();
	}

	public List<EngineCodeObject> getEngineCodes() {
		List<EngineCodeObject> data = new ArrayList<EngineCodeObject>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(ENGINE_CODES_TABLE_NAME, new String[]{ENGINE_CODE_COLUMN,OBS_TIME_COLUMN}, null, null, null, null, null);
		c.moveToFirst();
		while (!c.isAfterLast()){
			String engineCode = c.getString(0);
			long obstime = c.getLong(1);
			EngineCodeObject obj = new EngineCodeObject(engineCode, obstime);
			data.add(obj);
			c.moveToNext();
		}
		c.close();
		db.close();
		return data;
	}

	public void writeMapData(double obstime, JSONObject data) {
		SQLiteDatabase db = getWritableDatabase();
		String sql = String.format("insert into %s (%s,%s) values (?,?)", OBD_TABLE_NAME,
																			MAP_DATA_COLUMN,
																			OBS_TIME_COLUMN);
		SQLiteStatement stmt = db.compileStatement(sql);
		String json = data.toString();
		stmt.bindString(1,json);
		stmt.bindDouble(2,obstime);
		stmt.execute();
		stmt.close();
		db.close();
	}
	
	public void clearData(double maxAge) {
		SQLiteDatabase db = getWritableDatabase();
		String sql = String.format("delete from %s where %s < ?", OBD_TABLE_NAME, OBS_TIME_COLUMN);
		SQLiteStatement stmt = db.compileStatement(sql);
		double curr_time = System.currentTimeMillis() * 0.001;
		double min_time = curr_time - maxAge;
		stmt.bindDouble(1,min_time);
		stmt.execute();
		stmt.close();
		db.close();
	}
}
