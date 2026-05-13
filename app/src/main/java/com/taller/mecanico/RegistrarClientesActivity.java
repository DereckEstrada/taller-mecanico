package com.taller.mecanico;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.taller.mecanico.data.DBHelper;

public class RegistrarClientesActivity extends AppCompatActivity {

    EditText cedula, nombres, telefono, correo, direccion;
    Button btnGuardar;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarclientes);

        cedula = findViewById(R.id.txtCedula);
        nombres = findViewById(R.id.txtNombre);
        telefono = findViewById(R.id.txtTelefono);
        correo = findViewById(R.id.txtCorreo);
        direccion = findViewById(R.id.txtDireccion);

        btnGuardar = findViewById(R.id.btnGuardarCliente);

        dbHelper = new DBHelper(this);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCliente();
            }
        });
    }

    public void guardarCliente() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("cedula", cedula.getText().toString());
        cv.put("nombre", nombres.getText().toString());
        cv.put("telefono", telefono.getText().toString());
        cv.put("correo", correo.getText().toString());
        cv.put("direccion", direccion.getText().toString());

        long resultado = db.insert("cliente", null, cv);

        if (resultado != -1) {
            Toast.makeText(this, "Cliente guardado correctamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }
}