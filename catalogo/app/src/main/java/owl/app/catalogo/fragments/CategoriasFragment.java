package owl.app.catalogo.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import owl.app.catalogo.R;
import owl.app.catalogo.adapters.CategoriasAdapter;
import owl.app.catalogo.api.Api;
import owl.app.catalogo.api.RequestHandler;
import owl.app.catalogo.models.Categorias;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriasFragment extends Fragment implements View.OnClickListener{

    //usaremos esta lista para mostrar el héroe en listview
    List<Categorias> categoriasList;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private EditText titulo;
    private Button boton;

    private boolean isUpdate = false;
    private int ide;

    public CategoriasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categorias, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerViewCategorias);
        titulo = (EditText)view.findViewById(R.id.editTextAddUpdateCategoria);
        boton = (Button)view.findViewById(R.id.buttonAddUpdateCategoria);
        boton.setOnClickListener(this);
        boton.setText(getString(R.string.agregar_label));

        categoriasList = new ArrayList<>();
        readCategorias();

        return view;
    }

    private void createCategoria() {
        String titulos = titulo.getText().toString().trim();


        if (TextUtils.isEmpty(titulos)) {
            titulo.setError(getString(R.string.Escribe_titulo_label));
            titulo.requestFocus();
            return;
        }

        //en params.put en el estring va la llave exactamente como lo exige el api en php
        HashMap<String, String> params = new HashMap<>();
        params.put("titulo", titulos);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_CATEGORIA, params, Api.CODE_POST_REQUEST);
        request.execute();

        Snackbar.make(getView(), "haz agragado categoria: " + titulos + " correctamente!!!", Snackbar.LENGTH_LONG).show();
    }

    private void readCategorias() {
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_CATEGORIAS, null, Api.CODE_GET_REQUEST);
        request.execute();
    }

    private void updateCategoria(int id) {
        String identificador = String.valueOf(id);
        String titulos = titulo.getText().toString().trim();


        if (TextUtils.isEmpty(titulos)) {
            titulo.setError(getString(R.string.Escribe_titulo_label));
            titulo.requestFocus();
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("id", identificador);
        params.put("titulo", titulos);


        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_UPDATE_CATEGORIA, params, Api.CODE_POST_REQUEST);
        request.execute();

        isUpdate = false;
        boton.setText(getString(R.string.agregar_label));
        ide = 0;
        titulo.setText("");
    }

    private void deleteCategorias(int id) {
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_DELETE_CATEGORIA + id, null, Api.CODE_GET_REQUEST);
        request.execute();
    }

    private void refreshContenidoList(JSONArray contenido) throws JSONException {
        // limpiar noticias anteriores
        categoriasList.clear();
        int i;

        // recorrer todos los elementos de la matriz json
        // el json que recibimos de la respuesta
        for (i = 0; i < contenido.length(); i++) {
            // obteniendo cada objeto noticia
            JSONObject obj = contenido.getJSONObject(i);

            // Añadiendo la noticia a la lista
            categoriasList.add(new Categorias(
                    obj.getInt("id"),
                    obj.getString("titulo")
            ));
        }

        // crear el adaptador y configurarlo en la vista de lista
        mLayoutManager = new LinearLayoutManager(getContext());



        mAdapter = new CategoriasAdapter(categoriasList, R.layout.list_view_categorias, new CategoriasAdapter.OnClickListener() {
            @Override
            public void onItemClick(Categorias categorias, int position) {

                boton.setText(getString(R.string.editar_label));
                ide = categorias.getId();
                isUpdate = true;
                titulo.setText(categorias.getTitulo());
            }
        }, new CategoriasAdapter.OnLongClickListener() {
            @Override
            public void onLongItemClick(final Categorias categorias, int position) {

                Snackbar.make(getView(), "¿Quiere eliminar la categoria: " + categorias.getTitulo() + "?",
                        Snackbar.LENGTH_LONG).setAction("ELIMINAR", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteCategorias(categorias.getId());
                        Snackbar.make(getView(), "categoria: " + categorias.getTitulo() + " eliminada correctamente!!!",
                                Snackbar.LENGTH_LONG).show();
                    }}).setActionTextColor(getResources().getColor(R.color.colorPrimaryDark))
                        .show();
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    // clase interna para realizar la solicitud de red extendiendo un AsyncTask
    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        // la url donde necesitamos enviar la solicitud
        String url;

        //the parameters
        HashMap<String, String> params;

        // el código de solicitud para definir si se trata de un GET o POST
        int requestCode;

        // constructor para inicializar valores
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        // este método dará la respuesta de la petición
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    // refrescar la lista después de cada operación
                    // para que obtengamos una lista actualizada
                    refreshContenidoList(object.getJSONArray("contenido"));
                    //Toast.makeText(getContext(), "se actualizo la lista", Toast.LENGTH_LONG).show();
                }else
                {
                    Toast.makeText(getContext(), "no se pudo actualizar", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "verifique que su json este devolviendo la lista actualizada",
                        Toast.LENGTH_SHORT).show();
            }
        }

        // la operación de red se realizará en segundo plano
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == Api.CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == Api.CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }

    @Override
    public void onClick(View v) {
        if (isUpdate) {
            updateCategoria(ide);
        } else {
            createCategoria();
        }
    }
}
