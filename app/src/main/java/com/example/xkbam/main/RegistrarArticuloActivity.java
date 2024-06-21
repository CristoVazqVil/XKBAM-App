package com.example.xkbam.main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.xkbam.R;
import com.example.xkbam.api.ApiConexion;
import com.example.xkbam.dto.ArticuloDTO;
import com.example.xkbam.dto.TarjetaBancariaDTO;
import com.example.xkbam.utilidades.ClienteGrpc;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegistrarArticuloActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 2;
    private String selectedCategory, selectedColor;
    private EditText textCodigo, textNombre, textDescripcion, textPrecio;
    private Spinner spinnerCategoria, spinnerColor;
    private Button botonGuardar;
    private ImageView imgArticulo;
    private ArticuloDTO articuloExistente;
    private ClienteGrpc clienteGrpc;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_articulo);


        textCodigo = findViewById(R.id.item_code);
        textNombre = findViewById(R.id.item_name);
        textDescripcion = findViewById(R.id.description);
        spinnerCategoria = findViewById(R.id.category_spinner);
        spinnerColor = findViewById(R.id.color_spinner);
        textPrecio = findViewById(R.id.price_item);
        botonGuardar = findViewById(R.id.save_button);

        imgArticulo = findViewById(R.id.item_image);
        imgArticulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        clienteGrpc = new ClienteGrpc("192.168.17.59:8080");

        List<String> categorias = new ArrayList<>();
        categorias.add("Superiores");
        categorias.add("Inferiores");
        categorias.add("Accesorios");
        categorias.add("Conjuntos");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCategoria.setAdapter(adapter);

        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Guardar el elemento seleccionado en una variable
                selectedCategory = parent.getItemAtPosition(position).toString();
                Toast.makeText(RegistrarArticuloActivity.this, "Selected: " + selectedCategory, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<String> colores = new ArrayList<>();
        colores.add("Negro");
        colores.add("Blanco");
        colores.add("Verde");
        colores.add("Rojo");
        colores.add("Azul");
        colores.add("Gris");
        colores.add("Amarillo");
        colores.add("Multicolor");

        ArrayAdapter<String> adapterColor = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colores);
        adapterColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setAdapter(adapterColor);

        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedColor = parent.getItemAtPosition(position).toString();
                Toast.makeText(RegistrarArticuloActivity.this, "Color seleccionado: " + selectedColor, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });


        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarInformacion()) {
                    Toast.makeText(RegistrarArticuloActivity.this, "Información válida. Procesando...",
                            Toast.LENGTH_SHORT).show();
                    if (articuloExistente != null) {
                        actualizarArticulo();
                    } else {
                        registrarArticulo();
                    }
                }
            }
        });

        if (getIntent().hasExtra("articulo")) {
            articuloExistente = (ArticuloDTO) getIntent().getSerializableExtra("articulo");
            llenarCamposConDatosExistentes(articuloExistente);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            ImageView imageView = findViewById(R.id.item_image);
            imageView.setImageURI(selectedImageUri);
        }
    }


    private boolean validarInformacion(){
        EditText editTextCodigo = findViewById(R.id.item_code);
        EditText editTextNombre = findViewById(R.id.item_name);
        EditText editTextDescripcion = findViewById(R.id.description);
        EditText editTextPrecio = findViewById(R.id.price_item);

        String codigo = editTextCodigo.getText().toString().trim();
        String nombre = editTextNombre.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();
        String precio = editTextPrecio.getText().toString().trim();

        if (TextUtils.isEmpty(codigo)) {
            editTextNombre.setError("Este campo es obligatorio");
            return false;
        }

        if (TextUtils.isEmpty(nombre)) {
            editTextNombre.setError("Este campo es obligatorio");
            return false;
        }

        if (TextUtils.isEmpty(descripcion)) {
            editTextDescripcion.setError("Este campo es obligatorio");
            return false;
        }

        if (TextUtils.isEmpty(precio)) {
            editTextPrecio.setError("Este campo es obligatorio");
            return false;
        }

        if (selectedCategory == null) {
            Toast.makeText(this, "Seleccione una categoría", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedColor == null) {
            Toast.makeText(this, "Seleccione un color", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }




    private void registrarArticulo() {
        ArticuloDTO articuloDTO = crearArticuloDesdeCampos();
        JSONObject jsonObject = crearJsonDesdeArticulo(articuloDTO);

        ApiConexion.enviarRequestAsincrono("POST", "articulos", jsonObject.toString(),
                false, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegistrarArticuloActivity.this, "Error al enviar solicitud",
                                        Toast.LENGTH_SHORT).show();
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
                                    if (selectedImageUri != null) {
                                        String imagePath = getPathFromUri(selectedImageUri);
                                        String nombreArticulo = articuloDTO.getNombre();
                                        String itemId = articuloDTO.getCodigoArticulo();
                                        clienteGrpc.uploadMultimediaAsync(itemId, nombreArticulo, imagePath);
                                    }
                                    Toast.makeText(RegistrarArticuloActivity.this,
                                            "Artículo registrado correctamente", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(RegistrarArticuloActivity.this,
                                            "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }

    private void actualizarArticulo() {
        ArticuloDTO articuloDTO = crearArticuloDesdeCampos();
        articuloDTO.setCodigoArticulo(articuloExistente.getCodigoArticulo());
        JSONObject jsonObject = crearJsonDesdeArticulo(articuloDTO);

        ApiConexion.enviarRequestAsincrono("PUT", "articulos/" + articuloExistente.getCodigoArticulo(), jsonObject.toString(),
                true, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegistrarArticuloActivity.this, "Error al enviar solicitud",
                                        Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(RegistrarArticuloActivity.this,
                                            "Artículo actualizado correctamente", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(RegistrarArticuloActivity.this,
                                            "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }

    private ArticuloDTO crearArticuloDesdeCampos() {
        String nombreArticulo = textNombre.getText().toString().trim();
        String codigoArticulo = textCodigo.getText().toString().trim();
        String descripcionArticulo = textDescripcion.getText().toString().trim();
        Double precioArticulo = Double.valueOf(textPrecio.getText().toString().trim());
        int categoriaArticulo = asignarIdCategoria();
        int colorArticulo = asignarIdColor();

        ArticuloDTO articuloDTO = new ArticuloDTO();
        articuloDTO.setCodigoArticulo(codigoArticulo);
        articuloDTO.setNombre(nombreArticulo);
        articuloDTO.setDescripcion(descripcionArticulo);
        articuloDTO.setPrecio(precioArticulo);
        articuloDTO.setIdColor(colorArticulo);
        articuloDTO.setIdCategoria(categoriaArticulo);
        return articuloDTO;
    }

    private JSONObject crearJsonDesdeArticulo(ArticuloDTO articuloDTO) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("codigoArticulo", articuloDTO.getCodigoArticulo());
            jsonObject.put("nombre", articuloDTO.getNombre());
            jsonObject.put("descripcion", articuloDTO.getDescripcion());
            jsonObject.put("precio", articuloDTO.getPrecio());
            jsonObject.put("idColor", articuloDTO.getIdColor());
            jsonObject.put("idCategoria", articuloDTO.getIdCategoria());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    private int asignarIdCategoria() {
        String categoria = selectedCategory;
        int categoriaId = 0;

        switch (categoria) {
            case "Superiores":
                categoriaId = 1;
                break;
            case "Accesorios":
                categoriaId = 2;
                break;
            case "Inferiores":
                categoriaId = 3;
                break;
            case "Conjuntos":
                categoriaId = 4;
                break;
        }

        return categoriaId;
    }

    private int asignarIdColor() {
        String color = selectedColor;
        int colorId = 0;

        switch (color) {
            case "Negro":
                colorId = 1;
                break;
            case "Blanco":
                colorId = 2;
                break;
            case "Verde":
                colorId = 3;
                break;
            case "Rojo":
                colorId = 4;
                break;
            case "Azul":
                colorId = 5;
                break;
            case "Gris":
                colorId = 6;
                break;
            case "Amarillo":
                colorId = 7;
                break;
            case "Multicolor":
                colorId = 8;
                break;
        }

        return colorId;
    }


    private void llenarCamposConDatosExistentes(ArticuloDTO articulo) {
        textCodigo.setText(articulo.getCodigoArticulo());
        textNombre.setText(articulo.getNombre());
        textDescripcion.setText(articulo.getDescripcion());
        textPrecio.setText(String.valueOf(articulo.getPrecio()));
        spinnerCategoria.setSelection(getCategoriaPosition(articulo.getIdCategoria()));
        spinnerColor.setSelection(getColorPosition(articulo.getIdColor()));
    }

    private int getCategoriaPosition(int categoriaId) {
        switch (categoriaId) {
            case 1:
                return 0;
            case 2:
                return 2;
            case 3:
                return 1;
            case 4:
                return 3;
            default:
                return 0;
        }
    }

    private int getColorPosition(int colorId) {
        switch (colorId) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 3;
            case 5:
                return 4;
            case 6:
                return 5;
            case 7:
                return 6;
            case 8:
                return 7;
            default:
                return 0;
        }
    }

    private String getPathFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }

}