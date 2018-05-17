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
import owl.app.catalogo.models.Productos;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder> {
    private List<Productos> productos;
    private int layout;
    private OnClickListener listener;
    private OnLongClickListener listenerLong;

    private Context context;

    public ProductosAdapter(List<Productos> productos, int layout, OnClickListener listener, OnLongClickListener listenerLong) {
        this.productos = productos;
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
        holder.bind(productos.get(position), listener, listenerLong);
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titulo;
        private TextView descipcion;
        private TextView precio;
        private TextView calificacion;
        private ImageView imagen;

        public ViewHolder(View v) {
            super(v);
            titulo = (TextView) itemView.findViewById(R.id.textViewTitulo);
            descipcion = (TextView) itemView.findViewById(R.id.textViewDescripcion);
            precio = (TextView) itemView.findViewById(R.id.textViewPrecio);
            calificacion = (TextView)itemView.findViewById(R.id.textViewCalificacion);
            imagen = (ImageView)itemView.findViewById(R.id.imageViewProducto);
        }

        public void bind(final Productos productos, final OnClickListener listener, final OnLongClickListener listenerLong) {

            titulo.setText(productos.getTitulo());
            descipcion.setText(productos.getDescripcion());

            String precioConvecion = String.valueOf(productos.getPrecio());
            String calificacionConvercion = String.valueOf(productos.getCalificacion());

            precio.setText(precioConvecion);
            calificacion.setText(calificacionConvercion);

            Picasso.get().load(Api.GALERIA+productos.getImagen()).fit().into(imagen);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(productos, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listenerLong.onLongItemClick(productos, getAdapterPosition());
                    /*en el escuchador setOnLonfClickListener es importante devolver true en lugar de false.
                    si devolvemos falso el escuchador nos activara por defecto el onClickListener detras
                    del onLongClickListener: este ultimo siempre tiene que devolver un true*/
                    return true;
                }
            });


        }
    }

    public interface OnClickListener {
        void onItemClick(Productos productos, int position);
    }

    public interface OnLongClickListener {
        void onLongItemClick(Productos productos, int position);
    }
}