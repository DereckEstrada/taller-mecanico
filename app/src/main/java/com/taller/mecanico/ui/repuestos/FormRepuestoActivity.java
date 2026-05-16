package com.taller.mecanico.ui.repuestos;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.taller.mecanico.R;
import com.taller.mecanico.database.DatabaseHelper;
import com.taller.mecanico.model.Repuesto;

public class FormRepuestoActivity extends AppCompatActivity {

    public static final String EXTRA_ID_REPUESTO = "id_repuesto";

    private TextInputLayout    tilNombre, tilPrecio, tilStock;
    private TextInputEditText  etNombre, etPrecio, etStock;
    private MaterialButton     btnGuardar, btnCancelar;

    private DatabaseHelper db;
    private int            idRepuesto  = 0;
    private boolean        modoEdicion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_repuesto);

        db = DatabaseHelper.getInstance(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tilNombre  = findViewById(R.id.tilNombre);
        tilPrecio  = findViewById(R.id.tilPrecio);
        tilStock   = findViewById(R.id.tilStock);
        etNombre   = findViewById(R.id.etNombre);
        etPrecio   = findViewById(R.id.etPrecio);
        etStock    = findViewById(R.id.etStock);
        btnGuardar  = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        idRepuesto  = getIntent().getIntExtra(EXTRA_ID_REPUESTO, 0);
        modoEdicion = idRepuesto > 0;

        if (modoEdicion) {
            setTitle("Editar Repuesto");
            cargarDatos();
        } else {
            setTitle("Nuevo Repuesto");
        }

        btnGuardar.setOnClickListener(v -> guardar());
        btnCancelar.setOnClickListener(v -> finish());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }

    private void cargarDatos() {
        Repuesto r = db.getRepuestoById(idRepuesto);
        if (r == null) { finish(); return; }
        etNombre.setText(r.getNombre());
        etPrecio.setText(String.valueOf(r.getPrecio()));
        etStock.setText(String.valueOf(r.getStock()));
    }

    private void guardar() {
        tilNombre.setError(null);
        tilPrecio.setError(null);
        tilStock.setError(null);

        String nombre  = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
        String precioS = etPrecio.getText() != null ? etPrecio.getText().toString().trim() : "";
        String stockS  = etStock.getText()  != null ? etStock.getText().toString().trim()  : "";

        boolean valido = true;
        if (nombre.isEmpty())  { tilNombre.setError("Requerido"); valido = false; }
        if (precioS.isEmpty()) { tilPrecio.setError("Requerido"); valido = false; }
        if (stockS.isEmpty())  { tilStock.setError("Requerido");  valido = false; }
        if (!valido) return;

        double precio;
        int    stock;
        try { precio = Double.parseDouble(precioS); } catch (NumberFormatException e) { tilPrecio.setError("Valor inválido"); return; }
        try { stock  = Integer.parseInt(stockS);    } catch (NumberFormatException e) { tilStock.setError("Valor inválido");  return; }

        Repuesto r = new Repuesto();
        r.setNombre(nombre);
        r.setPrecio(precio);
        r.setStock(stock);

        if (modoEdicion) {
            r.setIdRepuesto(idRepuesto);
            if (db.actualizarRepuesto(r)) {
                Toast.makeText(this, "Repuesto actualizado", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        } else {
            long id = db.insertarRepuesto(r);
            if (id > 0) {
                Toast.makeText(this, "Repuesto registrado", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
