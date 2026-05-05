package com.taller.mecanico;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.taller.mecanico.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    static final String PREFS_NAME = "SesionPrefs";
    static final String KEY_USUARIO = "usuario";
    static final String KEY_PASSWORD = "password";
    static final String KEY_SESION_ACTIVA = "sesion_activa";

    private static final String USUARIO_CORRECTO = "admin";
    private static final String PASSWORD_CORRECTA = "taller123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Si hay sesión guardada, ir directo a la pantalla principal
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(KEY_SESION_ACTIVA, false)) {
            irAPantallaPrincipal();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(v -> {
            ocultarTeclado();
            intentarLogin();
        });
    }

    private void intentarLogin() {
        binding.tilUsuario.setError(null);
        binding.tilPassword.setError(null);

        String usuario = binding.etUsuario.getText() != null
                ? binding.etUsuario.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null
                ? binding.etPassword.getText().toString().trim() : "";

        boolean valido = true;

        if (usuario.isEmpty()) {
            binding.tilUsuario.setError("El usuario es requerido");
            valido = false;
        }

        if (password.isEmpty()) {
            binding.tilPassword.setError("La contraseña es requerida");
            valido = false;
        }

        if (!valido) return;

        if (usuario.equals(USUARIO_CORRECTO) && password.equals(PASSWORD_CORRECTA)) {
            if (binding.cbMantenerSesion.isChecked()) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString(KEY_USUARIO, usuario);
                editor.putString(KEY_PASSWORD, password);
                editor.putBoolean(KEY_SESION_ACTIVA, true);
                editor.apply();
            }
            Toast.makeText(this, "Acceso Concedido", Toast.LENGTH_SHORT).show();
            irAPantallaPrincipal();
        } else {
            Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_LONG).show();
        }
    }

    private void irAPantallaPrincipal() {
        Intent intent = new Intent(this, PantallaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void ocultarTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
