package com.taller.mecanico.ui.reparaciones;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.taller.mecanico.DTOmodel.ReparacionDTO;
import com.taller.mecanico.R;

import java.util.List;

public class ReparacionAdapter extends ArrayAdapter<ReparacionDTO> {

    private final Context            contexto;
    private final List<ReparacionDTO> lista;

    public ReparacionAdapter(Context context, List<ReparacionDTO> lista) {
        super(context, R.layout.item_reparacion, lista);
        this.contexto = context;
        this.lista    = lista;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(contexto)
                    .inflate(R.layout.item_reparacion, parent, false);
        }

        ReparacionDTO r = lista.get(position);

        TextView tvId       = convertView.findViewById(R.id.tvIdReparacion);
        TextView tvEstado   = convertView.findViewById(R.id.tvEstadoReparacion);
        TextView tvCliente  = convertView.findViewById(R.id.tvClienteReparacion);
        TextView tvVehiculo = convertView.findViewById(R.id.tvVehiculoReparacion);
        TextView tvTecnico  = convertView.findViewById(R.id.tvTecnicoReparacion);
        TextView tvFecha    = convertView.findViewById(R.id.tvFechaReparacion);

        tvId.setText("#" + r.getIdReparacion());
        tvEstado.setText(r.getEstadoLabel());
        tvEstado.setBackgroundColor(colorEstado(r.getEstado()));
        tvCliente.setText(r.getNombreCliente() != null ? r.getNombreCliente() : "—");

        String placa = r.getPlacaVehiculo();
        String info  = r.getInfoVehiculo();
        tvVehiculo.setText((placa != null ? placa : "Sin vehículo") +
                (info != null && !info.trim().equals("null null") ? " — " + info : ""));

        tvTecnico.setText("Técnico: " + (r.getNombreTecnico() != null ? r.getNombreTecnico() : "—"));
        tvFecha.setText("Ingreso: " + (r.getFechaIngreso() != null ? r.getFechaIngreso() : "—"));

        return convertView;
    }

    private int colorEstado(String estado) {
        if (estado == null) return Color.parseColor("#9E9E9E");
        switch (estado) {
            case "EN_DIAGNOSTICO":      return Color.parseColor("#1565C0");
            case "EN_REPARACION":       return Color.parseColor("#E65100");
            case "ESPERANDO_REPUESTOS": return Color.parseColor("#6A1B9A");
            case "LISTO_ENTREGA":       return Color.parseColor("#2E7D32");
            case "ENTREGADO":           return Color.parseColor("#757575");
            default:                    return Color.parseColor("#1a3a6e");
        }
    }
}
