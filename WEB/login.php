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

$sql = "SELECT * FROM users WHERE nomUser = '$nomUser' AND pass = '$pass'";

$result = mysqli_query($conn, $sql);

if (mysqli_num_rows($result) > 0) {
    echo "true";
} else {
    echo "false";
}

$conn->close();
?>