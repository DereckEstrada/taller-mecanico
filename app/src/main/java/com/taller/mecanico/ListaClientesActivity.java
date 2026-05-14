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

public class ListaClientesActivity extends AppCompatActivity {

    // =========================
    // VARIABLES
    // =========================

    ListView listaClientes;

    EditText txtBuscar;

    MaterialToolbar toolbar;

    DBHelper dbHelper;

    ArrayList<String> lista;

    ArrayList<Integer> listaIds;

    ArrayAdapter<String> adapter;

    // =========================
    // ON CREATE
    // =========================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lista_clientes);

        // =========================
        // TOOLBAR
        // =========================

        toolbar = findViewById(R.id.toolbarListaClientes);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // =========================
        // COMPONENTES
        // =========================

        listaClientes = findViewById(R.id.listaClientes);

        txtBuscar = findViewById(R.id.txtBuscar);

        // =========================
        // DB
        // =========================

        dbHelper = new DBHelper(this);

        // =========================
        // CARGAR CLIENTES
        // =========================

        cargarClientes("");

        // =========================
        // BUSCADOR
        // =========================

        txtBuscar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {

                cargarClientes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // =========================
        // CLICK EN CLIENTE
        // =========================

        listaClientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent,
                                    android.view.View view,
                                    int position,
                                    long id) {

                mostrarOpciones(position);
            }
        });
    }

    // =========================
    // CARGAR CLIENTES
    // =========================

    private void cargarClientes(String filtro) {

        lista = new ArrayList<>();

        listaIds = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(

                "SELECT * FROM cliente " +
                        "WHERE nombre LIKE ? " +
                        "OR cedula LIKE ?",

                new String[]{

                        "%" + filtro + "%",
                        "%" + filtro + "%"
                }
        );

        if (cursor.moveToFirst()) {

            do {

                int id =

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("id")
                        );

                String cedula =

                        cursor.getString(
                                cursor.getColumnIndexOrThrow("cedula")
                        );

                String nombre =

                        cursor.getString(
                                cursor.getColumnIndexOrThrow("nombre")
                        );

                String telefono =

                        cursor.getString(
                                cursor.getColumnIndexOrThrow("telefono")
                        );

                String correo =

                        cursor.getString(
                                cursor.getColumnIndexOrThrow("correo")
                        );

                String direccion =

                        cursor.getString(
                                cursor.getColumnIndexOrThrow("direccion")
                        );

                listaIds.add(id);

                lista.add(

                        "Nombre: " + nombre +

                                "\nCédula: " + cedula +

                                "\nTeléfono: " + telefono +

                                "\nCorreo: " + correo +

                                "\nDirección: " + direccion
                );

            }

            while (cursor.moveToNext());
        }

        cursor.close();

        adapter = new ArrayAdapter<>(

                this,

                android.R.layout.simple_list_item_1,

                lista
        );

        listaClientes.setAdapter(adapter);
    }

    // =========================
    // OPCIONES
    // =========================

    private void mostrarOpciones(int position) {

        int idCliente = listaIds.get(position);

        String[] opciones = {

                "Editar",
                "Eliminar",
                "Vehículos"
        };

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setTitle("Seleccione una opción");

        builder.setItems(opciones, (dialog, which) -> {

            // =========================
            // EDITAR
            // =========================

            if (which == 0) {

                Intent intent = new Intent(
                        this,
                        EditarClienteActivity.class
                );

                intent.putExtra("id", idCliente);

                startActivity(intent);
            }

            // =========================
            // ELIMINAR
            // =========================

            else if (which == 1) {

                eliminarCliente(idCliente);
            }

            // =========================
            // VEHÍCULO
            // =========================

            else if (which == 2) {

                Intent intent = new Intent(
                        this,
                        VehiculosClienteActivity.class
                );

                intent.putExtra(
                        "cliente_id",
                        idCliente
                );

                startActivity(intent);
            }
        });

        builder.show();
    }

    // =========================
    // ELIMINAR CLIENTE
    // =========================

    private void eliminarCliente(int id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int resultado = db.delete(

                "cliente",

                "id=?",

                new String[]{
                        String.valueOf(id)
                }
        );

        if (resultado > 0) {

            Toast.makeText(

                    this,

                    "Cliente eliminado correctamente",

                    Toast.LENGTH_SHORT

            ).show();

            cargarClientes("");
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
    // BOTÓN ATRÁS TOOLBAR
    // =========================

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        cargarClientes(
                txtBuscar.getText().toString()
        );
    }
}