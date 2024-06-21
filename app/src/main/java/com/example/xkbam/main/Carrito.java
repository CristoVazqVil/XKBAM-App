package com.example.xkbam.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xkbam.R;
import com.example.xkbam.adapters.ArticuloCarritoAdapter;
import com.example.xkbam.api.ApiConexion;
import com.example.xkbam.dto.ArticuloCarritoDTO;
import com.example.xkbam.utilidades.SesionSingleton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Carrito extends AppCompatActivity {

    private static final String TAG = "CarritoActivity";
    private List<ArticuloCarritoDTO> articulos = new ArrayList<>();
    private RecyclerView recyclerViewCarrito;
    private ArticuloCarritoAdapter adapter;
    private TextView labelTotal;
    private Button btnComprar;
    private Button btnOk;
    private int idCarrito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        recyclerViewCarrito = findViewById(R.id.recyclerViewCarrito);
        labelTotal = findViewById(R.id.totalPrecio);
        btnComprar = findViewById(R.id.btnComprar);
        btnOk = findViewById(R.id.btnOk);

        setupRecyclerView();
        obtenerIdCarritoYActualizar();

        btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirVentanaCompra();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarArticulosEnCarrito();
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
                Intent intent = new Intent(Carrito.this, MenuArticulos.class);
                intent.putExtra("categoria", "todos");
                startActivity(intent);
                finish();
            }
        });

        navItemSuperiores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Carrito.this, MenuArticulos.class);
                intent.putExtra("categoria", "superiores");
                startActivity(intent);
                finish();
            }
        });

        navItemInferiores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Carrito.this, MenuArticulos.class);
                intent.putExtra("categoria", "inferiores");
                startActivity(intent);
                finish();
            }
        });

        navItemAccesorios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Carrito.this, MenuArticulos.class);
                intent.putExtra("categoria", "accesorios");
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new ArticuloCarritoAdapter(this, articulos, new ArticuloCarritoAdapter.OnCantidadChangedListener() {
            @Override
            public void onCantidadChanged(ArticuloCarritoDTO articulo, int nuevaCantidad) {
                if (nuevaCantidad < 0) {
                    articulo.setCantidadArticulo(0);
                } else if (nuevaCantidad > 10) {
                    articulo.setCantidadArticulo(10);
                } else {
                    articulo.setCantidadArticulo(nuevaCantidad);
                }
                actualizarTotal(calcularTotal());
            }
        });
        recyclerViewCarrito.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCarrito.setAdapter(adapter);
    }

    private void obtenerIdCarritoYActualizar() {
        ApiConexion.enviarRequestAsincrono("GET", "carritos/carritousuario/" + SesionSingleton.getInstance().getUsuario(), null, true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Carrito.this, "Error al obtener el ID del carrito", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e(TAG, "Error al obtener el ID del carrito", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                        idCarrito = jsonObject.get("idCarrito").getAsInt();
                        cargarArticulos();
                    } catch (Exception e) {
                        Log.e(TAG, "Error al procesar la respuesta JSON", e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Carrito.this, "Error al procesar la respuesta JSON", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Carrito.this, "Error al obtener el ID del carrito", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void cargarArticulos() {
        ApiConexion.enviarRequestAsincrono("GET", "carritos/" + idCarrito, null, true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Carrito.this, "Error al cargar los artículos del carrito", Toast.LENGTH_SHORT).show();
                        btnComprar.setEnabled(false);
                        btnOk.setEnabled(false);
                        actualizarTotal(0.0);
                    }
                });
                Log.e(TAG, "Error al cargar los artículos del carrito", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        Type listType = new TypeToken<List<ArticuloCarritoDTO>>(){}.getType();
                        articulos = new Gson().fromJson(responseBody, listType);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (articulos.isEmpty()) {
                                    Toast.makeText(Carrito.this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
                                    btnComprar.setEnabled(false);
                                    btnOk.setEnabled(false);
                                    actualizarTotal(0.0);
                                } else {
                                    adapter.actualizarArticulos(articulos);
                                    btnComprar.setEnabled(true);
                                    btnOk.setEnabled(true);
                                    actualizarTotal(calcularTotal());
                                }
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Error al procesar la respuesta JSON", e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Carrito.this, "Error al procesar la respuesta JSON", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Carrito.this, "Error al cargar los artículos del carrito", Toast.LENGTH_SHORT).show();
                            btnComprar.setEnabled(false);
                            btnOk.setEnabled(false);
                            actualizarTotal(0.0);
                        }
                    });
                    Log.e(TAG, "Error al cargar los artículos del carrito: " + response.code() + " " + response.message());
                }
            }
        });
    }

    private void actualizarArticulosEnCarrito() {
        List<ArticuloCarritoDTO> articulosCopy = new ArrayList<>(articulos);

        for (ArticuloCarritoDTO articulo : articulosCopy) {
            if (articulo.getCantidadArticulo() == 0) {
                ApiConexion.enviarRequestAsincrono("DELETE", "carritos/vaciar/" + articulo.getIdArticuloCarrito(), null, true, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Carrito.this, "Error al vaciar el artículo del carrito", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.e(TAG, "Error al vaciar el artículo del carrito", e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Carrito.this, "Error al vaciar el artículo del carrito", Toast.LENGTH_SHORT).show();
                                }
                            });
                            Log.e(TAG, "Error al vaciar el artículo del carrito: " + response.code() + " " + response.message());
                        } else {
                            articulos.remove(articulo);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.actualizarArticulos(articulos);
                                    actualizarTotal(calcularTotal());
                                }
                            });
                        }
                    }
                });
            } else {
                Gson gson = new Gson();
                String jsonArticulo = gson.toJson(articulo);

                ApiConexion.enviarRequestAsincrono("PUT", "carritos/cantidad", jsonArticulo, true, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Carrito.this, "Error al actualizar la cantidad del artículo", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.e(TAG, "Error al actualizar la cantidad del artículo", e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Carrito.this, "Error al actualizar la cantidad del artículo", Toast.LENGTH_SHORT).show();
                                }
                            });
                            Log.e(TAG, "Error al actualizar la cantidad del artículo: " + response.code() + " " + response.message());
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Carrito.this, "Artículos actualizados en el carrito", Toast.LENGTH_SHORT).show();
                                    adapter.actualizarArticulos(articulos);
                                    actualizarTotal(calcularTotal());
                                }
                            });
                        }
                    }
                });
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                actualizarTotal(calcularTotal());
                Toast.makeText(Carrito.this, "Los cambios se han guardado correctamente.", Toast.LENGTH_SHORT).show();
            }
        });

        cargarArticulos();
    }

    private double calcularTotal() {
        double total = 0.0;
        for (ArticuloCarritoDTO articulo : articulos) {
            total += articulo.getPrecioUnitario() * articulo.getCantidadArticulo();
        }
        return total;
    }

    private void actualizarTotal(double total) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                labelTotal.setText(String.format("Total: $ %.2f", total));
            }
        });
    }

    private void abrirVentanaCompra() {
        Intent intent = new Intent(this, Pago.class);
        intent.putExtra("idCarrito", idCarrito);
        startActivity(intent);
        finish();
    }
}
