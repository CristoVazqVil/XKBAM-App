package com.example.xkbam;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.xkbam.api.ApiConexion;
import com.example.xkbam.databinding.ActivityDetallesUsuarioBinding;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import java.io.IOException;

public class DetallesUsuarioActivity extends AppCompatActivity {

    private ActivityDetallesUsuarioBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetallesUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Referencias a los campos de entrada
        EditText usernameEditText = findViewById(R.id.username);
        EditText firstNameEditText = findViewById(R.id.first_name);
        EditText paternoNameEditText = findViewById(R.id.paterno_name);
        EditText maternoNameEditText = findViewById(R.id.materno_name);
        Spinner genderSpinner = findViewById(R.id.gender_spinner);
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores de los campos de entrada
                String username = usernameEditText.getText().toString();
                String firstName = firstNameEditText.getText().toString();
                String paternoName = paternoNameEditText.getText().toString();
                String maternoName = maternoNameEditText.getText().toString();
                String gender = genderSpinner.getSelectedItem().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Convertir género a entero
                int genderValue = gender.equals("Masculino") ? 1 : 2; // Ajusta según tus valores de género

                // Crear JSON
                JSONObject usuarioJson = new JSONObject();
                JSONObject cuentaJson = new JSONObject();
                JSONObject dataJson = new JSONObject();

                try {
                    usuarioJson.put("usuario", username); // Ajusta según corresponda
                    usuarioJson.put("nombre", firstName);
                    usuarioJson.put("apellidoPaterno", paternoName);
                    usuarioJson.put("apellidoMaterno", maternoName);
                    usuarioJson.put("genero", genderValue);

                    cuentaJson.put("correo", email);
                    cuentaJson.put("contrasena", password);
                    cuentaJson.put("idRol", 2); // Ajusta según corresponda

                    dataJson.put("usuario", usuarioJson);
                    dataJson.put("cuenta", cuentaJson);

                    // Mostrar JSON en un AlertDialog
                    String jsonString = dataJson.toString(4); // Formato legible
                    mostrarDialogo("JSON", jsonString);

                    // Enviar solicitud a la API
                    ApiConexion.enviarRequestAsincrono("POST", "usuarios", dataJson.toString(), false, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mostrarDialogo("Error al enviar datos", e.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(DetallesUsuarioActivity.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                String responseBody = response.body().string();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mostrarDialogo("Error al guardar datos", responseBody);
                                    }
                                });
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    mostrarDialogo("Error al crear JSON", e.getMessage());
                }
            }
        });
    }

    private void mostrarDialogo(String titulo, String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
