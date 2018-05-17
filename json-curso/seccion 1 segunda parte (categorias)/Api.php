<?php

//obteniendo la clase ControllerJson
require_once 'ControllerJson.php';

// función validando todos los parámetros disponibles
// pasaremos los parámetros requeridos a esta función
function isTheseParametersAvailable($params)
{
    //suponiendo que todos los parámetros están disponibles
    $available     = true;
    $missingparams = "";

    foreach ($params as $param) {
        if (!isset($_POST[$param]) || strlen($_POST[$param]) <= 0) {
            $available     = false;
            $missingparams = $missingparams . ", " . $param;
        }
    }

    //si faltan parámetros
    if (!$available) {
        $response            = array();
        $response['error']   = true;
        $response['message'] = 'Parametro' . substr($missingparams, 1, strlen($missingparams)) . ' vacio';

        //error de visualización
        echo json_encode($response);

        //detener la ejecución adicional
        die();
    }
}

//una matriz para mostrar la respuesta
$response = array();

// si se trata de una llamada api
// que significa que un parámetro get llamado llamada api se establece en la URL
// y con este parámetro estamos concluyendo que es una llamada api
if (isset($_GET['apicall'])) {

    switch ($_GET['apicall']) {

        // la operación CREATE
        // si el valor de la llamada api es 'createContenido'
        // crearemos un registro en la base de datos
        case 'createusuario':
            #RUTA PARA HACER PRUEBA EN POSTMAN: http://localhost/json-curso/api.php?apicall=createusuario

            //primero verifique que los parámetros requeridos para esta solicitud estén disponibles o no
            isTheseParametersAvailable(array('usuarios', 'password', 'role'));

            //crear un nuevo objeto dboperation
            $db = new ControllerJson();

            //crear un nuevo registro en la base de datos
            $result = $db->createUsuarioController(
                $_POST['usuarios'],
                $_POST['password'],
                $_POST['role']
            );

            //si el registro se crea añadiendo, éxito a la respuesta
            if ($result) {
                //registro se crea significa que no hay error
                $response['error'] = false;

                //en mensaje tenemos un mensaje de éxito
                $response['message'] = 'usuario agregado correctamente!';

                //y estamos recibiendo todos los héroes de la base de datos en la respuesta
                //$response['contenido'] = $db->getContenido();
            } else {

                //si no se agrega registro que significa que hay un error
                $response['error'] = true;

                //y tenemos el mensaje de error
                $response['message'] = 'Ocurrio un error, intentar nuevamente';
            }

            break;

        // la operación READ
        // si la llamada es getcontenido
        case 'readusuarios':
            #RUTA PARA HACER PRUEBA EN POSTMAN: http://localhost/json-curso/Api.php?apicall=readusuarios
            $db                    = new ControllerJson();
            $response['error']     = false;
            $response['message']   = 'Solicitud completada correctamente';
            $response['contenido'] = $db->readUsuariosController();
            break;

        // la operación UPDATE
        case 'updateusuario':
            #RUTA PARA HACER PRUEBA EN POSTMAN: http://localhost/json-curso/api.php?apicall=updateusuario
            isTheseParametersAvailable(array('id', 'usuario', 'password', 'role'));
            $db     = new ControllerJson();
            $result = $db->updateUsuarioController(
                $_POST['id'],
                $_POST['usuario'],
                $_POST['password'],
                $_POST['role']
            );

            if ($result) {
                $response['error']     = false;
                $response['message']   = 'usuario modificado';
                //$response['contenido'] = $db->readUsuariosController();
            } else {
                $response['error']   = true;
                $response['message'] = 'Se produjo un error. Inténtalo de nuevo';
            }
            break;

        //la operación de delete
        case 'deleteusuario':
            #RUTA PARA HACER PRUEBA EN POSTMAN: http://localhost/json-curso/Api.php?apicall=deleteusuario&id=(ID QUE DESEAS ELIMINAR)

            //para la operación de borrado estamos obteniendo un parámetro GET de la url que tiene el id del registro que se va a eliminar
            if (isset($_GET['id']) && $_GET['id'] != "") {
                $db = new ControllerJson();
                if ($db->deleteUsuarioController($_GET['id'])) {
                    $response['error']     = false;
                    $response['message']   = 'usuario eliminado';
                    $response['contenido'] = $db->readUsuariosController();
                } else {
                    $response['error']   = true;
                    $response['message'] = 'Se produjo un error. Inténtalo de nuevo';
                }
            } else {
                $response['error']   = true;
                $response['message'] = 'Nada para eliminar, proporcione una identificación por favor';
            }
            break;

    //-----------------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------
    #CATEGORIAS

            case 'createcategoria':
            #RUTA PARA HACER PRUEBA EN POSTMAN: http://localhost/json-curso/api.php?apicall=createcategoria
            isTheseParametersAvailable(array('titulo'));
            
            $db = new ControllerJson();
            $result = $db->createCategoriaController($_POST['titulo']);

            if ($result) {
                $response['error'] = false;
                $response['message'] = 'categoria agregada correctamente!';
            } else {
                $response['error'] = true;
                $response['message'] = 'Ocurrio un error, intentar nuevamente';
            }

            break;

        // la operación READ
        case 'readcategorias':
            #RUTA PARA HACER PRUEBA EN POSTMAN: http://localhost/json-curso/Api.php?apicall=readcategorias
            $db                    = new ControllerJson();
            $response['error']     = false;
            $response['message']   = 'Solicitud completada correctamente';
            $response['contenido'] = $db->readCategoriasController();
            break;

        // la operación UPDATE
        case 'updatecategoria':
            #RUTA PARA HACER PRUEBA EN POSTMAN: http://localhost/json-curso/api.php?apicall=updatecategoria
            isTheseParametersAvailable(array('id', 'titulo'));
            $db     = new ControllerJson();
            $result = $db->updateCategoriaController(
                $_POST['id'],
                $_POST['titulo']
            );

            if ($result) {
                $response['error']     = false;
                $response['message']   = 'categoria modificada';
            } else {
                $response['error']   = true;
                $response['message'] = 'Se produjo un error. Inténtalo de nuevo';
            }
            break;

        //la operación DELETE
        case 'deletecategoria':
            #RUTA PARA HACER PRUEBA EN POSTMAN: http://localhost/json-curso/Api.php?apicall=deletecategoria&id=(ID QUE DESEAS ELIMINAR)
            if (isset($_GET['id']) && $_GET['id'] != "") {
                $db = new ControllerJson();
                if ($db->deleteCategoriaController($_GET['id'])) {
                    $response['error']     = false;
                    $response['message']   = 'categoria eliminada';
                } else {
                    $response['error']   = true;
                    $response['message'] = 'Se produjo un error. Inténtalo de nuevo';
                }
            } else {
                $response['error']   = true;
                $response['message'] = 'Nada para eliminar, proporcione una identificación por favor';
            }
            break;
    }

} else {
    // si no es api que se esta invocando
    // empujar los valores apropiados al array de respuesta
    $response['error']   = true;
    $response['message'] = 'Invalid API Call';
}

//mostrando la respuesta en la estructura json
echo json_encode($response);
