package owl.app.catalogo.api;

public class Api {

    private static final String URL_BASE = "http://192.168.1.69/json-curso/";
    private static final String ROOT_URL = URL_BASE + "Api.php?apicall=";

    //productos
    public static final String URL_READ_PRODUCTOS = ROOT_URL + "readproductos";
    public static final String URL_DELETE_PRODUCTO = ROOT_URL + "deleteproducto&id=";

    //usuarios
    public static final String URL_CREATE_USUARIO = ROOT_URL + "createusuario";
    public static final String URL_READ_USUARIOS = ROOT_URL + "readusuarios";
    public static final String URL_LOGIN_USUARIO = ROOT_URL + "loginusuario";

    //categorias
    public static final String URL_CREATE_CATEGORIA = ROOT_URL + "createcategoria";
    public static final String URL_READ_CATEGORIAS = ROOT_URL + "readcategorias";
    public static final String URL_UPDATE_CATEGORIA = ROOT_URL + "updatecategoria";
    public static final String URL_DELETE_CATEGORIA = ROOT_URL + "deletecategoria&id=";

    //ventas
    public static final String URL_CREATE_VENTAS = ROOT_URL + "createventa";
    public static final String URL_READ_VENTAS = ROOT_URL + "readventas";
    public static final String URL_READ_VENTAS_ESPECIFICAS = ROOT_URL + "readventaespecifica&usuario=";

    //galeria
    public static final String GALERIA = URL_BASE + "galeria/";

    //request codes
    public static final int CODE_GET_REQUEST = 1024;
    public static final int CODE_POST_REQUEST = 1025;
}
