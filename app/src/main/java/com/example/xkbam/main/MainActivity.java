package com.example.xkbam.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.xkbam.R;
import com.example.xkbam.main.DetallesUsuarioActivity; // Ensure this import is correct
import com.example.xkbam.main.MenuPrincipal;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private TextView newClientLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize login button
        loginButton = findViewById(R.id.loginButton);

        // Set onClickListener for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSecondActivity(); // Method to open the second activity
            }
        });

        // Initialize new client label
        newClientLabel = findViewById(R.id.newClientLabel);

        // Set onClickListener for new client label
        newClientLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewClientActivity(); // Method to open the new client activity
            }
        });
    }

    // Method to open the second activity
    private void openSecondActivity() {
        Intent intent = new Intent(this, MenuPrincipal.class); // Adjust the target activity name
        startActivity(intent);
    }

    // Method to open the new client activity
    private void openNewClientActivity() {
        Intent intent = new Intent(this, DetallesUsuarioActivity.class); // Adjust the target activity name
        startActivity(intent);
    }
}
