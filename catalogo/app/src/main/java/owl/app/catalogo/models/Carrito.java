package owl.app.catalogo.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import owl.app.catalogo.utils.MyApp;

public class Carrito extends RealmObject{

    //valores semanticos de base de datos a travez de anotaciones

    @PrimaryKey
    private int id;
    @Required
    private String producto;
    @Required
    private String imagen;
    private double costo;

    public Carrito(){}

    public Carrito(String producto, String imagen, double costo) {
        this.id = MyApp.CarritoID.incrementAndGet();
        this.producto = producto;
        this.imagen = imagen;
        this.costo = costo;
    }

    public int getId() {
        return id;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }
}
