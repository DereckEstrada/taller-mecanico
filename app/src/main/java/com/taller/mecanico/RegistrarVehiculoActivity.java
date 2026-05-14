package com.taller.mecanico;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.taller.mecanico.data.DBHelper;

public class RegistrarVehiculoActivity extends AppCompatActivity {

    EditText placa,
            marca,
            modelo,
            color,
            anio;

    MaterialButton btnGuardarVehiculo;

    MaterialToolbar toolbar;

    DBHelper dbHelper;

    int idCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registrar_vehiculo);

        // =========================
        // TOOLBAR
        // =========================

        toolbar = findViewById(R.id.toolbarVehiculo);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // =========================
        // CAMPOS
        // =========================

        placa = findViewById(R.id.txtPlaca);
        marca = findViewById(R.id.txtMarca);
        modelo = findViewById(R.id.txtModelo);
        color = findViewById(R.id.txtColor);
        anio = findViewById(R.id.txtAnio);

        btnGuardarVehiculo =
                findViewById(R.id.btnGuardarVehiculo);

        dbHelper = new DBHelper(this);

        // =========================
        // ID CLIENTE
        // =========================

        idCliente =
                getIntent().getIntExtra("cliente_id", 0);

        // =========================
        // GUARDAR
        // =========================

        btnGuardarVehiculo.setOnClickListener(v -> guardarVehiculo());
    }

    // =========================
    // GUARDAR VEHÍCULO
    // =========================

    private void guardarVehiculo() {

        String txtPlaca =
                placa.getText().toString()
                        .trim()
                        .toUpperCase();

        String txtMarca =
                marca.getText().toString()
                        .trim();

        String txtModelo =
                modelo.getText().toString()
                        .trim();

        String txtColor =
                color.getText().toString()
                        .trim();

        String txtAnio =
                anio.getText().toString()
                        .trim();

        // =========================
        // CAMPOS VACÍOS
        // =========================

        if (txtPlaca.isEmpty()
                || txtMarca.isEmpty()
                || txtModelo.isEmpty()
                || txtColor.isEmpty()
                || txtAnio.isEmpty()) {

            Toast.makeText(
                    this,
                    "Todos los campos son obligatorios",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =========================
        // VALIDAR PLACA
        // =========================

        if (txtPlaca.length() < 6) {

            placa.setError(
                    "Ingrese una placa válida"
            );

            placa.requestFocus();

            return;
        }

        // =========================
        // VALIDAR AÑO
        // =========================

        if (txtAnio.length() != 4) {

            anio.setError(
                    "El año debe tener 4 dígitos"
            );

            anio.requestFocus();

            return;
        }

        int anioVehiculo =
                Integer.parseInt(txtAnio);

        if (anioVehiculo < 1950
                || anioVehiculo > 2035) {

            anio.setError(
                    "Ingrese un año válido"
            );

            anio.requestFocus();

            return;
        }

        // =========================
        // SQLITE
        // =========================

        SQLiteDatabase db =
                dbHelper.getWritableDatabase();

        ContentValues cv =
                new ContentValues();

        cv.put("cliente_id", idCliente);

        cv.put("placa", txtPlaca);

        cv.put("marca", txtMarca);

        cv.put("modelo", txtModelo);

        cv.put("color", txtColor);

        cv.put("anio", txtAnio);

        long resultado =
                db.insert("vehiculo", null, cv);

        // =========================
        // RESULTADO
        // =========================

        if (resultado != -1) {

            Toast.makeText(
                    this,
                    "Vehículo registrado correctamente",
                    Toast.LENGTH_SHORT
            ).show();

            limpiarCampos();

            finish();
        }

        else {

            Toast.makeText(
                    this,
                    "Error al registrar vehículo",
                    Toast.LENGTH_SHORT
            ).show();
        }

        db.close();
    }

    private void limpiarCampos() {

        placa.setText("");

        marca.setText("");

        modelo.setText("");

        color.setText("");

        anio.setText("");

        placa.requestFocus();
    }


    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;
    }
}