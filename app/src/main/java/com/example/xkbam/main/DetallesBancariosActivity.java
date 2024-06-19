package com.example.xkbam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.xkbam.api.ApiConexion;
import com.example.xkbam.dto.TarjetaBancariaDTO;

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
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_bancarios);

        etCardNumber = findViewById(R.id.card_number);
        etCardHolder = findViewById(R.id.card_holder);
        etCVV = findViewById(R.id.cvv);
        etExpiryDate = findViewById(R.id.expiry_date);
        btnSave = findViewById(R.id.save_button);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDetallesBancarios();
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
}
