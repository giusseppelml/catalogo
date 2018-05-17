<?php

require_once "ModeloJson.php";

class ControllerJson
{

    #REGISTRO DE USUARIOS
    #------------------------------------
    public function createUsuarioController($usuario, $password, $role)
    {
        $datosController = array("usuario" => $usuario,
            "password"                         => $password,
            "role"                            => $role);

        $respuesta = Datos::createUsuarioModel($datosController, "usuarios");
        return $respuesta;
    }

    #VISTA DE USUARIOS
    #------------------------------------

    public function readUsuariosController()
    {

        $respuesta = Datos::readUsuariosModel("usuarios");
        return $respuesta;
    }

    #ACTUALIZAR USUARIO
    #------------------------------------
    public function updateUsuarioController($id, $usuario, $password, $role)
    {

        $datosController = array("id" => $id,
            "usuario"                     => $usuario,
            "password"                    => $password,
            "role"                       => $role);

        $respuesta = Datos::updateUsuarioModel($datosController, "usuarios");
        return $respuesta;

    }

    #BORRAR USUARIO
    #------------------------------------
    public function deleteUsuarioController($id)
    {
        $respuesta = Datos::deleteUsuarioModel($id, "usuarios");
        return $respuesta;
    }

    //-----------------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------
    #CATEGORIAS

    #REGISTRO DE CATEGORIA
    #------------------------------------
    public function createCategoriaController($titulo)
    {
        $respuesta = Datos::createCategoriaModel($titulo, "categorias");
        return $respuesta;
    }

    #VISTA DE CATEGORIAS
    #------------------------------------

    public function readCategoriasController()
    {

        $respuesta = Datos::readCategoriasModel("categorias");
        return $respuesta;
    }

    #ACTUALIZAR CATEGORIA
    #------------------------------------
    public function updateCategoriaController($id, $titulo)
    {

        $datosController = array("id" => $id,
            "titulo" => $titulo);

        $respuesta = Datos::updateCategoriaModel($datosController, "categorias");
        return $respuesta;

    }

    #BORRAR CATEGORIA
    #------------------------------------
    public function deleteCategoriaController($id)
    {
        $respuesta = Datos::deleteCategoriaModel($id, "categorias");
        return $respuesta;
    }

    //-----------------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------
    #VENTAS

    #REGISTRO DE VENTA
    #------------------------------------
    public function createVentaController($usuario, $producto, $imagen, $costo, $fecha)
    {
        $datosController = array("usuario" => $usuario,
            "producto" => $producto,
            "imagen" => $imagen,
            "costo" => $costo,
            "fecha" => $fecha);

        $respuesta = Datos::createVentaModel($datosController, "ventas");
        return $respuesta;
    }

    #VISTA DE VENTAS
    #------------------------------------
    public function readVentasController()
    {

        $respuesta = Datos::readVentasModel("ventas");
        return $respuesta;
    }

    #VISTA DE VENTA ESPECIFICA
    #------------------------------------
    public function readVentaEspecificaController($usuario)
    {

        $respuesta = Datos::readVentaEspecificaModel($usuario, "ventas");
        return $respuesta;
    }

    #BORRAR VENTA
    #------------------------------------
    public function deleteVentaController($id)
    {
        $respuesta = Datos::deleteVentasModel($id, "ventas");
        return $respuesta;
    }

    //-----------------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------
    #PRODUCTOS

     #REGISTRO DE PRODUCTOS
    #------------------------------------
    public function createProductoController($titulo, $descripcion, $contenido, $imagen, $precio, $calificacion, $categoria)
    {
        $datosController = array("titulo" => $titulo,
            "descripcion" => $descripcion,
            "contenido" => $contenido,
            "imagen" => $imagen,
            "precio" => $precio,
            "calificacion" => $calificacion,
            "categoria" => $categoria);

        $respuesta = Datos::createProductoModel($datosController, "productos");
        return $respuesta;
    }

    #VISTA DE PRODUCTOS
    #------------------------------------

    public function readProductosController()
    {

        $respuesta = Datos::readProductosModel("productos");
        return $respuesta;
    }

    #ACTUALIZAR PRODUCTOS
    #------------------------------------
    public function updateProductoController($id, $titulo, $descripcion, $contenido, $imagen, $precio, $calificacion, $categoria)
    {

        $datosController = array("id" => $id,
            "titulo" => $titulo,
            "descripcion" => $descripcion,
            "contenido" => $contenido,
            "imagen" => $imagen,
            "precio" => $precio,
            "calificacion" => $calificacion,
            "categoria" => $categoria);

        $respuesta = Datos::updateProductoModel($datosController, "productos");
        return $respuesta;

    }

    #BORRAR PRODUCTOS
    #------------------------------------
    public function deleteProductoController($id)
    {
        $respuesta = Datos::deleteProductoModel($id, "productos");
        return $respuesta;
    }

}