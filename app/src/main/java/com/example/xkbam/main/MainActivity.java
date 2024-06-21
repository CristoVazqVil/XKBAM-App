package com.example.xkbam.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.xkbam.R;
import com.example.xkbam.DetallesUsuarioActivity;
import com.example.xkbam.api.ApiConexion;
import com.example.xkbam.utilidades.SesionSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText userTextBox;
    private EditText passwordBox;
    private Button loginButton;
    private TextView newClientLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userTextBox = findViewById(R.id.userTextBox);
        passwordBox = findViewById(R.id.passwordBox);

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()) {
                    enviarCredenciales(userTextBox.getText().toString(), passwordBox.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        newClientLabel = findViewById(R.id.newClientLabel);

        newClientLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewClientActivity();
            }
        });
    }

    private void openSecondActivity() {
        Intent intent = new Intent(this, MenuPrincipal.class);
        startActivity(intent);
        finish();
    }

    private void openNewClientActivity() {
        Intent intent = new Intent(this, DetallesUsuarioActivity.class);
        intent.putExtra("NUMBER", 1);
        startActivity(intent);
    }

    private boolean validarCampos() {
        return !userTextBox.getText().toString().trim().isEmpty() && !passwordBox.getText().toString().trim().isEmpty();
    }

    private void enviarCredenciales(String correo, String contrasena) {
        JSONObject credenciales = new JSONObject();
        try {
            credenciales.put("correo", correo);
            credenciales.put("contrasena", contrasena);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String json = credenciales.toString();
        ApiConexion.enviarRequestAsincrono("POST", "autenticacion", json, false, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "No fue posible establecer conexión con el servidor", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String jsonResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonResponse);

                        String email = jsonObject.getString("correo");
                        String jwt = jsonObject.getString("jwt");
                        String usuario = jsonObject.getString("usuario");
                        int rol = jsonObject.getInt("rol");

                        SesionSingleton.getInstance().setCorreo(email);
                        SesionSingleton.getInstance().setUsuario(usuario);
                        SesionSingleton.setJWT(jwt);
                        SesionSingleton.getInstance().setRol(rol);

                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                            openSecondActivity();
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
