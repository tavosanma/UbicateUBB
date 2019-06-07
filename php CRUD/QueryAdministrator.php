<?php
header("Content-Type: text/html;charset=utf-8");
$hostname_localhost = "localhost";
$database_localhost = "id7933426_ubicateubb";
$username_localhost = "id7933426_tavo";
$password_localhost = "hangar18";

$json=array();

		$conexion = mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);
		$conexion->set_charset("utf8"); //words accented

		$consulta = "select * from administrator";
		$resultado = mysqli_query($conexion,$consulta);

		while($registro=mysqli_fetch_array($resultado)){
		$json['administrator'][]=$registro;
		
		}
		mysqli_close($conexion);
		echo json_encode($json);	
		
?> 