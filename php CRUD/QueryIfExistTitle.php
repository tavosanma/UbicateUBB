<?php 

$title = filter_input(INPUT_POST, "title");


$mysqli = new mysqli("localhost","id7933426_tavo","hangar18","id7933426_ubicateubb");

$result = mysqli_query($mysqli,"select * from coordinates where title = '".$title."'");


if ($data = mysqli_fetch_array($result)) {
	echo '1';
	
}
 ?>