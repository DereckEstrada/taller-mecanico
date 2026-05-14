package com.taller.mecanico;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.taller.mecanico.data.DBHelper;

public class EditarClienteActivity extends AppCompatActivity {

    // =========================
    // VARIABLES
    // =========================

    EditText cedula,
            nombre,
            telefono,
            correo,
            direccion;

    MaterialButton btnActualizar;

    MaterialToolbar toolbar;

    DBHelper dbHelper;

    int idCliente;

    // =========================
    // ON CREATE
    // =========================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editar_cliente);

        // =========================
        // TOOLBAR
        // =========================

        toolbar = findViewById(R.id.toolbarEditar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // =========================
        // CAMPOS
        // =========================

        cedula = findViewById(R.id.txtCedulaEditar);

        nombre = findViewById(R.id.txtNombreEditar);

        telefono = findViewById(R.id.txtTelefonoEditar);

        correo = findViewById(R.id.txtCorreoEditar);

        direccion = findViewById(R.id.txtDireccionEditar);

        // =========================
        // BOTÓN
        // =========================

        btnActualizar = findViewById(R.id.btnActualizar);

        // =========================
        // DB
        // =========================

        dbHelper = new DBHelper(this);

        // =========================
        // RECIBIR ID
        // =========================

        idCliente = getIntent().getIntExtra("id", 0);

        // =========================
        // CARGAR DATOS
        // =========================

        cargarDatosCliente();

        // =========================
        // ACTUALIZAR
        // =========================

        btnActualizar.setOnClickListener(v -> actualizarCliente());
    }

    // =========================
    // CARGAR DATOS
    // =========================

    private void cargarDatosCliente() {

        Cursor cursor =
                dbHelper.obtenerClientePorId(idCliente);

        if (cursor.moveToFirst()) {

            cedula.setText(

                    cursor.getString(
                            cursor.getColumnIndexOrThrow("cedula")
                    )
            );

            nombre.setText(

                    cursor.getString(
                            cursor.getColumnIndexOrThrow("nombre")
                    )
            );

            telefono.setText(

                    cursor.getString(
                            cursor.getColumnIndexOrThrow("telefono")
                    )
            );

            correo.setText(

                    cursor.getString(
                            cursor.getColumnIndexOrThrow("correo")
                    )
            );

            direccion.setText(

                    cursor.getString(
                            cursor.getColumnIndexOrThrow("direccion")
                    )
            );
        }

        cursor.close();
    }

    // =========================
    // ACTUALIZAR CLIENTE
    // =========================

    private void actualizarCliente() {

        String txtCed =
                cedula.getText().toString().trim();

        String txtNom =
                nombre.getText().toString().trim();

        String txtTel =
                telefono.getText().toString().trim();

        String txtCor =
                correo.getText().toString().trim();

        String txtDir =
                direccion.getText().toString().trim();

        // =========================
        // VALIDAR CAMPOS
        // =========================

        if (txtCed.isEmpty()
                || txtNom.isEmpty()
                || txtTel.isEmpty()
                || txtCor.isEmpty()
                || txtDir.isEmpty()) {

            Toast.makeText(
                    this,
                    "Todos los campos son obligatorios",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =========================
        // VALIDAR CÉDULA
        // =========================

        if (txtCed.length() != 10) {

            cedula.setError(
                    "La cédula debe tener 10 dígitos"
            );

            cedula.requestFocus();

            return;
        }

        // =========================
        // VALIDAR TELÉFONO
        // =========================

        if (txtTel.length() != 10) {

            telefono.setError(
                    "El teléfono debe tener 10 dígitos"
            );

            telefono.requestFocus();

            return;
        }

        // =========================
        // VALIDAR CORREO
        // =========================

        if (!android.util.Patterns
                .EMAIL_ADDRESS
                .matcher(txtCor)
                .matches()) {

            correo.setError(
                    "Correo inválido"
            );

            correo.requestFocus();

            return;
        }

        // =========================
        // ACTUALIZAR SQLITE
        // =========================

        int resultado =
                dbHelper.actualizarCliente(

                        idCliente,

                        txtCed,

                        txtNom,

                        txtTel,

                        txtCor,

                        txtDir
                );

        // =========================
        // RESULTADO
        // =========================

        if (resultado > 0) {

            Toast.makeText(
                    this,
                    "Cliente actualizado correctamente",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        }

        else {

            Toast.makeText(
                    this,
                    "Error al actualizar cliente",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =========================
    // BOTÓN ATRÁS TOOLBAR
    // =========================

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;
    }
}