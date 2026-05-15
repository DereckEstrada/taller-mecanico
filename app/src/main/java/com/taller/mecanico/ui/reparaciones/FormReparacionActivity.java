package com.taller.mecanico.ui.reparaciones;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.taller.mecanico.R;
import com.taller.mecanico.DTOmodel.ReparacionDTO;
import com.taller.mecanico.database.DatabaseHelper;
import com.taller.mecanico.model.Cliente;
import com.taller.mecanico.model.Tecnico;
import com.taller.mecanico.model.Vehiculo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FormReparacionActivity extends AppCompatActivity {

    public static final String EXTRA_ID_REPARACION = "id_reparacion";

    private Spinner           spinnerCliente, spinnerTecnico, spinnerVehiculo, spinnerEstado;
    private View              tvLabelEstado;
    private TextInputLayout   tilDescripcion, tilCosto, tilFechaIngreso;
    private TextInputEditText etDescripcion, etCosto, etFechaIngreso;
    private Button            btnGuardar, btnCancelar;

    private DatabaseHelper   db;
    private List<Cliente>    clientes  = new ArrayList<>();
    private List<Tecnico>    tecnicos  = new ArrayList<>();
    private List<Vehiculo>   vehiculos = new ArrayList<>();

    private int     idReparacion = 0;
    private boolean modoEdicion  = false;

    private static final String[] ESTADOS_KEYS = {
            "EN_DIAGNOSTICO", "EN_REPARACION", "ESPERANDO_REPUESTOS", "LISTO_ENTREGA", "ENTREGADO"
    };
    private static final String[] ESTADOS_LABELS = {
            "En Diagnóstico", "En Reparación", "Esperando Repuestos", "Listo para Entrega", "Entregado"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_reparacion);

        db = DatabaseHelper.getInstance(this);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinnerCliente  = findViewById(R.id.spinnerCliente);
        spinnerTecnico  = findViewById(R.id.spinnerTecnico);
        spinnerVehiculo = findViewById(R.id.spinnerVehiculo);
        spinnerEstado   = findViewById(R.id.spinnerEstado);
        tvLabelEstado   = findViewById(R.id.tvLabelEstado);
        tilDescripcion  = findViewById(R.id.tilDescripcion);
        tilCosto        = findViewById(R.id.tilCosto);
        tilFechaIngreso = findViewById(R.id.tilFechaIngreso);
        etDescripcion   = findViewById(R.id.etDescripcion);
        etCosto         = findViewById(R.id.etCosto);
        etFechaIngreso  = findViewById(R.id.etFechaIngreso);
        btnGuardar      = findViewById(R.id.btnGuardar);
        btnCancelar     = findViewById(R.id.btnCancelar);

        idReparacion = getIntent().getIntExtra(EXTRA_ID_REPARACION, 0);
        modoEdicion  = idReparacion > 0;

        cargarSpinners();
        etFechaIngreso.setOnClickListener(v -> mostrarDatePicker());

        if (modoEdicion) {
            setTitle("Editar Reparación");
            spinnerEstado.setVisibility(View.VISIBLE);
            tvLabelEstado.setVisibility(View.VISIBLE);
            cargarDatos();
        } else {
            setTitle("Nueva Reparación");
            // Fecha de hoy por defecto
            Calendar c = Calendar.getInstance();
            etFechaIngreso.setText(c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR));
        }

        btnGuardar.setOnClickListener(v -> guardar());
        btnCancelar.setOnClickListener(v -> finish());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }

    private void cargarSpinners() {
        // Clientes
        clientes = db.getClientes();
        List<String> nombresClientes = new ArrayList<>();
        nombresClientes.add("Seleccione cliente");
        for (Cliente c : clientes) nombresClientes.add(c.getNombreCompleto() + " — " + c.getCedula());
        ArrayAdapter<String> adCli = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresClientes);
        adCli.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCliente.setAdapter(adCli);

        // Al seleccionar cliente, cargar sus vehículos
        spinnerCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { vehiculos.clear(); actualizarSpinnerVehiculos(); return; }
                Cliente cli = clientes.get(position - 1);
                vehiculos = db.getVehiculosByCliente(cli.getIdCliente());
                actualizarSpinnerVehiculos();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Técnicos
        tecnicos = db.getTecnicos();
        List<String> nombresTecnicos = new ArrayList<>();
        nombresTecnicos.add("Seleccione técnico");
        for (Tecnico t : tecnicos) nombresTecnicos.add(t.getNombreCompleto());
        ArrayAdapter<String> adTec = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresTecnicos);
        adTec.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTecnico.setAdapter(adTec);

        // Estado (solo edición)
        ArrayAdapter<String> adEst = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ESTADOS_LABELS);
        adEst.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adEst);
    }

    private void actualizarSpinnerVehiculos() {
        List<String> nombresVeh = new ArrayList<>();
        nombresVeh.add("Sin vehículo");
        for (Vehiculo v : vehiculos) nombresVeh.add(v.getPlaca() + " — " + v.getDescripcion());
        ArrayAdapter<String> adVeh = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresVeh);
        adVeh.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehiculo.setAdapter(adVeh);
    }

    private void mostrarDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, day) -> etFechaIngreso.setText(day + "/" + (month + 1) + "/" + year),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void cargarDatos() {
        ReparacionDTO r = db.getReparacionById(idReparacion);
        if (r == null) { finish(); return; }

        etDescripcion.setText(r.getDescripcion());
        etCosto.setText(String.valueOf(r.getCosto()));
        etFechaIngreso.setText(r.getFechaIngreso());

        // Seleccionar cliente
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getIdCliente() == r.getIdCliente()) {
                spinnerCliente.setSelection(i + 1);
                break;
            }
        }
        // Seleccionar técnico
        for (int i = 0; i < tecnicos.size(); i++) {
            if (tecnicos.get(i).getIdTecnico() == r.getIdTecnico()) {
                spinnerTecnico.setSelection(i + 1);
                break;
            }
        }
        // Seleccionar estado
        for (int i = 0; i < ESTADOS_KEYS.length; i++) {
            if (ESTADOS_KEYS[i].equals(r.getEstado())) {
                spinnerEstado.setSelection(i);
                break;
            }
        }
    }

    private void guardar() {
        tilDescripcion.setError(null);
        tilCosto.setError(null);

        int posCliente = spinnerCliente.getSelectedItemPosition();
        int posTecnico = spinnerTecnico.getSelectedItemPosition();
        String desc  = etDescripcion.getText() != null ? etDescripcion.getText().toString().trim() : "";
        String costoS = etCosto.getText() != null ? etCosto.getText().toString().trim() : "";
        String fecha  = etFechaIngreso.getText() != null ? etFechaIngreso.getText().toString().trim() : "";

        if (posCliente == 0) { Toast.makeText(this, "Seleccione un cliente", Toast.LENGTH_SHORT).show(); return; }
        if (posTecnico == 0) { Toast.makeText(this, "Seleccione un técnico", Toast.LENGTH_SHORT).show(); return; }
        if (desc.isEmpty())  { tilDescripcion.setError("Requerido"); return; }

        double costo = 0.0;
        if (!costoS.isEmpty()) {
            try { costo = Double.parseDouble(costoS); } catch (NumberFormatException e) { tilCosto.setError("Valor inválido"); return; }
        }

        int idCli = clientes.get(posCliente - 1).getIdCliente();
        int idTec = tecnicos.get(posTecnico - 1).getIdTecnico();

        int posVeh = spinnerVehiculo.getSelectedItemPosition();
        int idVeh  = (posVeh > 0 && !vehiculos.isEmpty()) ? vehiculos.get(posVeh - 1).getIdVehiculo() : 0;

        ReparacionDTO dto = new ReparacionDTO();
        dto.setIdCliente(idCli);
        dto.setIdTecnico(idTec);
        dto.setIdVehiculo(idVeh);
        dto.setDescripcion(desc);
        dto.setCosto(costo);
        dto.setFechaIngreso(fecha.isEmpty() ? obtenerFechaHoy() : fecha);

        if (modoEdicion) {
            dto.setIdReparacion(idReparacion);
            dto.setEstado(ESTADOS_KEYS[spinnerEstado.getSelectedItemPosition()]);
            if (db.actualizarReparacion(dto)) {
                Toast.makeText(this, "Reparación actualizada", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        } else {
            dto.setEstado("EN_DIAGNOSTICO");
            long id = db.insertarReparacion(dto);
            if (id > 0) {
                Toast.makeText(this, "Reparación registrada", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String obtenerFechaHoy() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
    }
}
