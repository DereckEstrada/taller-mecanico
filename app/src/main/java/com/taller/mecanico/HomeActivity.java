package com.taller.mecanico;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.taller.mecanico.databinding.ActivityHomeBinding;
import com.taller.mecanico.ui.clientes.ClientesFragment;
import com.taller.mecanico.ui.dashboard.DashboardFragment;
import com.taller.mecanico.ui.reparaciones.ReparacionesFragment;
import com.taller.mecanico.ui.repuestos.RepuestosFragment;
import com.taller.mecanico.ui.tecnicos.TecnicoFragment;
import com.taller.mecanico.utils.SessionManager;

/**
 * HomeActivity — Pantalla principal post-login
 *
 * Gestiona la navegación entre módulos mediante BottomNavigationView.
 * Muestra u oculta pestañas según el rol del usuario:
 *   ADMIN    → Dashboard | Reparaciones | Clientes | Técnicos | Repuestos
 *   MECANICO → Reparaciones (las suyas)
 */
public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private SessionManager      session;
    private FragmentManager     fm;

    // Instancias de fragmentos
    private DashboardFragment    fragDashboard;
    private ReparacionesFragment fragReparaciones;
    private ClientesFragment fragClientes;
    private TecnicoFragment     fragTecnicos;
    private RepuestosFragment    fragRepuestos;

    private Fragment fragmentActivo;

    // Tag del fragmento activo (para restaurar estado al rotar pantalla)
    private static final String KEY_FRAGMENT_ACTIVO = "fragmentActivo";
    private static final String TAG_DASHBOARD    = "DASHBOARD";
    private static final String TAG_REPARACIONES = "REPARACIONES";
    private static final String TAG_CLIENTES     = "CLIENTES";
    private static final String TAG_TECNICOS     = "TECNICOS";
    private static final String TAG_REPUESTOS    = "REPUESTOS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        session = SessionManager.getInstance(this);
        fm      = getSupportFragmentManager();

        configurarToolbarSegunRol();
        configurarBottomNavSegunRol();
        configurarListenerBottomNav();
        configurarBackPress();

        // ── Restaurar o cargar fragmento inicial ─────────────────────────────
        if (savedInstanceState != null) {
            String tagGuardado = savedInstanceState.getString(KEY_FRAGMENT_ACTIVO, TAG_DASHBOARD);
            restaurarFragmento(tagGuardado);
        } else {
            cargarFragmentoInicial();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Guardar qué fragmento estaba activo al rotar la pantalla
        if (fragmentActivo != null) {
            outState.putString(KEY_FRAGMENT_ACTIVO, fragmentActivo.getTag());
        }
    }
    /**
     * Muestra el nombre del usuario y su rol en el subtítulo del Toolbar
     */
    private void configurarToolbarSegunRol() {
        String username = session.getUsername();
        String rol      = session.getRol();

        String etiquetaRol;
        switch (rol) {
            case SessionManager.ROL_ADMIN:    etiquetaRol = "Administrador"; break;
            case SessionManager.ROL_MECANICO: etiquetaRol = "Mecánico";      break;
            default:                          etiquetaRol = rol;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(username + "  |  " + etiquetaRol);
        }
    }

    /**
     * Oculta en el BottomNav los ítems a los que el rol no tiene acceso.
     *
     * ADMIN    → ve todo (5 tabs)
     * MECANICO → ve solo Reparaciones (1 tab visible)
     */
    private void configurarBottomNavSegunRol() {
        Menu menu = binding.bottomNav.getMenu();

        if (session.esMecanico()) {
            // Mecánico solo ve Reparaciones
            menu.findItem(R.id.nav_dashboard).setVisible(false);
            menu.findItem(R.id.nav_clientes).setVisible(false);
            menu.findItem(R.id.nav_tecnicos).setVisible(false);
            menu.findItem(R.id.nav_repuestos).setVisible(false);
        }
        // ADMIN ve todo → no se oculta nada
    }

    /** Fragmento inicial según rol */
    private void cargarFragmentoInicial() {
        if (session.esMecanico()) {
            mostrarFragmento(obtenerFragmento(TAG_REPARACIONES), TAG_REPARACIONES);
            binding.bottomNav.setSelectedItemId(R.id.nav_reparaciones);
        } else {
            mostrarFragmento(obtenerFragmento(TAG_DASHBOARD), TAG_DASHBOARD);
            binding.bottomNav.setSelectedItemId(R.id.nav_dashboard);
        }
    }


    /**
     * Método que actua de listener del menu de navegación inferior
     */
    private void configurarListenerBottomNav() {
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                mostrarFragmento(obtenerFragmento(TAG_DASHBOARD), TAG_DASHBOARD);
                return true;
            } else if (id == R.id.nav_reparaciones) {
                mostrarFragmento(obtenerFragmento(TAG_REPARACIONES), TAG_REPARACIONES);
                return true;
            } else if (id == R.id.nav_clientes) {
                mostrarFragmento(obtenerFragmento(TAG_CLIENTES), TAG_CLIENTES);
                return true;
            } else if (id == R.id.nav_tecnicos) {
                mostrarFragmento(obtenerFragmento(TAG_TECNICOS), TAG_TECNICOS);
                return true;
            } else if (id == R.id.nav_repuestos) {
                mostrarFragmento(obtenerFragmento(TAG_REPUESTOS), TAG_REPUESTOS);
                return true;
            }
            return false;
        });

        // Toque en el ítem ya seleccionado → scroll al tope (comportamiento estándar)
        binding.bottomNav.setOnItemReselectedListener(item -> {
            // Cada fragment puede implementar una interfaz ScrollToTop si se necesita
        });
    }

    /**
     * Obtiene (o crea) la instancia del fragmento solicitado.
     * Patrón: instancia única por tab — no se recrea al navegar entre tabs.
     */
    private Fragment obtenerFragmento(String tag) {
        // Intentar recuperar del back stack primero (sobrevive a rotaciones)
        Fragment existente = fm.findFragmentByTag(tag);
        if (existente != null) return existente;

        // Crear instancia nueva
        switch (tag) {
            case TAG_DASHBOARD:    return new DashboardFragment();
            case TAG_REPARACIONES: return new ReparacionesFragment();
            case TAG_CLIENTES:     return new ClientesFragment();
            case TAG_TECNICOS:     return new TecnicoFragment();
            case TAG_REPUESTOS:    return new RepuestosFragment();
            default: throw new IllegalArgumentException("Tag de fragmento no reconocido: " + tag);
        }
    }

    /**
     * Muestra el fragmento indicado en el contenedor.
     * Usa show/hide en lugar de replace para conservar el estado de cada tab
     * (listas, posición de scroll, etc.) al navegar entre pestañas.
     */
    private void mostrarFragmento(Fragment nuevo, String tag) {
        if (fragmentActivo != null && fragmentActivo == nuevo) return; // ya visible

        androidx.fragment.app.FragmentTransaction tx = fm.beginTransaction();

        // Añadir si aún no está en el FragmentManager
        if (!nuevo.isAdded()) {
            tx.add(R.id.fragmentContainer, nuevo, tag);
        }

        // Ocultar el fragmento activo actual
        if (fragmentActivo != null) {
            tx.hide(fragmentActivo);
        }

        // Mostrar el nuevo
        tx.show(nuevo);
        tx.commit();

        fragmentActivo = nuevo;
        actualizarTituloToolbar(tag);
    }

    /** Restaura el fragmento activo tras una rotación de pantalla */
    private void restaurarFragmento(String tag) {
        Fragment frag = obtenerFragmento(tag);
        mostrarFragmento(frag, tag);

        // Sincronizar el ítem seleccionado del BottomNav
        switch (tag) {
            case TAG_DASHBOARD:    binding.bottomNav.setSelectedItemId(R.id.nav_dashboard);    break;
            case TAG_REPARACIONES: binding.bottomNav.setSelectedItemId(R.id.nav_reparaciones); break;
            case TAG_CLIENTES:     binding.bottomNav.setSelectedItemId(R.id.nav_clientes);     break;
            case TAG_TECNICOS:     binding.bottomNav.setSelectedItemId(R.id.nav_tecnicos);     break;
            case TAG_REPUESTOS:    binding.bottomNav.setSelectedItemId(R.id.nav_repuestos);    break;
        }
    }

    /** Actualiza el título del Toolbar según la pestaña activa */
    private void actualizarTituloToolbar(String tag) {
        if (getSupportActionBar() == null) return;
        switch (tag) {
            case TAG_DASHBOARD:    getSupportActionBar().setTitle("Inicio");        break;
            case TAG_REPARACIONES: getSupportActionBar().setTitle("Reparaciones");  break;
            case TAG_CLIENTES:     getSupportActionBar().setTitle("Clientes");      break;
            case TAG_TECNICOS:     getSupportActionBar().setTitle("Técnicos");      break;
            case TAG_REPUESTOS:    getSupportActionBar().setTitle("Repuestos");     break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_inicio) {
            mostrarFragmento(obtenerFragmento(TAG_DASHBOARD), TAG_DASHBOARD);
            return true;
        } else if (id == R.id.action_acerca_de) {
            mostrarDialogoAcercaDe();
            return true;
        } else if (id == R.id.action_cerrar_sesion) {
            confirmarCerrarSesion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Deseas cerrar la sesión actual?")
                .setPositiveButton("Cerrar sesión", (dialog, which) -> cerrarSesion())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void cerrarSesion() {
        session.cerrarSesion();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * Si el usuario está en el tab inicial → preguntar si desea salir de la app.
     * Si está en otro tab → volver al tab inicial (Dashboard o Reparaciones).
     */
    private void configurarBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                int itemSeleccionado = binding.bottomNav.getSelectedItemId();
                int tabInicial = session.esMecanico()
                        ? R.id.nav_reparaciones
                        : R.id.nav_dashboard;

                if (itemSeleccionado == tabInicial) {
                    // Ya está en el tab inicial → preguntar si salir
                    new AlertDialog.Builder(HomeActivity.this)
                            .setTitle("Salir")
                            .setMessage("¿Deseas salir de la aplicación?")
                            .setPositiveButton("Salir", (d, w) -> finish())
                            .setNegativeButton("Cancelar", null)
                            .show();
                } else {
                    // Volver al tab inicial
                    binding.bottomNav.setSelectedItemId(tabInicial);
                }
            }
        });
    }

    private void mostrarDialogoAcercaDe() {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_acerca_de);
        dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.92),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.findViewById(R.id.btnCerrarAcercaDe).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}