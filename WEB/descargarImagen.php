<?php
$servername = "localhost";
$username = "Xhrobles002";
$password = "f0USH4tjm9";
$dbname = "Xhrobles002_PiPaTi";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$nomUser = $_POST["username"];

$sql = "SELECT * FROM users WHERE nomUser='$nomUser'";

$result = mysqli_query($conn, $sql);

if (mysqli_num_rows($result) > 0) {
    $datos = array();
    $cont = 0;
            
    while ($fila = mysqli_fetch_row($result)){
        $foto_base64 = base64_encode($fila[4]);
        
        $datos[$cont] = array(
            "_id" => $fila[0],
            "nomUser" => $fila[1],
            "pass" => $fila[2],
            "imagen" => $fila[3],
            "DEVICEID" => $fila[4]
        );
        $cont++;
    }
        
    $json = json_encode($datos);

    echo $json;
} else{
    echo "fail";
}

$conn->close();
?>