<?php

#EXTENSIÓN DE CLASES: Los objetos pueden ser extendidos, y pueden heredar propiedades y métodos. Para definir una clase como extensión, debo definir una clase padre, y se utiliza dentro de una clase hija.

require_once "Conexion.php";

class Datos extends Conexion
{

    #REGISTRO DE USUARIOS
    #-------------------------------------
    public function createUsuarioModel($datosModel, $tabla)
    {

        #prepare() Prepara una sentencia SQL para ser ejecutada por el método PDOStatement::execute(). La sentencia SQL puede contener cero o más marcadores de parámetros con nombre (:name) o signos de interrogación (?) por los cuales los valores reales serán sustituidos cuando la sentencia sea ejecutada. Ayuda a prevenir inyecciones SQL eliminando la necesidad de entrecomillar manualmente los parámetros.

        $stmt = Conexion::conectar()->prepare("INSERT INTO $tabla (usuario, password, role) VALUES (:usuario, :password, :role)");

        #bindParam() Vincula una variable de PHP a un parámetro de sustitución con nombre o de signo de interrogación correspondiente de la sentencia SQL que fue usada para preparar la sentencia.

        //si usamos estas lineas directas para agregar, retirar el parametro $datosModel del metodo
        /*$usuario = "Alexi Laiho";
        $password = "cob";
        $role = "administradores";

        $stmt->bindParam(":usuario", $usuario, PDO::PARAM_STR);
        $stmt->bindParam(":password", $password, PDO::PARAM_STR);
        $stmt->bindParam(":role", $role, PDO::PARAM_STR);*/

        //si usamos estas lineas para agregar, agregar el parametro $datosModel del metodo
        $stmt->bindParam(":usuario", $datosModel["usuario"], PDO::PARAM_STR);
        $stmt->bindParam(":password", $datosModel["password"], PDO::PARAM_STR);
        $stmt->bindParam(":role", $datosModel["role"], PDO::PARAM_STR);

        #execute() devuelve un resultado booleano, devolvera un true si la consulta se a ejecutado correctamente, de lo contrario devolvera un false

        if ($stmt->execute()) {
            //echo'Registro exitoso';
            return true;
        } else {
            //echo'Falló Registro';
            return false;
        }
    }

    public function readUsuariosModel($tabla)
    {

        $stmt = Conexion::conectar()->prepare("SELECT id, usuario, password, role FROM $tabla");
        $stmt->execute();

        #binColumn() El número (de primer índice 1) o el nombre de la columna del conjunto de resultados. Si se utiliza el nombre de la columna, tenga en cuenta que el nombre debería coincidir en mayúsculas/minúsculas con la columna, tal como la devuelve el controlador.

        $stmt->bindColumn("id", $id);
        $stmt->bindColumn("usuario", $usuario);
        $stmt->bindColumn("password", $password);
        $stmt->bindColumn("role", $role);

        $usuarios = array();

        /*echo'
        <table>
            <tr>
                <td><strong>id</strong></td>
                <td><strong>Nombre</strong></td>
                <td><strong>Password</strong></td>
                <td><strong>role</strong></td>
            </tr>
        ';*/

        while ($fila = $stmt->fetch(PDO::FETCH_BOUND)) {
            $user             = array();
            $user['id']       = utf8_encode($id);
            $user['usuario']  = utf8_encode($usuario);
            $user['password'] = utf8_encode($password);
            $user['role']    = utf8_encode($role);

            /*echo'
            <tr>
                <td>'.$user['id'] .'</td>
                <td>'.$user['usuario'] .'</td>
                <td>'.$user['password'].'</td>
                <td>'.$user['role'].'</td>
                </tr>
            ';*/

            array_push($usuarios, $user);
        }
        
        //echo'</table>';

        return $usuarios;

        $stmt->close();
    }

    #EDITAR USUARIO
    #-------------------------------------

