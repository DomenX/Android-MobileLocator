<?php
//http://www.movable-type.co.uk/scripts/latlong.html

// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/connection.php';
require_once __DIR__ . '/location.php';


if (isset($_GET["latitude"]) && isset($_GET["longitude"]) && isset($_GET["altitude"]) && isset($_GET["radius"])) {
    $user_lat  = $_GET['latitude'];
	$user_long = $_GET['longitude'];
	$user_alt  = $_GET['altitude'];
	$radius    = $_GET['radius'];
	
	// get the square area of the location, 4 points
	$location_north = new Location();
	$location_east  = new Location();	
	$location_south = new Location();
	$location_west  = new Location();

	getDestinationPoint($user_lat, $user_long, $location_north , $radius , 0);
	getDestinationPoint($user_lat, $user_long, $location_east  , $radius , 90);
	getDestinationPoint($user_lat, $user_long, $location_south , $radius , 180);
	getDestinationPoint($user_lat, $user_long, $location_west  , $radius , 270);
	
	$db = new DB_CONNECT();
	$query = "SELECT * FROM location WHERE location_lat BETWEEN $location_south->lat AND $location_north->lat 
			AND location_long BETWEEN $location_west->long AND $location_east->long";
	
	if (isset($_GET["search"])) {
		$search = $_GET["search"];
		$query = $query." AND location_name LIKE ('%$search%')";
	}
	
	if (isset($_GET["category"])) {
		$addString="";
		$category = $_GET["category"];
		
		switch($category) {
			case "NoIdea":
				$addString="";
				break;
			case "Emergency":
				$addString=" AND location_type = 1";
				break;
			case "Entertainment":
				$addString=" AND location_type = 2";
				break;
			case "Food":
				$addString=" AND location_type = 3";
				break;
			case "FacilityAndService":
				$addString=" AND location_type = 4";
				break;
			case "Shopping":
				$addString=" AND location_type = 5";
				break;
			case "Sport":
				$addString=" AND location_type = 6";
				break;
			case "Tourism":
				$addString=" AND location_type = 7";
				break;
			case "Transport":
				$addString=" AND location_type = 8";
				break;
			default:
				$addString=" AND location_type = 0";
				break;
		}
		$query = $query.$addString;
	}
	
	$result =  mysql_query($query) or die(mysql_error()); 
	$rowCount = mysql_num_rows($result);
	
	// check for empty result
	if ( $rowCount > 0) {
		$response["identifier"]= 'mb_lction';
		$response["success"] = 1;
		$response["rowCount"] = $rowCount;
		$response["location"] = array();
		
		
	    // looping through all results
		while ($row = mysql_fetch_array($result)) {
			
			/* this script only retrieve data within the square area of the point, but not in a circle. To improve efficiency, update the script
			to find the distance between user point and retrieved point, return it only when it is within radius.*/

			// temp user array
			$row_result = array();
			$row_result["mb_lction_id"] = $row["location_id"];
			$row_result["mb_lction_name"] = $row["location_name"];
			$row_result["mb_lction_long"] = $row["location_long"];
			$row_result["mb_lction_lat"] = $row["location_lat"];
			$row_result["mb_lction_type"] = $row["location_type"];
			$row_result["mb_lction_desc"] = $row["location_desc"];
			$row_result["mb_lction_city"] = $row["location_city"];
			$row_result["mb_lction_addr_state"] = $row["location_addr_state"];
			$row_result["mb_lction_img"] = $row["location_img"];
			
			$row_result["mb_lction_elevation"] = $row["location_altitude"];		//a degree from ground level to the point.for more information, check wiki geonames, elevation
		     
			// push single product into final response array
			array_push($response["location"], $row_result);
		}
		// success

		// echoing JSON response
		echo json_encode($response);
	} else {
		// no location found
		$response["success"] = 0;
		$response["message"] = "No location found";
 
		// echo no users JSON
		echo json_encode($response);
	}
}
?>
		











	

		
