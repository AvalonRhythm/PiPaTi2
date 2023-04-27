<?php 
$servername = "localhost";
$username = "Xhrobles002";
$password = "f0USH4tjm9";
$dbname = "Xhrobles002_PiPaTi";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$message=$_POST["mensaje"];
$nomUser=$_POST["username"];

$sql = "SELECT * FROM users WHERE nomUser = '$nomUser'";
$result = mysqli_query($conn, $sql);
$fila = mysqli_fetch_array($result);
$token = $fila['DEVICEID'];

$cabecera= array(
    'Authorization: key=AAAAzaP66mk:APA91bHenXZmZR8-7Y7mIEYw_m3CisequpfLFa-MDF8GDImprBcVEanD3y_VTO8tQyHYRw-pn-27O1ogelxJy5xPrDaKhnTLfbr1XT2eo3V5qmFtM6fQTGPUqstdOg5WdGt13H0V39jv', 
    'Content-Type: application/json' 
);

// Cuerpo de la solicitud HTTP POST
$msg = array(
    'to' => $token,
    'notification' => array(
        'title' => "PiPaTiFCM",
        'body' => $message,
        'sound' => 'default'
    )
);

$msgJSON= json_encode( $msg);

$ch= curl_init(); #inicializar el handlerde curl 
#indicar el destino de la petición, el servicio FCM de google 
curl_setopt( $ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send'); 
#indicar que la conexión es de tipo POST 
curl_setopt( $ch, CURLOPT_POST, true); 
#agregar las cabeceras 
curl_setopt( $ch, CURLOPT_HTTPHEADER, $cabecera); 
#Indicar que se desea recibir la respuesta a la conexión en forma de string 
curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true); 
#agregar los datos de la petición en formato JSON 
curl_setopt( $ch, CURLOPT_POSTFIELDS, $msgJSON);
#ejecutar la llamada 
$resultado = curl_exec( $ch); 
#cerrar el handlerde curl 
curl_close( $ch);

/*
// Verificar si la solicitud fue exitosa
if ($resultado === false) {
    echo 'Error: ' . curl_error($curl);
} else {
    echo 'Notificación enviada';
}
*/
echo $resultado;

?>