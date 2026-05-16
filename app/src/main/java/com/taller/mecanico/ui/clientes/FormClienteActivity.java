package com.taller.mecanico.ui.clientes;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.taller.mecanico.R;
import com.taller.mecanico.database.DatabaseHelper;
import com.taller.mecanico.model.Cliente;

public class FormClienteActivity extends AppCompatActivity {

    // Extra que se pasa por Intent: 0 = nuevo cliente, >0 = editar
    public static final String EXTRA_ID_CLIENTE = "id_cliente";

    // ── Vistas ───────────────────────────────────────────────────────────────
    private TextInputLayout    tilCedula, tilNombre, tilApellido, tilTelefono, tilCorreo;
    private TextInputEditText  etCedula, etNombre, etApellido, etTelefono, etCorreo;
    private MaterialButton     btnGuardar, btnCancelar;

    // ── Estado ───────────────────────────────────────────────────────────────
    private DatabaseHelper db;
    private int            idCliente = 0;   // 0 = modo nuevo
    private boolean        modoEdicion = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cliente);

        db = DatabaseHelper.getInstance(this);

        // ── Toolbar con botón atrás ──────────────────────────────────────────
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        enlazarVistas();
        configurarLimiteCedula();

        // ── Determinar modo: nuevo o edición ─────────────────────────────────
        idCliente   = getIntent().getIntExtra(EXTRA_ID_CLIENTE, 0);
        modoEdicion = idCliente > 0;

        if (modoEdicion) {
            setTitle("Editar Cliente");
            cargarDatosCliente();
        } else {
            setTitle("Nuevo Cliente");
        }

        btnGuardar.setOnClickListener(v -> guardar());
        btnCancelar.setOnClickListener(v -> finish());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Botón ← del Toolbar
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void enlazarVistas() {
        tilCedula    = findViewById(R.id.tilCedula);
        tilNombre    = findViewById(R.id.tilNombre);
        tilApellido  = findViewById(R.id.tilApellido);
        tilTelefono  = findViewById(R.id.tilTelefono);
        tilCorreo    = findViewById(R.id.tilCorreo);

        etCedula     = findViewById(R.id.etCedula);
        etNombre     = findViewById(R.id.etNombre);
        etApellido   = findViewById(R.id.etApellido);
        etTelefono   = findViewById(R.id.etTelefono);
        etCorreo     = findViewById(R.id.etCorreo);

        btnGuardar   = findViewById(R.id.btnGuardar);
        btnCancelar  = findViewById(R.id.btnCancelar);
    }

    /** Limita cédula a máximo 10 caracteres */
    private void configurarLimiteCedula() {
        etCedula.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(10) });
        etTelefono.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(10) });
    }

    /** Carga los datos del cliente en modo edición */
    private void cargarDatosCliente() {
        Cliente cl = db.getClienteById(idCliente);
        if (cl == null) {
            Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etCedula.setText(cl.getCedula());
        etNombre.setText(cl.getNombre());
        etApellido.setText(cl.getApellido());
        etTelefono.setText(cl.getTelefono());
        etCorreo.setText(cl.getCorreo() != null ? cl.getCorreo() : "");

        // En edición la cédula no se puede cambiar (es identificador único)
        etCedula.setEnabled(false);
        tilCedula.setHelperText("La cédula no puede modificarse");
    }

    private void guardar() {
        if (!validar()) return;

        Cliente cl = new Cliente();
        cl.setCedula(   etCedula.getText().toString().trim());
        cl.setNombre(   etNombre.getText().toString().trim());
        cl.setApellido( etApellido.getText().toString().trim());
        cl.setTelefono( etTelefono.getText().toString().trim());
        cl.setCorreo(   etCorreo.getText().toString().trim());

        if (modoEdicion) {
            cl.setIdCliente(idCliente);
            boolean ok = db.actualizarCliente(cl);
            if (ok) {
                Toast.makeText(this, "Cliente actualizado", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Verificar que la cédula no exista ya
            if (db.getClienteByCedula(cl.getCedula()) != null) {
                tilCedula.setError("Ya existe un cliente con esta cédula");
                return;
            }
            long id = db.insertarCliente(cl);
            if (id > 0) {
                Toast.makeText(this, "Cliente registrado", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validar() {
        boolean valido = true;

        // Limpiar errores previos
        tilCedula.setError(null);
        tilNombre.setError(null);
        tilApellido.setError(null);
        tilTelefono.setError(null);

        String cedula   = etCedula.getText() != null   ? etCedula.getText().toString().trim()   : "";
        String nombre   = etNombre.getText() != null   ? etNombre.getText().toString().trim()   : "";
        String apellido = etApellido.getText() != null ? etApellido.getText().toString().trim() : "";
        String telefono = etTelefono.getText() != null ? etTelefono.getText().toString().trim() : "";

        if (cedula.isEmpty()) {
            tilCedula.setError("La cédula es obligatoria");
            valido = false;
        } else if (!modoEdicion && !validarCedula(cedula)) {
            tilCedula.setError("Cédula ecuatoriana inválida (10 dígitos)");
            valido = false;
        }

        if (nombre.isEmpty()) {
            tilNombre.setError("El nombre es obligatorio");
            valido = false;
        } else if (nombre.length() < 2) {
            tilNombre.setError("Nombre demasiado corto");
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
            tilTelefono.setError("Teléfono inválido (mínimo 9 dígitos)");
            valido = false;
        }

        return valido;
    }

    /**
     * Valida cédula ecuatoriana.
     * Algoritmo oficial: 10 dígitos, módulo 10.
     */
    private boolean validarCedula(String cedula) {
        if (cedula.length() != 10) return false;

        try {
            int provincia = Integer.parseInt(cedula.substring(0, 2));
            if (provincia < 1 || provincia > 24) return false;

            int tercerDigito = Integer.parseInt(cedula.substring(2, 3));
            if (tercerDigito >= 6) return false;

            int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
            int suma = 0;

            for (int i = 0; i < 9; i++) {
                int valor = Integer.parseInt(cedula.substring(i, i + 1)) * coeficientes[i];
                if (valor >= 10) valor -= 9;
                suma += valor;
            }

            int digitoVerificador = Integer.parseInt(cedula.substring(9, 10));
            int residuo = suma % 10;
            int resultado = (residuo == 0) ? 0 : (10 - residuo);

            return resultado == digitoVerificador;

        } catch (NumberFormatException e) {
            return false;
        }
    }
}