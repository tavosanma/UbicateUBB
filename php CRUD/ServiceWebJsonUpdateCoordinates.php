<?PHP
$hostname_localhost="localhost";
$database_localhost="id7933426_ubicateubb";
$username_localhost="id7933426_tavo";
$password_localhost="hangar18";


	$conexion = mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);

	$title=$_POST["title"];
	$snippet=$_POST["snippet"];
	$latitude=$_POST["latitude"];
	$longitude=$_POST["longitude"];
	$information=$_POST["information"];
	$informationtwo=$_POST["informationtwo"];
	$link=$_POST["link"];
	

	$sql="UPDATE coordinates SET snippet=?, latitude=?, longitude=?, information=?, informationtwo=?, link=? WHERE title=?";
	$stm=$conexion->prepare($sql);
	$stm->bind_param('sssssss',$snippet,$latitude,$longitude,$information,$informationtwo,$link,$title);

	if($stm->execute()){
		echo "actualiza";
	}else{
		echo "noActualiza";
	}
	mysqli_close($conexion);

?>