package com.taller.mecanico;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.taller.mecanico.data.DBHelper;

import java.util.ArrayList;

public class VehiculosClienteActivity extends AppCompatActivity {

    // =========================
    // VARIABLES
    // =========================

    TextView txtNombreCliente;

    ListView listaVehiculos;

    MaterialButton btnNuevoVehiculo;

    MaterialToolbar toolbar;

    DBHelper dbHelper;

    ArrayList<String> lista;

    ArrayAdapter<String> adapter;

    int clienteId;

    String nombreCliente;

    // =========================
    // ON CREATE
    // =========================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_vehiculos_cliente
        );

        // =========================
        // TOOLBAR
        // =========================

        toolbar =
                findViewById(R.id.toolbarVehiculos);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);

            getSupportActionBar()
                    .setDisplayShowHomeEnabled(true);
        }

        // =========================
        // COMPONENTES
        // =========================

        txtNombreCliente =
                findViewById(R.id.txtNombreCliente);

        listaVehiculos =
                findViewById(R.id.listaVehiculos);

        btnNuevoVehiculo =
                findViewById(R.id.btnNuevoVehiculo);

        dbHelper =
                new DBHelper(this);

        // =========================
        // DATOS CLIENTE
        // =========================

        clienteId =
                getIntent().getIntExtra(
                        "cliente_id",
                        0
                );

        nombreCliente =
                getIntent().getStringExtra(
                        "nombre_cliente"
                );

        txtNombreCliente.setText(nombreCliente);

        // =========================
        // CARGAR VEHICULOS
        // =========================

        cargarVehiculos();

        // =========================
        // NUEVO VEHICULO
        // =========================

        btnNuevoVehiculo.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            this,
                            RegistrarVehiculoActivity.class
                    );

            intent.putExtra(
                    "cliente_id",
                    clienteId
            );

            startActivity(intent);
        });
    }

    // =========================
    // ON RESUME
    // =========================

    @Override
    protected void onResume() {
        super.onResume();

        cargarVehiculos();
    }

    // =========================
    // CARGAR VEHICULOS
    // =========================

    private void cargarVehiculos() {

        lista =
                new ArrayList<>();

        Cursor cursor =
                dbHelper.obtenerVehiculosCliente(
                        clienteId
                );

        if (cursor.moveToFirst()) {

            do {

                String placa =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "placa"
                                )
                        );

                String marca =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "marca"
                                )
                        );

                String modelo =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "modelo"
                                )
                        );

                String color =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "color"
                                )
                        );

                String anio =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "anio"
                                )
                        );

                lista.add(

                        marca + " " + modelo +

                                "\nPlaca: " + placa +

                                "\nColor: " + color +

                                "\nAño: " + anio
                );

            }

            while (cursor.moveToNext());
        }

        cursor.close();

        adapter =
                new ArrayAdapter<>(

                        this,

                        android.R.layout.simple_list_item_1,

                        lista
                );

        listaVehiculos.setAdapter(adapter);
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