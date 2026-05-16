package com.taller.mecanico.ui.tecnicos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.taller.mecanico.R;
import com.taller.mecanico.database.DatabaseHelper;
import com.taller.mecanico.model.Tecnico;

import java.util.ArrayList;
import java.util.List;

public class TecnicoFragment extends Fragment {

    private ListView       listView;
    private EditText       etBuscar;
    private Button         btnBuscar;
    private Button         btnNuevoTecnico;
    private TextView       tvVacio;

    private TecnicoAdapter adapter;
    private DatabaseHelper db;
    private List<Tecnico>  listaTecnicos = new ArrayList<>();

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                            cargarTecnicos();
                        }
                    }
            );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tecnico, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db              = DatabaseHelper.getInstance(requireContext());
        listView        = view.findViewById(R.id.listViewTecnicos);
        etBuscar        = view.findViewById(R.id.etBuscar);
        btnBuscar       = view.findViewById(R.id.btnBuscar);
        btnNuevoTecnico = view.findViewById(R.id.btnNuevoTecnico);
        tvVacio         = view.findViewById(R.id.tvVacio);

        cargarTecnicos();

        btnBuscar.setOnClickListener(v -> {
            String texto = etBuscar.getText().toString().trim();
            if (texto.isEmpty()) {
                cargarTecnicos();
            } else {
                // Filtro en memoria por nombre o cédula
                List<Tecnico> filtrada = new ArrayList<>();
                for (Tecnico t : db.getTecnicos()) {
                    if (t.getNombreCompleto().toLowerCase().contains(texto.toLowerCase())
                            || t.getCedula().contains(texto)) {
                        filtrada.add(t);
                    }
                }
                listaTecnicos = filtrada;
                adapter = new TecnicoAdapter(requireContext(), listaTecnicos);
                listView.setAdapter(adapter);
                tvVacio.setVisibility(listaTecnicos.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        btnNuevoTecnico.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), FormTecnicoActivity.class);
            launcher.launch(intent);
        });

        listView.setOnItemClickListener((parent, itemView, position, id) -> {
            Tecnico tecnico = listaTecnicos.get(position);
            mostrarOpciones(tecnico);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarTecnicos();
    }

    private void cargarTecnicos() {
        listaTecnicos = db.getTecnicos();
        adapter = new TecnicoAdapter(requireContext(), listaTecnicos);
        listView.setAdapter(adapter);
        tvVacio.setVisibility(listaTecnicos.isEmpty() ? View.VISIBLE : View.GONE);
        etBuscar.setText("");
    }

    private void mostrarOpciones(Tecnico tecnico) {
        String estadoToggle = tecnico.isActivo() ? "Dar de baja" : "Activar";
        String[] opciones = {"Editar", estadoToggle, "Eliminar"};

        new AlertDialog.Builder(requireContext())
                .setTitle(tecnico.getNombreCompleto())
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0: // Editar
                            Intent intent = new Intent(requireContext(), FormTecnicoActivity.class);
                            intent.putExtra(FormTecnicoActivity.EXTRA_ID_TECNICO, tecnico.getIdTecnico());
                            launcher.launch(intent);
                            break;
                        case 1: // Activar / Dar de baja (borrado lógico)
                            confirmarCambioEstado(tecnico);
                            break;
                        case 2: // Eliminar
                            confirmarEliminar(tecnico);
                            break;
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarCambioEstado(Tecnico tecnico) {
        String accion = tecnico.isActivo() ? "dar de baja" : "activar";
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmar")
                .setMessage("¿Desea " + accion + " a " + tecnico.getNombreCompleto() + "?")
                .setPositiveButton("Sí", (d, w) -> {
                    // Reutilizamos eliminarTecnico que hace borrado lógico (activo = 0)
                    // Si está inactivo necesitamos reactivarlo manualmente
                    if (tecnico.isActivo()) {
                        db.eliminarTecnico(tecnico.getIdTecnico());
                        Toast.makeText(requireContext(), "Técnico dado de baja", Toast.LENGTH_SHORT).show();
                    } else {
                        tecnico.setActivo(true);
                        db.actualizarTecnico(tecnico);
                        Toast.makeText(requireContext(), "Técnico activado", Toast.LENGTH_SHORT).show();
                    }
                    cargarTecnicos();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminar(Tecnico tecnico) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar técnico")
                .setMessage("¿Eliminar permanentemente a " + tecnico.getNombreCompleto() + "?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    db.eliminarTecnico(tecnico.getIdTecnico());
                    Toast.makeText(requireContext(), "Técnico eliminado", Toast.LENGTH_SHORT).show();
                    cargarTecnicos();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}