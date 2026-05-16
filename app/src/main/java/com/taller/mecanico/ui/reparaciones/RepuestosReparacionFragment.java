package com.taller.mecanico.ui.reparaciones;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.taller.mecanico.R;
import com.taller.mecanico.database.DatabaseHelper;
import com.taller.mecanico.model.Repuesto;
import com.taller.mecanico.model.RepuestoReparacion;

import java.util.ArrayList;
import java.util.List;

public class RepuestosReparacionFragment extends Fragment {

    private static final String ARG_ID = "id_reparacion";

    private int            idReparacion;
    private DatabaseHelper db;

    private Spinner    spinnerRepuesto;
    private EditText   etCantidad;
    private Button     btnAgregar;
    private TextView   tvTotal, tvVacio;
    private ListView   listView;

    private List<Repuesto>          catalogoRepuestos = new ArrayList<>();
    private List<RepuestoReparacion> listaDetalle      = new ArrayList<>();

    public static RepuestosReparacionFragment newInstance(int idReparacion) {
        RepuestosReparacionFragment f = new RepuestosReparacionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, idReparacion);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idReparacion = getArguments() != null ? getArguments().getInt(ARG_ID) : 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_repuestos_reparacion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db             = DatabaseHelper.getInstance(requireContext());
        tvTotal        = view.findViewById(R.id.tvTotalReparacion);
        spinnerRepuesto = view.findViewById(R.id.spinnerRepuesto);
        etCantidad     = view.findViewById(R.id.etCantidad);
        btnAgregar     = view.findViewById(R.id.btnAgregarRepuesto);
        tvVacio        = view.findViewById(R.id.tvVacioRepuestos);
        listView       = view.findViewById(R.id.listViewRepuestosReparacion);

        cargarCatalogo();
        cargarDetalle();

        btnAgregar.setOnClickListener(v -> agregarRepuesto());
        listView.setOnItemClickListener((parent, itemView, position, id) ->
                confirmarEliminarDetalle(listaDetalle.get(position)));
    }

    private void cargarCatalogo() {
        catalogoRepuestos = db.getRepuestos();
        List<String> nombres = new ArrayList<>();
        for (Repuesto r : catalogoRepuestos) {
            nombres.add(r.getNombre() + " ($" + String.format("%.2f", r.getPrecio()) + ") — stock: " + r.getStock());
        }
        ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, nombres);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepuesto.setAdapter(ad);
    }

    private void cargarDetalle() {
        listaDetalle = db.getRepuestosByReparacion(idReparacion);

        List<String> items = new ArrayList<>();
        for (RepuestoReparacion rr : listaDetalle) {
            items.add(rr.getNombreRepuesto() + " x" + rr.getCantidad() +
                    "  →  $" + String.format("%.2f", rr.getSubtotal()) +
                    "  (toca para eliminar)");
        }
        ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, items);
        listView.setAdapter(ad);
        tvVacio.setVisibility(listaDetalle.isEmpty() ? View.VISIBLE : View.GONE);

        double total = db.getTotalReparacion(idReparacion);
        tvTotal.setText(String.format("$%.2f", total));
    }

    private void agregarRepuesto() {
        if (catalogoRepuestos.isEmpty()) {
            Toast.makeText(requireContext(), "No hay repuestos en inventario", Toast.LENGTH_SHORT).show();
            return;
        }
        int pos = spinnerRepuesto.getSelectedItemPosition();
        String cantStr = etCantidad.getText().toString().trim();
        if (cantStr.isEmpty()) { Toast.makeText(requireContext(), "Ingrese cantidad", Toast.LENGTH_SHORT).show(); return; }

        int cant;
        try { cant = Integer.parseInt(cantStr); } catch (NumberFormatException e) { return; }
        if (cant <= 0) { Toast.makeText(requireContext(), "Cantidad inválida", Toast.LENGTH_SHORT).show(); return; }

        Repuesto r = catalogoRepuestos.get(pos);
        if (r.getStock() < cant) {
            Toast.makeText(requireContext(), "Stock insuficiente (disponible: " + r.getStock() + ")", Toast.LENGTH_LONG).show();
            return;
        }

        RepuestoReparacion rr = new RepuestoReparacion();
        rr.setIdReparacion(idReparacion);
        rr.setIdRepuesto(r.getIdRepuesto());
        rr.setCantidad(cant);
        rr.setSubtotal(r.getPrecio() * cant);

        long id = db.insertarRepuestoReparacion(rr);
        if (id > 0) {
            Toast.makeText(requireContext(), "Repuesto agregado", Toast.LENGTH_SHORT).show();
            etCantidad.setText("");
            cargarCatalogo();
            cargarDetalle();
        } else {
            Toast.makeText(requireContext(), "Error al agregar", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarEliminarDetalle(RepuestoReparacion rr) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar repuesto")
                .setMessage("¿Quitar " + rr.getNombreRepuesto() + " de esta reparación?")
                .setPositiveButton("Quitar", (d, w) -> {
                    db.eliminarRepuestoReparacion(rr.getIdDetalle());
                    cargarDetalle();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