    public function updateUsuarioModel($datosModel, $tabla)
    {

        $stmt = Conexion::conectar()->prepare("UPDATE $tabla SET usuario = :usuario, password = :password, role = :role WHERE id = :id");

        //si usamos estas lineas directas para agregar, retirar el parametro $datosModel del metodo
        /*$id = 3;
        $usuario = "Alexi Laiho";
        $password = "cob";
        $role = "administrador";

        $stmt->bindParam(":usuario", $usuario, PDO::PARAM_STR);
        $stmt->bindParam(":password", $password, PDO::PARAM_STR);
        $stmt->bindParam(":role", $role, PDO::PARAM_STR);
        $stmt->bindParam(":id", $id, PDO::PARAM_INT);*/

        //si usamos estas lineas para agregar, agregar el parametro $datosModel del metodo
        $stmt->bindParam(":usuario", $datosModel["usuario"], PDO::PARAM_STR);
        $stmt->bindParam(":password", $datosModel["password"], PDO::PARAM_STR);
        $stmt->bindParam(":role", $datosModel["role"], PDO::PARAM_STR);
        $stmt->bindParam(":id", $datosModel["id"], PDO::PARAM_INT);

        if ($stmt->execute()) {
            //echo'usuario: '.$datosModel["usuario"].' editado exitosamente';
            return true;
        } else {
            //echo'el usuario: '.$datosModel["usuario"].' no pudo ser editado';
            return false;
        }
    }

#BORRAR USUARIO
    #------------------------------------
    public function deleteUsuarioModel($id, $tabla)
    {

        $stmt = Conexion::conectar()->prepare("DELETE FROM $tabla WHERE id = :id");
        
        //si usamos estas lineas directas para agregar, retirar el parametro $datosModel del metodo
        /*$id = 5;
        $stmt->bindParam(":id", $id, PDO::PARAM_INT);*/

        //si usamos estas lineas para agregar, agregar el parametro $id en el metodo
        $stmt->bindParam(":id", $id, PDO::PARAM_INT);

        if ($stmt->execute()) {
            return true;
            //echo 'registro elmiminado';
        } else {
            return false;
            //echo 'hubo un error al intentar eliminar';
        }
        
    }

    //-----------------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------
    # CATEGORIAS

     #REGISTRO DE CATEGORIA
    #-------------------------------------
    public function createCategoriaModel($titulo, $tabla)
    {


        $stmt = Conexion::conectar()->prepare("INSERT INTO $tabla (titulo) VALUES (:titulo)");

        $stmt->bindParam(":titulo", $titulo, PDO::PARAM_STR);

        if ($stmt->execute()) {
            return true;
        } else {
            return false;
        }
    }

    #LECTURA DE CATEGORIAS
    #-------------------------------------
    public function readCategoriasModel($tabla)
    {

        $stmt = Conexion::conectar()->prepare("SELECT id, titulo FROM $tabla");
        $stmt->execute();

        $stmt->bindColumn("id", $id);
        $stmt->bindColumn("titulo", $titulo);

        $categorias = array();

        while ($fila = $stmt->fetch(PDO::FETCH_BOUND)) {
            $cat             = array();
            $cat['id']       = utf8_encode($id);
            $cat['titulo']  = utf8_encode($titulo);

            array_push($categorias, $cat);
        }

        return $categorias;

        $stmt->close();
    }

    #EDITAR CATEGORIA
    #-------------------------------------

    public function updateCategoriaModel($datosModel, $tabla)
    {

        $stmt = Conexion::conectar()->prepare("UPDATE $tabla SET titulo = :titulo WHERE id = :id");

        $stmt->bindParam(":titulo", $datosModel["titulo"], PDO::PARAM_STR);
        $stmt->bindParam(":id", $datosModel["id"], PDO::PARAM_INT);

        if ($stmt->execute()) {
            return true;
        } else {
            return false;
        }
    }

#BORRAR CATEGORIA
    #------------------------------------
    public function deleteCategoriaModel($id, $tabla)
    {

        $stmt = Conexion::conectar()->prepare("DELETE FROM $tabla WHERE id = :id");
        $stmt->bindParam(":id", $id, PDO::PARAM_INT);

        if ($stmt->execute()) {
            return true;
        } else {
            return false;
        }
        
    }

