<?php
//http://stackoverflow.com/questions/11534947/how-to-calculate-the-lat-lng-of-a-2nd-point-given-1st-point-and-distance
//http://www.movable-type.co.uk/scripts/latlong.html
//http://stackoverflow.com/questions/7222382/get-lat-long-given-current-point-distance-and-bearing

define ("EARTH_RADIUS", "6378");
define ("PI", "3.142");

class Location {
	private $lat;
	private $long;
	
	function __construct() {
		$this->lat = 0.00;
		$this->long = 0.00;
	}
	
	function __set($name,$value) {
		$this->$name = $value;
	}
	
	function __get($getname) {
		return $this->$getname;
	}
}

function getDestinationPoint($ori_lat, $ori_long,$location, $distance, $degree , $earthRadius = EARTH_RADIUS) {
	/*
	Formula:	φ2 = asin( sin(φ1)*cos(d/R) + cos(φ1)*sin(d/R)*cos(θ) )
				λ2 = λ1 + atan2( sin(θ)*sin(d/R)*cos(φ1), cos(d/R)−sin(φ1)*sin(φ2) )
	*/

	$lat1 = (PI/180)* $ori_lat;
	$lon1 = (PI/180)* $ori_long;
	$rad  = (PI/180)* $degree;

	$lat2 = asin( sin($lat1) * cos($distance/$earthRadius) + cos($lat1) * sin($distance/$earthRadius) * cos($rad));
	$lon2 = $lon1 + atan2( sin($rad) * sin($distance/$earthRadius) * cos($lat1),
					cos($distance/$earthRadius) - sin($lat1) * sin($lat2));

	$location->lat = $lat2 *(180/PI);
	$location->long= $lon2 *(180/PI);

}

?>