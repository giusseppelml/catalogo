package owl.app.catalogo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import owl.app.catalogo.R;
import owl.app.catalogo.models.Categorias;

public class CategoriasAdapter extends RecyclerView.Adapter<CategoriasAdapter.ViewHolder> {
    private List<Categorias> categorias;
    private int layout;
    private OnClickListener listener;
    private OnLongClickListener listenerLong;

    private Context context;

    public CategoriasAdapter(List<Categorias> categorias, int layout, OnClickListener listener, OnLongClickListener listenerLong) {
        this.categorias = categorias;
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
        holder.bind(categorias.get(position), listener, listenerLong);
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titulo;

        public ViewHolder(View v) {
            super(v);
            titulo = (TextView) itemView.findViewById(R.id.textViewCategoria);
        }

        public void bind(final Categorias categorias, final OnClickListener listener, final OnLongClickListener listenerLong) {

            titulo.setText(categorias.getTitulo());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(categorias, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listenerLong.onLongItemClick(categorias, getAdapterPosition());

                    //en el escuchador setOnLonfClickListener es importante devolver true en lugar de false.
                    //si devolvemos falso el escuchador nos activara por defecto el onClickListener detras
                    //del onLongClickListener: este ultimo siempre tiene que devolver un true
                    return true;
                }
            });


        }
    }

    public interface OnClickListener {
        void onItemClick(Categorias categorias, int position);
    }

    public interface OnLongClickListener {
        void onLongItemClick(Categorias categorias, int position);
    }
}
