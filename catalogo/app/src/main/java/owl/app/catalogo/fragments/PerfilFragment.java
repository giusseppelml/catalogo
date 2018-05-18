package owl.app.catalogo.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import owl.app.catalogo.models.Deseos;
import owl.app.catalogo.models.Ventas;
import owl.app.catalogo.utils.SharedPrefManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment implements View.OnClickListener{

    private TextView perfilNombre;
    private TextView contadorListaCarrito;
    private TextView contadorListaDeseos;
    private TextView montoTotalProductos;
    private TextView cantidadTotalProductos;

    private FloatingActionButton fabCarrito;
    private FloatingActionButton fabDeseos;

    private Realm realm;
    private RealmResults<Carrito> carritoList;
    private RealmResults<Deseos> deseosList;

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

        readVentas(SharedPrefManager.getInstance(getContext()).getUser().getId());

        perfilNombre.setText(SharedPrefManager.getInstance(getContext()).getUser().getUsuario());
        contadorListaCarrito.setText(contadorProductos(true));
        contadorListaDeseos.setText(contadorProductos(false));

        fabDeseos.setOnClickListener(this);
        fabCarrito.setOnClickListener(this);

        return view;
    }

    public void inicializaciones(){
        perfilNombre = (TextView)view.findViewById(R.id.textViewPerfilNombre);
        contadorListaCarrito = (TextView)view.findViewById(R.id.textViewPerfilCantidadCarrito);
        contadorListaDeseos = (TextView)view.findViewById(R.id.textViewPerfilCantidadDeseos);
        montoTotalProductos = (TextView)view.findViewById(R.id.textViewPerfilMontoProducto);
        cantidadTotalProductos = (TextView)view.findViewById(R.id.textViewPerfilCantidadProducto);

        fabCarrito = (FloatingActionButton)view.findViewById(R.id.fabPerfilCarrito);
        fabDeseos = (FloatingActionButton)view.findViewById(R.id.fabPerfilDeseos);

        realm = Realm.getDefaultInstance();
        carritoList = realm.where(Carrito.class).findAll();
        deseosList = realm.where(Deseos.class).findAll();
    }

    public String contadorProductos(boolean verificar){
        if(verificar){
            String resultado = (carritoList.size() == 1) ? "producto":"productos";
            String texto = carritoList.size()+ " " + resultado;
            return texto;
        }else {
            String resultado = (deseosList.size() == 1) ? "producto":"productos";
            String texto = deseosList.size()+ " " + resultado;
            return texto;
        }
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
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_VENTAS_ESPECIFICAS + id,
                null, Api.CODE_GET_REQUEST);
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

    private String costoTotal(boolean verificar){
        double total = 0;
        if(verificar){
            for (int position = 0; position < carritoList.size() ; position++) {
                total += carritoList.get(position).getCosto();
            }
        }else{
            for (int position = 0; position < deseosList.size() ; position++) {
                total += deseosList.get(position).getCosto();
            }
        }

        String respuesta = String.valueOf(total);
        return respuesta;
    }

    private void showInfoAlertComprarAll(final boolean verificar) {
        new AlertDialog.Builder(getContext())
                .setTitle("Todos los productos serán comprados!!!")
                .setMessage("el costo será de: $"+ costoTotal(verificar) + " pesos, Quieres Continuar?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(verificar){
                            for (int position = 0; position < carritoList.size() ; position++) {
                                createVenta(carritoList.get(position).getProducto(),
                                        carritoList.get(position).getImagen(),
                                        carritoList.get(position).getCosto());
                            }
                        }else{
                            for (int position = 0; position < deseosList.size() ; position++) {
                                createVenta(deseosList.get(position).getProducto(),
                                        deseosList.get(position).getImagen(),
                                        deseosList.get(position).getCosto());
                            }
                        }


                        Toast.makeText(getContext(), "Compra Realizada", Toast.LENGTH_LONG).show();

                        if (verificar){
                            realm.beginTransaction();
                            carritoList.deleteAllFromRealm();
                            realm.commitTransaction();
                            contadorListaCarrito.setText("0 productos");
                        }else{
                            realm.beginTransaction();
                            deseosList.deleteAllFromRealm();
                            realm.commitTransaction();
                            contadorListaDeseos.setText("0 productos");
                        }

                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void createVenta(String productos, String imagenes, double costos) {
        String usurioConversion = String.valueOf(SharedPrefManager.getInstance(getContext()).getUser().getId());
        String titulos = productos;
        String imagen = imagenes;
        String precios = String.valueOf(costos);
        String fecha = "2020-04-24 00:00:00";


        //en params.put en el estring va la llave exactamente como lo exige el api en php
        HashMap<String, String> params = new HashMap<>();
        params.put("usuarios", usurioConversion);
        params.put("producto", titulos);
        params.put("imagen", imagen);
        params.put("costo", precios);
        params.put("fecha", fecha);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_VENTAS, params, Api.CODE_POST_REQUEST);
        request.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabPerfilCarrito:
                if (carritoList.isEmpty())
                    Toast.makeText(getContext(), "no hay nada en tu carrito", Toast.LENGTH_SHORT).show();
                else
                    showInfoAlertComprarAll(true);
                break;
            case R.id.fabPerfilDeseos:
                if (deseosList.isEmpty())
                    Toast.makeText(getContext(), "no hay nada en tu lista de deseos", Toast.LENGTH_SHORT).show();
                else
                    showInfoAlertComprarAll(false);
                break;

        }
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
