package owl.app.catalogo.utils;

import android.app.Application;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;
import owl.app.catalogo.models.Carrito;
import owl.app.catalogo.models.Deseos;

public class MyApp extends Application {

    //esta clase es especial, al extender de application se ejecutara antes que toda la aplicacion
    // esto nos ayudara a hacer nuestros ID uto incrementable

    //configuracion para el autoincrement
    // atomicInteger es un entero solo que tiene un metodo implementado que es capaz de devolverlo
    public static AtomicInteger CarritoID = new AtomicInteger();
    public static AtomicInteger DeseoID = new AtomicInteger();

    @Override
    public void onCreate() {
        setUpRealmConfig();

        //configuracion para el autoincrement
        Realm realm = Realm.getDefaultInstance();
        CarritoID = getIdByTable(realm, Carrito.class);
        DeseoID = getIdByTable(realm, Deseos.class);
        realm.close();

        super.onCreate();
    }

    //configuración de la base de datos con realm
    private void setUpRealmConfig() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
    }

    //configuracion con clase anonima para los ids: configuracion para el autoincrement
    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass) {
        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0) ? new AtomicInteger(results.max("id").intValue()) : new AtomicInteger();
    }

}
