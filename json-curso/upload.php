<?php
if($_SERVER['REQUEST_METHOD']=='POST'){

$titulo = $_POST['titulo'];
$descripcion = $_POST['descripcion'];
$contenido = $_POST['contenido'];
$imagen = $_POST['imagen'];
$precio= $_POST['precio'];
$calificacion = $_POST['calificacion'];
$categoria = $_POST['categoria'];
$verificar = $_POST['verificar'];

require_once('dbConnect.php');
$sql ="SELECT id FROM productos ORDER BY id ASC";
$res = mysqli_query($con,$sql);
$id = 0;
while($row = mysqli_fetch_array($res)){
$id = $row['id'];
$lml = $id + 1;
}
$path = "galeria/$lml.png";
$actualpath = "http://192.168.1.77/json-curso/$path";

if($verificar == "true"){
	$sql = "INSERT INTO productos (titulo, descripcion, contenido, imagen, precio, calificacion, categoria)
VALUES ('$titulo', '$descripcion', '$contenido', '$actualpath', '$precio', '$calificacion', '$categoria')";

if(mysqli_query($con,$sql)){
file_put_contents($path,base64_decode($imagen));
echo "Cobro registrado e imagen subida Correctamente";
}
mysqli_close($con);
}else{
echo "Error";
}
}else{
	echo "no puedes hacer el proceso 2 veces";
}