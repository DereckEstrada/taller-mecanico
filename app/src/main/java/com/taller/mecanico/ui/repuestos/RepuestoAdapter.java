package com.taller.mecanico.ui.repuestos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.taller.mecanico.R;
import com.taller.mecanico.model.Repuesto;

import java.util.List;

public class RepuestoAdapter extends ArrayAdapter<Repuesto> {

    private final Context        contexto;
    private final List<Repuesto> lista;

    public RepuestoAdapter(Context context, List<Repuesto> lista) {
        super(context, R.layout.item_repuesto, lista);
        this.contexto = context;
        this.lista    = lista;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(contexto)
                    .inflate(R.layout.item_repuesto, parent, false);
        }

        Repuesto r = lista.get(position);

        TextView tvNombre = convertView.findViewById(R.id.tvNombreRepuesto);
        TextView tvPrecio = convertView.findViewById(R.id.tvPrecio);
        TextView tvStock  = convertView.findViewById(R.id.tvStock);

        tvNombre.setText(r.getNombre());
        tvPrecio.setText(String.format("$%.2f", r.getPrecio()));

        int stock = r.getStock();
        tvStock.setText("Stock: " + stock);
        tvStock.setTextColor(stock <= 5
                ? contexto.getResources().getColor(android.R.color.holo_red_dark, null)
                : contexto.getResources().getColor(android.R.color.darker_gray, null));

        return convertView;
    }
}
