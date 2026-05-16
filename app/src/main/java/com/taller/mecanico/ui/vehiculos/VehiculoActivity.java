package com.taller.mecanico.ui.vehiculos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.taller.mecanico.R;
import com.taller.mecanico.database.DatabaseHelper;
import com.taller.mecanico.model.Vehiculo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class VehiculoActivity extends AppCompatActivity {

    public static final String EXTRA_ID_CLIENTE     = "id_cliente";
    public static final String EXTRA_NOMBRE_CLIENTE = "nombre_cliente";

    private ListView       listView;
    private Button         btnNuevoVehiculo;
    private TextView       tvVacio;

    private VehiculoAdapter adapter;
    private DatabaseHelper  db;
    private int             idCliente;
    private String          nombreCliente;
    private List<Vehiculo>  listaVehiculos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculo);

        db            = DatabaseHelper.getInstance(this);
        idCliente     = getIntent().getIntExtra(EXTRA_ID_CLIENTE, 0);
        nombreCliente = getIntent().getStringExtra(EXTRA_NOMBRE_CLIENTE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Vehículos");
            getSupportActionBar().setSubtitle(nombreCliente);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listView        = findViewById(R.id.listViewVehiculos);
        btnNuevoVehiculo = findViewById(R.id.btnNuevoVehiculo);
        tvVacio         = findViewById(R.id.tvVacio);

        cargarVehiculos();

        btnNuevoVehiculo.setOnClickListener(v -> mostrarDialogoNuevoVehiculo());

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Vehiculo vehiculo = listaVehiculos.get(position);
            confirmarEliminar(vehiculo);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cargarVehiculos() {
        listaVehiculos = db.getVehiculosByCliente(idCliente);
        adapter = new VehiculoAdapter(this, listaVehiculos);
        listView.setAdapter(adapter);
        tvVacio.setVisibility(listaVehiculos.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void mostrarDialogoNuevoVehiculo() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_form_vehiculo, null);

        TextInputLayout   tilPlaca = dialogView.findViewById(R.id.tilPlaca);
        TextInputEditText etPlaca  = dialogView.findViewById(R.id.etPlaca);
        TextInputEditText etMarca  = dialogView.findViewById(R.id.etMarca);
        TextInputEditText etModelo = dialogView.findViewById(R.id.etModelo);
        Spinner           spAnio   = dialogView.findViewById(R.id.spAnio);

        // Spinner con años: actual hasta 30 años atrás
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        String[] anios = new String[31];
        for (int i = 0; i <= 30; i++) {
            anios[i] = String.valueOf(anioActual - i);
        }
        ArrayAdapter<String> adapterAnio = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, anios);
        spAnio.setAdapter(adapterAnio);

        // Construir el dialog manualmente para controlar el cierre
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Registrar Vehículo")
                .setView(dialogView)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.show();

        // Sobreescribir el botón Guardar para validar antes de cerrar
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

            tilPlaca.setError(null);

            String placa  = etPlaca.getText()  != null ? etPlaca.getText().toString().trim().toUpperCase()  : "";
            String marca  = etMarca.getText()  != null ? etMarca.getText().toString().trim()  : "";
            String modelo = etModelo.getText() != null ? etModelo.getText().toString().trim() : "";
            int    anio   = Integer.parseInt(spAnio.getSelectedItem().toString());

            if (placa.isEmpty()) {
                tilPlaca.setError("La placa es obligatoria");
                return;
            }
            if (placa.length() < 6 || placa.length() > 8) {
                tilPlaca.setError("Placa inválida (6-8 caracteres)");
                return;
            }
            if (marca.isEmpty()) {
                etMarca.setError("La marca es obligatoria");
                return;
            }
            if (modelo.isEmpty()) {
                etModelo.setError("El modelo es obligatorio");
                return;
            }
            if (db.getVehiculoByPlaca(placa) != null) {
                tilPlaca.setError("Ya existe un vehículo con esta placa");
                return;
            }

            Vehiculo veh = new Vehiculo();
            veh.setIdCliente(idCliente);
            veh.setPlaca(placa);
            veh.setMarca(marca);
            veh.setModelo(modelo);
            veh.setAnio(anio);

            long id = db.insertarVehiculo(veh);
            if (id > 0) {
                Toast.makeText(this, "Vehículo registrado", Toast.LENGTH_SHORT).show();
                cargarVehiculos();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmarEliminar(Vehiculo vehiculo) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar vehículo")
                .setMessage("¿Eliminar " + vehiculo.getMarca() + " " + vehiculo.getModelo()
                        + " (" + vehiculo.getPlaca() + ")?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    db.eliminarVehiculo(vehiculo.getIdVehiculo());
                    Toast.makeText(this, "Vehículo eliminado", Toast.LENGTH_SHORT).show();
                    cargarVehiculos();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}