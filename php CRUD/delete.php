<?PHP
$hostname_localhost="localhost";
$database_localhost="id7933426_ubicateubb";
$username_localhost="id7933426_tavo";
$password_localhost="hangar18";

if(isset($_GET["title"])){
	$title=$_GET["title"];


	$conexion = mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);

	$sql="DELETE FROM coordinates WHERE title= ? ";
	$stm=$conexion->prepare($sql);
	$stm->bind_param('s',$title);

	if($stm->execute()){
		echo "elimina";
	}else{
		echo "noElimina";
	}
	mysqli_close($conexion);
}else{
	echo "noExiste";
}
?>