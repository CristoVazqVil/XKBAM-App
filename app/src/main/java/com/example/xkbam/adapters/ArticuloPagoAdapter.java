package com.example.xkbam.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xkbam.R;
import com.example.xkbam.dto.ArticuloCarritoDTO;

import java.util.List;

public class ArticuloPagoAdapter extends RecyclerView.Adapter<ArticuloPagoAdapter.ArticuloPagoViewHolder> {

    private List<ArticuloCarritoDTO> articuloCarritoList;

    public ArticuloPagoAdapter(List<ArticuloCarritoDTO> articuloCarritoList) {
        this.articuloCarritoList = articuloCarritoList;
    }

    @NonNull
    @Override
    public ArticuloPagoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_articulo_pago, parent, false);
        return new ArticuloPagoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticuloPagoViewHolder holder, int position) {
        ArticuloCarritoDTO articuloCarrito = articuloCarritoList.get(position);
        holder.tvCodigoArticulo.setText(articuloCarrito.getCodigoArticulo());
        holder.tvCantidadArticulo.setText(String.valueOf(articuloCarrito.getCantidadArticulo()));
    }

    @Override
    public int getItemCount() {
        return articuloCarritoList.size();
    }

    public static class ArticuloPagoViewHolder extends RecyclerView.ViewHolder {
        TextView tvCodigoArticulo;
        TextView tvCantidadArticulo;

        public ArticuloPagoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCodigoArticulo = itemView.findViewById(R.id.tvCodigoArticulo);
            tvCantidadArticulo = itemView.findViewById(R.id.tvCantidadArticulo);
        }
    }
}
