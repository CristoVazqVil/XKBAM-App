package com.example.xkbam.main;

import android.content.Intent; // Add this import statement
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.xkbam.R;
import com.example.xkbam.MenuCuentaActivity; // Import MenuCuentaActivity

public class MenuPrincipal extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        drawerLayout = findViewById(R.id.drawerLayout);

        ImageView imgOpciones = findViewById(R.id.imgOpciones);
        imgOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(findViewById(R.id.navMenu));
            }
        });

        ImageView imgCerrarOpciones = findViewById(R.id.imgCerrarOpciones);
        imgCerrarOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(findViewById(R.id.navMenu));
            }
        });

        TextView navItemTodo = findViewById(R.id.navItemTodo);
        TextView navItemSuperiores = findViewById(R.id.navItemSuperiores);
        TextView navItemInferiores = findViewById(R.id.navItemInferiores);
        TextView navItemAccesorios = findViewById(R.id.navItemAccesorios);
        TextView navItemConjuntos = findViewById(R.id.navItemConjuntos);
        TextView navItemCuenta = findViewById(R.id.navItemCuenta);

        navItemTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción para la opción "Todo"
            }
        });

        navItemSuperiores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción para la opción "Superiores"
            }
        });

        navItemInferiores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción para la opción "Inferiores"
            }
        });

        navItemAccesorios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción para la opción "Accesorios"
            }
        });

        navItemConjuntos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción para la opción "Conjuntos"
            }
        });

        navItemCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this, MenuCuentaActivity.class);
                startActivity(intent);
            }
        });
    }
}
