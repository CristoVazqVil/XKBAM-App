package com.example.xkbam.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.xkbam.R;
import com.example.xkbam.api.ApiConexion;
import com.example.xkbam.dto.ArticuloDTO;
import com.example.xkbam.dto.MultimediaDTO;
import com.example.xkbam.dto.OpinionDTO;
import com.example.xkbam.utilidades.SesionSingleton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DetallesArticulo extends AppCompatActivity {
    private ImageView imgArticulo;
    private TextView txtNombreArticulo, txtDescripcionArticulo, txtPrecioArticulo;
    private Spinner spinnerTallas, spinnerCantidad, spinnerCalificacion;
    private EditText txtComentario;
    private LinearLayout stkOpiniones;
    private Button btnAñadirCarrito, btnEnviarOpinion, btnModificarArticulo;
    private ArticuloDTO articulo;
    private List<MultimediaDTO> multimediaList;
    private int idCarrito;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_articulo);

        // Initialize views
        imgArticulo = findViewById(R.id.imgArticulo);
        txtNombreArticulo = findViewById(R.id.txtNombreArticulo);
        txtDescripcionArticulo = findViewById(R.id.txtDescripcionArticulo);
        txtPrecioArticulo = findViewById(R.id.txtPrecioArticulo);
        spinnerTallas = findViewById(R.id.spinnerTallas);
        spinnerCantidad = findViewById(R.id.spinnerCantidad);
        spinnerCalificacion = findViewById(R.id.spinnerCalificacion);
        txtComentario = findViewById(R.id.txtComentario);
        stkOpiniones = findViewById(R.id.stkOpiniones);
        btnAñadirCarrito = findViewById(R.id.btnAñadirCarrito);
        btnEnviarOpinion = findViewById(R.id.btnEnviarOpinion);
        btnModificarArticulo = findViewById(R.id.btnModificarArticulo);

        // Set up spinners
        ArrayAdapter<String> tallasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Seleccione una talla", "XS", "S", "M", "L", "XL"});
        spinnerTallas.setAdapter(tallasAdapter);

        ArrayAdapter<Integer> cantidadesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        spinnerCantidad.setAdapter(cantidadesAdapter);

        ArrayAdapter<Integer> calificacionesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new Integer[]{0, 1, 2, 3, 4, 5});
        spinnerCalificacion.setAdapter(calificacionesAdapter);

        // Get article code from intent
        String codigoArticulo = getIntent().getStringExtra("codigoArticulo");

        // Load article details, opinions and cart ID
        loadArticuloData(codigoArticulo);
        loadOpiniones(codigoArticulo);
        obtenerIdCarrito();

        // Hide modify button if user role is not allowed
        if (SesionSingleton.getInstance().getRol() == 2) {
            btnModificarArticulo.setVisibility(View.GONE);
        }

        btnAñadirCarrito.setOnClickListener(v -> {
            String tallaSeleccionada = spinnerTallas.getSelectedItem().toString();
            if (tallaSeleccionada.equals("Seleccione una talla")) {
                Toast.makeText(DetallesArticulo.this, "Seleccione una talla válida", Toast.LENGTH_SHORT).show();
                return;
            }
            int cantidadSeleccionada = Integer.parseInt(spinnerCantidad.getSelectedItem().toString());
            if (cantidadSeleccionada == 0) {
                Toast.makeText(DetallesArticulo.this, "Seleccione una cantidad válida", Toast.LENGTH_SHORT).show();
                return;
            }
            añadirArticuloAlCarrito(codigoArticulo, tallaSeleccionada, cantidadSeleccionada);
        });

        btnEnviarOpinion.setOnClickListener(v -> {
            String comentario = txtComentario.getText().toString();
            int calificacion = Integer.parseInt(spinnerCalificacion.getSelectedItem().toString());
            if (calificacion == 0) {
                Toast.makeText(DetallesArticulo.this, "Seleccione una calificación válida", Toast.LENGTH_SHORT).show();
                return;
            }
            enviarOpinion(codigoArticulo, comentario, calificacion);
        });

        btnModificarArticulo.setOnClickListener(v -> {
            // Implement logic to modify the article
        });

        ImageView imgCarrito = findViewById(R.id.imgCarrito);
        imgCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir la actividad del carrito aquí
                startActivity(new Intent(DetallesArticulo.this, Carrito.class));
            }
        });
    }



    private void loadArticuloData(String codigoArticulo) {
        ApiConexion.enviarRequestAsincrono("GET", "articulos/" + codigoArticulo, null, true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(DetallesArticulo.this, "Error al obtener el artículo", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Gson gson = new Gson();
                    Type tipoArticulo = new TypeToken<ArticuloDTO>(){}.getType();
                    articulo = gson.fromJson(json, tipoArticulo);
                    runOnUiThread(() -> mostrarDatosArticulo());
                    loadMultimedia(codigoArticulo);
                } else {
                    runOnUiThread(() -> Toast.makeText(DetallesArticulo.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void mostrarDatosArticulo() {
        runOnUiThread(() -> {
            txtNombreArticulo.setText(articulo.getNombre());
            txtDescripcionArticulo.setText(articulo.getDescripcion());
            txtPrecioArticulo.setText(String.format("$%.2f", articulo.getPrecio()));
        });
    }

    private void loadMultimedia(String codigoArticulo) {
        ApiConexion.enviarRequestAsincrono("GET", "multimedia/codigo/" + codigoArticulo, null, true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(DetallesArticulo.this, "Error al obtener multimedia", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Gson gson = new Gson();
                    Type tipoListaMultimedia = new TypeToken<List<MultimediaDTO>>(){}.getType();
                    multimediaList = gson.fromJson(json, tipoListaMultimedia);
                    if (multimediaList != null && !multimediaList.isEmpty()) {
                        runOnUiThread(() -> mostrarImagen(multimediaList.get(0).getContenido().getData()));
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(DetallesArticulo.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void mostrarImagen(byte[] contenidoImagen) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(contenidoImagen, 0, contenidoImagen.length);
        runOnUiThread(() -> imgArticulo.setImageBitmap(bitmap));
    }

    private void obtenerIdCarrito() {
        ApiConexion.enviarRequestAsincrono("GET", "carritos/carritousuario/" + SesionSingleton.getInstance().getUsuario(), null, true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(DetallesArticulo.this, "Error al obtener el id del carrito", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        idCarrito = jsonObject.getInt("idCarrito");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(DetallesArticulo.this, "Error: No se pudo obtener el id del carrito", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(DetallesArticulo.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void loadOpiniones(String codigoArticulo) {
        ApiConexion.enviarRequestAsincrono("GET", "opiniones/" + codigoArticulo, null, true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(DetallesArticulo.this, "Error al obtener opiniones", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Gson gson = new Gson();
                    Type tipoListaOpiniones = new TypeToken<List<OpinionDTO>>(){}.getType();
                    List<OpinionDTO> opiniones = gson.fromJson(json, tipoListaOpiniones);
                    runOnUiThread(() -> mostrarOpiniones(opiniones));
                } else {
                    runOnUiThread(() -> Toast.makeText(DetallesArticulo.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void mostrarOpiniones(List<OpinionDTO> opiniones) {
        runOnUiThread(() -> {
            stkOpiniones.removeAllViews();
            for (OpinionDTO opinion : opiniones) {
                TextView txtUsuarioYCalificacion = new TextView(DetallesArticulo.this);
                txtUsuarioYCalificacion.setText(opinion.getUsuario() + "   " + getStarRating(opinion.getCalificacion()));
                txtUsuarioYCalificacion.setTextSize(18);
                txtUsuarioYCalificacion.setTextColor(getResources().getColor(android.R.color.black)); // Cambiar a un color visible

                TextView txtComentario = new TextView(DetallesArticulo.this);
                txtComentario.setText("Comentario: " + opinion.getComentario());
                txtComentario.setTextSize(14);
                txtComentario.setTextColor(getResources().getColor(android.R.color.black)); // Cambiar a un color visible

                stkOpiniones.addView(txtUsuarioYCalificacion);
                stkOpiniones.addView(txtComentario);

                View separator = new View(DetallesArticulo.this);
                separator.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
                separator.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

                stkOpiniones.addView(separator);
            }
        });
    }

    private String getStarRating(int calificacion) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < calificacion; i++) {
            stars.append("★");
        }
        return stars.toString();
    }

    private void añadirArticuloAlCarrito(String codigoArticulo, String tallaSeleccionada, int cantidadSeleccionada) {
        int idTalla = obtenerIdTalla(tallaSeleccionada);

        // Crear objeto para enviar al servidor
        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("codigoArticulo", codigoArticulo);
        jsonBody.put("cantidadArticulo", cantidadSeleccionada);
        jsonBody.put("idTalla", idTalla);
        jsonBody.put("idCarrito", idCarrito);

        String json = new Gson().toJson(jsonBody);

        ApiConexion.enviarRequestAsincrono("POST", "carritos/articulo", json, true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(DetallesArticulo.this, "Error al añadir al carrito", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(DetallesArticulo.this, "Artículo añadido al carrito", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(DetallesArticulo.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private int obtenerIdTalla(String talla) {
        switch (talla) {
            case "XS":
                return 1;
            case "S":
                return 2;
            case "M":
                return 3;
            case "L":
                return 4;
            case "XL":
                return 5;
            default:
                throw new IllegalArgumentException("La talla seleccionada no es válida.");
        }
    }

    private void enviarOpinion(String codigoArticulo, String comentario, int calificacion) {
        // Crear objeto para enviar al servidor
        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("codigoArticulo", codigoArticulo);
        jsonBody.put("comentario", comentario);
        jsonBody.put("calificacion", calificacion);
        jsonBody.put("usuario", SesionSingleton.getInstance().getUsuario());

        String json = new Gson().toJson(jsonBody);

        ApiConexion.enviarRequestAsincrono("POST", "opiniones", json, true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(DetallesArticulo.this, "Error al enviar la opinión", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(DetallesArticulo.this, "Opinión enviada correctamente", Toast.LENGTH_SHORT).show();
                        txtComentario.setText("");
                        spinnerCalificacion.setSelection(0);
                        loadOpiniones(codigoArticulo);
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(DetallesArticulo.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
