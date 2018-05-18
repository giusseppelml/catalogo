package owl.app.catalogo.fragments;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import owl.app.catalogo.R;
import owl.app.catalogo.adapters.DeseosAdapter;
import owl.app.catalogo.models.Carrito;
import owl.app.catalogo.models.Deseos;

public class DeseosFragment extends Fragment implements RealmChangeListener<RealmResults<Deseos>> {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Realm realm;
    private RealmResults<Deseos> deseosList;


    public DeseosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_deseos, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerViewDeseos);

        realm = Realm.getDefaultInstance();
        deseosList = realm.where(Deseos.class).findAll();
        deseosList.addChangeListener(this);


        // crear el adaptador y configurarlo en la vista de lista
        mLayoutManager = new LinearLayoutManager(getContext());

        mAdapter = new DeseosAdapter(deseosList, R.layout.card_view_deseos, new DeseosAdapter.OnClickListener() {
            @Override
            public void onItemClick(Deseos deseos, int position) {
                Toast.makeText(getActivity(), "id: " + deseos.getId()
                        + "\nTitulo: " + deseos.getProducto()
                        + "\n imagen: " + deseos.getImagen()
                        + "\n costo: " + deseos.getCosto(), Toast.LENGTH_LONG).show();
            }
        }, new DeseosAdapter.OnLongClickListener() {
            @Override
            public void onLongItemClick(final Deseos deseos, int position) {
                final String nombre = deseos.getProducto();

                Snackbar.make(getView(), "¿Quiere eliminar el producto: " + nombre + " de tu carrito?",
                        Snackbar.LENGTH_LONG).setAction("ELIMINAR", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        realm.beginTransaction();
                        deseos.deleteFromRealm();
                        realm.commitTransaction();

                        Snackbar.make(getView(),"Producto: " + nombre + " retirado correctamente!!!",
                                Snackbar.LENGTH_SHORT).show();
                    }}).setActionTextColor(getResources().getColor(R.color.colorPrimaryDark))
                        .show();
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }


    @Override
    public void onChange(RealmResults<Deseos> deseos) {
        mAdapter.notifyDataSetChanged();
    }
}
