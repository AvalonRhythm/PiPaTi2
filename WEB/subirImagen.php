<?php
$servername = "localhost";
$username = "Xhrobles002";
$password = "f0USH4tjm9";
$dbname = "Xhrobles002_PiPaTi";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$image = $_POST["image"];
$nomUser = $_POST["username"];

$sql = "UPDATE users SET imagen='$image' WHERE nomUser='$nomUser'";

if (mysqli_query($conn, $sql)) {
    echo "success";
} else {
    echo 'Error de consulta: ' . mysqli_error($con);
}

// Cerrar la conexión
mysqli_close($conn);
?>