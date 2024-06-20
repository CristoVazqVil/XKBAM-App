package com.example.xkbam.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.xkbam.R;
import com.example.xkbam.api.ApiConexion;
import com.example.xkbam.dto.ArticuloDTO;
import com.example.xkbam.dto.MultimediaDTO;
import com.example.xkbam.main.DetallesArticulo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ArticuloAdapter extends RecyclerView.Adapter<ArticuloAdapter.ArticuloViewHolder> {

    private Context context;
    private List<ArticuloDTO> articuloList;

    public ArticuloAdapter(Context context, List<ArticuloDTO> articuloList) {
        this.context = context;
        this.articuloList = articuloList;
    }

    @NonNull
    @Override
    public ArticuloViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_articulo, parent, false);
        return new ArticuloViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticuloViewHolder holder, int position) {
        ArticuloDTO articulo = articuloList.get(position);
        holder.txtNombreArticulo.setText(articulo.getNombre());
        holder.txtPrecioArticulo.setText(String.format("$%.2f", articulo.getPrecio()));

        // Cargar la imagen del artículo usando la API y OkHttp
        cargarImagenArticulo(articulo.getCodigoArticulo(), holder.imgArticulo);

        // Configurar OnClickListener para abrir DetallesArticulo
        holder.imgArticulo.setOnClickListener(v -> {
            // Crear un Intent para abrir DetallesArticulo
            Intent intent = new Intent(context, DetallesArticulo.class);
            // Pasar el código del artículo como extra
            intent.putExtra("codigoArticulo", articulo.getCodigoArticulo());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return articuloList.size();
    }

    public static class ArticuloViewHolder extends RecyclerView.ViewHolder {
        ImageView imgArticulo;
        TextView txtNombreArticulo;
        TextView txtPrecioArticulo;

        public ArticuloViewHolder(@NonNull View itemView) {
            super(itemView);
            imgArticulo = itemView.findViewById(R.id.imgArticulo);
            txtNombreArticulo = itemView.findViewById(R.id.txtNombreArticulo);
            txtPrecioArticulo = itemView.findViewById(R.id.txtPrecioArticulo);
        }
    }

    private void cargarImagenArticulo(String codigoArticulo, ImageView imgArticulo) {
        String endpoint = "multimedia/codigo/" + codigoArticulo;
        ApiConexion.enviarRequestAsincrono("GET", endpoint, null, true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error al cargar la imagen del artículo", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        Gson gson = new Gson();
                        Type multimediaListType = new TypeToken<List<MultimediaDTO>>() {}.getType();
                        List<MultimediaDTO> multimediaList = gson.fromJson(responseBody, multimediaListType);

                        if (multimediaList != null && !multimediaList.isEmpty()) {
                            byte[] imageData = multimediaList.get(0).getContenido().getData();
                            // Decodificar el byte array en un Bitmap
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inMutable = true; // Permitir la mutabilidad del bitmap
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);

                            // Cargar la imagen en el ImageView usando Glide en el hilo principal
                            new Handler(Looper.getMainLooper()).post(() -> {
                                Glide.with(context)
                                        .load(bitmap)
                                        .into(imgArticulo);
                            });
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error al procesar la respuesta del servidor", e);
                    }
                } else {
                    Log.e(TAG, "Error en la respuesta del servidor: " + response.code());
                }
            }
        });
    }

    private static final String TAG = "ArticuloAdapter";
}
