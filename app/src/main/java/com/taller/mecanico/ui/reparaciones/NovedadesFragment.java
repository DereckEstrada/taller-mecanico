package com.taller.mecanico.ui.reparaciones;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.taller.mecanico.R;
import com.taller.mecanico.database.DatabaseHelper;
import com.taller.mecanico.model.Novedad;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NovedadesFragment extends Fragment {

    private static final String ARG_ID = "id_reparacion";

    private int            idReparacion;
    private DatabaseHelper db;

    private EditText     etDescripcion;
    private Button       btnAgregar;
    private TextView     tvVacio;
    private ListView     listView;

    private List<Novedad> listaNovedades = new ArrayList<>();

    public static NovedadesFragment newInstance(int idReparacion) {
        NovedadesFragment f = new NovedadesFragment();
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
        return inflater.inflate(R.layout.fragment_novedades, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db            = DatabaseHelper.getInstance(requireContext());
        etDescripcion = view.findViewById(R.id.etDescripcionNovedad);
        btnAgregar    = view.findViewById(R.id.btnAgregarNovedad);
        tvVacio       = view.findViewById(R.id.tvVacioNovedades);
        listView      = view.findViewById(R.id.listViewNovedades);

        cargarNovedades();

        btnAgregar.setOnClickListener(v -> agregarNovedad());
        listView.setOnItemClickListener((parent, itemView, position, id) ->
                confirmarEliminar(listaNovedades.get(position)));
    }

    private void cargarNovedades() {
        listaNovedades = db.getNovedadesByReparacion(idReparacion);

        List<String> items = new ArrayList<>();
        for (Novedad n : listaNovedades) {
            items.add("[" + n.getFechaNovedad() + "]\n" + n.getDescripcion() + "\n(Toca para eliminar)");
        }
        ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, items);
        listView.setAdapter(ad);
        tvVacio.setVisibility(listaNovedades.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void agregarNovedad() {
        String desc = etDescripcion.getText().toString().trim();
        if (desc.isEmpty()) {
            Toast.makeText(requireContext(), "Escriba una novedad", Toast.LENGTH_SHORT).show();
            return;
        }

        Novedad n = new Novedad();
        n.setIdReparacion(idReparacion);
        n.setDescripcion(desc);
        n.setFechaNovedad(obtenerFechaHoy());

        long id = db.insertarNovedad(n);
        if (id > 0) {
            etDescripcion.setText("");
            cargarNovedades();
        } else {
            Toast.makeText(requireContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarEliminar(Novedad novedad) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar novedad")
                .setMessage("¿Eliminar esta novedad?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    db.eliminarNovedad(novedad.getIdNovedad());
                    cargarNovedades();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private String obtenerFechaHoy() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
    }
}
