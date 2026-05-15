package com.taller.mecanico.ui.reparaciones;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.taller.mecanico.R;
import com.taller.mecanico.database.DatabaseHelper;

public class DetalleReparacionActivity extends AppCompatActivity {

    public static final String EXTRA_ID_REPARACION = "id_reparacion";

    private Button btnTabRepuestos, btnTabNovedades;
    private int    idReparacion;

    private RepuestosReparacionFragment fragRepuestos;
    private NovedadesFragment           fragNovedades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_reparacion);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        idReparacion = getIntent().getIntExtra(EXTRA_ID_REPARACION, 0);
        setTitle("Reparación #" + idReparacion);

        btnTabRepuestos = findViewById(R.id.btnTabRepuestos);
        btnTabNovedades = findViewById(R.id.btnTabNovedades);

        fragRepuestos = RepuestosReparacionFragment.newInstance(idReparacion);
        fragNovedades = NovedadesFragment.newInstance(idReparacion);

        mostrarTab(fragRepuestos, true);

        btnTabRepuestos.setOnClickListener(v -> mostrarTab(fragRepuestos, true));
        btnTabNovedades.setOnClickListener(v -> mostrarTab(fragNovedades, false));
    }

    private void mostrarTab(Fragment frag, boolean esRepuestos) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, frag)
                .commit();

        btnTabRepuestos.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        esRepuestos ? android.graphics.Color.parseColor("#e94560")
                                    : android.graphics.Color.parseColor("#1a3a6e")));
        btnTabNovedades.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        esRepuestos ? android.graphics.Color.parseColor("#1a3a6e")
                                    : android.graphics.Color.parseColor("#e94560")));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
