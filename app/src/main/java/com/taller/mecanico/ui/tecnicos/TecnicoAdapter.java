package com.taller.mecanico.ui.tecnicos;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.taller.mecanico.R;
import com.taller.mecanico.model.Tecnico;

import java.util.List;

public class TecnicoAdapter extends ArrayAdapter<Tecnico> {

    private final Context      contexto;
    private final List<Tecnico> lista;

    public TecnicoAdapter(Context context, List<Tecnico> lista) {
        super(context, R.layout.item_tecnico, lista);
        this.contexto = context;
        this.lista    = lista;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(contexto)
                    .inflate(R.layout.item_tecnico, parent, false);
        }

        Tecnico tecnico = lista.get(position);

        TextView tvNombre       = convertView.findViewById(R.id.tvNombreCompleto);
        TextView tvCedula       = convertView.findViewById(R.id.tvCedula);
        TextView tvTelefono     = convertView.findViewById(R.id.tvTelefono);
        TextView tvEspecialidad = convertView.findViewById(R.id.tvEspecialidad);
        TextView tvEstado       = convertView.findViewById(R.id.tvEstado);

        tvNombre.setText(tecnico.getNombreCompleto());
        tvCedula.setText("CI: " + tecnico.getCedula());
        tvTelefono.setText("Tel: " + tecnico.getTelefono());

        String esp = tecnico.getEspecialidad();
        if (esp != null && !esp.isEmpty()) {
            tvEspecialidad.setVisibility(View.VISIBLE);
            tvEspecialidad.setText("Especialidad: " + esp);
        } else {
            tvEspecialidad.setVisibility(View.GONE);
        }

        // Badge de estado
        if (tecnico.isActivo()) {
            tvEstado.setText("Activo");
            tvEstado.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else {
            tvEstado.setText("Inactivo");
            tvEstado.setBackgroundColor(Color.parseColor("#F44336"));
        }

        return convertView;
    }
}