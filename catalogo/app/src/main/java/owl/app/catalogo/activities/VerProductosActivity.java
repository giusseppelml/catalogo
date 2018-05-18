package owl.app.catalogo.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import owl.app.catalogo.R;
import owl.app.catalogo.api.Api;
import owl.app.catalogo.api.RequestHandler;
import owl.app.catalogo.models.Carrito;
import owl.app.catalogo.models.Deseos;
import owl.app.catalogo.utils.SharedPrefManager;

public class VerProductosActivity extends AppCompatActivity implements View.OnClickListener, RealmChangeListener<RealmResults<Deseos>> {

    private TextView titulo;
    private TextView descripcion;
    private TextView precio;
    private TextView contenido;

    private ImageView imagen;
    private ImageView imagenDeseo;

    private Button btnComprar;
    private Button btnCarrito;

    private int keyID;
    private String keyTitulo;
    private String keyImagen;
    private double keyCosto;

    private boolean verificarDeseo = false;

    //------------------------------------
    //aqui empieza el carrito con realm
    private Realm realm;
    private RealmResults<Deseos> deseosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_productos);

        //llamamos la configuracion por defecto de nuestra base de datos
        realm = Realm.getDefaultInstance();
        deseosList = realm.where(Deseos.class).findAll();
        deseosList.addChangeListener(this);

        //Tomar los datos del intent
        Bundle bundle = getIntent().getExtras();

        inicializaciones();

        keyID = bundle.getInt("id");
        keyTitulo = bundle.getString("titulo");
        keyImagen = bundle.getString("imagen");
        keyCosto = Double.valueOf(bundle.getString("precio"));

        titulo.setText(keyTitulo);
        descripcion.setText(bundle.getString("descripcion"));
        precio.setText(bundle.getString("precio"));
        contenido.setText(bundle.getString("contenido"));

        Picasso.get().load(Api.GALERIA+keyImagen).fit().into(imagen);

        btnComprar.setOnClickListener(this);
        btnCarrito.setOnClickListener(this);
        imagenDeseo.setOnClickListener(this);

        Picasso.get().load(R.drawable.deseo_apagado).into(imagenDeseo);

        for (int i = 0; i < deseosList.size() ; i++) {
            if(keyID == deseosList.get(i).getIdeWeb()){
                Picasso.get().load(R.drawable.deseo_activo).into(imagenDeseo);
                verificarDeseo = true;
                break;
            }
        }
    }

    public void inicializaciones(){
        titulo = (TextView)findViewById(R.id.textViewVerTitulo);
        descripcion = (TextView)findViewById(R.id.textViewVerDescripcion);
        precio = (TextView)findViewById(R.id.textViewVerPrecio);
        contenido = (TextView)findViewById(R.id.textViewVerContenid);

        imagen = (ImageView)findViewById(R.id.imageViewVerProducto);
        imagenDeseo = (ImageView)findViewById(R.id.imageViewDeseos);

        btnCarrito = (Button)findViewById(R.id.buttonCarrito);
        btnComprar = (Button)findViewById(R.id.buttonComprar);
    }

    private void showInfoAlertCompra() {
        new AlertDialog.Builder(VerProductosActivity.this)
                .setTitle("Estas a punto de realizar esta compra!!!")
                .setMessage("Si quieres comprar este producto haz click en OK?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        createVenta();
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void createVenta() {
        String usurioConversion = String.valueOf(SharedPrefManager.getInstance(VerProductosActivity.this).getUser().getId());
        String titulos = keyTitulo;
        String imagen = keyImagen;
        String costoConversion = String.valueOf(keyCosto);
        String fecha = "2020-04-24 00:00:00";


        //en params.put en el estring va la llave exactamente como lo exige el api en php
        HashMap<String, String> params = new HashMap<>();
        params.put("usuarios", usurioConversion);
        params.put("producto", titulos);
        params.put("imagen", imagen);
        params.put("costo", costoConversion);
        params.put("fecha", fecha);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_VENTAS, params, Api.CODE_POST_REQUEST);
        request.execute();

        Toast.makeText(VerProductosActivity.this, "Compra Realizada", Toast.LENGTH_LONG).show();
    }

    //metodo para agregar al carrito hecho con realm
    private void agregarProductoAlCarrito(String producto, String imagen, Double costo){

        realm.beginTransaction();
        Carrito carrito = new Carrito(producto, imagen, costo);
        realm.copyToRealm(carrito);
        realm.commitTransaction();

        Toast.makeText(VerProductosActivity.this, "producto agregado al carrito", Toast.LENGTH_SHORT).show();
    }

    //metodo para agregar a la lista de deseos hecho con realm
    private void agregarProductoADeseos(int id, String producto, String imagen, Double costo){

        realm.beginTransaction();
        Deseos deseos = new Deseos(producto, imagen, id, costo);
        realm.copyToRealm(deseos);
        realm.commitTransaction();

        Picasso.get().load(R.drawable.deseo_activo).into(imagenDeseo);
        Toast.makeText(VerProductosActivity.this, "producto agregado a tu lista de deseos", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(VerProductosActivity.this, MainActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_back_in, R.anim.left_out);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.buttonCarrito:
                if(SharedPrefManager.getInstance(VerProductosActivity.this).isLoggedIn()
                        && SharedPrefManager.getInstance(VerProductosActivity.this).getUser().getRole().equals("cliente")){
                    agregarProductoAlCarrito(keyTitulo, keyImagen, keyCosto);
                }else{
                    Toast.makeText(VerProductosActivity.this, "Estas como administrador", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.buttonComprar:
                if(SharedPrefManager.getInstance(VerProductosActivity.this).isLoggedIn()
                        && SharedPrefManager.getInstance(VerProductosActivity.this).getUser().getRole().equals("cliente")){
                    showInfoAlertCompra();
                }else{
                    Toast.makeText(VerProductosActivity.this, "Estas como administrador", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.imageViewDeseos:
                if(SharedPrefManager.getInstance(VerProductosActivity.this).isLoggedIn()
                        && SharedPrefManager.getInstance(VerProductosActivity.this).getUser().getRole().equals("cliente")){
                    if(verificarDeseo){
                        startActivity(new Intent(VerProductosActivity.this, PerfilActivity.class));
                        overridePendingTransition(R.anim.left_in, R.anim.zoom_back_out);
                    }else {
                        agregarProductoADeseos(keyID, keyTitulo, keyImagen, keyCosto);
                    }
                }else{
                    Toast.makeText(VerProductosActivity.this, "Estas como administrador", Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    @Override
    public void onChange(RealmResults<Deseos> deseos) {

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
