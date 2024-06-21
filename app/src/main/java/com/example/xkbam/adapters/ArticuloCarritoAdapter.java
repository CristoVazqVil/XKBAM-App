package com.example.xkbam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xkbam.R;
import com.example.xkbam.dto.ArticuloCarritoDTO;

import java.util.List;

public class ArticuloCarritoAdapter extends RecyclerView.Adapter<ArticuloCarritoAdapter.ArticuloViewHolder> {

    private Context context;
    private List<ArticuloCarritoDTO> articulos;
    private OnCantidadChangedListener cantidadChangedListener;

    public interface OnCantidadChangedListener {
        void onCantidadChanged(ArticuloCarritoDTO articulo, int nuevaCantidad);
    }

    public ArticuloCarritoAdapter(Context context, List<ArticuloCarritoDTO> articulos, OnCantidadChangedListener listener) {
        this.context = context;
        this.articulos = articulos;
        this.cantidadChangedListener = listener;
    }

    @NonNull
    @Override
    public ArticuloViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_articulo_carrito, parent, false);
        return new ArticuloViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticuloViewHolder holder, int position) {
        ArticuloCarritoDTO articulo = articulos.get(position);
        holder.bind(articulo);
        holder.cantidadArticulo.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String cantidadStr = holder.cantidadArticulo.getText().toString().trim();
                int nuevaCantidad;
                if (!cantidadStr.isEmpty()) {
                    nuevaCantidad = Integer.parseInt(cantidadStr);
                    if (nuevaCantidad < 0) {
                        nuevaCantidad = 0;
                    } else if (nuevaCantidad > 10) {
                        nuevaCantidad = 10;
                    }
                } else {
                    nuevaCantidad = 0;
                }
                holder.cantidadArticulo.setText(String.valueOf(nuevaCantidad));
                if (cantidadChangedListener != null) {
                    cantidadChangedListener.onCantidadChanged(articulo, nuevaCantidad);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return articulos.size();
    }

    public void actualizarArticulos(List<ArticuloCarritoDTO> nuevosArticulos) {
        articulos.clear();
        articulos.addAll(nuevosArticulos);
        notifyDataSetChanged();
    }

    class ArticuloViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreArticulo;
        private EditText cantidadArticulo;
        private TextView precioArticulo;

        public ArticuloViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreArticulo = itemView.findViewById(R.id.nombreArticulo);
            cantidadArticulo = itemView.findViewById(R.id.cantidadArticulo);
            precioArticulo = itemView.findViewById(R.id.precioArticulo);
        }

        public void bind(ArticuloCarritoDTO articulo) {
            nombreArticulo.setText(articulo.getCodigoArticulo());
            cantidadArticulo.setText(String.valueOf(articulo.getCantidadArticulo()));
            precioArticulo.setText(String.format("$ %.2f", articulo.getPrecioUnitario()));
        }
    }
}
