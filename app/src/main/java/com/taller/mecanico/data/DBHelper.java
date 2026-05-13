package com.taller.mecanico.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "taller.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CLIENTE = "CREATE TABLE cliente (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "cedula TEXT UNIQUE," +
                "nombre TEXT," +
                "telefono TEXT," +
                "correo TEXT," +
                "direccion TEXT" +
                ")";

        db.execSQL(CREATE_CLIENTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS cliente");
        onCreate(db);
    }
}
