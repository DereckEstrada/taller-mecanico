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

public class RegistrarClientesActivity extends AppCompatActivity {

    // =========================
    // VARIABLES
    // =========================

    EditText cedula, nombres, telefono, correo, direccion;

    MaterialButton btnGuardarCliente, btnRegresar;

    MaterialToolbar toolbar;

    DBHelper dbHelper;

    // =========================
    // ON CREATE
    // =========================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarclientes);

        // =========================
        // TOOLBAR
        // =========================

        toolbar = findViewById(R.id.toolbarRegistroCliente);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // =========================
        // CAMPOS
        // =========================

        cedula = findViewById(R.id.txtCedula);
        nombres = findViewById(R.id.txtNombre);
        telefono = findViewById(R.id.txtTelefono);
        correo = findViewById(R.id.txtCorreo);
        direccion = findViewById(R.id.txtDireccion);

        // =========================
        // BOTONES
        // =========================

        btnGuardarCliente = findViewById(R.id.btnGuardarCliente);
        btnRegresar = findViewById(R.id.btnRegresar);

        // =========================
        // BASE DE DATOS
        // =========================

        dbHelper = new DBHelper(this);

        // =========================
        // BOTÓN GUARDAR
        // =========================

        btnGuardarCliente.setOnClickListener(v -> guardarCliente());

        // =========================
        // BOTÓN REGRESAR
        // =========================

        btnRegresar.setOnClickListener(v -> finish());
    }

    // =========================
    // MÉTODO GUARDAR
    // =========================

    public void guardarCliente() {

        String txtCed = cedula.getText().toString().trim();
        String txtNom = nombres.getText().toString().trim();
        String txtTel = telefono.getText().toString().trim();
        String txtCor = correo.getText().toString().trim();
        String txtDir = direccion.getText().toString().trim();

        // =========================
        // VALIDAR CAMPOS VACÍOS
        // =========================

        if (txtCed.isEmpty()
                || txtNom.isEmpty()
                || txtTel.isEmpty()
                || txtCor.isEmpty()
                || txtDir.isEmpty()) {

            Toast.makeText(this,
                    "Todos los campos son obligatorios",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        // =========================
        // VALIDAR CÉDULA
        // =========================

        if (txtCed.length() != 10) {

            cedula.setError("La cédula debe tener 10 dígitos");
            cedula.requestFocus();

            return;
        }

        // =========================
        // VALIDAR TELÉFONO
        // =========================

        if (txtTel.length() != 10) {

            telefono.setError("El teléfono debe tener 10 dígitos");
            telefono.requestFocus();

            return;
        }

        // =========================
        // VALIDAR CORREO
        // =========================

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(txtCor)
                .matches()) {

            correo.setError("Correo electrónico inválido");
            correo.requestFocus();

            return;
        }

        // =========================
        // GUARDAR EN SQLITE
        // =========================

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("cedula", txtCed);
        cv.put("nombre", txtNom);
        cv.put("telefono", txtTel);
        cv.put("correo", txtCor);
        cv.put("direccion", txtDir);

        long resultado = db.insert("cliente", null, cv);

        // =========================
        // RESULTADO
        // =========================

        if (resultado != -1) {

            Toast.makeText(this,
                    "Cliente guardado correctamente",
                    Toast.LENGTH_SHORT).show();

            limpiarCampos();

        } else {

            Toast.makeText(this,
                    "Error al guardar cliente",
                    Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    // =========================
    // LIMPIAR CAMPOS
    // =========================

    private void limpiarCampos() {

        cedula.setText("");
        nombres.setText("");
        telefono.setText("");
        correo.setText("");
        direccion.setText("");

        cedula.requestFocus();
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