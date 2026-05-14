package com.taller.mecanico.ui.clientes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.taller.mecanico.R;
import com.taller.mecanico.model.Cliente;

import java.util.List;

public class ClienteAdapter extends ArrayAdapter<Cliente> {

    private final Context       contexto;
    private final List<Cliente> lista;

    public ClienteAdapter(Context context, List<Cliente> lista) {
        super(context, R.layout.item_cliente, lista);
        this.contexto = context;
        this.lista    = lista;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(contexto)
                    .inflate(R.layout.item_cliente, parent, false);
        }

        Cliente cliente = lista.get(position);

        TextView tvNombre   = convertView.findViewById(R.id.tvNombreCompleto);
        TextView tvCedula   = convertView.findViewById(R.id.tvCedula);
        TextView tvTelefono = convertView.findViewById(R.id.tvTelefono);
        TextView tvCorreo   = convertView.findViewById(R.id.tvCorreo);

        tvNombre.setText(cliente.getNombreCompleto());
        tvCedula.setText("CI: " + cliente.getCedula());
        tvTelefono.setText("Tel: " + cliente.getTelefono());

        String correo = cliente.getCorreo();
        if (correo != null && !correo.isEmpty()) {
            tvCorreo.setVisibility(View.VISIBLE);
            tvCorreo.setText("✉ " + correo);
        } else {
            tvCorreo.setVisibility(View.GONE);
        }

        return convertView;
    }
}