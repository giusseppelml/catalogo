package owl.app.catalogo.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import owl.app.catalogo.R;
import owl.app.catalogo.api.Api;
import owl.app.catalogo.api.RequestHandler;
import owl.app.catalogo.models.Carrito;
import owl.app.catalogo.models.Ventas;
import owl.app.catalogo.utils.SharedPrefManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {

    private TextView perfilNombre;
    private TextView contadorListaCarrito;
    private TextView contadorListaDeseos;
    private TextView montoTotalProductos;
    private TextView cantidadTotalProductos;

    private FloatingActionButton fabCarrito;
    private FloatingActionButton fabDeseos;

    private Realm realm;
    private RealmResults<Carrito> carritoList;

    private List<Ventas> ventasList;

    private View view;

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_perfil, container, false);

        inicializaciones();

        ventasList = new ArrayList<>();

        //Toast.makeText(getContext(), "cantidad de productos comprados: " + ventasList.size(), Toast.LENGTH_LONG).show();

        readVentas(SharedPrefManager.getInstance(getContext()).getUser().getId());

        perfilNombre.setText(SharedPrefManager.getInstance(getContext()).getUser().getUsuario());
        contadorListaCarrito.setText(contadorProductos());

        return view;
    }

    public void inicializaciones(){
        perfilNombre = (TextView)view.findViewById(R.id.textViewPerfilNombre);
        contadorListaCarrito = (TextView)view.findViewById(R.id.textViewPerfilCantidadCarrito);
        contadorListaDeseos = (TextView)view.findViewById(R.id.textViewPerfilCantidadDeseos);
        montoTotalProductos = (TextView)view.findViewById(R.id.textViewPerfilMontoProducto);
        cantidadTotalProductos = (TextView)view.findViewById(R.id.textViewPerfilCantidadProducto);

        realm = Realm.getDefaultInstance();
        carritoList = realm.where(Carrito.class).findAll();
    }

    public String contadorProductos(){
        String resultado = (carritoList.size() == 1) ? "producto":"productos";
        String texto = carritoList.size()+ " " + resultado;
        return texto;
    }

    public String contadorProductosVendidos(List<Ventas> ventas){
        String resultado = (ventasList.size() == 1) ? "producto":"productos";
        String texto = ventas.size()+ " " + resultado;
        return texto;
    }

    public String montoTotal(){

        double montoTotal = 0;
        for (int i = 0; i <ventasList.size() ; i++) {
            montoTotal += ventasList.get(i).getCosto();
        }

        String resultado = (ventasList.size() < 1) ? "0 pesos" : montoTotal + " Pesos";

        return resultado;
    }

    private void readVentas(int id) {
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_VENTAS_ESPECIFICAS + id, null, Api.CODE_GET_REQUEST);
        request.execute();
    }

    private void refreshContenidoList(JSONArray contenido) throws JSONException {
        // limpiar noticias anteriores
        ventasList.clear();
        int i;

        // recorrer todos los elementos de la matriz json
        // el json que recibimos de la respuesta
        for (i = 0; i < contenido.length(); i++) {
            // obteniendo cada objeto noticia
            JSONObject obj = contenido.getJSONObject(i);

            // Añadiendo la lista de ventas (importante) los parametros a pasar
            // tienen que ser iguales al nombnre de posicion que tienen en php en el arraylist
            ventasList.add(new Ventas(
                    obj.getInt("id"),
                    obj.getString("usuario"),
                    obj.getString("producto"),
                    obj.getString("imagen"),
                    obj.getDouble("costo"),
                    obj.getString("fecha")
            ));
        }

        cantidadTotalProductos.setText(contadorProductosVendidos(ventasList));
        montoTotalProductos.setText(montoTotal());

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
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
}
