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
}

$obj = new ControllerJson();
//$obj->createUsuarioController("James", "metallica", "Cliente");
//$obj->readUsuariosController();
//$obj->updateUsuarioController(4, "James Hetfield", "metallica", "cliente");
//$obj->deleteUsuarioController(4);