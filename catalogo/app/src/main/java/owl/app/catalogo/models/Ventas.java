package owl.app.catalogo.models;

public class Ventas {

    private int id;
    private String usuario;
    private String producto;
    private String ruta;
    private double costo;
    private String fecha;

    public Ventas(int id, String usuario, String producto, String ruta, double costo, String fecha) {
        setId(id);
        setUsuario(usuario);
        setProducto(producto);
        setRuta(ruta);
        setCosto(costo);
        setFecha(fecha);
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getProducto() {
        return producto;
    }

    private void setProducto(String producto) {
        this.producto = producto;
    }

    public String getRuta() {
        return ruta;
    }

    private void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public double getCosto() {
        return costo;
    }

    private void setCosto(double costo) {
        this.costo = costo;
    }

    public String getFecha() {
        return fecha;
    }

    private void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
