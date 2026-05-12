package com.taller.mecanico;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "taller_mecanico.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USUARIOS = "usuarios";
    public static final String COL_ID = "id";
    public static final String COL_CEDULA = "cedula";
    public static final String COL_NOMBRES = "nombres";
    public static final String COL_APELLIDOS = "apellidos";
    public static final String COL_FECHA = "fecha_nacimiento";
    public static final String COL_EDAD = "edad";
    public static final String COL_NACIONALIDAD = "nacionalidad";
    public static final String COL_GENERO = "genero";
    public static final String COL_ESTADO_CIVIL = "estado_civil";
    public static final String COL_NIVEL_INGLES = "nivel_ingles";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_USUARIOS + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_CEDULA + " TEXT UNIQUE NOT NULL, " +
            COL_NOMBRES + " TEXT, " +
            COL_APELLIDOS + " TEXT, " +
            COL_FECHA + " TEXT, " +
            COL_EDAD + " TEXT, " +
            COL_NACIONALIDAD + " TEXT, " +
            COL_GENERO + " TEXT, " +
            COL_ESTADO_CIVIL + " TEXT, " +
            COL_NIVEL_INGLES + " REAL)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    public long insertarUsuario(String cedula, String nombres, String apellidos,
                                 String fecha, String edad, String nacionalidad,
                                 String genero, String estadoCivil, float nivelIngles) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CEDULA, cedula);
        values.put(COL_NOMBRES, nombres);
        values.put(COL_APELLIDOS, apellidos);
        values.put(COL_FECHA, fecha);
        values.put(COL_EDAD, edad);
        values.put(COL_NACIONALIDAD, nacionalidad);
        values.put(COL_GENERO, genero);
        values.put(COL_ESTADO_CIVIL, estadoCivil);
        values.put(COL_NIVEL_INGLES, nivelIngles);
        long resultado = db.insertOrThrow(TABLE_USUARIOS, null, values);
        db.close();
        return resultado;
    }

    public Cursor buscarPorCedula(String cedula) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USUARIOS, null,
                COL_CEDULA + " = ?", new String[]{cedula},
                null, null, null);
    }

    public int actualizarUsuario(String cedula, String nombres, String apellidos,
                                  String fecha, String edad, String nacionalidad,
                                  String genero, String estadoCivil, float nivelIngles) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRES, nombres);
        values.put(COL_APELLIDOS, apellidos);
        values.put(COL_FECHA, fecha);
        values.put(COL_EDAD, edad);
        values.put(COL_NACIONALIDAD, nacionalidad);
        values.put(COL_GENERO, genero);
        values.put(COL_ESTADO_CIVIL, estadoCivil);
        values.put(COL_NIVEL_INGLES, nivelIngles);
        int filas = db.update(TABLE_USUARIOS, values, COL_CEDULA + " = ?", new String[]{cedula});
        db.close();
        return filas;
    }

    public int eliminarUsuario(String cedula) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filas = db.delete(TABLE_USUARIOS, COL_CEDULA + " = ?", new String[]{cedula});
        db.close();
        return filas;
    }
}
