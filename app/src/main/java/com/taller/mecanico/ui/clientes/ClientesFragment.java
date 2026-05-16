package com.taller.mecanico.ui.clientes;

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
import com.taller.mecanico.model.Cliente;
import com.taller.mecanico.ui.vehiculos.VehiculoActivity;

import java.util.ArrayList;
import java.util.List;

public class ClientesFragment extends Fragment {

    private ListView       listView;
    private EditText       etBuscar;
    private Button         btnBuscar;
    private Button         btnNuevoCliente;
    private TextView       tvVacio;

    private ClienteAdapter adapter;
    private DatabaseHelper db;
    private List<Cliente>  listaClientes = new ArrayList<>();

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                            cargarClientes();
                        }
                    }
            );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clientes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db              = DatabaseHelper.getInstance(requireContext());
        listView        = view.findViewById(R.id.listViewClientes);
        etBuscar        = view.findViewById(R.id.etBuscar);
        btnBuscar       = view.findViewById(R.id.btnBuscar);
        btnNuevoCliente = view.findViewById(R.id.btnNuevoCliente);
        tvVacio         = view.findViewById(R.id.tvVacio);

        cargarClientes();

        btnBuscar.setOnClickListener(v -> {
            String texto = etBuscar.getText().toString().trim();
            if (texto.isEmpty()) {
                cargarClientes();
            } else {
                listaClientes = db.buscarClientes(texto);
                adapter.clear();
                adapter.addAll(listaClientes);
                adapter.notifyDataSetChanged();
                tvVacio.setVisibility(listaClientes.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        btnNuevoCliente.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), FormClienteActivity.class);
            launcher.launch(intent);
        });

        listView.setOnItemClickListener((parent, itemView, position, id) -> {
            Cliente cliente = listaClientes.get(position);
            mostrarOpcionesCliente(cliente);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarClientes();
    }

    private void cargarClientes() {
        listaClientes = db.getClientes();
        adapter = new ClienteAdapter(requireContext(), listaClientes);
        listView.setAdapter(adapter);
        tvVacio.setVisibility(listaClientes.isEmpty() ? View.VISIBLE : View.GONE);
        etBuscar.setText("");
    }

    private void mostrarOpcionesCliente(Cliente cliente) {
        String[] opciones = {"Editar", "Ver Vehículos", "Eliminar"};

        new AlertDialog.Builder(requireContext())
                .setTitle(cliente.getNombreCompleto())
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Intent intent = new Intent(requireContext(), FormClienteActivity.class);
                            intent.putExtra(FormClienteActivity.EXTRA_ID_CLIENTE, cliente.getIdCliente());
                            launcher.launch(intent);
                            break;
                        case 1:
                            Intent intentVeh = new Intent(requireContext(), VehiculoActivity.class);
                            intentVeh.putExtra(VehiculoActivity.EXTRA_ID_CLIENTE,     cliente.getIdCliente());
                            intentVeh.putExtra(VehiculoActivity.EXTRA_NOMBRE_CLIENTE, cliente.getNombreCompleto());
                            startActivity(intentVeh);
                            break;
                        case 2:
                            confirmarEliminar(cliente);
                            break;
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminar(Cliente cliente) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar cliente")
                .setMessage("¿Eliminar a " + cliente.getNombreCompleto() + "?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    db.eliminarCliente(cliente.getIdCliente());
                    Toast.makeText(requireContext(), "Cliente eliminado", Toast.LENGTH_SHORT).show();
                    cargarClientes();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}