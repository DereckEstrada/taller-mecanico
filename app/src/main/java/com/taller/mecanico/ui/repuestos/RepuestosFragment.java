package com.taller.mecanico.ui.repuestos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.taller.mecanico.R;

public class RepuestosFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // En Fase 5 se añade: RecyclerView + SearchView + FAB + indicador de stock
        return inflater.inflate(R.layout.fragment_repuestos, container, false);
    }
}