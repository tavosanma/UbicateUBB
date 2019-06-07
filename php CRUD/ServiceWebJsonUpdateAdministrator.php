<?PHP
$hostname_localhost="localhost";
$database_localhost="id7933426_ubicateubb";
$username_localhost="id7933426_tavo";
$password_localhost="hangar18";


	$conexion = mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);

	$id=$_POST["id"];
	$username=$_POST["username"];
	$password=$_POST["password"];
	

	$sql="UPDATE administrator SET username=?, password=? WHERE id=?";
	$stm=$conexion->prepare($sql);
	$stm->bind_param('sss',$username,$password,$id);

	if($stm->execute()){
		echo "actualiza";
	}else{
		echo "noActualiza";
	}
	mysqli_close($conexion);

?>