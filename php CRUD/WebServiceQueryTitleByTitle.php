<?php

$hostname_localhost = "localhost";
$database_localhost = "id7933426_ubicateubb";
$username_localhost = "id7933426_tavo";
$password_localhost = "hangar18";

$json=array();

		if(isset($_GET["title"])){
			$title=$_GET["title"];
		

		$conexion = mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);
		

		$consulta = "select information, informationtwo, link from coordinates where title= '{$title}'";
		$resultado = mysqli_query($conexion,$consulta);

		if($registro=mysqli_fetch_array($resultado)){
			$json['coordinates'][]=$registro;
		}else{
			$resultar["title"]='No registra';
			$json['coordinates'][]=$resultar;
		}

		
		mysqli_close($conexion);
		echo json_encode($json);	
	}
	else{
		$resultar["success"]=0;
		$resultar["message"]='Ws no Retorna';
		$json['coordinates'][]=$resultar;
		echo json_encode($json);
	}

		
?> 