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
$pass = $_POST["pass"];

$sql = "INSERT INTO users (nomUser, pass) VALUES ('$nomUser', '$pass')";

if (mysqli_query($conn, $sql)) {
    echo "El usuario ha sido creado";
} else {
    echo "Error: " . mysqli_error($conn);
}

// Cerrar la conexión
mysqli_close($conn);
?>