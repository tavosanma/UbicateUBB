<?php 

$username = filter_input(INPUT_POST, "username");
$password = filter_input(INPUT_POST, "password");

$mysqli = new mysqli("localhost","id7933426_tavo","hangar18","id7933426_ubicateubb");

$result = mysqli_query($mysqli,"select * from administrator where username = '".$username."'");
$result2 = mysqli_query($mysqli,"select * from administrator where password = '".$password."'");

if ($data = mysqli_fetch_array($result)) {
	echo '1';
	
}
if ($data2 = mysqli_fetch_array($result2)) {
	echo '2';
	
}

 ?>