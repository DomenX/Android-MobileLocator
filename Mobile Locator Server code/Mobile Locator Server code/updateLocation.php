<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();

$lct_name="";	$lct_lat="";	$lct_long="";	$lct_type="";	$lct_desc="";	$lct_city="";	$lct_state="";	$lct_image="";

if (isset($_GET['name']))
	$lct_name = $_GET['name'];
if (isset($_GET['latitude']))
	$lct_lat = $_GET['latitude'];
if (isset($_GET['longitude']))
	$lct_long = $_GET['longitude'];
if (isset($_GET['type']))
	$lct_type = $_GET['type'];
if (isset($_GET['desc']))
	$lct_desc = $_GET['desc'];
if (isset($_GET['city']))
	$lct_city = $_GET['city'];
if (isset($_GET['state']))
	$lct_state = $_GET['state'];	
if (isset($_GET['image']))
	$lct_image = $_GET['image'];
	
// include db connect class
require_once __DIR__ . '/connection.php';
 
 if ($lct_name !="" && $lct_lat !="" && $lct_long !="" && $lct_type !="") {
    // connecting to db
    $db = new DB_CONNECT();
 

	switch($lct_type) {
		case "NoIdea":
			$type=0;
			break;
		case "Emergency":
			$type=1;
			break;
		case "Entertainment":
			$type=2;
			break;
		case "Food":
			$type=3;
			break;
		case "FacilityAndService":
			$type=4;
			break;
		case "Shopping":
			$type=5;
			break;
		case "Sport":
			$type=6;
			break;
		case "Tourism":
			$type=7;
			break;
		case "Transport":
			$type=8;
			break;
		default:
			$type=0;
			break;
	}
 
    // mysql inserting a new row, if not exist create new table CREATE TABLE new_table  AS (SELECT * FROM old_table)	
    $result = mysql_query("INSERT INTO temp_location(location_name, location_type, location_lat, location_long, location_city, location_addr_state, 
						location_desc, location_img) VALUES('$lct_name', '$type', '$lct_lat', '$lct_long', '$lct_city', '$lct_state', '$lct_desc', '$lct_image')");

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["mb_UpdateStatus:"] = 1;
        $response["message"] = "Location is successfully updated.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["mb_UpdateStatus:"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
        // echoing JSON response
        echo json_encode($response);
    }
}
else {
    // required field is missing
    $response["mb_UpdateStatus:"] = 2;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>