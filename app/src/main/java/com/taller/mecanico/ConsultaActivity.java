package com.taller.mecanico;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.Dialog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ConsultaActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private String cedulaActual = null;

    private TextInputEditText etBuscarCedula, etCedula, etNombres, etApellidos, etEdad, etFecha;
    private Spinner cbNacionalidad, cbGenero;
    private RadioButton btnSoltero, btnCasado, btnDivorciado;
    private RatingBar ratingBar;
    private Button btnBuscar, btnActualizar, btnEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = new DatabaseHelper(this);

        etBuscarCedula = findViewById(R.id.etBuscarCedula);
        etCedula       = findViewById(R.id.etCedula);
        etNombres      = findViewById(R.id.etNombres);
        etApellidos    = findViewById(R.id.etApellidos);
        etEdad         = findViewById(R.id.etEdad);
        etFecha        = findViewById(R.id.etFecha);
        cbNacionalidad = findViewById(R.id.cbNacionalidad);
        cbGenero       = findViewById(R.id.cbGenero);
        btnSoltero     = findViewById(R.id.btnSoltero);
        btnCasado      = findViewById(R.id.btnCasado);
        btnDivorciado  = findViewById(R.id.btnDivorciado);
        ratingBar      = findViewById(R.id.ratingBar);
        btnBuscar      = findViewById(R.id.btnBuscar);
        btnActualizar  = findViewById(R.id.btnActualizar);
        btnEliminar    = findViewById(R.id.btnEliminar);

        cargarSpinners();
        deshabilitarFormulario();

        btnBuscar.setOnClickListener(v -> buscarRegistro());
        btnActualizar.setOnClickListener(v -> actualizarRegistro());
        btnEliminar.setOnClickListener(v -> confirmarEliminar());
    }

    private void cargarSpinners() {
        ArrayAdapter<CharSequence> adapterNac = ArrayAdapter.createFromResource(
                this, R.array.lista_nacionalidades, android.R.layout.simple_spinner_item);
        adapterNac.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cbNacionalidad.setAdapter(adapterNac);

        ArrayAdapter<CharSequence> adapterGen = ArrayAdapter.createFromResource(
                this, R.array.lista_generos, android.R.layout.simple_spinner_item);
        adapterGen.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cbGenero.setAdapter(adapterGen);
    }

    private void buscarRegistro() {
        String cedula = etBuscarCedula.getText() != null
                ? etBuscarCedula.getText().toString().trim() : "";

        if (cedula.isEmpty()) {
            etBuscarCedula.setError("Ingrese una cédula");
            return;
        }

        Cursor cursor = db.buscarPorCedula(cedula);

        if (cursor != null && cursor.moveToFirst()) {
            cedulaActual = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CEDULA));
            String nombres     = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOMBRES));
            String apellidos   = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_APELLIDOS));
            String fecha       = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FECHA));
            String edad        = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EDAD));
            String nacionalidad= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NACIONALIDAD));
            String genero      = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GENERO));
            String estadoCivil = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ESTADO_CIVIL));
            float nivelIngles  = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NIVEL_INGLES));
            cursor.close();

            etCedula.setText(cedulaActual);
            etNombres.setText(nombres);
            etApellidos.setText(apellidos);
            etFecha.setText(fecha);
            etEdad.setText(edad);
            seleccionarSpinner(cbNacionalidad, nacionalidad);
            seleccionarSpinner(cbGenero, genero);
            seleccionarEstadoCivil(estadoCivil);
            ratingBar.setRating(nivelIngles);

            habilitarFormulario();
            Toast.makeText(this, "Registro encontrado", Toast.LENGTH_SHORT).show();
        } else {
            if (cursor != null) cursor.close();
            cedulaActual = null;
            limpiarFormulario();
            deshabilitarFormulario();
            Toast.makeText(this, "No se encontró ningún registro con esa cédula", Toast.LENGTH_LONG).show();
        }
    }

    private void actualizarRegistro() {
        if (cedulaActual == null) {
            Toast.makeText(this, "Primero busque un registro", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombres     = etNombres.getText() != null ? etNombres.getText().toString().trim() : "";
        String apellidos   = etApellidos.getText() != null ? etApellidos.getText().toString().trim() : "";
        String fecha       = etFecha.getText() != null ? etFecha.getText().toString().trim() : "";
        String edad        = etEdad.getText() != null ? etEdad.getText().toString().trim() : "";
        String nacionalidad= cbNacionalidad.getSelectedItem() != null ? cbNacionalidad.getSelectedItem().toString() : "";
        String genero      = cbGenero.getSelectedItem() != null ? cbGenero.getSelectedItem().toString() : "";
        float nivelIngles  = ratingBar.getRating();

        String estadoCivil = "No seleccionado";
        if (btnSoltero.isChecked()) estadoCivil = "Soltero";
        else if (btnCasado.isChecked()) estadoCivil = "Casado";
        else if (btnDivorciado.isChecked()) estadoCivil = "Divorciado";

        if (nombres.isEmpty() || apellidos.isEmpty()) {
            Toast.makeText(this, "Nombres y apellidos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int filas = db.actualizarUsuario(cedulaActual, nombres, apellidos, fecha, edad,
                nacionalidad, genero, estadoCivil, nivelIngles);

        if (filas > 0) {
            Toast.makeText(this, "Registro actualizado correctamente", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error al actualizar el registro", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarEliminar() {
        if (cedulaActual == null) {
            Toast.makeText(this, "Primero busque un registro", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de que desea eliminar el registro de la cédula " + cedulaActual + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarRegistro())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarRegistro() {
        int filas = db.eliminarUsuario(cedulaActual);

        if (filas > 0) {
            Toast.makeText(this, "Registro eliminado correctamente", Toast.LENGTH_LONG).show();
            cedulaActual = null;
            etBuscarCedula.setText("");
            limpiarFormulario();
            deshabilitarFormulario();
        } else {
            Toast.makeText(this, "Error al eliminar el registro", Toast.LENGTH_SHORT).show();
        }
    }

    private void seleccionarSpinner(Spinner spinner, String valor) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter == null) return;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(valor)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void seleccionarEstadoCivil(String estadoCivil) {
        btnSoltero.setChecked(false);
        btnCasado.setChecked(false);
        btnDivorciado.setChecked(false);
        if ("Soltero".equals(estadoCivil)) btnSoltero.setChecked(true);
        else if ("Casado".equals(estadoCivil)) btnCasado.setChecked(true);
        else if ("Divorciado".equals(estadoCivil)) btnDivorciado.setChecked(true);
    }

    private void habilitarFormulario() {
        etNombres.setEnabled(true);
        etApellidos.setEnabled(true);
        etFecha.setEnabled(true);
        etEdad.setEnabled(true);
        cbNacionalidad.setEnabled(true);
        cbGenero.setEnabled(true);
        btnSoltero.setEnabled(true);
        btnCasado.setEnabled(true);
        btnDivorciado.setEnabled(true);
        ratingBar.setIsIndicator(false);
        btnActualizar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    private void deshabilitarFormulario() {
        etNombres.setEnabled(false);
        etApellidos.setEnabled(false);
        etFecha.setEnabled(false);
        etEdad.setEnabled(false);
        cbNacionalidad.setEnabled(false);
        cbGenero.setEnabled(false);
        btnSoltero.setEnabled(false);
        btnCasado.setEnabled(false);
        btnDivorciado.setEnabled(false);
        ratingBar.setIsIndicator(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private void limpiarFormulario() {
        etCedula.setText("");
        etNombres.setText("");
        etApellidos.setText("");
        etFecha.setText("");
        etEdad.setText("");
        cbNacionalidad.setSelection(0);
        cbGenero.setSelection(0);
        btnSoltero.setChecked(false);
        btnCasado.setChecked(false);
        btnDivorciado.setChecked(false);
        ratingBar.setRating(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_inicio) {
            startActivity(new Intent(this, PantallaActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        } else if (id == R.id.action_registros) {
            startActivity(new Intent(this, principalActivity.class));
            return true;
        } else if (id == R.id.action_consulta) {
            Toast.makeText(this, "Ya estás en Consulta", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_servicios) {
            startActivity(new Intent(this, ServiciosActivity.class));
            return true;
        } else if (id == R.id.action_acerca_de) {
            mostrarAcercaDe();
            return true;
        } else if (id == R.id.action_cerrar_sesion) {
            getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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
}
