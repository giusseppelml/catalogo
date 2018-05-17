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

}