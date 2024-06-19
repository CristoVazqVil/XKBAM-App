package com.example.xkbam.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.xkbam.R;
import com.example.xkbam.main.MenuPrincipal;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar botón de login
        loginButton = findViewById(R.id.loginButton);

        // Configurar onClickListener para el botón de login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSecondActivity(); // Método para abrir la segunda actividad
            }
        });
    }

    // Método para abrir la segunda actividad
    private void openSecondActivity() {
        Intent intent = new Intent(this, MenuPrincipal .class); // Ajusta el nombre de la actividad destino
        startActivity(intent);
    }
}
