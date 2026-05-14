package com.taller.mecanico.ui.reparaciones;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.taller.mecanico.R;

public class ReparacionesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // En Fase 6 se añade: RecyclerView + FAB + Spinner filtro de estado
        return inflater.inflate(R.layout.fragment_reparaciones, container, false);
    }
}