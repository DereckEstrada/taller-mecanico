package com.taller.mecanico;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.taller.mecanico.data.DBHelper;

public class EditarVehiculoActivity extends AppCompatActivity {

    TextInputEditText placa, marca, modelo, color, anio;

    MaterialButton btnActualizar;

    MaterialToolbar toolbar;

    DBHelper dbHelper;

    int vehiculoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_editar_vehiculo
        );

        // =========================
        // TOOLBAR
        // =========================

        toolbar =
                findViewById(
                        R.id.toolbarEditarVehiculo
                );

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);

            getSupportActionBar()
                    .setDisplayShowHomeEnabled(true);
        }

        // =========================
        // CAMPOS
        // =========================

        placa =
                findViewById(
                        R.id.txtPlacaEditar
                );

        marca =
                findViewById(
                        R.id.txtMarcaEditar
                );

        modelo =
                findViewById(
                        R.id.txtModeloEditar
                );

        color =
                findViewById(
                        R.id.txtColorEditar
                );

        anio =
                findViewById(
                        R.id.txtAnioEditar
                );

        btnActualizar =
                findViewById(
                        R.id.btnActualizarVehiculo
                );

        dbHelper =
                new DBHelper(this);

        // =========================
        // ID VEHICULO
        // =========================

        vehiculoId =
                getIntent().getIntExtra(
                        "vehiculo_id",
                        0
                );

        // =========================
        // CARGAR DATOS
        // =========================

        cargarDatosVehiculo();

        // =========================
        // ACTUALIZAR
        // =========================

        btnActualizar.setOnClickListener(v -> {

            actualizarVehiculo();
        });
    }

    // =========================
    // CARGAR DATOS
    // =========================

    private void cargarDatosVehiculo() {

        Cursor cursor =
                dbHelper.obtenerVehiculoPorId(
                        vehiculoId
                );

        if (cursor.moveToFirst()) {

            placa.setText(

                    cursor.getString(

                            cursor.getColumnIndexOrThrow(
                                    "placa"
                            )
                    )
            );

            marca.setText(

                    cursor.getString(

                            cursor.getColumnIndexOrThrow(
                                    "marca"
                            )
                    )
            );

            modelo.setText(

                    cursor.getString(

                            cursor.getColumnIndexOrThrow(
                                    "modelo"
                            )
                    )
            );

            color.setText(

                    cursor.getString(

                            cursor.getColumnIndexOrThrow(
                                    "color"
                            )
                    )
            );

            anio.setText(

                    cursor.getString(

                            cursor.getColumnIndexOrThrow(
                                    "anio"
                            )
                    )
            );
        }

        cursor.close();
    }

    // =========================
    // ACTUALIZAR VEHICULO
    // =========================

    private void actualizarVehiculo() {

        String txtPlaca =
                placa.getText()
                        .toString()
                        .trim()
                        .toUpperCase();

        String txtMarca =
                marca.getText()
                        .toString()
                        .trim();

        String txtModelo =
                modelo.getText()
                        .toString()
                        .trim();

        String txtColor =
                color.getText()
                        .toString()
                        .trim();

        String txtAnio =
                anio.getText()
                        .toString()
                        .trim();

        // =========================
        // VALIDAR VACIOS
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
                    "Placa inválida"
            );

            placa.requestFocus();

            return;
        }

        // =========================
        // VALIDAR AÑO
        // =========================

        if (txtAnio.length() != 4) {

            anio.setError(
                    "Ingrese un año válido"
            );

            anio.requestFocus();

            return;
        }

        int anioVehiculo =
                Integer.parseInt(txtAnio);

        if (anioVehiculo < 1950
                || anioVehiculo > 2035) {

            anio.setError(
                    "Año inválido"
            );

            anio.requestFocus();

            return;
        }

        // =========================
        // ACTUALIZAR
        // =========================

        dbHelper.actualizarVehiculo(

                vehiculoId,

                txtPlaca,

                txtMarca,

                txtModelo,

                txtColor,

                txtAnio
        );

        Toast.makeText(

                this,

                "Vehículo actualizado",

                Toast.LENGTH_SHORT

        ).show();

        finish();
    }

    // =========================
    // TOOLBAR ATRAS
    // =========================

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;
    }
}