package com.example.xkbam.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xkbam.R;
import com.example.xkbam.adapters.ArticuloPagoAdapter;
import com.example.xkbam.api.ApiConexion;
import com.example.xkbam.dto.ArticuloCarritoDTO;
import com.example.xkbam.dto.ArticuloCompraDTO;
import com.example.xkbam.dto.DireccionDTO;
import com.example.xkbam.utilidades.SesionSingleton;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Pago extends AppCompatActivity {

    private static final String TAG = "PagoActivity";

    private RecyclerView recyclerViewArticulos;
    private Spinner spinnerDirecciones;
    private TextView txtNombreUsuario;
    private TextView txtTotal;
    private Button btnPagar;
    private Button btnCancelar;

    private int idCarrito;
    private List<ArticuloCarritoDTO> articulos = new ArrayList<>();
    private List<DireccionDTO> direcciones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago);

        idCarrito = getIntent().getIntExtra("idCarrito", -1);

        recyclerViewArticulos = findViewById(R.id.recyclerViewArticulos);
        spinnerDirecciones = findViewById(R.id.spinnerDirecciones);
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        txtTotal = findViewById(R.id.txtTotal);
        btnPagar = findViewById(R.id.btnPagar);
        btnCancelar = findViewById(R.id.btnCancelar);

        recyclerViewArticulos.setLayoutManager(new LinearLayoutManager(this));

        cargarDatosCompra();

        btnPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarCompra();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarCompra();
            }
        });

    }

    private void cargarDatosCompra() {
        try {
            cargarArticulos();
            cargarDirecciones();
            txtNombreUsuario.setText(SesionSingleton.getInstance().getUsuario());
        } catch (Exception ex) {
            Log.e(TAG, "Error al cargar datos de compra: " + ex.getMessage());
            Toast.makeText(Pago.this, "Error al cargar datos de compra", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarArticulos() {
        ApiConexion.enviarRequestAsincrono("GET", "carritos/" + idCarrito, null, true, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "Error al obtener los artículos del carrito: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Pago.this, "Error al obtener los artículos del carrito", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    JSONArray jsonArray = new JSONArray(responseBody);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonArticulo = jsonArray.getJSONObject(i);
                        ArticuloCarritoDTO articuloCarrito = new ArticuloCarritoDTO();
                        articuloCarrito.setIdArticuloCarrito(jsonArticulo.getInt("idArticuloCarrito"));
                        articuloCarrito.setCantidadArticulo(jsonArticulo.getInt("cantidadArticulo"));
                        articuloCarrito.setPrecioUnitario(jsonArticulo.getDouble("precioUnitario"));
                        articuloCarrito.setPrecioFinal(jsonArticulo.getDouble("precioFinal"));
                        articuloCarrito.setCodigoArticulo(jsonArticulo.getString("codigoArticulo"));
                        articuloCarrito.setIdCarrito(jsonArticulo.getInt("idCarrito"));
                        articuloCarrito.setIdTalla(jsonArticulo.getInt("idTalla"));
                        articulos.add(articuloCarrito);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArticuloPagoAdapter adapter = new ArticuloPagoAdapter(articulos);
                            recyclerViewArticulos.setAdapter(adapter);
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Pago.this, "Error al obtener los artículos del carrito", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void cargarDirecciones() {
        ApiConexion.enviarRequestAsincrono("GET", "direcciones/" + SesionSingleton.getInstance().getUsuario(), null, true, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "Error al obtener las direcciones: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Pago.this, "Error al obtener las direcciones", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    JSONArray jsonArray = new JSONArray(responseBody);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonDireccion = jsonArray.getJSONObject(i);
                        DireccionDTO direccion = new DireccionDTO();
                        direccion.setEstado(jsonDireccion.getString("estado"));
                        direccion.setMunicipio(jsonDireccion.getString("municipio"));
                        direccion.setCodigoPostal(jsonDireccion.getString("codigoPostal"));
                        direccion.setCalle(jsonDireccion.getString("calle"));
                        direccion.setNumeroExterno(jsonDireccion.getInt("numeroExterno"));
                        direccion.setUsuario(jsonDireccion.getString("usuario"));
                        direcciones.add(direccion);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<DireccionDTO> adapter = new ArrayAdapter<>(Pago.this, android.R.layout.simple_spinner_item, direcciones);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerDirecciones.setAdapter(adapter);
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Pago.this, "Error al obtener las direcciones", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void realizarCompra() {
        if (spinnerDirecciones.getSelectedItem() == null) {
            Toast.makeText(this, "Por favor, seleccione una dirección para el envío.", Toast.LENGTH_SHORT).show();
            return;
        }

        DireccionDTO direccionSeleccionada = direcciones.get(spinnerDirecciones.getSelectedItemPosition());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmación");
        builder.setMessage("¿Está seguro de que desea realizar la compra de los artículos?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<ArticuloCompraDTO> articulosCompra = convertirArticulos();

                JSONObject requestObject = new JSONObject();
                try {
                    requestObject.put("usuario", SesionSingleton.getInstance().getUsuario());
                    requestObject.put("estado", "Realizada");

                    JSONArray jsonArrayArticulos = new JSONArray(new Gson().toJson(articulosCompra));
                    requestObject.put("articulos", jsonArrayArticulos);

                } catch (JSONException e) {
                    Log.e(TAG, "Error al crear JSON de solicitud: " + e.getMessage());
                    Toast.makeText(Pago.this, "Error al realizar la compra. Por favor, intente de nuevo.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String jsonCompra = requestObject.toString();

                ApiConexion.enviarRequestAsincrono("POST", "compras/", jsonCompra, true, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.e(TAG, "Error al realizar la compra: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Pago.this, "Error al realizar la compra. Por favor, intente de nuevo.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            vaciarCarrito();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(Pago.this, CompraRealizada.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else {
                            Log.e(TAG, "Error al realizar la compra. Código de error: " + response.code());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Pago.this, "Hubo un problema al realizar la compra. Por favor, intente de nuevo.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    private List<ArticuloCompraDTO> convertirArticulos() {
        List<ArticuloCompraDTO> articulosCompra = new ArrayList<>();

        for (ArticuloCarritoDTO articuloCarrito : articulos) {
            ArticuloCompraDTO articuloCompra = new ArticuloCompraDTO();
            articuloCompra.setIdArticuloCompra(articuloCarrito.getIdArticuloCarrito());
            articuloCompra.setCantidadArticulo(articuloCarrito.getCantidadArticulo());
            articuloCompra.setPrecioUnitario(articuloCarrito.getPrecioUnitario());
            articuloCompra.setPrecioFinal(articuloCarrito.getPrecioFinal());
            articuloCompra.setCodigoArticulo(articuloCarrito.getCodigoArticulo());
            articuloCompra.setIdTalla(articuloCarrito.getIdTalla());
            articulosCompra.add(articuloCompra);
        }

        return articulosCompra;
    }

    private void cancelarCompra() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmación");
        builder.setMessage("¿Está seguro de que desea cancelar la compra?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Pago.this, "Compra cancelada.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void vaciarCarrito() {
        for (ArticuloCarritoDTO articulo : articulos) {
            ApiConexion.enviarRequestAsincrono("DELETE", "carritos/vaciar/" + articulo.getIdArticuloCarrito(), null, true, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e(TAG, "Error al vaciar el artículo del carrito: " + articulo.getIdArticuloCarrito() + ", " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Pago.this, "Error al vaciar el artículo del carrito", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "Error al vaciar el artículo del carrito. Código de error: " + response.code());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Pago.this, "Error al vaciar el artículo del carrito", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

}
