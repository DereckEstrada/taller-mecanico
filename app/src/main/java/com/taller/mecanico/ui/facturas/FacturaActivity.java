package com.taller.mecanico.ui.facturas;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.taller.mecanico.R;
import com.taller.mecanico.DTOmodel.ReparacionDTO;
import com.taller.mecanico.database.DatabaseHelper;
import com.taller.mecanico.model.RepuestoReparacion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FacturaActivity extends AppCompatActivity {

    public static final String EXTRA_ID_REPARACION = "id_reparacion";
    // true = se puede confirmar entrega; false = solo lectura
    public static final String EXTRA_MODO_GENERAR  = "modo_generar";

    private static final double IVA = 0.15;

    private TextView    tvNumeroFactura, tvFactCliente, tvFactVehiculo;
    private TextView    tvFactTecnico, tvFactFecha, tvFactDescripcion;
    private LinearLayout layoutRepuestosFactura;
    private TextView    tvSinRepuestos;
    private TextView    tvFactManoObra, tvFactSubtotal, tvFactIva, tvFactTotal;
    private Button      btnConfirmar, btnCancelar;

    private DatabaseHelper db;
    private int            idReparacion;
    private boolean        modoGenerar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura);

        db           = DatabaseHelper.getInstance(this);
        idReparacion = getIntent().getIntExtra(EXTRA_ID_REPARACION, 0);
        modoGenerar  = getIntent().getBooleanExtra(EXTRA_MODO_GENERAR, false);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(modoGenerar ? "Generar Factura" : "Factura");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        enlazarVistas();
        cargarFactura();
        configurarBotones();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }

    private void enlazarVistas() {
        tvNumeroFactura       = findViewById(R.id.tvNumeroFactura);
        tvFactCliente         = findViewById(R.id.tvFactCliente);
        tvFactVehiculo        = findViewById(R.id.tvFactVehiculo);
        tvFactTecnico         = findViewById(R.id.tvFactTecnico);
        tvFactFecha           = findViewById(R.id.tvFactFecha);
        tvFactDescripcion     = findViewById(R.id.tvFactDescripcion);
        layoutRepuestosFactura = findViewById(R.id.layoutRepuestosFactura);
        tvSinRepuestos        = findViewById(R.id.tvSinRepuestos);
        tvFactManoObra        = findViewById(R.id.tvFactManoObra);
        tvFactSubtotal        = findViewById(R.id.tvFactSubtotal);
        tvFactIva             = findViewById(R.id.tvFactIva);
        tvFactTotal           = findViewById(R.id.tvFactTotal);
        btnConfirmar          = findViewById(R.id.btnConfirmarEntrega);
        btnCancelar           = findViewById(R.id.btnCancelarFactura);
    }

    private void cargarFactura() {
        ReparacionDTO rpa = db.getReparacionById(idReparacion);
        if (rpa == null) { finish(); return; }

        String fechaHoy = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tvNumeroFactura.setText("Factura #" + idReparacion + "   |   " + fechaHoy);

        tvFactCliente.setText("Cliente: " + rpa.getNombreCliente());

        String veh = rpa.getPlacaVehiculo();
        tvFactVehiculo.setText((veh != null && !veh.isEmpty())
                ? "Vehículo: " + veh + " — " + rpa.getInfoVehiculo()
                : "Vehículo: No especificado");

        tvFactTecnico.setText("Técnico: " + rpa.getNombreTecnico());
        tvFactFecha.setText("Fecha ingreso: " + rpa.getFechaIngreso());
        tvFactDescripcion.setText(rpa.getDescripcion());

        // Cargar repuestos usados dinámicamente
        List<RepuestoReparacion> repuestos = db.getRepuestosByReparacion(idReparacion);
        double subtotalRepuestos = 0.0;

        if (repuestos.isEmpty()) {
            tvSinRepuestos.setVisibility(View.VISIBLE);
        } else {
            tvSinRepuestos.setVisibility(View.GONE);
            for (RepuestoReparacion rr : repuestos) {
                agregarFilaRepuesto(rr);
                subtotalRepuestos += rr.getSubtotal();
            }
        }

        // Calcular totales
        double manoObra = rpa.getCosto();
        double subtotal = manoObra + subtotalRepuestos;
        double iva      = subtotal * IVA;
        double total    = subtotal + iva;

        tvFactManoObra.setText(String.format(Locale.getDefault(), "$%.2f", manoObra));
        tvFactSubtotal.setText(String.format(Locale.getDefault(), "$%.2f", subtotalRepuestos));
        tvFactIva.setText(String.format(Locale.getDefault(),      "$%.2f", iva));
        tvFactTotal.setText(String.format(Locale.getDefault(),    "$%.2f", total));
    }

    // Agrega una fila a la tabla de repuestos en el layout dinámico
    private void agregarFilaRepuesto(RepuestoReparacion rr) {
        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setPadding(4, 6, 4, 6);

        LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 3);
        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

        TextView tvNombre = new TextView(this);
        tvNombre.setLayoutParams(p3);
        tvNombre.setText(rr.getNombreRepuesto());
        tvNombre.setTextSize(13);

        TextView tvCant = new TextView(this);
        tvCant.setLayoutParams(p1);
        tvCant.setText("x" + rr.getCantidad());
        tvCant.setTextSize(13);
        tvCant.setGravity(Gravity.CENTER);

        TextView tvSub = new TextView(this);
        tvSub.setLayoutParams(p1);
        tvSub.setText(String.format(Locale.getDefault(), "$%.2f", rr.getSubtotal()));
        tvSub.setTextSize(13);
        tvSub.setGravity(Gravity.END);

        fila.addView(tvNombre);
        fila.addView(tvCant);
        fila.addView(tvSub);
        layoutRepuestosFactura.addView(fila);
    }

    private void configurarBotones() {
        if (modoGenerar) {
            // Modo generar: muestra botones Confirmar y Cancelar
            btnConfirmar.setVisibility(View.VISIBLE);
            btnCancelar.setVisibility(View.VISIBLE);

            btnConfirmar.setOnClickListener(v -> confirmarEntrega());

            // Cancelar → NO cambia el estado, vuelve sin resultado OK
            btnCancelar.setOnClickListener(v -> {
                Toast.makeText(this, "Factura cancelada. Estado no modificado.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
                finish();
            });
        }
        // Modo ver (solo lectura): botones ocultos, el usuario navega con el botón atrás
    }

    private void confirmarEntrega() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar entrega")
                .setMessage("¿Confirmar la entrega del vehículo?\nEl estado pasará a ENTREGADO y no podrá revertirse.")
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    boolean ok = db.actualizarEstadoReparacion(idReparacion, DatabaseHelper.ESTADO_ENTREGADO);
                    if (ok) {
                        Toast.makeText(this, "Vehículo entregado. Factura generada.", Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(this, "Error al actualizar el estado", Toast.LENGTH_SHORT).show();
                    }
                })
                // Cancelar dentro del dialog tampoco cambia el estado
                .setNegativeButton("Cancelar", null)
                .show();
    }
}