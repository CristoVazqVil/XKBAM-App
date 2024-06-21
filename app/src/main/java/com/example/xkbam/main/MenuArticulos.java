package com.example.xkbam.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xkbam.R;
import com.example.xkbam.adapters.ArticuloAdapter;
import com.example.xkbam.api.ApiConexion;
import com.example.xkbam.dto.ArticuloDTO;
import com.example.xkbam.dto.MultimediaDTO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MenuArticulos extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerViewArticulos;
    private ArticuloAdapter articuloAdapter;
    private Map<String, Bitmap> imagenesArticulos = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_articulos);

        drawerLayout = findViewById(R.id.drawerLayout);
        recyclerViewArticulos = findViewById(R.id.recyclerViewArticulos);
        recyclerViewArticulos.setLayoutManager(new LinearLayoutManager(this));

        String categoria = getIntent().getStringExtra("categoria");
        cargarDatos(categoria);

        ImageView imgCarrito = findViewById(R.id.imgCarrito);
        imgCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuArticulos.this, Carrito.class));
            }
        });

        ImageView imgOpciones = findViewById(R.id.imgOpciones);
        imgOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(findViewById(R.id.navMenu));
            }
        });

        ImageView imgCerrarOpciones = findViewById(R.id.imgCerrarOpciones);
        imgCerrarOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(findViewById(R.id.navMenu));
            }
        });

        TextView navItemTodo = findViewById(R.id.navItemTodo);
        TextView navItemSuperiores = findViewById(R.id.navItemSuperiores);
        TextView navItemInferiores = findViewById(R.id.navItemInferiores);
        TextView navItemAccesorios = findViewById(R.id.navItemAccesorios);
        TextView navItemConjuntos = findViewById(R.id.navItemConjuntos);
        TextView navItemCuenta = findViewById(R.id.navItemCuenta);

        navItemTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuArticulos.this, MenuArticulos.class);
                intent.putExtra("categoria", "todos");
                startActivity(intent);
                finish();
            }
        });

        navItemSuperiores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuArticulos.this, MenuArticulos.class);
                intent.putExtra("categoria", "superiores");
                startActivity(intent);
                finish();
            }
        });

        navItemInferiores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuArticulos.this, MenuArticulos.class);
                intent.putExtra("categoria", "inferiores");
                startActivity(intent);
                finish();
            }
        });

        navItemAccesorios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuArticulos.this, MenuArticulos.class);
                intent.putExtra("categoria", "accesorios");
                startActivity(intent);
                finish();
            }
        });

        navItemConjuntos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuArticulos.this, MenuArticulos.class);
                intent.putExtra("categoria", "conjuntos");
                startActivity(intent);
                finish();
            }
        });

        navItemCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuArticulos.this, MenuCuentaActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void cargarDatos(String categoria) {
        String endpoint = getEndpointForCategory(categoria);
        ApiConexion.enviarRequestAsincrono("GET", endpoint, null, true, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(MenuArticulos.this, "Error al cargar los artículos", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    Type articuloListType = new TypeToken<List<ArticuloDTO>>() {}.getType();
                    List<ArticuloDTO> articuloList = gson.fromJson(responseBody, articuloListType);

                    for (ArticuloDTO articulo : articuloList) {
                        cargarImagenArticulo(articulo);
                    }

                    runOnUiThread(() -> {
                        articuloAdapter = new ArticuloAdapter(MenuArticulos.this, articuloList);
                        recyclerViewArticulos.setAdapter(articuloAdapter);
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(MenuArticulos.this, "Error al cargar los artículos", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void cargarImagenArticulo(ArticuloDTO articulo) {
        String codigoArticulo = articulo.getCodigoArticulo();
        ApiConexion.enviarRequestAsincrono("GET", "multimedia/codigo/" + codigoArticulo, null, true, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("MenuArticulos", "Error al obtener imagen para artículo " + codigoArticulo, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    Type multimediaListType = new TypeToken<List<MultimediaDTO>>() {}.getType();

                    try {
                        List<MultimediaDTO> multimediaList = gson.fromJson(responseBody, multimediaListType);

                        if (multimediaList != null && !multimediaList.isEmpty()) {
                            byte[] imageData = multimediaList.get(0).getContenido().getData();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                            runOnUiThread(() -> {
                                imagenesArticulos.put(codigoArticulo, bitmap);

                                if (articuloAdapter != null) {
                                    articuloAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    } catch (JsonSyntaxException e) {
                        Log.e("MenuArticulos", "Error de sintaxis JSON al deserializar", e);
                    }
                } else {
                    Log.e("MenuArticulos", "Error al cargar imagen para artículo " + codigoArticulo);
                }
            }
        });
    }


    private String getEndpointForCategory(String categoria) {
        switch (categoria) {
            case "superiores":
                return "articulos/categoria/1";
            case "inferiores":
                return "articulos/categoria/2";
            case "accesorios":
                return "articulos/categoria/3";
            case "conjuntos":
                return "articulos/categoria/4";
            case "todos":
            default:
                return "articulos";
        }
    }
}
