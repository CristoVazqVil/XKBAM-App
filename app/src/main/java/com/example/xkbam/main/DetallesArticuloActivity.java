package com.example.xkbam.main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.xkbam.R;

public class DetallesArticuloActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 2;
    private String selectedCategory;
    private String selectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_articulo);


        EditText textCodigo = findViewById(R.id.item_code);
        EditText textNombre = findViewById(R.id.item_name);
        EditText textDescripcion = findViewById(R.id.description);
        Spinner spinnerCategoria = findViewById(R.id.category_spinner);
        Spinner spinnerColor = findViewById(R.id.color_spinner);
        EditText textPrecio = findViewById(R.id.price_item);
        Button botonGuardar = findViewById(R.id.save_button);


        ImageView imgArticulo = findViewById(R.id.item_image);
        imgArticulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });


        // Crear una lista de categorías para el Spinner
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
                Toast.makeText(DetallesArticuloActivity.this, "Selected: " + selectedCategory, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DetallesArticuloActivity.this, "Color seleccionado: " + selectedColor, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });


        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarInformacion()){
                    Toast.makeText(DetallesArticuloActivity.this, "Información válida. Registrando artículo...",
                            Toast.LENGTH_SHORT).show();
                    registrarArticulo();

                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            ImageView imageView = findViewById(R.id.item_image);
            imageView.setImageURI(selectedImage);
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




    private void registrarArticulo(){

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

}