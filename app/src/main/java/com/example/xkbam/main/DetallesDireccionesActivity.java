package com.example.xkbam;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.xkbam.api.ApiConexion;
import com.example.xkbam.dto.DireccionDTO;
import com.example.xkbam.utilidades.SesionSingleton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DetallesDireccionesActivity extends AppCompatActivity {

    private EditText etEstado, etMunicipio, etCodigoPostal, etCalle, etNumeroExterno;
    private Spinner directionSpinner;
    private Button btnSave, btnModify;
    private List<JSONObject> direccionesList = new ArrayList<>();
    private String idDireccionSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_direcciones);

        etEstado = findViewById(R.id.card_number);
        etMunicipio = findViewById(R.id.card_holder);
        etCodigoPostal = findViewById(R.id.cvv);
        etCalle = findViewById(R.id.expiry_date);
        etNumeroExterno = findViewById(R.id.number_exterior);
        directionSpinner = findViewById(R.id.direction_spinner);
        btnSave = findViewById(R.id.save_button);
        btnModify = findViewById(R.id.modify_button);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDireccion();
            }
        });

        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarDireccion();
            }
        });

        obtenerDireccionesDelUsuario();
    }

    private void obtenerDireccionesDelUsuario() {
        String usuario = SesionSingleton.getInstance().getUsuario();

        ApiConexion.enviarRequestAsincrono("GET", "direcciones/" + usuario, null, true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(DetallesDireccionesActivity.this, "Error al obtener direcciones", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonResponse = new JSONArray(responseData);
                        direccionesList.clear();
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            direccionesList.add(jsonResponse.getJSONObject(i));
                        }
                        if (direccionesList.size() == 1) {
                            direccionesList.add(new JSONObject());
                        }
                        cargarSpinnerDirecciones();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DetallesDireccionesActivity.this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void cargarSpinnerDirecciones() {
        List<String> direccionTitles = new ArrayList<>();
        for (int i = 0; i < direccionesList.size(); i++) {
            JSONObject direccion = direccionesList.get(i);
            try {
                String titulo = "Dirección " + (i + 1);
                if (direccion.has("calle") && !direccion.getString("calle").isEmpty()) {
                    titulo += ": " + direccion.getString("calle");
                } else {
                    titulo += ": Nueva Dirección";
                }
                direccionTitles.add(titulo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, direccionTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directionSpinner.setAdapter(adapter);

        directionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarDatosDireccion(direccionesList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (!direccionesList.isEmpty()) {
            cargarDatosDireccion(direccionesList.get(0));
        }
    }

    private void cargarDatosDireccion(JSONObject direccion) {
        try {
            etEstado.setText(direccion.has("estado") ? direccion.getString("estado") : "");
            etMunicipio.setText(direccion.has("municipio") ? direccion.getString("municipio") : "");
            etCodigoPostal.setText(direccion.has("codigoPostal") ? direccion.getString("codigoPostal") : "");
            etCalle.setText(direccion.has("calle") ? direccion.getString("calle") : "");
            etNumeroExterno.setText(direccion.has("numeroExterno") ? direccion.getString("numeroExterno") : "");
            idDireccionSeleccionada = direccion.has("idDireccion") ? direccion.getString("idDireccion") : null;

            if (etEstado.getText().toString().trim().isEmpty()) {
                btnSave.setVisibility(View.VISIBLE);
                btnModify.setVisibility(View.GONE);
            } else {
                btnSave.setVisibility(View.GONE);
                btnModify.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar datos de dirección", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarDireccion() {
        String estado = etEstado.getText().toString().trim();
        String municipio = etMunicipio.getText().toString().trim();
        String codigoPostal = etCodigoPostal.getText().toString().trim();
        String calle = etCalle.getText().toString().trim();
        String numeroExternoStr = etNumeroExterno.getText().toString().trim();
        String usuario = SesionSingleton.getInstance().getUsuario();

        if (estado.isEmpty() || municipio.isEmpty() || codigoPostal.isEmpty() || calle.isEmpty() || numeroExternoStr.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int numeroExterno;
        try {
            numeroExterno = Integer.parseInt(numeroExternoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Número exterior debe ser un número válido", Toast.LENGTH_SHORT).show();
            return;
        }

        DireccionDTO direccionDTO = new DireccionDTO();
        direccionDTO.setEstado(estado);
        direccionDTO.setMunicipio(municipio);
        direccionDTO.setCodigoPostal(codigoPostal);
        direccionDTO.setCalle(calle);
        direccionDTO.setNumeroExterno(numeroExterno);
        direccionDTO.setUsuario(usuario);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("estado", direccionDTO.getEstado());
            jsonObject.put("municipio", direccionDTO.getMunicipio());
            jsonObject.put("codigoPostal", direccionDTO.getCodigoPostal());
            jsonObject.put("calle", direccionDTO.getCalle());
            jsonObject.put("numeroExterno", direccionDTO.getNumeroExterno());
            jsonObject.put("usuario", direccionDTO.getUsuario());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiConexion.enviarRequestAsincrono("POST", "direcciones", jsonObject.toString(), true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(DetallesDireccionesActivity.this, "Error al enviar solicitud", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        Toast.makeText(DetallesDireccionesActivity.this, "Dirección guardada correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DetallesDireccionesActivity.this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void modificarDireccion() {
        String estado = etEstado.getText().toString().trim();
        String municipio = etMunicipio.getText().toString().trim();
        String codigoPostal = etCodigoPostal.getText().toString().trim();
        String calle = etCalle.getText().toString().trim();
        String numeroExternoStr = etNumeroExterno.getText().toString().trim();
        String usuario = SesionSingleton.getInstance().getUsuario();

        if (estado.isEmpty() || municipio.isEmpty() || codigoPostal.isEmpty() || calle.isEmpty() || numeroExternoStr.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int numeroExterno;
        try {
            numeroExterno = Integer.parseInt(numeroExternoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Número exterior debe ser un número válido", Toast.LENGTH_SHORT).show();
            return;
        }

        DireccionDTO direccionDTO = new DireccionDTO();
        direccionDTO.setEstado(estado);
        direccionDTO.setMunicipio(municipio);
        direccionDTO.setCodigoPostal(codigoPostal);
        direccionDTO.setCalle(calle);
        direccionDTO.setNumeroExterno(numeroExterno);
        direccionDTO.setUsuario(usuario);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("estado", direccionDTO.getEstado());
            jsonObject.put("municipio", direccionDTO.getMunicipio());
            jsonObject.put("codigoPostal", direccionDTO.getCodigoPostal());
            jsonObject.put("calle", direccionDTO.getCalle());
            jsonObject.put("numeroExterno", direccionDTO.getNumeroExterno());
            jsonObject.put("usuario", direccionDTO.getUsuario());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idDireccionSeleccionada == null) {
            Toast.makeText(this, "Error: ID de dirección no encontrada", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiConexion.enviarRequestAsincrono("PUT", "direcciones/" + idDireccionSeleccionada, jsonObject.toString(), true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(DetallesDireccionesActivity.this, "Error al enviar solicitud", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        Toast.makeText(DetallesDireccionesActivity.this, "Dirección modificada correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DetallesDireccionesActivity.this, "Dirección modificada correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }
}
