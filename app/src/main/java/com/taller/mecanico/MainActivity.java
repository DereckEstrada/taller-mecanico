package com.taller.mecanico;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.taller.mecanico.database.DatabaseHelper;
import com.taller.mecanico.databinding.ActivityMainBinding;
import com.taller.mecanico.model.Usuario;
import com.taller.mecanico.utils.SessionManager;

/**
 * MainActivity — Pantalla de Login
 *
 * Cambios respecto a la versión anterior:
 *  ✓ Credenciales validadas contra SQLite (DatabaseHelper) en lugar de hardcodeadas
 *  ✓ Sesión gestionada por SessionManager (guarda id, username, rol, idReferencia)
 *  ✓ Redirige a HomeActivity según el rol: ADMIN/MECANICO → HomeActivity, CLIENTE → ClienteActivity
 *  ✓ Eliminado el botón "Registrarse" (en un taller el admin crea los usuarios)
 *  ✓ Sin import innecesario de Principal
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DatabaseHelper      db;
    private SessionManager      session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db      = DatabaseHelper.getInstance(this);
        session = SessionManager.getInstance(this);

        // ── Si hay sesión guardada, saltar el login ───────────────────────────
        if (session.haySesionActiva()) {
            navegarSegunRol(session.getRol());
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(v -> {
            ocultarTeclado();
            intentarLogin();
        });

        // ── El botón "Registrarse" ya no aplica en esta app ───────────────────
        // Los usuarios los crea el administrador desde el panel de administración.
        // Si el XML aún tiene btnRegistrarse, se oculta:
        if (binding.getRoot().findViewById(R.id.btnRegistrarse) != null) {
            binding.getRoot().findViewById(R.id.btnRegistrarse).setVisibility(View.GONE);
        }
    }

    /**
     * Login de nuestra App
     */
    private void intentarLogin() {

        // ── Limpiar errores previos ───────────────────────────────────────────
        binding.tilUsuario.setError(null);
        binding.tilPassword.setError(null);

        // ── Leer campos ───────────────────────────────────────────────────────
        String username = binding.etUsuario.getText() != null
                ? binding.etUsuario.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null
                ? binding.etPassword.getText().toString().trim() : "";

        // ── Validación de campos vacíos ───────────────────────────────────────
        boolean valido = true;
        if (username.isEmpty()) {
            binding.tilUsuario.setError("El usuario es requerido");
            valido = false;
        }
        if (password.isEmpty()) {
            binding.tilPassword.setError("La contraseña es requerida");
            valido = false;
        }
        if (!valido) return;

        // ── Consultar la base de datos ────────────────────────────────────────
        Usuario usuario = db.login(username, password);

        if (usuario != null) {

            // Guardar sesión (con o sin persistencia según el checkbox)
            session.guardarSesion(
                    usuario.getIdUsuario(),
                    usuario.getUsername(),
                    usuario.getRol(),
                    usuario.getIdReferencia(),
                    binding.cbMantenerSesion.isChecked()
            );

            Toast.makeText(this, "Bienvenido, " + usuario.getUsername(), Toast.LENGTH_SHORT).show();
            navegarSegunRol(usuario.getRol());

        } else {
            // Credenciales incorrectas o usuario inactivo
            binding.tilUsuario.setError(" ");                    // resalta el campo
            binding.tilPassword.setError("Usuario o contraseña incorrectos");
            binding.etPassword.setText("");                      // limpia solo la contraseña
        }
    }

    /**
     * Redirige a la pantalla correcta según el rol del usuario autenticado.
     *
     *  ADMIN    → HomeActivity  (acceso completo: todas las pestañas)
     *  MECANICO → HomeActivity  (acceso parcial: solo Reparaciones y Novedades)
     *  CLIENTE  → ClienteActivity (vista de autoservicio: estado de su vehículo)
     */
    private void navegarSegunRol(String rol) {
        Intent intent;

        switch (rol) {
            case SessionManager.ROL_ADMIN:
            case SessionManager.ROL_MECANICO:
                intent = new Intent(this, HomeActivity.class);
                break;

//            case SessionManager.ROL_CLIENTE:
//                //intent = new Intent(this, ClienteActivity.class); No exite esta activity
//                intent = new Intent(this, HomeActivity.class);
//                break;

            default:
                // Rol desconocido: cerrar sesión y quedarse en login
                session.cerrarSesion();
                Toast.makeText(this, "Rol no reconocido. Contacte al administrador.", Toast.LENGTH_LONG).show();
                return;
        }

        // Limpiar el back stack para que no pueda volver al login con el botón atrás
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //  UTILIDADES
    private void ocultarTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}