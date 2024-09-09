package com.example.sporty;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

public class BaseDatos extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "entrenos";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "sesiones";

    // Nombres de las columnas
    public static String COLUMN_ID = "_id";
    public  static String COLUMN_ACTIVITY_TYPE = "tipo";
    public static String COLUMN_CIUDAD = "inicio";
    public  static String COLUMN_TIME = "fin";
    public  static String COLUMN_DISTANCE = "distancia";
    public  static String COLUMN_PASOS = "pasos";
    public static String COLUMN_CALORIES = "calorias";
    public static String COLUMN_DATE = "fecha";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ACTIVITY_TYPE + " TEXT, " +
            COLUMN_CIUDAD + " TEXT, " +
            COLUMN_DISTANCE + " REAL, " +
            COLUMN_TIME + " REAL, " +
            COLUMN_PASOS + " REAL,"+
            COLUMN_DATE + " REAL,"+
            COLUMN_CALORIES + " REAL)";

    public BaseDatos(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
