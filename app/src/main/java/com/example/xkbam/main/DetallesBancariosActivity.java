package com.example.xkbam;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import com.example.xkbam.api.ApiConexion;
import com.example.xkbam.dto.TarjetaBancariaDTO;
import com.example.xkbam.utilidades.SesionSingleton;
import org.json.JSONException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DetallesBancariosActivity extends AppCompatActivity {

    private EditText etCardNumber, etCardHolder, etCVV, etExpiryDate;
    private Button btnSave, btnModify;
    private String usuario = SesionSingleton.getInstance().getUsuario();
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
        btnSave.setOnClickListener(v -> guardarDetallesBancarios());
        btnModify.setOnClickListener(v -> modificarDetallesBancarios());
    }

    private void cargarDetallesBancarios() {
        ApiConexion.enviarRequestAsincrono("GET", "cuentasbancarias/" + usuario, null, true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(DetallesBancariosActivity.this, "Error al obtener detalles bancarios", Toast.LENGTH_SHORT).show();
                    btnSave.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        if (jsonArray.length() > 0) {
                            JSONObject jsonResponse = jsonArray.getJSONObject(0);
                            etCardNumber.setText(jsonResponse.getString("numeroTarjeta"));
                            etCardHolder.setText(jsonResponse.getString("titular"));
                            etCVV.setText(jsonResponse.getString("cvv"));

                            String fechaExpiracionStr = jsonResponse.getString("fechaExpiracion");
                            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            Date fechaExpiracion = sdfInput.parse(fechaExpiracionStr);
                            SimpleDateFormat sdfOutput = new SimpleDateFormat("MM/yy");
                            String fechaExpiracionFormatted = sdfOutput.format(fechaExpiracion);
                            etExpiryDate.setText(fechaExpiracionFormatted);

                            btnModify.setVisibility(View.VISIBLE);
                        } else {
                            btnSave.setVisibility(View.VISIBLE);
                        }
                        tarjetaModificada = etCardNumber.getText().toString().trim();
                        Toast.makeText(DetallesBancariosActivity.this, "Detalles bancarios cargados correctamente", Toast.LENGTH_SHORT).show();
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(DetallesBancariosActivity.this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                        btnSave.setVisibility(View.VISIBLE);
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
        String usuario = SesionSingleton.getInstance().getUsuario();

        if (numeroTarjeta.isEmpty() || titular.isEmpty() || cvv.isEmpty() || fechaExpiracionStr.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidCreditCard(numeroTarjeta)) {
            Toast.makeText(this, "Número de tarjeta inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cvv.length() > 3) {
            Toast.makeText(this, "El CVV no puede tener más de 3 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!fechaExpiracionStr.matches("\\d{2}/\\d{2}")) {
            Toast.makeText(this, "Formato de fecha inválido", Toast.LENGTH_SHORT).show();
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

        TarjetaBancariaDTO tarjetaDTO = new TarjetaBancariaDTO();
        tarjetaDTO.setNumeroTarjeta(numeroTarjeta);
        tarjetaDTO.setTitular(titular);
        tarjetaDTO.setCvv(cvv);
        tarjetaDTO.setFechaExpiracion(fechaExpiracion);
        tarjetaDTO.setUsuario(usuario);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("numeroTarjeta", tarjetaDTO.getNumeroTarjeta());
            jsonObject.put("titular", tarjetaDTO.getTitular());
            jsonObject.put("cvv", tarjetaDTO.getCvv());
            SimpleDateFormat sdfOutput = new SimpleDateFormat("yyyy-MM-dd");
            String fechaExpiracionFormatted = sdfOutput.format(tarjetaDTO.getFechaExpiracion());
            jsonObject.put("fechaExpiracion", fechaExpiracionFormatted);
            jsonObject.put("usuario", tarjetaDTO.getUsuario());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiConexion.enviarRequestAsincrono("POST", "cuentasbancarias", jsonObject.toString(), true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(DetallesBancariosActivity.this, "Error al enviar solicitud", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        Toast.makeText(DetallesBancariosActivity.this, "Detalles bancarios guardados correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DetallesBancariosActivity.this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
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

        if (numeroTarjeta.isEmpty() || titular.isEmpty() || cvv.isEmpty() || fechaExpiracionStr.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidCreditCard(numeroTarjeta)) {
            Toast.makeText(this, "Número de tarjeta inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cvv.length() > 3) {
            Toast.makeText(this, "El CVV no puede tener más de 3 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!fechaExpiracionStr.matches("\\d{2}/\\d{2}")) {
            Toast.makeText(this, "Formato de fecha inválido", Toast.LENGTH_SHORT).show();
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

        ApiConexion.enviarRequestAsincrono("PUT", "cuentasbancarias/" + numeroTarjeta, jsonObject.toString(), true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(DetallesBancariosActivity.this, "Error al enviar solicitud", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        Toast.makeText(DetallesBancariosActivity.this, "Detalles bancarios actualizados correctamente", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DetallesBancariosActivity.this, "Detalles bancarios actualizados correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }

    private boolean isValidCreditCard(String cardNumber) {
        String regex = "^(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|6(?:011|5[0-9]{2})[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|(?:2131|1800|35\\d{3})\\d{11})$";
        return Pattern.matches(regex, cardNumber);
    }
}
