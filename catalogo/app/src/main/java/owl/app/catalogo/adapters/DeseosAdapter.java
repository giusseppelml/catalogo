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
import owl.app.catalogo.models.Deseos;

public class DeseosAdapter extends RecyclerView.Adapter<DeseosAdapter.ViewHolder> {
    private List<Deseos> deseos;
    private int layout;
    private OnClickListener listener;
    private OnLongClickListener listenerLong;

    private Context context;

    public DeseosAdapter(List<Deseos> deseos, int layout, OnClickListener listener, OnLongClickListener listenerLong) {
        this.deseos = deseos;
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
        holder.bind(deseos.get(position), listener, listenerLong);
    }

    @Override
    public int getItemCount() {
        return deseos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titulo;
        private TextView precio;
        private ImageView imagen;

        public ViewHolder(View v) {
            super(v);
            titulo = (TextView) itemView.findViewById(R.id.textViewTituloDeseos);
            precio = (TextView) itemView.findViewById(R.id.textViewPrecioDeseos);
            imagen = (ImageView)itemView.findViewById(R.id.imageViewDeseos);
        }

        public void bind(final Deseos deseos, final OnClickListener listener, final OnLongClickListener listenerLong) {

            titulo.setText(deseos.getProducto());

            String precioConvecion = String.valueOf(deseos.getCosto());
            precio.setText(precioConvecion);

            Picasso.get().load(Api.GALERIA+deseos.getImagen()).fit().into(imagen);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(deseos, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listenerLong.onLongItemClick(deseos, getAdapterPosition());
                    /*en el escuchador setOnLonfClickListener es importante devolver true en lugar de false.
                    si devolvemos falso el escuchador nos activara por defecto el onClickListener detras
                    del onLongClickListener: este ultimo siempre tiene que devolver un true*/
                    return true;
                }
            });


        }
    }

    public interface OnClickListener {
        void onItemClick(Deseos deseos, int position);
    }

    public interface OnLongClickListener {
        void onLongItemClick(Deseos deseos, int position);
    }
}
