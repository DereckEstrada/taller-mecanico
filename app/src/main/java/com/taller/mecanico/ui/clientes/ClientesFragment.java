package com.taller.mecanico.ui.clientes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.taller.mecanico.R;

public class ClientesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // En Fase 3 se añade: RecyclerView + SearchView + FAB
        return inflater.inflate(R.layout.fragment_clientes, container, false);
    }
}