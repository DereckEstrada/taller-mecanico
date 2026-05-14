package com.taller.mecanico.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.taller.mecanico.R;
import com.taller.mecanico.database.DatabaseHelper;
import com.taller.mecanico.utils.SessionManager;

public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseHelper db      = DatabaseHelper.getInstance(requireContext());
        SessionManager session = SessionManager.getInstance(requireContext());

        // Saludo personalizado
        TextView tvSaludo = view.findViewById(R.id.tvSaludo);
        tvSaludo.setText("Hola, " + session.getUsername() + " 👋");

        // Tarjetas de resumen — se completan en Fase 7
        ((TextView) view.findViewById(R.id.tvCountReparaciones))
                .setText(String.valueOf(
                        db.contarReparacionesPorEstado(DatabaseHelper.ESTADO_REPARACION)
                                + db.contarReparacionesPorEstado(DatabaseHelper.ESTADO_DIAGNOSTICO)
                                + db.contarReparacionesPorEstado(DatabaseHelper.ESTADO_ESPERA)));

        ((TextView) view.findViewById(R.id.tvCountClientes))
                .setText(String.valueOf(db.contarClientes()));

        ((TextView) view.findViewById(R.id.tvCountTecnicos))
                .setText(String.valueOf(db.contarTecnicosActivos()));

        ((TextView) view.findViewById(R.id.tvCountStockBajo))
                .setText(String.valueOf(db.contarRepuestosConStockBajo(5)));
    }
}
