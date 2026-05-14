package com.taller.mecanico.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "taller.db";
    private static final int DB_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CLIENTE =

                "CREATE TABLE cliente (" +

                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +

                        "cedula TEXT UNIQUE," +

                        "nombre TEXT," +

                        "telefono TEXT," +

                        "correo TEXT," +

                        "direccion TEXT" +

                        ")";

        db.execSQL(CREATE_CLIENTE);

    String CREATE_VEHICULO =

            "CREATE TABLE vehiculo (" +

                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +

                    "cliente_id INTEGER," +

                    "placa TEXT UNIQUE," +

                    "marca TEXT," +

                    "modelo TEXT," +

                    "color TEXT," +

                    "anio TEXT" +

                    ")";

        db.execSQL(CREATE_VEHICULO);
}
    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS cliente");

        onCreate(db);
    }

    // =========================
    // OBTENER CLIENTES
    // =========================

    public Cursor obtenerClientes(String filtro) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(

                "SELECT * FROM cliente " +
                        "WHERE nombre LIKE ? " +
                        "OR cedula LIKE ?",

                new String[]{

                        "%" + filtro + "%",
                        "%" + filtro + "%"
                }
        );
    }

    // =========================
    // OBTENER CLIENTE POR ID
    // =========================

    public Cursor obtenerClientePorId(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(

                "SELECT * FROM cliente WHERE id=?",

                new String[]{
                        String.valueOf(id)
                }
        );
    }

    // =========================
    // ELIMINAR CLIENTE
    // =========================

    public int eliminarCliente(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(

                "cliente",

                "id=?",

                new String[]{
                        String.valueOf(id)
                }
        );
    }

    // =========================
    // ACTUALIZAR CLIENTE
    // =========================

    public int actualizarCliente(int id,
                                 String cedula,
                                 String nombre,
                                 String telefono,
                                 String correo,
                                 String direccion) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("cedula", cedula);
        cv.put("nombre", nombre);
        cv.put("telefono", telefono);
        cv.put("correo", correo);
        cv.put("direccion", direccion);

        return db.update(

                "cliente",

                cv,

                "id=?",

                new String[]{
                        String.valueOf(id)
                }
        );
    }

    // =========================
// OBTENER VEHICULOS CLIENTE
// =========================

    public Cursor obtenerVehiculosCliente(int clienteId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.rawQuery(

                "SELECT * FROM vehiculo WHERE cliente_id=?",

                new String[]{
                        String.valueOf(clienteId)
                }
        );
    }

    // =========================
// ELIMINAR VEHICULO
// =========================

    public void eliminarVehiculo(int id) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        db.delete(

                "vehiculo",

                "id=?",

                new String[]{
                        String.valueOf(id)
                }
        );

        db.close();
    }

    // =========================
// OBTENER VEHICULO POR ID
// =========================

    public Cursor obtenerVehiculoPorId(int id) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.rawQuery(

                "SELECT * FROM vehiculo WHERE id=?",

                new String[]{
                        String.valueOf(id)
                }
        );
    }

// =========================
// ACTUALIZAR VEHICULO
// =========================

    public void actualizarVehiculo(
            int id,
            String placa,
            String marca,
            String modelo,
            String color,
            String anio
    ) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        android.content.ContentValues cv =
                new android.content.ContentValues();

        cv.put("placa", placa);
        cv.put("marca", marca);
        cv.put("modelo", modelo);
        cv.put("color", color);
        cv.put("anio", anio);

        db.update(

                "vehiculo",

                cv,

                "id=?",

                new String[]{
                        String.valueOf(id)
                }
        );

        db.close();
    }
}