package com.taller.mecanico;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.taller.mecanico.databinding.ActivityPantallaBinding;

public class PantallaActivity extends AppCompatActivity {

    private ActivityPantallaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPantallaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        String usuario = prefs.getString(MainActivity.KEY_USUARIO, "");
        if (!usuario.isEmpty()) {
            binding.tvBienvenida.setText("Bienvenido, " + usuario);
        }

        binding.btnBorrarDatos.setOnClickListener(v -> borrarDatosSesion());
    }

    private void borrarDatosSesion() {
        getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE).edit().clear().apply();
        Toast.makeText(this, "Datos de sesión eliminados", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
