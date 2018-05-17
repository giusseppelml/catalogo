package owl.app.catalogo.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import owl.app.catalogo.R;
import owl.app.catalogo.api.Api;
import owl.app.catalogo.adapters.ProductosAdapter;
import owl.app.catalogo.api.RequestHandler;
import owl.app.catalogo.models.Carrito;
import owl.app.catalogo.models.Productos;
import owl.app.catalogo.utils.SharedPrefManager;

public class MainActivity extends AppCompatActivity {

    //usaremos esta lista para mostrar el héroe en listview
    List<Productos> productosList;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Toolbar myToolbar;

    private MenuItem loginMenuItem;
    private MenuItem signInMenuItem;
    private MenuItem carritoMenuItem;
    private MenuItem administrativoMenuItem;
    private MenuItem perfil;
    private MenuItem salir;

    //------------------------------------
    //aqui empieza el carrito con realm
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //documentacion
        //recyclerview: https://developer.android.com/guide/topics/ui/layout/recyclerview?hl=es-419#java
        //cardview: https://developer.android.com/guide/topics/ui/layout/cardview?hl=es-419
        //picasso: http://square.github.io/picasso/


        //codigo para prueba de logueo con shared Preferences
        /*int id = SharedPrefManager.getInstance(MainActivity.this).getUser().getId();
        String nombre = SharedPrefManager.getInstance(MainActivity.this).getUser().getUsuario();
        String password = SharedPrefManager.getInstance(MainActivity.this).getUser().getPassword();
        String role = SharedPrefManager.getInstance(MainActivity.this).getUser().getRole();


        Toast.makeText(MainActivity.this, "id: " + id
                +"\nnombre: " + nombre
                + "\npassword: " + password
                + "\nrol: " + role, Toast.LENGTH_LONG).show();*/


        //llamamos la configuracion por defecto de nuestra base de datos
        realm = Realm.getDefaultInstance();

