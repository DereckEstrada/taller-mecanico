package com.taller.mecanico;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.taller.mecanico.DTOmodel.ReparacionDTO;
import com.taller.mecanico.database.DatabaseHelper;
import com.taller.mecanico.model.Cliente;
import com.taller.mecanico.model.Vehiculo;

import java.util.List;

public class ConsultaVehiculoActivity extends AppCompatActivity {

    private EditText      etPlaca;
    private Button        btnBuscar;
    private CardView      cardVehiculo;
    private TextView      tvPlaca, tvInfoVehiculo, tvPropietario;
    private TextView      tvTituloHistorial, tvMensaje;
    private LinearLayout  containerReparaciones;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_vehiculo);
        setTitle("Consulta por Placa");

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = DatabaseHelper.getInstance(this);

        etPlaca              = findViewById(R.id.etPlaca);
        btnBuscar            = findViewById(R.id.btnBuscar);
        cardVehiculo         = findViewById(R.id.cardVehiculo);
        tvPlaca              = findViewById(R.id.tvPlacaVehiculo);
        tvInfoVehiculo       = findViewById(R.id.tvInfoVehiculo);
        tvPropietario        = findViewById(R.id.tvPropietario);
        tvTituloHistorial    = findViewById(R.id.tvTituloHistorial);
        tvMensaje            = findViewById(R.id.tvMensaje);
        containerReparaciones = findViewById(R.id.containerReparaciones);

        btnBuscar.setOnClickListener(v -> buscar());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }

    private void buscar() {
        String placa = etPlaca.getText().toString().trim().toUpperCase();
        if (placa.isEmpty()) {
            etPlaca.setError("Ingrese una placa");
            return;
        }

        Vehiculo v = db.getVehiculoByPlaca(placa);
        if (v == null) {
            tvMensaje.setText("No se encontró ningún vehículo con la placa \"" + placa + "\".");
            tvMensaje.setVisibility(View.VISIBLE);
            cardVehiculo.setVisibility(View.GONE);
            tvTituloHistorial.setVisibility(View.GONE);
            containerReparaciones.removeAllViews();
            return;
        }

        // Mostrar datos del vehículo
        tvPlaca.setText("Placa: " + v.getPlaca());
        tvInfoVehiculo.setText(v.getMarca() + " " + v.getModelo() + " — " + v.getAnio());

        Cliente cli = db.getClienteById(v.getIdCliente());
        tvPropietario.setText("Propietario: " + (cli != null ? cli.getNombreCompleto() : "Desconocido"));

        cardVehiculo.setVisibility(View.VISIBLE);
        tvMensaje.setVisibility(View.GONE);
        tvTituloHistorial.setVisibility(View.VISIBLE);

        // Historial de reparaciones
        containerReparaciones.removeAllViews();
        List<ReparacionDTO> reparaciones = db.getReparaciones();

        boolean hayReparaciones = false;
        for (ReparacionDTO r : reparaciones) {
            if (r.getIdVehiculo() == v.getIdVehiculo()) {
                hayReparaciones = true;
                containerReparaciones.addView(crearCardReparacion(r));
            }
        }

        if (!hayReparaciones) {
            TextView tvSinRep = new TextView(this);
            tvSinRep.setText("Sin reparaciones registradas para este vehículo.");
            tvSinRep.setTextColor(Color.parseColor("#9E9E9E"));
            tvSinRep.setPadding(16, 16, 16, 16);
            containerReparaciones.addView(tvSinRep);
        }
    }

    private View crearCardReparacion(ReparacionDTO r) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 12);
        card.setLayoutParams(params);
        card.setRadius(10f);
        card.setCardElevation(3f);

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(32, 24, 32, 24);

        TextView tvEstado = new TextView(this);
        tvEstado.setText(r.getEstadoLabel());
        tvEstado.setTextColor(Color.WHITE);
        tvEstado.setTextSize(12f);
        tvEstado.setTypeface(null, android.graphics.Typeface.BOLD);
        tvEstado.setBackgroundColor(colorEstado(r.getEstado()));
        tvEstado.setPadding(16, 4, 16, 4);

        TextView tvDesc = new TextView(this);
        tvDesc.setText(r.getDescripcion());
        tvDesc.setTextColor(Color.parseColor("#333333"));
        tvDesc.setTextSize(14f);
        tvDesc.setPadding(0, 8, 0, 0);

        TextView tvTec = new TextView(this);
        tvTec.setText("Técnico: " + (r.getNombreTecnico() != null ? r.getNombreTecnico() : "—"));
        tvTec.setTextColor(Color.parseColor("#666666"));
        tvTec.setTextSize(13f);

        TextView tvFecha = new TextView(this);
        tvFecha.setText("Ingreso: " + r.getFechaIngreso());
        tvFecha.setTextColor(Color.parseColor("#9E9E9E"));
        tvFecha.setTextSize(12f);

        inner.addView(tvEstado);
        inner.addView(tvDesc);
        inner.addView(tvTec);
        inner.addView(tvFecha);
        card.addView(inner);
        return card;
    }

    private int colorEstado(String estado) {
        if (estado == null) return Color.parseColor("#9E9E9E");
        switch (estado) {
            case "EN_DIAGNOSTICO":      return Color.parseColor("#1565C0");
            case "EN_REPARACION":       return Color.parseColor("#E65100");
            case "ESPERANDO_REPUESTOS": return Color.parseColor("#6A1B9A");
            case "LISTO_ENTREGA":       return Color.parseColor("#2E7D32");
            case "ENTREGADO":           return Color.parseColor("#757575");
            default:                    return Color.parseColor("#1a3a6e");
        }
    }
}
