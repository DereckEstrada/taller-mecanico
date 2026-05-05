package com.taller.mecanico;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.taller.mecanico.databinding.ActivityMainBinding;

import java.security.Principal;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private static final String USUARIO_CORRECTO = "admin";
    private static final String PASSWORD_CORRECTA = "taller123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            Toast.makeText(this, "Acceso Concedido", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_LONG).show();
        }
    }

    private void ocultarTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void registrarse(View view){
        Intent intentPagPrincipal = new Intent(this, principalActivity.class);
        startActivity(intentPagPrincipal);
    }
}
