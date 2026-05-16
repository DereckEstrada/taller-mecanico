package com.taller.mecanico.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager
 *
 * Guarda y recupera la sesión activa del usuario en SharedPreferences.
 * Almacena: id, username, rol e id de referencia (técnico/cliente vinculado).
 *
 * Uso:
 *   SessionManager session = SessionManager.getInstance(context);
 *   session.guardarSesion(usuario, mantenerActiva);
 *   session.getRol()         → "ADMIN" | "MECANICO" | "CLIENTE"
 *   session.haySesionActiva() → true/false
 *   session.cerrarSesion()
 */
public class SessionManager {
    // ─── Claves SharedPreferences ─────────────────────────────────────────────
    private static final String PREFS_NAME        = "SesionPrefs";
    private static final String KEY_SESION_ACTIVA = "sesion_activa";
    private static final String KEY_ID_USUARIO    = "id_usuario";
    private static final String KEY_USERNAME      = "username";
    private static final String KEY_ROL           = "rol";
    private static final String KEY_ID_REFERENCIA = "id_referencia";

    // ─── Roles ────────────────────────────────────────────────────────────────
    public static final String ROL_ADMIN    = "ADMIN";
    public static final String ROL_MECANICO = "MECANICO";
    public static final String ROL_CLIENTE  = "CLIENTE";

    // ─── Singleton ────────────────────────────────────────────────────────────
    private static SessionManager instance;
    private final SharedPreferences prefs;

    private SessionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }


    // ══════════════════════════════════════════════════════════════════════════
    //  GUARDAR / CERRAR SESIÓN
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Guarda la sesión del usuario autenticado.
     *
     * @param idUsuario    ID del usuario en la tabla usuarios
     * @param username     Nombre de usuario
     * @param rol          ROL_ADMIN | ROL_MECANICO | ROL_CLIENTE
     * @param idReferencia ID del técnico o cliente vinculado (0 si es admin puro)
     * @param mantener     true → la sesión persiste al cerrar la app
     */
    public void guardarSesion(int idUsuario, String username,
                              String rol, int idReferencia, boolean mantener) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_SESION_ACTIVA, mantener);  // solo persiste si marcó el check
        editor.putInt(    KEY_ID_USUARIO,    idUsuario);
        editor.putString( KEY_USERNAME,      username);
        editor.putString( KEY_ROL,           rol);
        editor.putInt(    KEY_ID_REFERENCIA, idReferencia);
        editor.apply();
    }

    /** Elimina todos los datos de sesión (logout). */
    public void cerrarSesion() {
        prefs.edit().clear().apply();
    }


    // ══════════════════════════════════════════════════════════════════════════
    //  CONSULTAR SESIÓN
    // ══════════════════════════════════════════════════════════════════════════

    /** Retorna true si hay una sesión guardada con "mantener sesión activa". */
    public boolean haySesionActiva() {
        return prefs.getBoolean(KEY_SESION_ACTIVA, false);
    }

    public int    getIdUsuario()    { return prefs.getInt(    KEY_ID_USUARIO,    -1);     }
    public String getUsername()     { return prefs.getString( KEY_USERNAME,      "");     }
    public String getRol()          { return prefs.getString( KEY_ROL,           "");     }
    public int    getIdReferencia() { return prefs.getInt(    KEY_ID_REFERENCIA, 0);      }

    // ─── Helpers de rol ───────────────────────────────────────────────────────
    public boolean esAdmin()    { return ROL_ADMIN.equals(getRol());    }
    public boolean esMecanico() { return ROL_MECANICO.equals(getRol()); }
    public boolean esCliente()  { return ROL_CLIENTE.equals(getRol());  }
}
