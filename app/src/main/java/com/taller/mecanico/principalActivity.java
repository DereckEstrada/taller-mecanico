package com.taller.mecanico;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class principalActivity extends AppCompatActivity {

    private static final String ARCHIVO_REGISTROS = "registros.txt";

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cargarNacionalidad();
        cargarGenero();
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
            finish();
            return true;
        } else if (id == R.id.action_registros) {
            Toast.makeText(this, "Ya estás en Registros", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_consulta) {
            startActivity(new Intent(this, ConsultaActivity.class));
            return true;
        } else if (id == R.id.action_servicios) {
            startActivity(new Intent(this, ServiciosActivity.class));
            return true;
        } else if (id == R.id.action_acerca_de) {
            mostrarAcercaDe();
            return true;
        } else if (id == R.id.action_cerrar_sesion) {
            getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE).edit().clear().apply();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
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

    public void showDatePickerDialog(View v) {
        final TextView etPlannedDate = (TextView) findViewById(R.id.etFecha);

        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                final String selectedDate = day + "/" + (month + 1) + "/" + year;
                etPlannedDate.setText(selectedDate);
                calcularYMostrarEdad(year, month, day);
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void regresar(View view) {
        finish();
    }

    public void registrar(View view) {
        if (!validarCampos()) return;

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

        String cedula = etCedula.getText().toString();
        String nombres = etNombres.getText().toString();
        String apellidos = etApellidos.getText().toString();
        String fecha = etFecha.getText().toString();
        String edad = etEdad.getText().toString();
        String genero = cbGenero.getSelectedItem() != null ? cbGenero.getSelectedItem().toString() : "Vacío";
        String nacionalidad = cbNacionalidad.getSelectedItem() != null ? cbNacionalidad.getSelectedItem().toString() : "Vacío";
        float valorEstrellas = miRatingBar.getRating();

        String estadoCivil = "No seleccionado";
        if (rdbSoltero.isChecked()) estadoCivil = "Soltero";
        else if (rdbCasado.isChecked()) estadoCivil = "Casado";
        else if (rdbDivorciado.isChecked()) estadoCivil = "Divorciado";

        String linea = cedula + ";" + nombres + ";" + apellidos + ";" + fecha + ";" +
                edad + ";" + nacionalidad + ";" + genero + ";" + estadoCivil + ";" + valorEstrellas + "\n";

        try {
            FileOutputStream fos = openFileOutput(ARCHIVO_REGISTROS, MODE_APPEND);
            fos.write(linea.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e("TallerMecanico", "Error al guardar en archivo: " + e.getMessage());
        }

        // Guardar también en SQLite
        try {
            DatabaseHelper db = new DatabaseHelper(this);
            db.insertarUsuario(cedula, nombres, apellidos, fecha, edad, nacionalidad, genero, estadoCivil, valorEstrellas);
            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("TallerMecanico", "Error al guardar en SQLite: " + e.getMessage());
            Toast.makeText(this, "Error: la cédula ya existe en la base de datos", Toast.LENGTH_LONG).show();
        }

        Log.d("TallerMecanico", "=== NUEVO REGISTRO ===");
        Log.d("TallerMecanico", "Cédula: " + cedula);
        Log.d("TallerMecanico", "Nombres: " + nombres);
        Log.d("TallerMecanico", "Apellidos: " + apellidos);
        Log.d("TallerMecanico", "Fecha Nac: " + fecha);
        Log.d("TallerMecanico", "Edad: " + edad);
        Log.d("TallerMecanico", "Género: " + genero);
        Log.d("TallerMecanico", "Nacionalidad: " + nacionalidad);
        Log.d("TallerMecanico", "Estado Civil: " + estadoCivil);
        Log.d("TallerMecanico", "Nivel Inglés: " + valorEstrellas);

        borrar(view);
    }

    public void verDatos(View view) {
        StringBuilder contenido = new StringBuilder();

        try {
            InputStream is = openFileInput(ARCHIVO_REGISTROS);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String linea;
            int numero = 1;

            while ((linea = br.readLine()) != null) {
                String[] campos = linea.split(";");
                if (campos.length >= 9) {
                    contenido.append("── Registro #").append(numero).append(" ──\n");
                    contenido.append("Cédula:       ").append(campos[0]).append("\n");
                    contenido.append("Nombres:      ").append(campos[1]).append("\n");
                    contenido.append("Apellidos:    ").append(campos[2]).append("\n");
                    contenido.append("Fecha Nac.:   ").append(campos[3]).append("\n");
                    contenido.append("Edad:         ").append(campos[4]).append("\n");
                    contenido.append("Nacionalidad: ").append(campos[5]).append("\n");
                    contenido.append("Género:       ").append(campos[6]).append("\n");
                    contenido.append("Estado Civil: ").append(campos[7]).append("\n");
                    contenido.append("Nivel Inglés: ").append(campos[8]).append(" ★\n\n");
                    numero++;
                }
            }
            br.close();
        } catch (IOException e) {
            contenido.append("No hay registros guardados aún.");
        }

        if (contenido.length() == 0) {
            contenido.append("No hay registros guardados aún.");
        }

        new AlertDialog.Builder(this)
                .setTitle("Registros Guardados")
                .setMessage(contenido.toString())
                .setPositiveButton("Cerrar", null)
                .show();
    }

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
