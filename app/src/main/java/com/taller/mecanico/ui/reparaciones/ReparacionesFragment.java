package com.taller.mecanico.ui.reparaciones;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.taller.mecanico.R;
import com.taller.mecanico.DTOmodel.ReparacionDTO;
import com.taller.mecanico.database.DatabaseHelper;
import com.taller.mecanico.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ReparacionesFragment extends Fragment {

    private ListView           listView;
    private Spinner            spinnerFiltro;
    private Button             btnNueva;
    private TextView           tvVacio;

    private ReparacionAdapter  adapter;
    private DatabaseHelper     db;
    private SessionManager     session;
    private List<ReparacionDTO> listaReparaciones = new ArrayList<>();

    private String filtroEstadoActual = "TODOS";

    private static final String[] ESTADOS_FILTRO = {
            "TODOS", "EN_DIAGNOSTICO", "EN_REPARACION",
            "ESPERANDO_REPUESTOS", "LISTO_ENTREGA", "ENTREGADO"
    };
    private static final String[] ETIQUETAS_FILTRO = {
            "Todos", "En Diagnóstico", "En Reparación",
            "Esperando Repuestos", "Listo para Entrega", "Entregado"
    };

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> cargarReparaciones());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reparaciones, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db       = DatabaseHelper.getInstance(requireContext());
        session  = SessionManager.getInstance(requireContext());
        listView     = view.findViewById(R.id.listViewReparaciones);
        spinnerFiltro = view.findViewById(R.id.spinnerFiltroEstado);
        btnNueva      = view.findViewById(R.id.btnNuevaReparacion);
        tvVacio       = view.findViewById(R.id.tvVacio);

        // Spinner de filtro
        ArrayAdapter<String> filtroAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, ETIQUETAS_FILTRO);
        filtroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltro.setAdapter(filtroAdapter);
        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                filtroEstadoActual = ESTADOS_FILTRO[position];
                cargarReparaciones();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Solo el admin puede crear reparaciones
        btnNueva.setVisibility(session.esAdmin() ? View.VISIBLE : View.GONE);
        btnNueva.setOnClickListener(v ->
                launcher.launch(new Intent(requireContext(), FormReparacionActivity.class)));

        listView.setOnItemClickListener((parent, itemView, position, id) ->
                mostrarOpciones(listaReparaciones.get(position)));

        cargarReparaciones();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarReparaciones();
    }

    private void cargarReparaciones() {
        List<ReparacionDTO> todas = session.esMecanico()
                ? db.getReparacionesByTecnico(session.getIdReferencia())
                : db.getReparaciones();

        if ("TODOS".equals(filtroEstadoActual)) {
            listaReparaciones = todas;
        } else {
            listaReparaciones = new ArrayList<>();
            for (ReparacionDTO r : todas) {
                if (filtroEstadoActual.equals(r.getEstado())) listaReparaciones.add(r);
            }
        }

        adapter = new ReparacionAdapter(requireContext(), listaReparaciones);
        listView.setAdapter(adapter);
        tvVacio.setVisibility(listaReparaciones.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void mostrarOpciones(ReparacionDTO rpa) {
        List<String> opciones = new ArrayList<>();
        opciones.add("Ver detalle / Repuestos / Novedades");
        if (session.esAdmin()) {
            opciones.add("Editar");
            opciones.add("Eliminar");
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Reparación #" + rpa.getIdReparacion())
                .setItems(opciones.toArray(new String[0]), (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(requireContext(), DetalleReparacionActivity.class);
                        intent.putExtra(DetalleReparacionActivity.EXTRA_ID_REPARACION, rpa.getIdReparacion());
                        launcher.launch(intent);
                    } else if (which == 1) {
                        Intent intent = new Intent(requireContext(), FormReparacionActivity.class);
                        intent.putExtra(FormReparacionActivity.EXTRA_ID_REPARACION, rpa.getIdReparacion());
                        launcher.launch(intent);
                    } else if (which == 2) {
                        confirmarEliminar(rpa);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminar(ReparacionDTO rpa) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar reparación")
                .setMessage("Solo se puede eliminar si está en diagnóstico. ¿Continuar?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    boolean ok = db.eliminarReparacion(rpa.getIdReparacion());
                    if (ok) {
                        android.widget.Toast.makeText(requireContext(), "Reparación eliminada", android.widget.Toast.LENGTH_SHORT).show();
                        cargarReparaciones();
                    } else {
                        android.widget.Toast.makeText(requireContext(), "No se puede eliminar en este estado", android.widget.Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
