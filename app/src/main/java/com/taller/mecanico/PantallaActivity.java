package com.taller.mecanico;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.taller.mecanico.databinding.ActivityPantallaBinding;

public class PantallaActivity extends AppCompatActivity {

    private ActivityPantallaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPantallaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        String usuario = prefs.getString(MainActivity.KEY_USUARIO, "");
        if (!usuario.isEmpty()) {
            binding.tvBienvenida.setText("Bienvenido, " + usuario);
        }

        binding.btnBorrarDatos.setOnClickListener(v -> borrarDatosSesion());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_inicio) {
            Toast.makeText(this, "Ya estás en el inicio", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_registros) {
            startActivity(new Intent(this, principalActivity.class));
            return true;
        } else if (id == R.id.action_consulta) {
            startActivity(new Intent(this, ConsultaActivity.class));
            return true;
        } else if (id == R.id.action_servicios) {
            startActivity(new Intent(this, ServiciosActivity.class));
            return true;
        } else if (id == R.id.action_acerca_de) {
            mostrarAcercaDe();
            return true;
        } else if (id == R.id.action_cerrar_sesion) {
            borrarDatosSesion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void mostrarAcercaDe() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_acerca_de);
        dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.92),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.findViewById(R.id.btnCerrarAcercaDe).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void borrarDatosSesion() {
        getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE).edit().clear().apply();
        Toast.makeText(this, "Datos de sesión eliminados", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
