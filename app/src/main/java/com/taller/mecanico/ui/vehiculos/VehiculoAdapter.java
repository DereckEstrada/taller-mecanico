package com.taller.mecanico.ui.vehiculos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.taller.mecanico.R;
import com.taller.mecanico.model.Vehiculo;

import java.util.List;

public class VehiculoAdapter extends ArrayAdapter<Vehiculo> {

    private final Context        contexto;
    private final List<Vehiculo> lista;

    public VehiculoAdapter(Context context, List<Vehiculo> lista) {
        super(context, R.layout.item_vehiculo, lista);
        this.contexto = context;
        this.lista    = lista;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(contexto)
                    .inflate(R.layout.item_vehiculo, parent, false);
        }

        Vehiculo v = lista.get(position);

        TextView tvPlaca       = convertView.findViewById(R.id.tvPlaca);
        TextView tvDescripcion = convertView.findViewById(R.id.tvDescripcion);
        TextView tvAnio        = convertView.findViewById(R.id.tvAnio);

        tvPlaca.setText(v.getPlaca());
        tvDescripcion.setText(v.getMarca() + " " + v.getModelo());
        tvAnio.setText("Año: " + v.getAnio());

        return convertView;
    }
}
