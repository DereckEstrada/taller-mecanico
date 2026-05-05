package com.taller.mecanico;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class principalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cargarNacionalidad();
        cargarGenero();
    }

    public void showDatePickerDialog(View v) {
        final TextView etPlannedDate = (TextView) findViewById(R.id.etFecha);

        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = day + "/" + (month + 1) + "/" + year;
                etPlannedDate.setText(selectedDate);
                calcularYMostrarEdad(year,month,day);
            }
        });

        newFragment.show(getSupportFragmentManager(), "Calendario");
    }

    private void calcularYMostrarEdad(int anioNacimiento, int mesNacimiento, int diaNacimiento) {
        java.util.Calendar hoy = java.util.Calendar.getInstance();

        java.util.Calendar nacimiento = java.util.Calendar.getInstance();
        nacimiento.set(anioNacimiento, mesNacimiento, diaNacimiento);

        int edad = hoy.get(java.util.Calendar.YEAR) - nacimiento.get(java.util.Calendar.YEAR);

        if (hoy.get(java.util.Calendar.DAY_OF_YEAR) < nacimiento.get(java.util.Calendar.DAY_OF_YEAR)) {
            edad--;
        }
        android.widget.EditText etEdad = findViewById(R.id.etEdad);
        etEdad.setText(String.valueOf(edad));
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        int id = view.getId();

        if (id == R.id.btnSoltero) {
            if (checked) {
                Toast.makeText(this, "Seleccionó Soltero", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.btnCasado) {
            if (checked) {
                Toast.makeText(this, "Seleccionó Casado", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.btnDivorciado) {
            if (checked) {
                Toast.makeText(this, "Seleccionó Divorciado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void cargarNacionalidad() {
        Spinner spinner = (Spinner) findViewById(R.id.cbNacionalidad);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.lista_nacionalidades, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String nacionalidadSeleccionada = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void cargarGenero() {
        Spinner spinner = (Spinner) findViewById(R.id.cbGenero);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.lista_generos, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String generoSeleccionado = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void regresar(View view){
        finish();
    }

    // metodo para registrar e imprimir en Logs
    public void registrar(View view) {

        //Verificar campos
        if (!validarCampos()) return;
        // Elementos visuales
        EditText etCedula = findViewById(R.id.etCedula);
        EditText etNombres = findViewById(R.id.etNombres);
        EditText etApellidos = findViewById(R.id.etApellidos);
        TextView etFecha = findViewById(R.id.etFecha);
        TextView etEdad = findViewById(R.id.etEdad);
        Spinner cbGenero = findViewById(R.id.cbGenero);
        Spinner cbNacionalidad = findViewById(R.id.cbNacionalidad);
        RatingBar miRatingBar = findViewById(R.id.ratingBar);

        RadioButton rdbSoltero = findViewById(R.id.btnSoltero);
        RadioButton rdbCasado = findViewById(R.id.btnCasado);
        RadioButton rdbDivorciado = findViewById(R.id.btnDivorciado);

        // Extraer el texto de los campos
        String cedula = etCedula.getText().toString();
        String nombres = etNombres.getText().toString();
        String apellidos = etApellidos.getText().toString();
        String fecha = etFecha.getText().toString();
        String edad = etEdad.getText().toString();

        // Extraer el nacionalidad, genero y nivel de ingles
        String genero = cbGenero.getSelectedItem() != null ? cbGenero.getSelectedItem().toString() : "Vacio";
        String nacionalidad = cbNacionalidad.getSelectedItem() != null ? cbNacionalidad.getSelectedItem().toString() : "Vacio";
        float valorEstrellas = miRatingBar.getRating();

        // Determinar Estado Civil está seleccionado
        String estadoCivil = "No seleccionado";
        if (rdbSoltero.isChecked()) estadoCivil = "Soltero";
        else if (rdbCasado.isChecked()) estadoCivil = "Casado";
        else if (rdbDivorciado.isChecked()) estadoCivil = "Divorciado";
        Toast.makeText(this, "Usuario Registrado", Toast.LENGTH_LONG).show();
        // Imprimir en logs
        Log.d("TallerMecanico", "=== NUEVO REGISTRO ===");
        Log.d("TallerMecanico", "Cédula: " + cedula);
        Log.d("TallerMecanico", "Nombres: " + nombres);
        Log.d("TallerMecanico", "Apellidos: " + apellidos);
        Log.d("TallerMecanico", "Fecha Nac: " + fecha);
        Log.d("TallerMecanico", "Edad: " + edad);
        Log.d("TallerMecanico", "Género: " + genero);
        Log.d("TallerMecanico", "Nacionalidad: " + nacionalidad);
        Log.d("TallerMecanico", "Estado Civil: " + estadoCivil);
        Log.d("TallerMecanico", "Nivel Ingles: " + valorEstrellas);

        borrar(view);
    }

    /*
    Metodo que borra el formaulario
     */
    public void borrar(View view) {
        ((EditText) findViewById(R.id.etCedula)).setText("");
        ((EditText) findViewById(R.id.etNombres)).setText("");
        ((EditText) findViewById(R.id.etApellidos)).setText("");
        ((TextView) findViewById(R.id.etFecha)).setText("");
        ((TextView) findViewById(R.id.etEdad)).setText("");

        ((Spinner) findViewById(R.id.cbGenero)).setSelection(0);
        ((Spinner) findViewById(R.id.cbNacionalidad)).setSelection(0);

        ((RadioButton) findViewById(R.id.btnSoltero)).setChecked(false);
        ((RadioButton) findViewById(R.id.btnCasado)).setChecked(false);
        ((RadioButton) findViewById(R.id.btnDivorciado)).setChecked(false);
        RatingBar miRatingBar = findViewById(R.id.ratingBar);
        miRatingBar.setRating(0.0f);
    }

    /*
     valida campos vacios
     */
    private boolean validarCampos() {
        boolean esValido = true;

        EditText etCedula = findViewById(R.id.etCedula);
        EditText etNombres = findViewById(R.id.etNombres);
        EditText etApellidos = findViewById(R.id.etApellidos);
        TextView etFecha = findViewById(R.id.etFecha);
        RadioButton rdbSoltero = findViewById(R.id.btnSoltero);
        RadioButton rdbCasado = findViewById(R.id.btnCasado);
        RadioButton rdbDivorciado = findViewById(R.id.btnDivorciado);

        if (etCedula.getText().toString().trim().isEmpty()) {
            etCedula.setError("La cédula es obligatoria");
            esValido = false;
        }
        if (etNombres.getText().toString().trim().isEmpty()) {
            etNombres.setError("El nombre es obligatorio");
            esValido = false;
        }
        if (etApellidos.getText().toString().trim().isEmpty()) {
            etApellidos.setError("El apellido es obligatorio");
            esValido = false;
        }
        if (etFecha.getText().toString().trim().isEmpty()) {
            etFecha.setError("Debe seleccionar una fecha");
            esValido = false;
        }
        if (!rdbSoltero.isChecked() && !rdbCasado.isChecked() && !rdbDivorciado.isChecked()) {
            esValido = false;
        }
        if (!esValido) {
            Toast.makeText(this, "Campos incompletos. Revise las alertas en rojo.", Toast.LENGTH_LONG).show();
        }
        return esValido;
    }
}