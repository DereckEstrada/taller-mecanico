package com.taller.mecanico.ui.facturas;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.taller.mecanico.R;
import com.taller.mecanico.DTOmodel.ReparacionDTO;
import com.taller.mecanico.database.DatabaseHelper;
import com.taller.mecanico.model.Cliente;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistorialFacturasActivity extends AppCompatActivity {

    private ListView            listView;
    private TextView            tvVacio;
    private EditText            etFiltroCedula, etFiltroFecha;

    private DatabaseHelper      db;
    private HistorialAdapter    adapter;

    // Lista de respaldo con TODOS los entregados y lista dinámica para el adapter
    private List<ReparacionDTO> listaFacturasTodas = new ArrayList<>();
    private List<ReparacionDTO> listaFacturasFiltradas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_facturas);

        db = DatabaseHelper.getInstance(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Historial de Caja");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listView       = findViewById(R.id.listViewHistorial);
        tvVacio        = findViewById(R.id.tvVacioHistorial);
        etFiltroCedula = findViewById(R.id.etFiltroCedula);
        etFiltroFecha  = findViewById(R.id.etFiltroFecha);

        cargarHistorial();
        configurarBuscadores();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            ReparacionDTO rpa = listaFacturasFiltradas.get(position);
            Intent intent = new Intent(HistorialFacturasActivity.this, FacturaActivity.class);
            intent.putExtra(FacturaActivity.EXTRA_ID_REPARACION, rpa.getIdReparacion());
            intent.putExtra(FacturaActivity.EXTRA_MODO_GENERAR, false);
            startActivity(intent);
        });
    }

    private void cargarHistorial() {
        List<ReparacionDTO> todas = db.getReparaciones();
        listaFacturasTodas = new ArrayList<>();
        listaFacturasFiltradas = new ArrayList<>();

        // Traer solo los entregados de la DB
        for (ReparacionDTO r : todas) {
            if (DatabaseHelper.ESTADO_ENTREGADO.equals(r.getEstado())) {
                listaFacturasTodas.add(r);
            }
        }

        // Al inicio, la lista mostrada es igual a la lista completa
        listaFacturasFiltradas.addAll(listaFacturasTodas);

        adapter = new HistorialAdapter(this, listaFacturasFiltradas, db);
        listView.setAdapter(adapter);
        tvVacio.setVisibility(listaFacturasFiltradas.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void configurarBuscadores() {
        TextWatcher filtroWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                aplicarFiltros();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        etFiltroCedula.addTextChangedListener(filtroWatcher);
        etFiltroFecha.addTextChangedListener(filtroWatcher);
    }

    private void aplicarFiltros() {
        String txtCedula = etFiltroCedula.getText().toString().trim();
        String txtFecha  = etFiltroFecha.getText().toString().trim();

        listaFacturasFiltradas.clear();

        for (ReparacionDTO r : listaFacturasTodas) {
            boolean coincideCedula = true;
            boolean coincideFecha  = true;

            // Filtro por Cédula: cruzamos datos con la tabla clientes
            if (!txtCedula.isEmpty()) {
                Cliente cl = db.getClienteById(r.getIdCliente());
                if (cl != null) {
                    coincideCedula = cl.getCedula().contains(txtCedula);
                } else {
                    coincideCedula = false;
                }
            }

            // Filtro por Fecha
            if (!txtFecha.isEmpty()) {
                coincideFecha = r.getFechaIngreso().contains(txtFecha);
            }

            // Si pasa ambas validaciones, entra en la vista
            if (coincideCedula && coincideFecha) {
                listaFacturasFiltradas.add(r);
            }
        }

        adapter.notifyDataSetChanged();
        tvVacio.setVisibility(listaFacturasFiltradas.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }

    private static class HistorialAdapter extends ArrayAdapter<ReparacionDTO> {
        private final Context            context;
        private final List<ReparacionDTO> datos;
        private final DatabaseHelper     database;

        public HistorialAdapter(Context context, List<ReparacionDTO> datos, DatabaseHelper database) {
            super(context, R.layout.item_factura_historial, datos);
            this.context  = context;
            this.datos    = datos;
            this.database = database;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_factura_historial, parent, false);
            }

            ReparacionDTO rpa = datos.get(position);

            TextView tvNumFactura = convertView.findViewById(R.id.tvHistorialNumFactura);
            TextView tvFecha      = convertView.findViewById(R.id.tvHistorialFecha);
            TextView tvCliente    = convertView.findViewById(R.id.tvHistorialCliente);
            TextView tvVehiculo   = convertView.findViewById(R.id.tvHistorialVehiculo);
            TextView tvTotal      = convertView.findViewById(R.id.tvHistorialTotal);

            tvNumFactura.setText("Factura #" + rpa.getIdReparacion());
            tvFecha.setText(rpa.getFechaIngreso());
            tvCliente.setText("Cliente: " + rpa.getNombreCliente());

            String placa = rpa.getPlacaVehiculo();
            tvVehiculo.setText(placa != null && !placa.isEmpty()
                    ? "Vehículo: " + placa + " — " + rpa.getInfoVehiculo()
                    : "Vehículo: No especificado");

            double subtotalBase = database.getTotalReparacion(rpa.getIdReparacion());
            double totalConIva  = subtotalBase * 1.15;

            tvTotal.setText(String.format(Locale.getDefault(), "$%.2f", totalConIva));

            return convertView;
        }
    }
}