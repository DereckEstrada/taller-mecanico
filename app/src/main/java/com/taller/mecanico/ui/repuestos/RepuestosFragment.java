package com.taller.mecanico.ui.repuestos;

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
import com.taller.mecanico.model.Repuesto;

import java.util.ArrayList;
import java.util.List;

public class RepuestosFragment extends Fragment {

    private ListView        listView;
    private EditText        etBuscar;
    private Button          btnBuscar, btnNuevoRepuesto;
    private TextView        tvVacio;

    private RepuestoAdapter adapter;
    private DatabaseHelper  db;
    private List<Repuesto>  listaRepuestos = new ArrayList<>();

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                            cargarRepuestos();
                        }
                    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_repuestos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db              = DatabaseHelper.getInstance(requireContext());
        listView        = view.findViewById(R.id.listViewRepuestos);
        etBuscar        = view.findViewById(R.id.etBuscar);
        btnBuscar       = view.findViewById(R.id.btnBuscar);
        btnNuevoRepuesto = view.findViewById(R.id.btnNuevoRepuesto);
        tvVacio         = view.findViewById(R.id.tvVacio);

        cargarRepuestos();

        btnBuscar.setOnClickListener(v -> {
            String texto = etBuscar.getText().toString().trim();
            if (texto.isEmpty()) {
                cargarRepuestos();
            } else {
                listaRepuestos = db.buscarRepuestos(texto);
                adapter = new RepuestoAdapter(requireContext(), listaRepuestos);
                listView.setAdapter(adapter);
                tvVacio.setVisibility(listaRepuestos.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        btnNuevoRepuesto.setOnClickListener(v ->
                launcher.launch(new Intent(requireContext(), FormRepuestoActivity.class)));

        listView.setOnItemClickListener((parent, itemView, position, id) ->
                mostrarOpciones(listaRepuestos.get(position)));
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarRepuestos();
    }

    private void cargarRepuestos() {
        listaRepuestos = db.getRepuestos();
        adapter = new RepuestoAdapter(requireContext(), listaRepuestos);
        listView.setAdapter(adapter);
        tvVacio.setVisibility(listaRepuestos.isEmpty() ? View.VISIBLE : View.GONE);
        etBuscar.setText("");
    }

    private void mostrarOpciones(Repuesto repuesto) {
        new AlertDialog.Builder(requireContext())
                .setTitle(repuesto.getNombre())
                .setItems(new String[]{"Editar", "Eliminar"}, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(requireContext(), FormRepuestoActivity.class);
                        intent.putExtra(FormRepuestoActivity.EXTRA_ID_REPUESTO, repuesto.getIdRepuesto());
                        launcher.launch(intent);
                    } else {
                        confirmarEliminar(repuesto);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminar(Repuesto repuesto) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar repuesto")
                .setMessage("¿Eliminar \"" + repuesto.getNombre() + "\"?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    db.eliminarRepuesto(repuesto.getIdRepuesto());
                    Toast.makeText(requireContext(), "Repuesto eliminado", Toast.LENGTH_SHORT).show();
                    cargarRepuestos();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
