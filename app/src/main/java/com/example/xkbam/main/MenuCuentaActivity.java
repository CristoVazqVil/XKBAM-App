package com.example.xkbam;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MenuCuentaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_cuenta);

        TextView addressTitle = findViewById(R.id.address_title);
        TextView accountTitle = findViewById(R.id.account_title);

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

    }
}
