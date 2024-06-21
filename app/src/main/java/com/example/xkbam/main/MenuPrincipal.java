package com.example.xkbam.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.xkbam.R;
import com.example.xkbam.api.ApiConexion;
import com.example.xkbam.dto.ArticuloDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MenuPrincipal extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private EditText txbArticulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        drawerLayout = findViewById(R.id.drawerLayout);
        txbArticulo = findViewById(R.id.txbArticulo);


        Button btnBuscar = findViewById(R.id.btnBuscar);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String termino = txbArticulo.getText().toString().trim();
                if (!termino.isEmpty()) {
                    buscarArticuloPorTermino(termino);
                } else {
                    Toast.makeText(MenuPrincipal.this, "Ingrese el nombre del artículo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView imgCarrito = findViewById(R.id.imgCarrito);
        imgCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipal.this, Carrito.class));
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
                Intent intent = new Intent(MenuPrincipal.this, MenuArticulos.class);
                intent.putExtra("categoria", "todos");
                startActivity(intent);
            }
        });

        navItemSuperiores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this, MenuArticulos.class);
                intent.putExtra("categoria", "superiores");
                startActivity(intent);
            }
        });

        navItemInferiores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this, MenuArticulos.class);
                intent.putExtra("categoria", "inferiores");
                startActivity(intent);
            }
        });

        navItemAccesorios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this, MenuArticulos.class);
                intent.putExtra("categoria", "accesorios");
                startActivity(intent);
            }
        });

        navItemConjuntos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this, MenuArticulos.class);
                intent.putExtra("categoria", "conjuntos");
                startActivity(intent);
            }
        });

        navItemCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this, MenuCuentaActivity.class);
                startActivity(intent);
            }
        });
    }

    private void buscarArticuloPorTermino(String termino) {
        String endpoint = "articulos/search/" + termino;
        ApiConexion.enviarRequestAsincrono("GET", endpoint, null, true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MenuPrincipal.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                if (response.isSuccessful()) {
                    try {
                        Type listType = new TypeToken<List<ArticuloDTO>>() {}.getType();
                        List<ArticuloDTO> articulos = new Gson().fromJson(responseData, listType);
                        if (!articulos.isEmpty()) {
                            ArticuloDTO articuloEncontrado = articulos.get(0);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    abrirDetallesArticulo(articuloEncontrado.getCodigoArticulo());
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MenuPrincipal.this, "No se encontró el artículo", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (JsonSyntaxException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MenuPrincipal.this, "Error al parsear la respuesta JSON", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    final String errorMessage = response.message();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MenuPrincipal.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void abrirDetallesArticulo(String codigoArticulo) {
        Intent intent = new Intent(MenuPrincipal.this, DetallesArticulo.class);
        intent.putExtra("codigoArticulo", codigoArticulo);
        startActivity(intent);
    }
}
