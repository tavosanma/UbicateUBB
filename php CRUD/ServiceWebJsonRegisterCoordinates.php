<?PHP
$hostname_localhost="localhost";
$database_localhost="id7933426_ubicateubb";
$username_localhost="id7933426_tavo";
$password_localhost="hangar18";

$json=array();
 if(isset($_GET["title"]) && isset($_GET["snippet"]) && isset($_GET["information"])&& isset($_GET["link"])&& isset($_GET["informationtwo"]) && isset($_GET["latitude"]) && isset($_GET["longitude"])){
  $title=$_GET['title'];
  $snippet=$_GET['snippet'];
  $latitude=$_GET['latitude'];
  $longitude=$_GET['longitude'];
  $information=$_GET['information'];
  $informationtwo=$_GET['informationtwo'];
  $link=$_GET['link'];

  $conexion = new mysqli($hostname_localhost, $username_localhost, $password_localhost, $database_localhost);

 
  
  
  
  $insert="INSERT INTO coordinates(title, snippet, latitude, longitude, information, informationtwo,link) VALUES ('{$title}','{$snippet}','{$latitude}','{$longitude}','{$information}','{$informationtwo}','{$link}')";
  
  
 
  if($conexion->query($insert)===TRUE){
   
   
   $resultado = $conexion->query("SELECT * FROM coordinates WHERE title = '{$title}'");
   
  
   if($registro=mysqli_fetch_array($resultado)){
    $json['coordinates'][]=$registro;
   }
   mysqli_close($conexion);
   echo json_encode($json);
   
  }else{
   $resulta["title"]="NO registra";
   $resulta["snippet"]="NO registra";
   $resulta["latitude"]=0;
   $resulta["longitude"]=0;
   $resulta["information"]="NO registra";
   $resulta["informationtwo"]="NO registra";
   $resulta["link"]="NO registra";
   $json['coordinates'][]=$resulta;
   echo json_encode($json);
  }
 }else{
  $resulta["title"]="NO registra";
   $resulta["snippet"]="NO registra";
   $resulta["latitude"]=0;
   $resulta["longitude"]=0;
   $resulta["information"]="NO registra";
   $resulta["informationtwo"]="NO registra";
   $resulta["link"]="NO registra";
  $json['coordinates'][]=$resulta;
  echo json_encode($json);
 }
?>﻿