    //-----------------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------
    # VENTAS

     #REGISTRO DE VENTA
    #-------------------------------------$datosModel, 
    public function createVentaModel($datosModel, $tabla)
    {
        $stmt = Conexion::conectar()->prepare("INSERT INTO $tabla (usuario, producto, imagen, costo, fecha) VALUES (:usuario, :producto, :imagen, :costo, :fecha)");

        $stmt->bindParam(":usuario", $datosModel["usuario"], PDO::PARAM_INT);
        $stmt->bindParam(":producto", $datosModel["producto"], PDO::PARAM_STR);
        $stmt->bindParam(":imagen", $datosModel["imagen"], PDO::PARAM_STR);
        $stmt->bindParam(":costo", $datosModel["costo"], PDO::PARAM_INT);
        $stmt->bindParam(":fecha", $datosModel["fecha"], PDO::PARAM_STR);

        if ($stmt->execute()) {
            //echo'agregado';
            return true;
        } else {
            //echo 'ucurrio un error';
            return false;
        }
    }

    #LECTURA DE VENTAS
    #-------------------------------------
    public function readVentasModel($tabla)
    {

        $stmt = Conexion::conectar()->prepare("SELECT id, usuario, producto, imagen, costo, fecha FROM $tabla");
        $stmt->execute();

        $stmt->bindColumn("id", $id);
        $stmt->bindColumn("usuario", $usuario);
        $stmt->bindColumn("producto", $producto);
        $stmt->bindColumn("imagen", $imagen);
        $stmt->bindColumn("costo", $costo);
        $stmt->bindColumn("fecha", $fecha);

        $ventas = array();

        while ($fila = $stmt->fetch(PDO::FETCH_BOUND)) {
            $ven             = array();
            $ven['id']       = utf8_encode($id);
            $ven['usuario']  = utf8_encode($usuario);
            $ven['producto']  = utf8_encode($producto);
            $ven['imagen']  = utf8_encode($imagen);
            $ven['costo']  = utf8_encode($costo);
            $ven['fecha']  = utf8_encode($fecha);

            array_push($ventas, $ven);
        }

        return $ventas;

        $stmt->close();
    }

    #LECTURA DE VENTAS DE UN USUARIO EN ESPECIFICO
    #-------------------------------------
    public function readVentaEspecificaModel($usuario, $tabla)
    {

        $stmt = Conexion::conectar()->prepare("SELECT id, usuario, producto, imagen, costo, fecha FROM $tabla WHERE usuario = $usuario");
        $stmt->execute();

        $stmt->bindColumn("id", $id);
        $stmt->bindColumn("usuario", $usuario);
        $stmt->bindColumn("producto", $producto);
        $stmt->bindColumn("imagen", $imagen);
        $stmt->bindColumn("costo", $costo);
        $stmt->bindColumn("fecha", $fecha);

        $ventas = array();

        while ($fila = $stmt->fetch(PDO::FETCH_BOUND)) {
            $ven             = array();
            $ven['id']       = utf8_encode($id);
            $ven['usuario']  = utf8_encode($usuario);
            $ven['producto']  = utf8_encode($producto);
            $ven['imagen']  = utf8_encode($imagen);
            $ven['costo']  = utf8_encode($costo);
            $ven['fecha']  = utf8_encode($fecha);

            array_push($ventas, $ven);
        }

        return $ventas;

        $stmt->close();
    }

    #BORRAR CATEGORIA
    #------------------------------------
    public function deleteVentasModel($id, $tabla)
    {

        $stmt = Conexion::conectar()->prepare("DELETE FROM $tabla WHERE id = :id");
        $stmt->bindParam(":id", $id, PDO::PARAM_INT);

        if ($stmt->execute()) {
            return true;
        } else {
            return false;
        }
        
    }
}