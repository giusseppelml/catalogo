package owl.app.catalogo.models;

public class Categorias {

    private int id;
    private String titulo;

    public Categorias(int id, String titulo) {
        setId(id);
        setTitulo(titulo);
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    private void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
