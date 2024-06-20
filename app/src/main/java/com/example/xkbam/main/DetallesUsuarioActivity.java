package com.example.xkbam;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.xkbam.api.ApiConexion;
import com.example.xkbam.databinding.ActivityDetallesUsuarioBinding;
import com.example.xkbam.utilidades.SesionSingleton;

import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import java.io.IOException;

public class DetallesUsuarioActivity extends AppCompatActivity {

    private ActivityDetallesUsuarioBinding binding;

    // Declare EditText variables as member variables
    private EditText usernameEditText;
    private EditText firstNameEditText;
    private EditText paternoNameEditText;
    private EditText maternoNameEditText;
    private Spinner genderSpinner;
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView passwordlabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetallesUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the EditText variables
        usernameEditText = findViewById(R.id.username);
        firstNameEditText = findViewById(R.id.first_name);
        paternoNameEditText = findViewById(R.id.paterno_name);
        maternoNameEditText = findViewById(R.id.materno_name);
        genderSpinner = findViewById(R.id.gender_spinner);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        passwordlabel = findViewById(R.id.password_label);

        Button saveButton = findViewById(R.id.save_button);
        Button modifyButton = findViewById(R.id.modify_button);


        // Obtain the intent and the number
        int number = getIntent().getIntExtra("NUMBER", 0);

        // Check the number and adjust visibility and actions accordingly
        if (number == 1) {
            saveButton.setVisibility(View.VISIBLE);
        } else if (number == 2) {
            saveButton.setVisibility(View.GONE);
            obtenerDatosUsuario(SesionSingleton.getInstance().getUsuario());
            passwordEditText.setVisibility(View.GONE);
            passwordlabel.setVisibility(View.GONE);
            usernameEditText.setEnabled(false);
            modifyButton.setVisibility(View.VISIBLE);
        }

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
                                        finish();

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


        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores de los campos de entrada
                String username = usernameEditText.getText().toString();
                String firstName = firstNameEditText.getText().toString();
                String paternoName = paternoNameEditText.getText().toString();
                String maternoName = maternoNameEditText.getText().toString();
                String gender = genderSpinner.getSelectedItem().toString();
                String email = emailEditText.getText().toString();

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
                    cuentaJson.put("idRol", 2); // Ajusta según corresponda

                    dataJson.put("usuario", usuarioJson);
                    dataJson.put("cuenta", cuentaJson);



                    // Enviar solicitud a la API
                    ApiConexion.enviarRequestAsincrono("PUT", "usuarios/" + username, dataJson.toString(), true, new Callback() {
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
                                        Toast.makeText(DetallesUsuarioActivity.this, "Datos modificados correctamente", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            } else {
                                String responseBody = response.body().string();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mostrarDialogo("Error al modificar datos", responseBody);
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

    private void obtenerDatosUsuario(String usuario) {
        ApiConexion.enviarRequestAsincrono("GET", "usuarios/" + usuario, null, false, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mostrarDialogo("Error al obtener datos", e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONObject usuarioObj = jsonObject.getJSONObject("usuario");
                        JSONObject cuentaObj = jsonObject.getJSONObject("cuenta");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    usernameEditText.setText(usuarioObj.getString("usuario"));
                                    firstNameEditText.setText(usuarioObj.getString("nombre"));
                                    paternoNameEditText.setText(usuarioObj.getString("apellidoPaterno"));
                                    maternoNameEditText.setText(usuarioObj.getString("apellidoMaterno"));
                                    genderSpinner.setSelection(usuarioObj.getInt("genero") == 1 ? 0 : 1);
                                    emailEditText.setText(cuentaObj.getString("correo"));
                                    passwordEditText.setText(""); // No se recomienda mostrar la contraseña real
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    mostrarDialogo("Error al procesar datos", e.getMessage());
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mostrarDialogo("Error al procesar datos", e.getMessage());
                    }
                } else {
                    String responseBody = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mostrarDialogo("Error al obtener datos", responseBody);
                        }
                    });
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
