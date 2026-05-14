package com.taller.mecanico;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.taller.mecanico.data.DBHelper;

import java.util.ArrayList;

public class VehiculosClienteActivity extends AppCompatActivity {

    // =========================
    // VARIABLES
    // =========================

    ListView listaVehiculos;

    EditText txtBuscarVehiculo;

    MaterialToolbar toolbar;

    DBHelper dbHelper;

    ArrayList<String> lista;

    ArrayList<Integer> listaIds;

    ArrayAdapter<String> adapter;

    int clienteId;

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
                findViewById(
                        R.id.toolbarVehiculos
                );

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

        listaVehiculos =
                findViewById(
                        R.id.listaVehiculos
                );

        txtBuscarVehiculo =
                findViewById(
                        R.id.txtBuscarVehiculo
                );

        // =========================
        // DB
        // =========================

        dbHelper =
                new DBHelper(this);

        // =========================
        // CLIENTE ID
        // =========================

        clienteId =
                getIntent().getIntExtra(
                        "cliente_id",
                        0
                );

        // =========================
        // CARGAR VEHICULOS
        // =========================

        cargarVehiculos("");

        // =========================
        // BUSCADOR
        // =========================

        txtBuscarVehiculo
                .addTextChangedListener(

                        new TextWatcher() {

                            @Override
                            public void beforeTextChanged(
                                    CharSequence s,
                                    int start,
                                    int count,
                                    int after
                            ) {
                            }

                            @Override
                            public void onTextChanged(
                                    CharSequence s,
                                    int start,
                                    int before,
                                    int count
                            ) {

                                cargarVehiculos(
                                        s.toString()
                                );
                            }

                            @Override
                            public void afterTextChanged(
                                    Editable s
                            ) {
                            }
                        }
                );

        // =========================
        // CLICK VEHICULO
        // =========================

        listaVehiculos
                .setOnItemClickListener(

                        new AdapterView
                                .OnItemClickListener() {

                            @Override
                            public void onItemClick(
                                    AdapterView<?> parent,
                                    android.view.View view,
                                    int position,
                                    long id
                            ) {

                                mostrarOpciones(
                                        position
                                );
                            }
                        }
                );

        // =========================
        // BOTON NUEVO VEHICULO
        // =========================

        findViewById(R.id.btnNuevoVehiculo)
                .setOnClickListener(v -> {

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
    // CARGAR VEHICULOS
    // =========================

    private void cargarVehiculos(
            String filtro
    ) {

        lista =
                new ArrayList<>();

        listaIds =
                new ArrayList<>();

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(

                        "SELECT * FROM vehiculo " +
                                "WHERE cliente_id=? " +
                                "AND (" +
                                "marca LIKE ? " +
                                "OR modelo LIKE ? " +
                                "OR placa LIKE ?" +
                                ")",

                        new String[]{

                                String.valueOf(
                                        clienteId
                                ),

                                "%" + filtro + "%",

                                "%" + filtro + "%",

                                "%" + filtro + "%"
                        }
                );

        if (cursor.moveToFirst()) {

            do {

                int id =

                        cursor.getInt(

                                cursor.getColumnIndexOrThrow(
                                        "id"
                                )
                        );

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

                listaIds.add(id);

                lista.add(

                        "Marca: " + marca +

                                "\nModelo: " + modelo +

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
    // OPCIONES
    // =========================

    private void mostrarOpciones(
            int position
    ) {

        int idVehiculo =
                listaIds.get(position);

        String[] opciones = {

                "Editar",

                "Eliminar"
        };

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setTitle(
                "Seleccione una opción"
        );

        builder.setItems(opciones,
                (dialog, which) -> {

                    // =========================
                    // EDITAR
                    // =========================

                    if (which == 0) {

                        Intent intent =
                                new Intent(

                                        this,

                                        EditarVehiculoActivity.class
                                );

                        intent.putExtra(
                                "vehiculo_id",
                                idVehiculo
                        );

                        startActivity(intent);
                    }

                    // =========================
                    // ELIMINAR
                    // =========================

                    else if (which == 1) {

                        eliminarVehiculo(
                                idVehiculo
                        );
                    }
                });

        builder.show();
    }

    // =========================
    // ELIMINAR VEHICULO
    // =========================

    private void eliminarVehiculo(
            int id
    ) {

        SQLiteDatabase db =
                dbHelper.getWritableDatabase();

        int resultado =
                db.delete(

                        "vehiculo",

                        "id=?",

                        new String[]{
                                String.valueOf(id)
                        }
                );

        if (resultado > 0) {

            Toast.makeText(

                    this,

                    "Vehículo eliminado correctamente",

                    Toast.LENGTH_SHORT

            ).show();

            cargarVehiculos("");
        }

        else {

            Toast.makeText(

                    this,

                    "Error al eliminar",

                    Toast.LENGTH_SHORT

            ).show();
        }

        db.close();
    }

    // =========================
    // TOOLBAR ATRAS
    // =========================

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;
    }

    // =========================
    // RECARGAR
    // =========================

    @Override
    protected void onResume() {
        super.onResume();

        cargarVehiculos(
                txtBuscarVehiculo
                        .getText()
                        .toString()
        );
    }
}