        setToolbar();

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerViewProductos);
        productosList = new ArrayList<>();
        readProductos();
    }

    private void setToolbar() {
        myToolbar = (Toolbar) findViewById(R.id.toolbarProductos);
        setSupportActionBar(myToolbar);
       // getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void readProductos() {
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_PRODUCTOS, null, Api.CODE_GET_REQUEST);
        request.execute();
    }

    private void deleteUsuario(int id) {
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_DELETE_PRODUCTO + id, null, Api.CODE_GET_REQUEST);
        request.execute();
    }

    private void refreshContenidoList(JSONArray contenido) throws JSONException {
        // limpiar noticias anteriores
        productosList.clear();
        int i;

        // recorrer todos los elementos de la matriz json
        // el json que recibimos de la respuesta
        for (i = 0; i < contenido.length(); i++) {
            // obteniendo cada objeto noticia
            JSONObject obj = contenido.getJSONObject(i);

            // Añadiendo la noticia a la lista
            productosList.add(new Productos(
                    obj.getInt("id"),
                    obj.getString("titulo"),
                    obj.getString("descripcion"),
                    obj.getString("contenido"),
                    obj.getString("imagen"),
                    obj.getDouble("precio"),
                    obj.getDouble("calificacion"),
                    obj.getInt("categoria")
            ));



        }

        // crear el adaptador y configurarlo en la vista de lista
        mLayoutManager = new LinearLayoutManager(this);

        mAdapter = new ProductosAdapter(productosList, R.layout.card_view_productos, new ProductosAdapter.OnClickListener() {
            @Override
            public void onItemClick(Productos productos, int position) {

                String precio = String.valueOf(productos.getPrecio());
                String calificacion = String.valueOf(productos.getCalificacion());

                Intent intent = new Intent(MainActivity.this, VerProductosActivity.class);
                intent.putExtra("id", productos.getId());
                intent.putExtra("titulo", productos.getTitulo());
                intent.putExtra("descripcion", productos.getDescripcion());
                intent.putExtra("precio", precio);
                intent.putExtra("contenido", productos.getContenido());
                intent.putExtra("imagen", productos.getImagen());
                startActivity(intent);
                overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
            }
        }, new ProductosAdapter.OnLongClickListener() {
            @Override
            public void onLongItemClick(final Productos productos, int position) {

                if(SharedPrefManager.getInstance(MainActivity.this).isLoggedIn() &&
                        SharedPrefManager.getInstance(MainActivity.this).getUser().getRole().equals("administrador")){

                    Snackbar.make(findViewById(R.id.mainActivityID), "¿Quiere eliminar el producto: " + productos.getTitulo() + " ?",
                            Snackbar.LENGTH_LONG).setAction("ELIMINAR", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteUsuario(productos.getId());
                            Snackbar.make(findViewById(R.id.mainActivityID),"Producto: " + productos.getTitulo() + " eliminado correctamente!!!",
                                    Snackbar.LENGTH_SHORT).show();
                        }}).setActionTextColor(getResources().getColor(R.color.colorPrimaryDark))
                            .show();

                }else{
                    agregarProductoAlCarrito(productos.getTitulo(), productos.getImagen(), productos.getPrecio());
                }
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    //metodo para agregar al carrito hecho con realm
    private void agregarProductoAlCarrito(final String producto, final String imagen, final Double costo){

        //realm.beginTransaction();
        //Carrito carrito = new Carrito(producto, imagen, costo);
        //realm.copyToRealm(carrito);
        //realm.commitTransaction();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Carrito carrito = new Carrito(producto, imagen, costo);
                realm.copyToRealm(carrito);
            }
        });

        Toast.makeText(MainActivity.this, "producto agregado al carrito", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);

        this.loginMenuItem = menu.findItem(R.id.loginActionBar);
        this.signInMenuItem = menu.findItem(R.id.signInActionBar);
        this.carritoMenuItem = menu.findItem(R.id.carritoActionBar);
        this.perfil = menu.findItem(R.id.perfilActionBar);
        this.administrativoMenuItem = menu.findItem(R.id.administrativoActionBar);
        this.salir = menu.findItem(R.id.logOutActionBar);

        if(SharedPrefManager.getInstance(MainActivity.this).isLoggedIn()){

            if(SharedPrefManager.getInstance(MainActivity.this).getUser().getRole().equals("administrador")){

                this.loginMenuItem.setVisible(false);
                this.signInMenuItem.setVisible(false);
                this.perfil.setVisible(false);
                this.administrativoMenuItem.setVisible(true);
                this.salir.setVisible(true);
                this.carritoMenuItem.setVisible(false);
            }else{
                this.loginMenuItem.setVisible(false);
                this.signInMenuItem.setVisible(false);
                this.carritoMenuItem.setVisible(true);
                this.perfil.setVisible(true);
                this.administrativoMenuItem.setVisible(false);
                this.salir.setVisible(true);
            }

        }else{
            this.loginMenuItem.setVisible(true);
            this.signInMenuItem.setVisible(true);
            carritoMenuItem.setVisible(true);
            this.perfil.setVisible(false);
            this.administrativoMenuItem.setVisible(false);
            this.salir.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
                case R.id.loginActionBar:
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    return true;
            case R.id.signInActionBar:
                    startActivity(new Intent(MainActivity.this, RegistrarseActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    return true;
            case R.id.administrativoActionBar:
                startActivity(new Intent(MainActivity.this, AdministradorActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.zoom_back_out);
                return true;
            case R.id.perfilActionBar:
                startActivity(new Intent(MainActivity.this, PerfilActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.zoom_back_out);
                return true;
            case R.id.logOutActionBar:
                SharedPrefManager.getInstance(MainActivity.this).logout();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                return true;
            case R.id.carritoActionBar:
                startActivity(new Intent(MainActivity.this, CarritoActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
