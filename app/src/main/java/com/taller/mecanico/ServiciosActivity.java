package com.taller.mecanico;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class ServiciosActivity extends AppCompatActivity {

    private DatabaseHelper db;

    private EditText etCedulaCliente, etDescripcion, etCosto;
    private TextView etFechaServicio;
    private Spinner cbTipoServicio, cbEstado;
    private Button btnRegistrarServicio, btnBorrarServicio;
    private TextView tvListaServicios;

    private int servicioIdEdicion = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DatabaseHelper(this);

        etCedulaCliente   = findViewById(R.id.etCedulaCliente);
        etDescripcion     = findViewById(R.id.etDescripcionServicio);
        etCosto           = findViewById(R.id.etCosto);
        etFechaServicio   = findViewById(R.id.etFechaServicio);
        cbTipoServicio    = findViewById(R.id.cbTipoServicio);
        cbEstado          = findViewById(R.id.cbEstado);
        btnRegistrarServicio = findViewById(R.id.btnRegistrarServicio);
        btnBorrarServicio    = findViewById(R.id.btnBorrarServicio);
        tvListaServicios  = findViewById(R.id.tvListaServicios);

        cargarSpinners();
        cargarListaServicios();

        btnRegistrarServicio.setOnClickListener(v -> guardarServicio());
        btnBorrarServicio.setOnClickListener(v -> limpiarFormulario());
        etFechaServicio.setOnClickListener(v -> mostrarDatePicker());

        TextView btnVerTodos = findViewById(R.id.btnVerTodosServicios);
        btnVerTodos.setOnClickListener(v -> cargarListaServicios());
    }

    private void cargarSpinners() {
        ArrayAdapter<CharSequence> adapterTipo = ArrayAdapter.createFromResource(
                this, R.array.lista_tipos_servicio, android.R.layout.simple_spinner_item);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cbTipoServicio.setAdapter(adapterTipo);

        ArrayAdapter<CharSequence> adapterEstado = ArrayAdapter.createFromResource(
                this, R.array.lista_estados_servicio, android.R.layout.simple_spinner_item);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cbEstado.setAdapter(adapterEstado);
    }

    private void mostrarDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (DatePicker view, int year, int month, int day) -> {
            etFechaServicio.setText(day + "/" + (month + 1) + "/" + year);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void guardarServicio() {
        String cedula      = etCedulaCliente.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String costoStr    = etCosto.getText().toString().trim();
        String fecha       = etFechaServicio.getText().toString().trim();
        String tipo        = cbTipoServicio.getSelectedItem() != null ? cbTipoServicio.getSelectedItem().toString() : "";
        String estado      = cbEstado.getSelectedItem() != null ? cbEstado.getSelectedItem().toString() : "";

        if (cedula.isEmpty()) {
            etCedulaCliente.setError("Ingrese la cédula del cliente");
            return;
        }
        if (descripcion.isEmpty()) {
            etDescripcion.setError("Ingrese una descripción");
            return;
        }
        if (costoStr.isEmpty()) {
            etCosto.setError("Ingrese el costo");
            return;
        }

        double costo;
        try {
            costo = Double.parseDouble(costoStr);
        } catch (NumberFormatException e) {
            etCosto.setError("Costo inválido");
            return;
        }

        if (servicioIdEdicion == -1) {
            long id = db.insertarServicio(cedula, tipo, descripcion, fecha, costo, estado);
            if (id != -1) {
                Toast.makeText(this, "Servicio registrado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al registrar el servicio", Toast.LENGTH_SHORT).show();
            }
        } else {
            int filas = db.actualizarServicio(servicioIdEdicion, tipo, descripcion, fecha, costo, estado);
            if (filas > 0) {
                Toast.makeText(this, "Servicio actualizado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al actualizar el servicio", Toast.LENGTH_SHORT).show();
            }
            servicioIdEdicion = -1;
            btnRegistrarServicio.setText("Registrar Servicio");
        }

        limpiarFormulario();
        cargarListaServicios();
    }

    private void cargarListaServicios() {
        Cursor cursor = db.obtenerTodosServicios();
        if (cursor == null || cursor.getCount() == 0) {
            tvListaServicios.setText("No hay servicios registrados.");
            if (cursor != null) cursor.close();
            return;
        }

        StringBuilder sb = new StringBuilder();
        int num = 1;
        while (cursor.moveToNext()) {
            int id       = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERV_ID));
            String ced   = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERV_CEDULA));
            String tipo  = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERV_TIPO));
            String desc  = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERV_DESCRIPCION));
            String fecha = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERV_FECHA));
            double costo = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERV_COSTO));
            String est   = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERV_ESTADO));

            sb.append("── Servicio #").append(num).append(" (ID: ").append(id).append(") ──\n");
            sb.append("Cliente:     ").append(ced).append("\n");
            sb.append("Tipo:        ").append(tipo).append("\n");
            sb.append("Descripción: ").append(desc).append("\n");
            sb.append("Fecha:       ").append(fecha).append("\n");
            sb.append("Costo:       $").append(String.format("%.2f", costo)).append("\n");
            sb.append("Estado:      ").append(est).append("\n\n");
            num++;
        }
        cursor.close();
        tvListaServicios.setText(sb.toString());
    }

    public void editarServicio(int id) {
        Cursor cursor = db.buscarServicioPorId(id);
        if (cursor != null && cursor.moveToFirst()) {
            etCedulaCliente.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERV_CEDULA)));
            etDescripcion.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERV_DESCRIPCION)));
            etFechaServicio.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERV_FECHA)));
            etCosto.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERV_COSTO))));
            seleccionarSpinner(cbTipoServicio, cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERV_TIPO)));
            seleccionarSpinner(cbEstado, cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERV_ESTADO)));
            cursor.close();
            servicioIdEdicion = id;
            btnRegistrarServicio.setText("Actualizar Servicio");
        }
    }

    public void confirmarEliminarServicio(int id) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar servicio")
                .setMessage("¿Desea eliminar el servicio con ID " + id + "?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    db.eliminarServicio(id);
                    Toast.makeText(this, "Servicio eliminado", Toast.LENGTH_SHORT).show();
                    cargarListaServicios();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void seleccionarSpinner(Spinner spinner, String valor) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter == null || valor == null) return;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(valor)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void limpiarFormulario() {
        etCedulaCliente.setText("");
        etDescripcion.setText("");
        etCosto.setText("");
        etFechaServicio.setText("");
        cbTipoServicio.setSelection(0);
        cbEstado.setSelection(0);
        servicioIdEdicion = -1;
        btnRegistrarServicio.setText("Registrar Servicio");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_inicio) {
            startActivity(new Intent(this, PantallaActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        } else if (id == R.id.action_registros) {
            startActivity(new Intent(this, principalActivity.class));
            return true;
        } else if (id == R.id.action_consulta) {
            startActivity(new Intent(this, ConsultaActivity.class));
            return true;
        } else if (id == R.id.action_servicios) {
            Toast.makeText(this, "Ya estás en Servicios", Toast.LENGTH_SHORT).show();
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
