<?php
@session_start();

if (isset($_SESSION['user_id']))
{
@$variable =$_GET["success"];

if(!empty($variable)) {
if ($variable==1) {
?>
<script type="text/javascript">alert("Operation Success!")</script>
<?php
}
else if ($variable ==2) {
?>
<script type="text/javascript">alert("Operation Failed!")</script>
<?php
}
}
?>

<html>

<head>
<link rel="stylesheet" href="table.css" type="text/css" />
<title>validateLocation</title>
</head>

<body>
<form action="./processData.php" method="post">
<table class="CSSTableGenerator">
<tr><td>Edit:</td><td>Name:</td><td>Latitude</td><td>Longitude</td><td>Location Type</td><td>Location Desc</td><td>Location City</td><td>Location State</td></tr>

<?php
// include db connect class
require_once __DIR__ . '/connection.php';

// connecting to db
$db = new DB_CONNECT();

// array for JSON response
$response = array();

$result =  mysql_query("SELECT * FROM temp_location");
$rowCount = mysql_num_rows($result);

if ( $rowCount>0 ) {

	while ($row = mysql_fetch_array($result)) {
	
		$lct_id = $row["location_id"];
		$lct_name = $row["location_name"];
		$lct_lat = 	$row["location_lat"];
		$lct_long = $row["location_long"];
		$lct_type = $row["location_type"];
		$lct_desc = $row["location_desc"];
		$lct_city = $row["location_city"];
		$lct_state = $row["location_addr_state"];
		$lct_img = $row["location_img"];		
		
		echo "<tr><td><input type=\"checkbox\" name=\"lct_id[]\" value=\"$lct_id\"></td>
			<td><input type=\"text\" name=\"name_$lct_id\" value=\"$lct_name\"></td>
			<td><input type=\"text\" name=\"lat_$lct_id\" value=\"$lct_lat\"></td>
			<td><input type=\"text\" name=\"long_$lct_id\" value=\"$lct_long\"></td>
			<td><input type=\"text\" name=\"type_$lct_id\" value=\"$lct_type\"></td>
			<td><input type=\"text\" name=\"desc_$lct_id\" value=\"$lct_desc\"></td>
			<td><input type=\"text\" name=\"city_$lct_id\" value=\"$lct_city\"></td>
			<td><input type=\"text\" name=\"state_$lct_id\" value=\"$lct_state\">
			<input type=\"hidden\" name=\"img_$lct_id\" value=\"$lct_img\"></td></tr>";
	}

}
?>
</table>
<center style="margin:20 20 20 20;">
<input type="submit" id="submit" name="update" value="Update to Database" size=20>
<input type="submit" id="submit" name="discard" value="Discard Data">
</center>
</form>

</body></html>
<?php
}
else
{
	header('Location:./login.php?success=2');
	exit();
	}
?>

		
