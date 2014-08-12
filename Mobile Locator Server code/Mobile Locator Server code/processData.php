<?php
require_once __DIR__ . '/connection.php';
$db = new DB_CONNECT();

@$lctID = $_POST['lct_id'];

$insert_default = "INSERT INTO location(location_name, location_type, location_lat, location_long, location_city, location_addr_state, 
						location_desc, location_img) VALUES('";

$delete_query = 'DELETE FROM temp_location WHERE location_id IN (';

if ( !empty($lctID) && isset($_POST['update'])) {

	for($i=0;$i<count($lctID);$i++) {
		$temp = $lctID[$i];
				$insert_query = $insert_default.$_POST["name_$temp"].'\', \''.$_POST["type_$temp"].'\', \''.$_POST["lat_$temp"].'\', \''.$_POST["long_$temp"].'\', \''.$_POST["city_$temp"].'\', \''.$_POST["state_$temp"].'\', \''.$_POST["desc_$temp"].'\', \''.$_POST["img_$temp"].'\');';
				echo "name_$temp";
				$result2 = mysql_query($insert_query);
				
				echo $insert_query;
				if ($i==0)
					$delete_query = $delete_query.$temp;
				else
					$delete_query = $delete_query. ','.$temp;
		}
	}

	$delete_query= $delete_query.');';
		echo $delete_query;
	$result3 = mysql_query($delete_query);
	if ($result3) {
		header('Location:./validatelocation.php?success=1');
		exit();
	}


	


   
   
 


?>