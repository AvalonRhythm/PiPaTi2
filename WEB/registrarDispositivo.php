<?php

$servername = "localhost";
$username = "Xhrobles002";
$password = "f0USH4tjm9";
$dbname = "Xhrobles002_PiPaTi";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$token = $_POST["token"];
$nomUser = $_POST["username"];

$sql = "UPDATE users SET DEVICEID = '$token' WHERE nomUser = '$nomUser'";

$result = mysqli_query($conn, $sql);

if (mysqli_query($conn, $sql)) {
    echo "true";
} else {
    echo "false";
}

$conn->close();
?>