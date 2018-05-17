package owl.app.catalogo.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import owl.app.catalogo.R;
import owl.app.catalogo.adapters.CarritoAdapter;
import owl.app.catalogo.api.Api;
import owl.app.catalogo.api.RequestHandler;
import owl.app.catalogo.models.Carrito;
import owl.app.catalogo.utils.SharedPrefManager;

public class CarritoActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<Carrito>>, View.OnClickListener{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Toolbar myToolbar;

    private Realm realm;
    private RealmResults<Carrito> carritoList;

    private FloatingActionButton fab;

    private MenuItem loginMenuItem;
    private MenuItem signInMenuItem;
    private MenuItem perfil;
    private MenuItem salir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        setToolbar();

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerViewCarrito);
        fab = (FloatingActionButton)findViewById(R.id.fabCarrito);

        if(SharedPrefManager.getInstance(CarritoActivity.this).isLoggedIn()){
            fab.setImageResource(R.mipmap.ic_venta_on);
        }else{
            fab.setImageResource(R.mipmap.ic_venta_off);
        }

        fab.setOnClickListener(this);
        setHideShowFAB();

        realm = Realm.getDefaultInstance();
        carritoList = realm.where(Carrito.class).findAll();
        carritoList.addChangeListener(this);


        // crear el adaptador y configurarlo en la vista de lista
        mLayoutManager = new LinearLayoutManager(this);

        mAdapter = new CarritoAdapter(carritoList, R.layout.card_view_carrito, new CarritoAdapter.OnClickListener() {
            @Override
            public void onItemClick(Carrito carrito, int position) {

                Toast.makeText(CarritoActivity.this, "id: " + carrito.getId()
                        + "\nTitulo: " + carrito.getProducto()
                        + "\n imagen: " + carrito.getImagen()
                        + "\n costo: " + carrito.getCosto(), Toast.LENGTH_LONG).show();

            }
        }, new CarritoAdapter.OnLongClickListener() {
            @Override
            public void onLongItemClick(final Carrito carrito, final int position) {

                final String nombre = carrito.getProducto();

                Snackbar.make(findViewById(R.id.carritoActivityID), "¿Quiere eliminar el producto: " + nombre + " de tu carrito?",
                        Snackbar.LENGTH_LONG).setAction("ELIMINAR", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        realm.beginTransaction();
                        carrito.deleteFromRealm();
                        realm.commitTransaction();

                        Snackbar.make(findViewById(R.id.carritoActivityID),"Producto: " + nombre + " retirado correctamente!!!",
                                Snackbar.LENGTH_SHORT).show();
                    }}).setActionTextColor(getResources().getColor(R.color.colorPrimaryDark))
                        .show();
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void setToolbar() {
        myToolbar = (Toolbar) findViewById(R.id.toolbarCarrito);
        setSupportActionBar(myToolbar);
        //getSupportActionBar().setTitle(getString(R.string.app_name));
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private String costoTotal(){
        double total = 0;
        for (int position = 0; position < carritoList.size() ; position++) {
            total += carritoList.get(position).getCosto();
        }

        String respuesta = String.valueOf(total);
        return respuesta;
    }

    private void showInfoAlertDelete() {
        new AlertDialog.Builder(CarritoActivity.this)
                .setTitle("Eliminar todo el carrito?")
                .setMessage("Todos los productos que fueron agregado serán eliminados y no podras deshacer esta acción, ¿Quieres continuar?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        realm.beginTransaction();
                        realm.deleteAll();
                        realm.commitTransaction();
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void showInfoAlertComprarAll() {
        new AlertDialog.Builder(CarritoActivity.this)
                .setTitle("Todos los productos serán comprados!!!")
                .setMessage("el costo será de: $"+ costoTotal() + " pesos, Quieres Continuar?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        for (int position = 0; position < carritoList.size() ; position++) {
                            createVenta(carritoList.get(position).getProducto(),
                                    carritoList.get(position).getImagen(),
                                    carritoList.get(position).getCosto());
                        }

                        Toast.makeText(CarritoActivity.this, "Compra Realizada", Toast.LENGTH_LONG).show();

                        realm.beginTransaction();
                        realm.deleteAll();
                        realm.commitTransaction();
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void setHideShowFAB() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    fab.hide();
                else if (dy < 0)
                    fab.show();
            }
        });
    }

    private void createVenta(String productos, String imagenes, double costos) {
        String usurioConversion = String.valueOf(SharedPrefManager.getInstance(CarritoActivity.this).getUser().getId());
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

    //refrescar el adaptador a la hora de crar, modificar, eliminar información en Realm DataBase
    @Override
    public void onChange(RealmResults<Carrito> carritos) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu_carrito, menu);

        this.loginMenuItem = menu.findItem(R.id.loginActionBarCarrito);
        this.signInMenuItem = menu.findItem(R.id.signInActionBarCarrito);
        this.perfil = menu.findItem(R.id.perfilActionBarCarrito);
        this.salir = menu.findItem(R.id.logOutActionBarCarrito);

        if(SharedPrefManager.getInstance(CarritoActivity.this).isLoggedIn()){
            this.loginMenuItem.setVisible(false);
            this.signInMenuItem.setVisible(false);
            this.perfil.setVisible(true);
            this.salir.setVisible(true);
        }else{
            this.loginMenuItem.setVisible(true);
            this.signInMenuItem.setVisible(true);
            this.perfil.setVisible(false);
            this.salir.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.loginActionBarCarrito:
                startActivity(new Intent(CarritoActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                return true;
            case R.id.signInActionBarCarrito:
                startActivity(new Intent(CarritoActivity.this, RegistrarseActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.zoom_back_out);
                return true;
            case R.id.perfilActionBarCarrito:
                startActivity(new Intent(CarritoActivity.this, PerfilActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.zoom_back_out);
                return true;
            case R.id.logOutActionBarCarrito:
                SharedPrefManager.getInstance(CarritoActivity.this).logout();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                return true;
            case R.id.allDeletecarritoActionBar:
                showInfoAlertDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        if(SharedPrefManager.getInstance(CarritoActivity.this).isLoggedIn()
                && SharedPrefManager.getInstance(CarritoActivity.this).getUser().getRole().equals("cliente")
                && carritoList.size() > 0){
            showInfoAlertComprarAll();
        }else{
            Toast.makeText(CarritoActivity.this, "logueate", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CarritoActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        //super.onBackPressed();
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
