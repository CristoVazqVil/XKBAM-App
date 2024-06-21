package com.example.xkbam;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkbam.main.MainActivity;
import com.example.xkbam.utilidades.SesionSingleton;

public class MenuCuentaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_cuenta);

        TextView addressTitle = findViewById(R.id.address_title);
        TextView accountTitle = findViewById(R.id.account_title);
        TextView infoTitle = findViewById(R.id.info_title);
        TextView logOutTitle = findViewById(R.id.log_out_title);
        TextView addArticleTitle = findViewById(R.id.add_article_title);
        TextView salesReportTitle = findViewById(R.id.sales_report_title);

        int idRol = SesionSingleton.getInstance().getRol();

        if (idRol == 1) {
            addArticleTitle.setVisibility(View.VISIBLE);
            salesReportTitle.setVisibility(View.VISIBLE);
            addressTitle.setVisibility(View.GONE);
            accountTitle.setVisibility(View.GONE);
            infoTitle.setVisibility(View.GONE);
        } else {
            addArticleTitle.setVisibility(View.GONE);
            salesReportTitle.setVisibility(View.GONE);
        }

        addressTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuCuentaActivity.this, DetallesDireccionesActivity.class);
                startActivity(intent);
            }
        });

        accountTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuCuentaActivity.this, DetallesBancariosActivity.class);
                startActivity(intent);
            }
        });

        infoTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuCuentaActivity.this, DetallesUsuarioActivity.class);
                intent.putExtra("NUMBER", 2);
                startActivity(intent);
            }
        });

        logOutTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

        addArticleTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuCuentaActivity.this, "Añadir Articulo fue presionado", Toast.LENGTH_SHORT).show();
            }
        });

        salesReportTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuCuentaActivity.this, "Reporte de Ventas fue presionado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cerrarSesion() {

        Intent intent = new Intent(MenuCuentaActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        MenuCuentaActivity.this.finish();
    }
}
