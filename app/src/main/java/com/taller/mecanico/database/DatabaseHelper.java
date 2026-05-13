package com.taller.mecanico.database;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.taller.mecanico.DTOmodel.ReparacionDTO;
import com.taller.mecanico.model.Cliente;
import com.taller.mecanico.model.Novedad;
import com.taller.mecanico.model.Reparacion;
import com.taller.mecanico.model.RepuestoReparacion;
import com.taller.mecanico.model.Repuesto;
import com.taller.mecanico.model.Tecnico;
import com.taller.mecanico.model.Usuario;
import com.taller.mecanico.model.Vehiculo;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static final String DB_NAME    = "taller_mecanico.db";
    private static final int    DB_VERSION = 1;

    private static DatabaseHelper instance;
    // Tablas
    public static final String TABLE_USUARIOS              = "usuarios";
    public static final String TABLE_CLIENTES              = "clientes";
    public static final String TABLE_TECNICOS              = "tecnicos";
    public static final String TABLE_VEHICULOS             = "vehiculos";
    public static final String TABLE_REPUESTOS             = "repuestos";
    public static final String TABLE_REPARACIONES          = "reparaciones";
    public static final String TABLE_REPUESTOS_REPARACION  = "repuestos_reparacion";
    public static final String TABLE_NOVEDADES             = "novedades_reparacion";

    // ─── Columnas: USUARIOS ───────────────────────────────────────────────────
    public static final String USU_ID          = "id_usuario";
    public static final String USU_USERNAME    = "username";
    public static final String USU_PASSWORD    = "password";
    public static final String USU_ROL         = "rol";           // ADMIN | MECANICO | CLIENTE
    public static final String USU_ID_REF      = "id_referencia"; // id del tecnico o cliente vinculado
    public static final String USU_ACTIVO      = "activo";

    // Columnas: CLIENTES
    public static final String CLI_ID          = "id_cliente";
    public static final String CLI_CEDULA      = "cedula";
    public static final String CLI_NOMBRE      = "nombre";
    public static final String CLI_APELLIDO    = "apellido";
    public static final String CLI_TELEFONO    = "telefono";
    public static final String CLI_CORREO      = "correo";
    public static final String CLI_ACTIVO      = "activo";

    // Columnas: TECNICOS
    public static final String TEC_ID          = "id_tecnico";
    public static final String TEC_CEDULA      = "cedula";
    public static final String TEC_NOMBRE      = "nombre";
    public static final String TEC_APELLIDO    = "apellido";
    public static final String TEC_TELEFONO    = "telefono";
    public static final String TEC_ESPECIALIDAD = "especialidad";
    public static final String TEC_ACTIVO      = "activo";

    // Columnas: VEHICULOS
    public static final String VEH_ID          = "id_vehiculo";
    public static final String VEH_ID_CLIENTE  = "id_cliente";
    public static final String VEH_PLACA       = "placa";
    public static final String VEH_MARCA       = "marca";
    public static final String VEH_MODELO      = "modelo";
    public static final String VEH_ANIO        = "anio";

    // Columnas: REPUESTOS
    public static final String REP_ID          = "id_repuesto";
    public static final String REP_NOMBRE      = "nombre";
    public static final String REP_PRECIO      = "precio";
    public static final String REP_STOCK       = "stock";
    public static final String REP_ACTIVO      = "activo";

    //  Columnas: REPARACIONES
    public static final String RPA_ID          = "id_reparacion";
    public static final String RPA_ID_CLIENTE  = "id_cliente";
    public static final String RPA_ID_TECNICO  = "id_tecnico";
    public static final String RPA_ID_VEHICULO = "id_vehiculo";
    public static final String RPA_FECHA_ING   = "fecha_ingreso";
    public static final String RPA_FECHA_RET   = "fecha_retiro";
    public static final String RPA_ESTADO      = "estado";       // EN_DIAGNOSTICO | EN_REPARACION | ESPERANDO_REPUESTOS | LISTO | ENTREGADO
    public static final String RPA_DESCRIPCION = "descripcion";
    public static final String RPA_COSTO       = "costo";        // mano de obra fija

    //  Columnas: REPUESTOS_REPARACION
    public static final String RR_ID           = "id_detalle";
    public static final String RR_ID_REPARACION = "id_reparacion";
    public static final String RR_ID_REPUESTO  = "id_repuesto";
    public static final String RR_CANTIDAD     = "cantidad";
    public static final String RR_SUBTOTAL     = "subtotal";

    // Columnas: NOVEDADES_REPARACION
    public static final String NOV_ID          = "id_novedad";
    public static final String NOV_ID_REPARACION = "id_reparacion";
    public static final String NOV_FECHA       = "fecha_novedad";
    public static final String NOV_DESCRIPCION = "descripcion";

    // Roles disponibles
    public static final String ROL_ADMIN    = "ADMIN";
    public static final String ROL_MECANICO = "MECANICO";
    public static final String ROL_CLIENTE  = "CLIENTE";

    // Estados de reparación
    public static final String ESTADO_DIAGNOSTICO  = "EN_DIAGNOSTICO";
    public static final String ESTADO_REPARACION   = "EN_REPARACION";
    public static final String ESTADO_ESPERA       = "ESPERANDO_REPUESTOS";
    public static final String ESTADO_LISTO        = "LISTO_ENTREGA";
    public static final String ESTADO_ENTREGADO    = "ENTREGADO";


    //  SQL — CREAR TABLES
    private static final String SQL_CREATE_USUARIOS =
            "CREATE TABLE " + TABLE_USUARIOS + " (" +
                    USU_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    USU_USERNAME + " TEXT NOT NULL UNIQUE, " +
                    USU_PASSWORD + " TEXT NOT NULL, " +
                    USU_ROL      + " TEXT NOT NULL DEFAULT 'ADMIN', " +
                    USU_ID_REF   + " INTEGER DEFAULT 0, " +
                    USU_ACTIVO   + " INTEGER NOT NULL DEFAULT 1" +
                    ");";

    private static final String SQL_CREATE_CLIENTES =
            "CREATE TABLE " + TABLE_CLIENTES + " (" +
                    CLI_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CLI_CEDULA   + " TEXT NOT NULL UNIQUE, " +
                    CLI_NOMBRE   + " TEXT NOT NULL, " +
                    CLI_APELLIDO + " TEXT NOT NULL, " +
                    CLI_TELEFONO + " TEXT NOT NULL, " +
                    CLI_CORREO   + " TEXT, " +
                    CLI_ACTIVO   + " INTEGER NOT NULL DEFAULT 1" +
                    ");";

    private static final String SQL_CREATE_TECNICOS =
            "CREATE TABLE " + TABLE_TECNICOS + " (" +
                    TEC_ID          + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TEC_CEDULA      + " TEXT NOT NULL UNIQUE, " +
                    TEC_NOMBRE      + " TEXT NOT NULL, " +
                    TEC_APELLIDO    + " TEXT NOT NULL, " +
                    TEC_TELEFONO    + " TEXT NOT NULL, " +
                    TEC_ESPECIALIDAD + " TEXT, " +
                    TEC_ACTIVO      + " INTEGER NOT NULL DEFAULT 1" +
                    ");";

    private static final String SQL_CREATE_VEHICULOS =
            "CREATE TABLE " + TABLE_VEHICULOS + " (" +
                    VEH_ID         + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    VEH_ID_CLIENTE + " INTEGER NOT NULL, " +
                    VEH_PLACA      + " TEXT NOT NULL UNIQUE, " +
                    VEH_MARCA      + " TEXT NOT NULL, " +
                    VEH_MODELO     + " TEXT NOT NULL, " +
                    VEH_ANIO       + " INTEGER NOT NULL, " +
                    "FOREIGN KEY (" + VEH_ID_CLIENTE + ") REFERENCES " + TABLE_CLIENTES + "(" + CLI_ID + ")" +
                    ");";

    private static final String SQL_CREATE_REPUESTOS =
            "CREATE TABLE " + TABLE_REPUESTOS + " (" +
                    REP_ID     + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    REP_NOMBRE + " TEXT NOT NULL, " +
                    REP_PRECIO + " REAL NOT NULL, " +
                    REP_STOCK  + " INTEGER NOT NULL DEFAULT 0, " +
                    REP_ACTIVO + " INTEGER NOT NULL DEFAULT 1" +
                    ");";

    private static final String SQL_CREATE_REPARACIONES =
            "CREATE TABLE " + TABLE_REPARACIONES + " (" +
                    RPA_ID          + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RPA_ID_CLIENTE  + " INTEGER NOT NULL, " +
                    RPA_ID_TECNICO  + " INTEGER NOT NULL, " +
                    RPA_ID_VEHICULO + " INTEGER, " +
                    RPA_FECHA_ING   + " TEXT NOT NULL, " +
                    RPA_FECHA_RET   + " TEXT, " +
                    RPA_ESTADO      + " TEXT NOT NULL DEFAULT 'EN_DIAGNOSTICO', " +
                    RPA_DESCRIPCION + " TEXT NOT NULL, " +
                    RPA_COSTO       + " REAL DEFAULT 0.0, " +
                    "FOREIGN KEY (" + RPA_ID_CLIENTE  + ") REFERENCES " + TABLE_CLIENTES  + "(" + CLI_ID  + "), " +
                    "FOREIGN KEY (" + RPA_ID_TECNICO  + ") REFERENCES " + TABLE_TECNICOS  + "(" + TEC_ID  + "), " +
                    "FOREIGN KEY (" + RPA_ID_VEHICULO + ") REFERENCES " + TABLE_VEHICULOS + "(" + VEH_ID  + ")" +
                    ");";

    private static final String SQL_CREATE_REPUESTOS_REPARACION =
            "CREATE TABLE " + TABLE_REPUESTOS_REPARACION + " (" +
                    RR_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RR_ID_REPARACION + " INTEGER NOT NULL, " +
                    RR_ID_REPUESTO   + " INTEGER NOT NULL, " +
                    RR_CANTIDAD      + " INTEGER NOT NULL, " +
                    RR_SUBTOTAL      + " REAL NOT NULL, " +
                    "FOREIGN KEY (" + RR_ID_REPARACION + ") REFERENCES " + TABLE_REPARACIONES + "(" + RPA_ID + "), " +
                    "FOREIGN KEY (" + RR_ID_REPUESTO   + ") REFERENCES " + TABLE_REPUESTOS    + "(" + REP_ID + ")" +
                    ");";

    private static final String SQL_CREATE_NOVEDADES =
            "CREATE TABLE " + TABLE_NOVEDADES + " (" +
                    NOV_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NOV_ID_REPARACION + " INTEGER NOT NULL, " +
                    NOV_FECHA         + " TEXT NOT NULL, " +
                    NOV_DESCRIPCION   + " TEXT NOT NULL, " +
                    "FOREIGN KEY (" + NOV_ID_REPARACION + ") REFERENCES " + TABLE_REPARACIONES + "(" + RPA_ID + ")" +
                    ");";

    //  Constructor
    private DatabaseHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");
        db.execSQL(SQL_CREATE_USUARIOS);
        db.execSQL(SQL_CREATE_CLIENTES);
        db.execSQL(SQL_CREATE_TECNICOS);
        db.execSQL(SQL_CREATE_VEHICULOS);
        db.execSQL(SQL_CREATE_REPUESTOS);
        db.execSQL(SQL_CREATE_REPARACIONES);
        db.execSQL(SQL_CREATE_REPUESTOS_REPARACION);
        db.execSQL(SQL_CREATE_NOVEDADES);

        // Insertar usuario administrador por defecto
        insertarAdminPorDefecto(db);
        Log.i(TAG, "Base de datos creada correctamente.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Actualizando DB de versión " + oldVersion + " a " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOVEDADES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPUESTOS_REPARACION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPARACIONES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VEHICULOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPUESTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TECNICOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys = ON;");
        }
    }

    /** Inserta el admin predeterminado al crear la DB por primera vez */
    private void insertarAdminPorDefecto(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(USU_USERNAME, "admin");
        cv.put(USU_PASSWORD, "taller123"); // En producción usar hash (BCrypt / SHA-256)
        cv.put(USU_ROL,      ROL_ADMIN);
        cv.put(USU_ID_REF,   0);
        cv.put(USU_ACTIVO,   1);
        db.insert(TABLE_USUARIOS, null, cv);
    }

    //  MÓDULO: USUARIOS
    /**
     *  Valida credenciales. Retorna el Usuario si coincide, null si no.
     */
    public Usuario login(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Usuario usuario = null;

        String query = "SELECT * FROM " + TABLE_USUARIOS +
                " WHERE " + USU_USERNAME + " = ? AND " + USU_PASSWORD + " = ? AND " + USU_ACTIVO + " = 1";
        Cursor c = db.rawQuery(query, new String[]{username, password});

        if (c.moveToFirst()) {
            usuario = cursorToUsuario(c);
        }
        c.close();
        return usuario;
    }

    public long insertarUsuario(Usuario u) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USU_USERNAME, u.getUsername());
        cv.put(USU_PASSWORD, u.getPassword());
        cv.put(USU_ROL,      u.getRol());
        cv.put(USU_ID_REF,   u.getIdReferencia());
        cv.put(USU_ACTIVO,   1);
        return db.insert(TABLE_USUARIOS, null, cv);
    }

    public List<Usuario> getUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        SQLiteDatabase db   = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USUARIOS + " WHERE " + USU_ACTIVO + " = 1", null);
        while (c.moveToNext()) lista.add(cursorToUsuario(c));
        c.close();
        return lista;
    }

    public boolean actualizarUsuario(Usuario u) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(USU_USERNAME, u.getUsername());
        cv.put(USU_PASSWORD, u.getPassword());
        cv.put(USU_ROL,      u.getRol());
        int rows = db.update(TABLE_USUARIOS, cv, USU_ID + " = ?", new String[]{String.valueOf(u.getIdUsuario())});
        return rows > 0;
    }

    /** Borrado lógico */
    public boolean eliminarUsuario(int idUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(USU_ACTIVO, 0);
        int rows = db.update(TABLE_USUARIOS, cv, USU_ID + " = ?", new String[]{String.valueOf(idUsuario)});
        return rows > 0;
    }

    private Usuario cursorToUsuario(Cursor c) {
        Usuario u = new Usuario();
        u.setIdUsuario(  c.getInt(   c.getColumnIndexOrThrow(USU_ID)));
        u.setUsername(   c.getString(c.getColumnIndexOrThrow(USU_USERNAME)));
        u.setPassword(   c.getString(c.getColumnIndexOrThrow(USU_PASSWORD)));
        u.setRol(        c.getString(c.getColumnIndexOrThrow(USU_ROL)));
        u.setIdReferencia(c.getInt(  c.getColumnIndexOrThrow(USU_ID_REF)));
        u.setActivo(     c.getInt(   c.getColumnIndexOrThrow(USU_ACTIVO)) == 1);
        return u;
    }



    //  MÓDULO: CLIENTES
    public long insertarCliente(Cliente cl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(CLI_CEDULA,   cl.getCedula());
        cv.put(CLI_NOMBRE,   cl.getNombre());
        cv.put(CLI_APELLIDO, cl.getApellido());
        cv.put(CLI_TELEFONO, cl.getTelefono());
        cv.put(CLI_CORREO,   cl.getCorreo());
        cv.put(CLI_ACTIVO,   1);
        return db.insert(TABLE_CLIENTES, null, cv);
    }

    public boolean actualizarCliente(Cliente cl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(CLI_CEDULA,   cl.getCedula());
        cv.put(CLI_NOMBRE,   cl.getNombre());
        cv.put(CLI_APELLIDO, cl.getApellido());
        cv.put(CLI_TELEFONO, cl.getTelefono());
        cv.put(CLI_CORREO,   cl.getCorreo());
        int rows = db.update(TABLE_CLIENTES, cv, CLI_ID + " = ?", new String[]{String.valueOf(cl.getIdCliente())});
        return rows > 0;
    }

    /** Borrado lógico */
    public boolean eliminarCliente(int idCliente) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(CLI_ACTIVO, 0);
        int rows = db.update(TABLE_CLIENTES, cv, CLI_ID + " = ?", new String[]{String.valueOf(idCliente)});
        return rows > 0;
    }

    public List<Cliente> getClientes() {
        List<Cliente> lista = new ArrayList<>();
        SQLiteDatabase db   = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CLIENTES + " WHERE " + CLI_ACTIVO + " = 1 ORDER BY " + CLI_NOMBRE, null);
        while (c.moveToNext()) lista.add(cursorToCliente(c));
        c.close();
        return lista;
    }

    public Cliente getClienteById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CLIENTES + " WHERE " + CLI_ID + " = ?", new String[]{String.valueOf(id)});
        Cliente cl = null;
        if (c.moveToFirst()) cl = cursorToCliente(c);
        c.close();
        return cl;
    }

    public Cliente getClienteByCedula(String cedula) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CLIENTES + " WHERE " + CLI_CEDULA + " = ? AND " + CLI_ACTIVO + " = 1", new String[]{cedula});
        Cliente cl = null;
        if (c.moveToFirst()) cl = cursorToCliente(c);
        c.close();
        return cl;
    }

    /** Búsqueda por nombre o cédula (LIKE) */
    public List<Cliente> buscarClientes(String query) {
        List<Cliente> lista = new ArrayList<>();
        SQLiteDatabase db   = this.getReadableDatabase();
        String filtro = "%" + query + "%";
        String sql = "SELECT * FROM " + TABLE_CLIENTES +
                " WHERE " + CLI_ACTIVO + " = 1 AND (" +
                CLI_NOMBRE + " LIKE ? OR " + CLI_APELLIDO + " LIKE ? OR " + CLI_CEDULA + " LIKE ?)";
        Cursor c = db.rawQuery(sql, new String[]{filtro, filtro, filtro});
        while (c.moveToNext()) lista.add(cursorToCliente(c));
        c.close();
        return lista;
    }

    private Cliente cursorToCliente(Cursor c) {
        Cliente cl = new Cliente();
        cl.setIdCliente( c.getInt(   c.getColumnIndexOrThrow(CLI_ID)));
        cl.setCedula(    c.getString(c.getColumnIndexOrThrow(CLI_CEDULA)));
        cl.setNombre(    c.getString(c.getColumnIndexOrThrow(CLI_NOMBRE)));
        cl.setApellido(  c.getString(c.getColumnIndexOrThrow(CLI_APELLIDO)));
        cl.setTelefono(  c.getString(c.getColumnIndexOrThrow(CLI_TELEFONO)));
        cl.setCorreo(    c.getString(c.getColumnIndexOrThrow(CLI_CORREO)));
        cl.setActivo(    c.getInt(   c.getColumnIndexOrThrow(CLI_ACTIVO)) == 1);
        return cl;
    }

    //  MÓDULO: TÉCNICOS
    public long insertarTecnico(Tecnico t) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(TEC_CEDULA,       t.getCedula());
        cv.put(TEC_NOMBRE,       t.getNombre());
        cv.put(TEC_APELLIDO,     t.getApellido());
        cv.put(TEC_TELEFONO,     t.getTelefono());
        cv.put(TEC_ESPECIALIDAD, t.getEspecialidad());
        cv.put(TEC_ACTIVO,       1);
        return db.insert(TABLE_TECNICOS, null, cv);
    }

    public boolean actualizarTecnico(Tecnico t) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(TEC_CEDULA,       t.getCedula());
        cv.put(TEC_NOMBRE,       t.getNombre());
        cv.put(TEC_APELLIDO,     t.getApellido());
        cv.put(TEC_TELEFONO,     t.getTelefono());
        cv.put(TEC_ESPECIALIDAD, t.getEspecialidad());
        int rows = db.update(TABLE_TECNICOS, cv, TEC_ID + " = ?", new String[]{String.valueOf(t.getIdTecnico())});
        return rows > 0;
    }

    public boolean eliminarTecnico(int idTecnico) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(TEC_ACTIVO, 0);
        int rows = db.update(TABLE_TECNICOS, cv, TEC_ID + " = ?", new String[]{String.valueOf(idTecnico)});
        return rows > 0;
    }

    public List<Tecnico> getTecnicos() {
        List<Tecnico> lista = new ArrayList<>();
        SQLiteDatabase db   = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_TECNICOS + " WHERE " + TEC_ACTIVO + " = 1 ORDER BY " + TEC_NOMBRE, null);
        while (c.moveToNext()) lista.add(cursorToTecnico(c));
        c.close();
        return lista;
    }

    public Tecnico getTecnicoById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_TECNICOS + " WHERE " + TEC_ID + " = ?", new String[]{String.valueOf(id)});
        Tecnico t = null;
        if (c.moveToFirst()) t = cursorToTecnico(c);
        c.close();
        return t;
    }

    private Tecnico cursorToTecnico(Cursor c) {
        Tecnico t = new Tecnico();
        t.setIdTecnico(    c.getInt(   c.getColumnIndexOrThrow(TEC_ID)));
        t.setCedula(       c.getString(c.getColumnIndexOrThrow(TEC_CEDULA)));
        t.setNombre(       c.getString(c.getColumnIndexOrThrow(TEC_NOMBRE)));
        t.setApellido(     c.getString(c.getColumnIndexOrThrow(TEC_APELLIDO)));
        t.setTelefono(     c.getString(c.getColumnIndexOrThrow(TEC_TELEFONO)));
        t.setEspecialidad( c.getString(c.getColumnIndexOrThrow(TEC_ESPECIALIDAD)));
        t.setActivo(       c.getInt(   c.getColumnIndexOrThrow(TEC_ACTIVO)) == 1);
        return t;
    }

    //  MÓDULO: VEHÍCULOS
    public long insertarVehiculo(Vehiculo v) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(VEH_ID_CLIENTE, v.getIdCliente());
        cv.put(VEH_PLACA,      v.getPlaca());
        cv.put(VEH_MARCA,      v.getMarca());
        cv.put(VEH_MODELO,     v.getModelo());
        cv.put(VEH_ANIO,       v.getAnio());
        return db.insert(TABLE_VEHICULOS, null, cv);
    }

    public boolean eliminarVehiculo(int idVehiculo) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_VEHICULOS, VEH_ID + " = ?", new String[]{String.valueOf(idVehiculo)});
        return rows > 0;
    }

    public List<Vehiculo> getVehiculosByCliente(int idCliente) {
        List<Vehiculo> lista = new ArrayList<>();
        SQLiteDatabase db    = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_VEHICULOS + " WHERE " + VEH_ID_CLIENTE + " = ?", new String[]{String.valueOf(idCliente)});
        while (c.moveToNext()) lista.add(cursorToVehiculo(c));
        c.close();
        return lista;
    }

    public Vehiculo getVehiculoByPlaca(String placa) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_VEHICULOS + " WHERE " + VEH_PLACA + " = ?", new String[]{placa});
        Vehiculo v = null;
        if (c.moveToFirst()) v = cursorToVehiculo(c);
        c.close();
        return v;
    }

    private Vehiculo cursorToVehiculo(Cursor c) {
        Vehiculo v = new Vehiculo();
        v.setIdVehiculo( c.getInt(   c.getColumnIndexOrThrow(VEH_ID)));
        v.setIdCliente(  c.getInt(   c.getColumnIndexOrThrow(VEH_ID_CLIENTE)));
        v.setPlaca(      c.getString(c.getColumnIndexOrThrow(VEH_PLACA)));
        v.setMarca(      c.getString(c.getColumnIndexOrThrow(VEH_MARCA)));
        v.setModelo(     c.getString(c.getColumnIndexOrThrow(VEH_MODELO)));
        v.setAnio(       c.getInt(   c.getColumnIndexOrThrow(VEH_ANIO)));
        return v;
    }

    //  MÓDULO: REPUESTOS
    public long insertarRepuesto(Repuesto r) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(REP_NOMBRE,  r.getNombre());
        cv.put(REP_PRECIO,  r.getPrecio());
        cv.put(REP_STOCK,   r.getStock());
        cv.put(REP_ACTIVO,  1);
        return db.insert(TABLE_REPUESTOS, null, cv);
    }

    public boolean actualizarRepuesto(Repuesto r) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(REP_NOMBRE, r.getNombre());
        cv.put(REP_PRECIO, r.getPrecio());
        cv.put(REP_STOCK,  r.getStock());
        int rows = db.update(TABLE_REPUESTOS, cv, REP_ID + " = ?", new String[]{String.valueOf(r.getIdRepuesto())});
        return rows > 0;
    }

    public boolean eliminarRepuesto(int idRepuesto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(REP_ACTIVO, 0);
        int rows = db.update(TABLE_REPUESTOS, cv, REP_ID + " = ?", new String[]{String.valueOf(idRepuesto)});
        return rows > 0;
    }

    public List<Repuesto> getRepuestos() {
        List<Repuesto> lista = new ArrayList<>();
        SQLiteDatabase db    = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_REPUESTOS + " WHERE " + REP_ACTIVO + " = 1 ORDER BY " + REP_NOMBRE, null);
        while (c.moveToNext()) lista.add(cursorToRepuesto(c));
        c.close();
        return lista;
    }

    public Repuesto getRepuestoById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_REPUESTOS + " WHERE " + REP_ID + " = ?", new String[]{String.valueOf(id)});
        Repuesto r = null;
        if (c.moveToFirst()) r = cursorToRepuesto(c);
        c.close();
        return r;
    }

    /** Descuenta stock cuando se usa en una reparación */
    public boolean descontarStock(int idRepuesto, int cantidad) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE " + TABLE_REPUESTOS + " SET " + REP_STOCK + " = " + REP_STOCK + " - ? WHERE " + REP_ID + " = ? AND " + REP_STOCK + " >= ?";
        db.execSQL(sql, new Object[]{cantidad, idRepuesto, cantidad});
        // Verificar que se aplicó
        Repuesto r = getRepuestoById(idRepuesto);
        return r != null;
    }

    public List<Repuesto> buscarRepuestos(String query) {
        List<Repuesto> lista = new ArrayList<>();
        SQLiteDatabase db    = this.getReadableDatabase();
        String filtro = "%" + query + "%";
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_REPUESTOS + " WHERE " + REP_ACTIVO + " = 1 AND " + REP_NOMBRE + " LIKE ?", new String[]{filtro});
        while (c.moveToNext()) lista.add(cursorToRepuesto(c));
        c.close();
        return lista;
    }

    private Repuesto cursorToRepuesto(Cursor c) {
        Repuesto r = new Repuesto();
        r.setIdRepuesto(c.getInt(   c.getColumnIndexOrThrow(REP_ID)));
        r.setNombre(    c.getString(c.getColumnIndexOrThrow(REP_NOMBRE)));
        r.setPrecio(    c.getDouble(c.getColumnIndexOrThrow(REP_PRECIO)));
        r.setStock(     c.getInt(   c.getColumnIndexOrThrow(REP_STOCK)));
        r.setActivo(    c.getInt(   c.getColumnIndexOrThrow(REP_ACTIVO)) == 1);
        return r;
    }

    //  MÓDULO: REPARACIONES
    public long insertarReparacion(ReparacionDTO rpa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(RPA_ID_CLIENTE,  rpa.getIdCliente());
        cv.put(RPA_ID_TECNICO,  rpa.getIdTecnico());
        cv.put(RPA_ID_VEHICULO, rpa.getIdVehiculo());
        cv.put(RPA_FECHA_ING,   rpa.getFechaIngreso());
        cv.put(RPA_FECHA_RET,   rpa.getFechaRetiro());
        cv.put(RPA_ESTADO,      rpa.getEstado());
        cv.put(RPA_DESCRIPCION, rpa.getDescripcion());
        cv.put(RPA_COSTO,       rpa.getCosto());
        return db.insert(TABLE_REPARACIONES, null, cv);
    }

    public boolean actualizarReparacion(ReparacionDTO rpa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(RPA_ID_CLIENTE,  rpa.getIdCliente());
        cv.put(RPA_ID_TECNICO,  rpa.getIdTecnico());
        cv.put(RPA_ID_VEHICULO, rpa.getIdVehiculo());
        cv.put(RPA_FECHA_ING,   rpa.getFechaIngreso());
        cv.put(RPA_FECHA_RET,   rpa.getFechaRetiro());
        cv.put(RPA_ESTADO,      rpa.getEstado());
        cv.put(RPA_DESCRIPCION, rpa.getDescripcion());
        cv.put(RPA_COSTO,       rpa.getCosto());
        int rows = db.update(TABLE_REPARACIONES, cv, RPA_ID + " = ?", new String[]{String.valueOf(rpa.getIdReparacion())});
        return rows > 0;
    }

    public boolean eliminarReparacion(int idReparacion) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Solo se puede eliminar si está en diagnóstico (borrado físico)
        int rows = db.delete(TABLE_REPARACIONES, RPA_ID + " = ? AND " + RPA_ESTADO + " = ?",
                new String[]{String.valueOf(idReparacion), ESTADO_DIAGNOSTICO});
        return rows > 0;
    }

    public List<ReparacionDTO> getReparaciones() {
        List<ReparacionDTO> lista = new ArrayList<>();
        SQLiteDatabase db      = this.getReadableDatabase();
        // JOIN con clientes y técnicos para traer nombres
        String sql =
                "SELECT r.*, " +
                        "  c." + CLI_NOMBRE    + " AS nombre_cliente, c." + CLI_APELLIDO + " AS apellido_cliente, " +
                        "  t." + TEC_NOMBRE    + " AS nombre_tecnico, t." + TEC_APELLIDO + " AS apellido_tecnico, " +
                        "  v." + VEH_PLACA     + " AS placa_vehiculo, v." + VEH_MARCA    + " AS marca_vehiculo, v." + VEH_MODELO + " AS modelo_vehiculo " +
                        "FROM " + TABLE_REPARACIONES + " r " +
                        "LEFT JOIN " + TABLE_CLIENTES  + " c ON r." + RPA_ID_CLIENTE  + " = c." + CLI_ID  +
                        " LEFT JOIN " + TABLE_TECNICOS  + " t ON r." + RPA_ID_TECNICO  + " = t." + TEC_ID  +
                        " LEFT JOIN " + TABLE_VEHICULOS + " v ON r." + RPA_ID_VEHICULO + " = v." + VEH_ID  +
                        " ORDER BY r." + RPA_ID + " DESC";
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) lista.add(cursorToReparacion(c));
        c.close();
        return lista;
    }

    public List<ReparacionDTO> getReparacionesByTecnico(int idTecnico) {
        List<ReparacionDTO> lista = new ArrayList<>();
        SQLiteDatabase db      = this.getReadableDatabase();
        String sql =
                "SELECT r.*, " +
                        "  c." + CLI_NOMBRE + " AS nombre_cliente, c." + CLI_APELLIDO + " AS apellido_cliente, " +
                        "  t." + TEC_NOMBRE + " AS nombre_tecnico, t." + TEC_APELLIDO + " AS apellido_tecnico, " +
                        "  v." + VEH_PLACA  + " AS placa_vehiculo, v." + VEH_MARCA   + " AS marca_vehiculo, v." + VEH_MODELO + " AS modelo_vehiculo " +
                        "FROM " + TABLE_REPARACIONES + " r " +
                        "LEFT JOIN " + TABLE_CLIENTES  + " c ON r." + RPA_ID_CLIENTE  + " = c." + CLI_ID +
                        " LEFT JOIN " + TABLE_TECNICOS  + " t ON r." + RPA_ID_TECNICO  + " = t." + TEC_ID +
                        " LEFT JOIN " + TABLE_VEHICULOS + " v ON r." + RPA_ID_VEHICULO + " = v." + VEH_ID +
                        " WHERE r." + RPA_ID_TECNICO + " = ?" +
                        " ORDER BY r." + RPA_ID + " DESC";
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(idTecnico)});
        while (c.moveToNext()) lista.add(cursorToReparacion(c));
        c.close();
        return lista;
    }

    public ReparacionDTO getReparacionById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql =
                "SELECT r.*, " +
                        "  c." + CLI_NOMBRE + " AS nombre_cliente, c." + CLI_APELLIDO + " AS apellido_cliente, " +
                        "  t." + TEC_NOMBRE + " AS nombre_tecnico, t." + TEC_APELLIDO + " AS apellido_tecnico, " +
                        "  v." + VEH_PLACA  + " AS placa_vehiculo, v." + VEH_MARCA   + " AS marca_vehiculo, v." + VEH_MODELO + " AS modelo_vehiculo " +
                        "FROM " + TABLE_REPARACIONES + " r " +
                        "LEFT JOIN " + TABLE_CLIENTES  + " c ON r." + RPA_ID_CLIENTE  + " = c." + CLI_ID +
                        " LEFT JOIN " + TABLE_TECNICOS  + " t ON r." + RPA_ID_TECNICO  + " = t." + TEC_ID +
                        " LEFT JOIN " + TABLE_VEHICULOS + " v ON r." + RPA_ID_VEHICULO + " = v." + VEH_ID +
                        " WHERE r." + RPA_ID + " = ?";
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(id)});
        ReparacionDTO rpa = null;
        if (c.moveToFirst()) rpa = cursorToReparacion(c);
        c.close();
        return rpa;
    }

    /**
     * Actualiza solo el estado de la reparación
     */
    public boolean actualizarEstadoReparacion(int idReparacion, String nuevoEstado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(RPA_ESTADO, nuevoEstado);
        int rows = db.update(TABLE_REPARACIONES, cv, RPA_ID + " = ?", new String[]{String.valueOf(idReparacion)});
        return rows > 0;
    }

    /**
     * Obtiene total de repuestos + costo de mano de obra
     */
    public double getTotalReparacion(int idReparacion) {
        SQLiteDatabase db = this.getReadableDatabase();
        double totalRepuestos = 0.0;
        double costoManoObra  = 0.0;

        Cursor c1 = db.rawQuery("SELECT SUM(" + RR_SUBTOTAL + ") FROM " + TABLE_REPUESTOS_REPARACION + " WHERE " + RR_ID_REPARACION + " = ?", new String[]{String.valueOf(idReparacion)});
        if (c1.moveToFirst()) totalRepuestos = c1.getDouble(0);
        c1.close();

        Cursor c2 = db.rawQuery("SELECT " + RPA_COSTO + " FROM " + TABLE_REPARACIONES + " WHERE " + RPA_ID + " = ?", new String[]{String.valueOf(idReparacion)});
        if (c2.moveToFirst()) costoManoObra = c2.getDouble(0);
        c2.close();

        return totalRepuestos + costoManoObra;
    }

    private ReparacionDTO cursorToReparacion(Cursor c) {
        ReparacionDTO r = new ReparacionDTO();
        r.setIdReparacion( c.getInt(   c.getColumnIndexOrThrow(RPA_ID)));
        r.setIdCliente(    c.getInt(   c.getColumnIndexOrThrow(RPA_ID_CLIENTE)));
        r.setIdTecnico(    c.getInt(   c.getColumnIndexOrThrow(RPA_ID_TECNICO)));
        r.setIdVehiculo(   c.getInt(   c.getColumnIndexOrThrow(RPA_ID_VEHICULO)));
        r.setFechaIngreso( c.getString(c.getColumnIndexOrThrow(RPA_FECHA_ING)));
        r.setFechaRetiro(  c.getString(c.getColumnIndexOrThrow(RPA_FECHA_RET)));
        r.setEstado(       c.getString(c.getColumnIndexOrThrow(RPA_ESTADO)));
        r.setDescripcion(  c.getString(c.getColumnIndexOrThrow(RPA_DESCRIPCION)));
        r.setCosto(        c.getDouble(c.getColumnIndexOrThrow(RPA_COSTO)));
        // Campos JOIN (pueden ser null si no hay join)
        try { r.setNombreCliente( c.getString(c.getColumnIndexOrThrow("nombre_cliente"))  + " " + c.getString(c.getColumnIndexOrThrow("apellido_cliente"))); } catch (Exception ignored) {}
        try { r.setNombreTecnico( c.getString(c.getColumnIndexOrThrow("nombre_tecnico"))  + " " + c.getString(c.getColumnIndexOrThrow("apellido_tecnico"))); } catch (Exception ignored) {}
        try { r.setPlacaVehiculo( c.getString(c.getColumnIndexOrThrow("placa_vehiculo"))); }    catch (Exception ignored) {}
        try { r.setInfoVehiculo(  c.getString(c.getColumnIndexOrThrow("marca_vehiculo"))  + " " + c.getString(c.getColumnIndexOrThrow("modelo_vehiculo"))); } catch (Exception ignored) {}
        return r;
    }

    //  MÓDULO: REPUESTOS_REPARACION (Detalle)
    public long insertarRepuestoReparacion(RepuestoReparacion rr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(RR_ID_REPARACION, rr.getIdReparacion());
        cv.put(RR_ID_REPUESTO,   rr.getIdRepuesto());
        cv.put(RR_CANTIDAD,      rr.getCantidad());
        cv.put(RR_SUBTOTAL,      rr.getSubtotal());
        // Descontar stock automáticamente
        descontarStock(rr.getIdRepuesto(), rr.getCantidad());
        return db.insert(TABLE_REPUESTOS_REPARACION, null, cv);
    }

    public boolean actualizarRepuestoReparacion(RepuestoReparacion rr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(RR_CANTIDAD, rr.getCantidad());
        cv.put(RR_SUBTOTAL, rr.getSubtotal());
        int rows = db.update(TABLE_REPUESTOS_REPARACION, cv, RR_ID + " = ?", new String[]{String.valueOf(rr.getIdDetalle())});
        return rows > 0;
    }

    public boolean eliminarRepuestoReparacion(int idDetalle) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_REPUESTOS_REPARACION, RR_ID + " = ?", new String[]{String.valueOf(idDetalle)});
        return rows > 0;
    }

    public List<RepuestoReparacion> getRepuestosByReparacion(int idReparacion) {
        List<RepuestoReparacion> lista = new ArrayList<>();
        SQLiteDatabase db              = this.getReadableDatabase();
        String sql =
                "SELECT rr.*, r." + REP_NOMBRE + " AS nombre_repuesto, r." + REP_PRECIO + " AS precio_repuesto " +
                        "FROM " + TABLE_REPUESTOS_REPARACION + " rr " +
                        "INNER JOIN " + TABLE_REPUESTOS + " r ON rr." + RR_ID_REPUESTO + " = r." + REP_ID +
                        " WHERE rr." + RR_ID_REPARACION + " = ?";
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(idReparacion)});
        while (c.moveToNext()) {
            RepuestoReparacion rr = new RepuestoReparacion();
            rr.setIdDetalle(    c.getInt(   c.getColumnIndexOrThrow(RR_ID)));
            rr.setIdReparacion( c.getInt(   c.getColumnIndexOrThrow(RR_ID_REPARACION)));
            rr.setIdRepuesto(   c.getInt(   c.getColumnIndexOrThrow(RR_ID_REPUESTO)));
            rr.setCantidad(     c.getInt(   c.getColumnIndexOrThrow(RR_CANTIDAD)));
            rr.setSubtotal(     c.getDouble(c.getColumnIndexOrThrow(RR_SUBTOTAL)));
            rr.setNombreRepuesto(c.getString(c.getColumnIndexOrThrow("nombre_repuesto")));
            rr.setPrecioRepuesto(c.getDouble(c.getColumnIndexOrThrow("precio_repuesto")));
            lista.add(rr);
        }
        c.close();
        return lista;
    }

    //  MÓDULO: NOVEDADES
    public long insertarNovedad(Novedad n) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(NOV_ID_REPARACION, n.getIdReparacion());
        cv.put(NOV_FECHA,         n.getFechaNovedad());
        cv.put(NOV_DESCRIPCION,   n.getDescripcion());
        return db.insert(TABLE_NOVEDADES, null, cv);
    }

    public boolean actualizarNovedad(Novedad n) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(NOV_FECHA,       n.getFechaNovedad());
        cv.put(NOV_DESCRIPCION, n.getDescripcion());
        int rows = db.update(TABLE_NOVEDADES, cv, NOV_ID + " = ?", new String[]{String.valueOf(n.getIdNovedad())});
        return rows > 0;
    }

    public boolean eliminarNovedad(int idNovedad) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_NOVEDADES, NOV_ID + " = ?", new String[]{String.valueOf(idNovedad)});
        return rows > 0;
    }

    public List<Novedad> getNovedadesByReparacion(int idReparacion) {
        List<Novedad> lista = new ArrayList<>();
        SQLiteDatabase db   = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NOVEDADES + " WHERE " + NOV_ID_REPARACION + " = ? ORDER BY " + NOV_FECHA + " DESC", new String[]{String.valueOf(idReparacion)});
        while (c.moveToNext()) {
            Novedad n = new Novedad();
            n.setIdNovedad(    c.getInt(   c.getColumnIndexOrThrow(NOV_ID)));
            n.setIdReparacion( c.getInt(   c.getColumnIndexOrThrow(NOV_ID_REPARACION)));
            n.setFechaNovedad( c.getString(c.getColumnIndexOrThrow(NOV_FECHA)));
            n.setDescripcion(  c.getString(c.getColumnIndexOrThrow(NOV_DESCRIPCION)));
            lista.add(n);
        }
        c.close();
        return lista;
    }


    //  DASHBOARD — Consultas de resumen
    public int contarReparacionesPorEstado(String estado) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_REPARACIONES + " WHERE " + RPA_ESTADO + " = ?", new String[]{estado});
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public int contarClientes() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CLIENTES + " WHERE " + CLI_ACTIVO + " = 1", null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public int contarTecnicosActivos() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TECNICOS + " WHERE " + TEC_ACTIVO + " = 1", null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public int contarRepuestosConStockBajo(int umbral) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_REPUESTOS + " WHERE " + REP_ACTIVO + " = 1 AND " + REP_STOCK + " <= ?", new String[]{String.valueOf(umbral)});
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }
}

