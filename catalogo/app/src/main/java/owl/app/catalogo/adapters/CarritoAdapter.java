package owl.app.catalogo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import owl.app.catalogo.R;
import owl.app.catalogo.api.Api;
import owl.app.catalogo.models.Carrito;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.ViewHolder> {
    private List<Carrito> carritos;
    private int layout;
    private OnClickListener listener;
    private OnLongClickListener listenerLong;

    private Context context;

    public CarritoAdapter(List<Carrito> carritos, int layout, OnClickListener listener, OnLongClickListener listenerLong) {
        this.carritos = carritos;
        this.layout = layout;
        this.listener = listener;
        this.listenerLong = listenerLong;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        context = parent.getContext();
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(carritos.get(position), listener, listenerLong);
    }

    @Override
    public int getItemCount() {
        return carritos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titulo;
        private TextView precio;
        private ImageView imagen;

        public ViewHolder(View v) {
            super(v);
            titulo = (TextView) itemView.findViewById(R.id.textViewTituloCarrito);
            precio = (TextView) itemView.findViewById(R.id.textViewPrecioCarrito);
            imagen = (ImageView)itemView.findViewById(R.id.imageViewCarrito);
        }

        public void bind(final Carrito carritos, final OnClickListener listener, final OnLongClickListener listenerLong) {

            titulo.setText(carritos.getProducto());

            String precioConvecion = String.valueOf(carritos.getCosto());
            precio.setText(precioConvecion);

            Picasso.get().load(Api.GALERIA+carritos.getImagen()).fit().into(imagen);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(carritos, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listenerLong.onLongItemClick(carritos, getAdapterPosition());
                    /*en el escuchador setOnLonfClickListener es importante devolver true en lugar de false.
                    si devolvemos falso el escuchador nos activara por defecto el onClickListener detras
                    del onLongClickListener: este ultimo siempre tiene que devolver un true*/
                    return true;
                }
            });


        }
    }

    public interface OnClickListener {
        void onItemClick(Carrito carritos, int position);
    }

    public interface OnLongClickListener {
        void onLongItemClick(Carrito carritos, int position);
    }
}
