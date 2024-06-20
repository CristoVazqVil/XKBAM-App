package com.example.xkbam.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.xkbam.R;
import com.example.xkbam.api.ApiConexion;
import com.example.xkbam.dto.TarjetaBancariaDTO;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DetallesBancariosActivity extends AppCompatActivity {


    private EditText etCardNumber, etCardHolder, etCVV, etExpiryDate;
    private Button btnSave,btnModify;
    private String usuario = "diddydeuxANDROID"; // Obtener el usuario actualmente logueado
    private String tarjetaModificada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_bancarios);

        etCardNumber = findViewById(R.id.card_number);
        etCardHolder = findViewById(R.id.card_holder);
        etCVV = findViewById(R.id.cvv);
        etExpiryDate = findViewById(R.id.expiry_date);
        btnSave = findViewById(R.id.save_button);
        btnModify = findViewById(R.id.modify_button);

        cargarDetallesBancarios();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDetallesBancarios();
            }
        });

        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarDetallesBancarios();
            }
        });
    }

    private void cargarDetallesBancarios() {
        ApiConexion.enviarRequestAsincrono("GET", "cuentasbancarias/" + usuario, null, true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetallesBancariosActivity.this, "Error al obtener detalles bancarios", Toast.LENGTH_SHORT).show();
                        // Mostrar el botón Guardar si no hay detalles bancarios obtenidos
                        btnSave.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray jsonArray = new JSONArray(responseData);
                            if (jsonArray.length() > 0) {
                                JSONObject jsonResponse = jsonArray.getJSONObject(0);
                                // Mostrar los detalles bancarios en los EditText
                                etCardNumber.setText(jsonResponse.getString("numeroTarjeta"));
                                etCardHolder.setText(jsonResponse.getString("titular"));
                                etCVV.setText(jsonResponse.getString("cvv"));

                                // Manejar el formato de fecha adecuadamente
                                String fechaExpiracionStr = jsonResponse.getString("fechaExpiracion");
                                SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                Date fechaExpiracion = sdfInput.parse(fechaExpiracionStr);
                                SimpleDateFormat sdfOutput = new SimpleDateFormat("MM/yy");
                                String fechaExpiracionFormatted = sdfOutput.format(fechaExpiracion);
                                etExpiryDate.setText(fechaExpiracionFormatted);

                                // Mostrar el botón Modificar si se obtienen detalles bancarios
                                btnModify.setVisibility(View.VISIBLE);
                            } else {
                                // Mostrar el botón Guardar si no se obtienen detalles bancarios
                                btnSave.setVisibility(View.VISIBLE);
                            }
                            String tarjetaModificada = etCardNumber.getText().toString().trim();
                            Toast.makeText(DetallesBancariosActivity.this, "Detalles bancarios cargados correctamente", Toast.LENGTH_SHORT).show();
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(DetallesBancariosActivity.this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                            // Mostrar el botón Guardar si hay un error al procesar la respuesta
                            btnSave.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }



    private void guardarDetallesBancarios() {
        String numeroTarjeta = etCardNumber.getText().toString().trim();
        String titular = etCardHolder.getText().toString().trim();
        String cvv = etCVV.getText().toString().trim();
        String fechaExpiracionStr = etExpiryDate.getText().toString().trim();
        String usuario = "diddydeuxANDROID"; // Aquí debes obtener el usuario actualmente logueado

        // Validación básica de campos
        if (numeroTarjeta.isEmpty() || titular.isEmpty() || cvv.isEmpty() || fechaExpiracionStr.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/yy");
        Date fechaExpiracion;
        try {
            fechaExpiracion = sdf.parse(fechaExpiracionStr);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Formato de fecha inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto TarjetaBancariaDTO
        TarjetaBancariaDTO tarjetaDTO = new TarjetaBancariaDTO();
        tarjetaDTO.setNumeroTarjeta(numeroTarjeta);
        tarjetaDTO.setTitular(titular);
        tarjetaDTO.setCvv(cvv);
        tarjetaDTO.setFechaExpiracion(fechaExpiracion);
        tarjetaDTO.setUsuario(usuario);

        // Convertir a JSON
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("numeroTarjeta", tarjetaDTO.getNumeroTarjeta());
            jsonObject.put("titular", tarjetaDTO.getTitular());
            jsonObject.put("cvv", tarjetaDTO.getCvv());
            // Convertir fecha de expiración a formato deseado, por ejemplo "yyyy-MM-dd"
            SimpleDateFormat sdfOutput = new SimpleDateFormat("yyyy-MM-dd");
            String fechaExpiracionFormatted = sdfOutput.format(tarjetaDTO.getFechaExpiracion());
            jsonObject.put("fechaExpiracion", fechaExpiracionFormatted);
            jsonObject.put("usuario", tarjetaDTO.getUsuario());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        // Enviar solicitud a la API
        ApiConexion.enviarRequestAsincrono("POST", "cuentasbancarias", jsonObject.toString(), true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetallesBancariosActivity.this, "Error al enviar solicitud", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            // Aquí puedes manejar la respuesta de la API según necesites
                            Toast.makeText(DetallesBancariosActivity.this, "Detalles bancarios guardados correctamente", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DetallesBancariosActivity.this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    private void modificarDetallesBancarios() {
        String numeroTarjeta = etCardNumber.getText().toString().trim();
        String titular = etCardHolder.getText().toString().trim();
        String cvv = etCVV.getText().toString().trim();
        String fechaExpiracionStr = etExpiryDate.getText().toString().trim();



// Validación básica de campos
        if (numeroTarjeta.isEmpty() || titular.isEmpty() || cvv.isEmpty() || fechaExpiracionStr.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/yy");
        Date fechaExpiracion;
        try {
            fechaExpiracion = sdf.parse(fechaExpiracionStr);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Formato de fecha inválido", Toast.LENGTH_SHORT).show();
            return;
        }

// Construir el objeto JSON
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("numeroTarjeta", numeroTarjeta);
            jsonObject.put("titular", titular);
            jsonObject.put("cvv", cvv);
            String fechaExpiracionFormatted = sdf.format(fechaExpiracion);
            jsonObject.put("fechaExpiracion", fechaExpiracionFormatted);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear JSON", Toast.LENGTH_SHORT).show();
            return;
        }

// Mostrar el JSON en un AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("JSON a enviar");
        builder.setMessage(jsonObject.toString());
        builder.setPositiveButton("OK", null); // Botón OK sin acción adicional
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

// Enviar solicitud a la API para actualizar detalles bancarios
        ApiConexion.enviarRequestAsincrono("PUT", "cuentasbancarias/" + numeroTarjeta, jsonObject.toString(), true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetallesBancariosActivity.this, "Error al enviar solicitud", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            // Aquí puedes manejar la respuesta de la API según necesites
                            Toast.makeText(DetallesBancariosActivity.this, "Detalles bancarios actualizados correctamente", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DetallesBancariosActivity.this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}