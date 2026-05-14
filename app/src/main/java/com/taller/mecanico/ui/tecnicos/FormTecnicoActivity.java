package com.taller.mecanico.ui.tecnicos;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.taller.mecanico.R;
import com.taller.mecanico.database.DatabaseHelper;
import com.taller.mecanico.model.Tecnico;

public class FormTecnicoActivity extends AppCompatActivity {

    public static final String EXTRA_ID_TECNICO = "id_tecnico";

    // Vistas
    private TextInputLayout    tilCedula, tilNombre, tilApellido, tilTelefono;
    private TextInputEditText  etCedula, etNombre, etApellido, etTelefono;
    private Spinner            spinnerEspecialidad;
    private Switch             switchActivo;
    private Button             btnGuardar, btnCancelar;

    // Estado
    private DatabaseHelper db;
    private int            idTecnico   = 0;
    private boolean        modoEdicion = false;

    // Opciones del Spinner de especialidad
    private final String[] especialidades = {
            "Motor y Transmisión",
            "Sistema Eléctrico",
            "Frenos y Suspensión",
            "Carrocería y Pintura",
            "Aire Acondicionado",
            "Diagnóstico General",
            "Otro"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_tecnico);

        db = DatabaseHelper.getInstance(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        enlazarVistas();
        cargarSpinnerEspecialidad();

        idTecnico   = getIntent().getIntExtra(EXTRA_ID_TECNICO, 0);
        modoEdicion = idTecnico > 0;

        if (modoEdicion) {
            setTitle("Editar Técnico");
            cargarDatosTecnico();
        } else {
            setTitle("Nuevo Técnico");
        }

        btnGuardar.setOnClickListener(v -> guardar());
        btnCancelar.setOnClickListener(v -> finish());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Enlazar vistas con sus IDs
    private void enlazarVistas() {
        tilCedula    = findViewById(R.id.tilCedula);
        tilNombre    = findViewById(R.id.tilNombre);
        tilApellido  = findViewById(R.id.tilApellido);
        tilTelefono  = findViewById(R.id.tilTelefono);

        etCedula     = findViewById(R.id.etCedula);
        etNombre     = findViewById(R.id.etNombre);
        etApellido   = findViewById(R.id.etApellido);
        etTelefono   = findViewById(R.id.etTelefono);

        spinnerEspecialidad = findViewById(R.id.spinnerEspecialidad);
        switchActivo        = findViewById(R.id.switchActivo);
        btnGuardar          = findViewById(R.id.btnGuardar);
        btnCancelar         = findViewById(R.id.btnCancelar);
    }

    // Cargar el Spinner con la lista de especialidades
    private void cargarSpinnerEspecialidad() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                especialidades
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEspecialidad.setAdapter(adapter);
    }

    // Cargar datos existentes en modo edición
    private void cargarDatosTecnico() {
        Tecnico t = db.getTecnicoById(idTecnico);
        if (t == null) {
            Toast.makeText(this, "Técnico no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etCedula.setText(t.getCedula());
        etNombre.setText(t.getNombre());
        etApellido.setText(t.getApellido());
        etTelefono.setText(t.getTelefono());
        switchActivo.setChecked(t.isActivo());

        // Seleccionar la especialidad en el Spinner
        String esp = t.getEspecialidad();
        if (esp != null) {
            for (int i = 0; i < especialidades.length; i++) {
                if (especialidades[i].equals(esp)) {
                    spinnerEspecialidad.setSelection(i);
                    break;
                }
            }
        }

        // Cédula no editable en modo edición
        etCedula.setEnabled(false);
        tilCedula.setHelperText("La cédula no puede modificarse");
    }

    // Guardar nuevo técnico o actualizar existente
    private void guardar() {
        if (!validar()) return;

        Tecnico t = new Tecnico();
        t.setCedula(      etCedula.getText().toString().trim());
        t.setNombre(      etNombre.getText().toString().trim());
        t.setApellido(    etApellido.getText().toString().trim());
        t.setTelefono(    etTelefono.getText().toString().trim());
        t.setEspecialidad(spinnerEspecialidad.getSelectedItem().toString());
        t.setActivo(      switchActivo.isChecked());

        if (modoEdicion) {
            t.setIdTecnico(idTecnico);
            boolean ok = db.actualizarTecnico(t);
            if (ok) {
                Toast.makeText(this, "Técnico actualizado", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        } else {
            long id = db.insertarTecnico(t);
            if (id > 0) {
                Toast.makeText(this, "Técnico registrado", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Validar campos obligatorios
    private boolean validar() {
        boolean valido = true;

        tilCedula.setError(null);
        tilNombre.setError(null);
        tilApellido.setError(null);
        tilTelefono.setError(null);

        String cedula   = etCedula.getText()   != null ? etCedula.getText().toString().trim()   : "";
        String nombre   = etNombre.getText()   != null ? etNombre.getText().toString().trim()   : "";
        String apellido = etApellido.getText() != null ? etApellido.getText().toString().trim() : "";
        String telefono = etTelefono.getText() != null ? etTelefono.getText().toString().trim() : "";

        if (cedula.isEmpty()) {
            tilCedula.setError("La cédula es obligatoria");
            valido = false;
        } else if (cedula.length() != 10) {
            tilCedula.setError("La cédula debe tener 10 dígitos");
            valido = false;
        }

        if (nombre.isEmpty()) {
            tilNombre.setError("El nombre es obligatorio");
            valido = false;
        }

        if (apellido.isEmpty()) {
            tilApellido.setError("El apellido es obligatorio");
            valido = false;
        }

        if (telefono.isEmpty()) {
            tilTelefono.setError("El teléfono es obligatorio");
            valido = false;
        } else if (telefono.length() < 9) {
            tilTelefono.setError("Teléfono inválido");
            valido = false;
        }

        return valido;
    }
}