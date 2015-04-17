<?php 
	//http://www.movable-type.co.uk/scripts/latlong-db.html
	// array for JSON response
	$response = array();
	if(isset($_GET['lat']) && isset($_GET['lon']) && isset($_GET['rad'])){
		$lat = $_GET['lat'];  // latitude of centre of bounding circle in degrees
		$lon = $_GET['lon'];  // longitude of centre of bounding circle in degrees
		$rad = $_GET['rad'];  // radius of bounding circle in kilometers 
		$lockid = $_GET['lockid'];  // radius of bounding circle in kilometers 
		$R = 6371;  // earth's mean radius, km
  
		// first-cut bounding box (in degrees)
		$maxLat = $lat + rad2deg($rad/$R);
		$minLat = $lat - rad2deg($rad/$R);
		// compensate for degrees longitude getting smaller with increasing latitude
		$maxLon = $lon + rad2deg($rad/$R/cos(deg2rad($lat)));
		$minLon = $lon - rad2deg($rad/$R/cos(deg2rad($lat)));
		$newLat = deg2rad($lat);
		$newLong = deg2rad($lon);

		echo "maxLat  : ",$maxLat, PHP_EOL;
		echo "<br>minLat  : ",$minLat;
		echo "<br>maxLon  : ",$maxLon;
		echo "<br>minLon  : ",$minLon;
		echo "<br>newLat  : ",$newLat;
		echo "<br>newLong : ",$newLong;



		// include db connect class
		require_once __DIR__ . '/db_parameter/db_connect.php';
		// connecting to db
		$db = new DB_CONNECT();
		$query = mysql_query("
				Select LOCKID, USERNAME , HOME_LATITUDE, HOME_LONGITUDE, 
				   acos(sin($newLat)*sin(radians(HOME_LATITUDE)) + cos($newLat)*cos(radians(HOME_LATITUDE))*cos(radians(HOME_LONGITUDE)-$newLong)) * $R As D
			From (
			  Select LOCKID, Username, HOME_LATITUDE, HOME_LONGITUDE
			  From ACN_LOCK_LOCATION_INFO
			  Where LOCKID = '$lockid' AND HOME_LATITUDE Between $minLat And $maxLat
				And HOME_LONGITUDE Between $minLon And $maxLon
			  ) As FirstCut 
			Where acos(sin($newLat)*sin(radians(HOME_LATITUDE)) + cos($newLat)*cos(radians(HOME_LATITUDE))*cos(radians(HOME_LONGITUDE)-$newLong)) * $R <= $rad
			Order by D") 
		or die(mysql_error()); 

		// check if row inserted or not
		if (!empty($query) && (mysql_num_rows($query) >= 1)) {
			$response["dblockinfo"] = array();
			while($result = mysql_fetch_array($query)){
				$dblockinfo = array();
				$dblockinfo['lockid'] = $result['LOCKID'];
				$dblockinfo['username'] = $result['USERNAME'];
				$dblockinfo['home_latitude'] = $result['HOME_LATITUDE'];
				$dblockinfo['home_longitude'] = $result['HOME_LONGITUDE'];
				array_push($response["dblockinfo"], $dblockinfo);
			}
			$response['success'] = 1;
			echo "<br>",json_encode($response);
		}
		else {
			$response['success'] = 0;
			$response['message'] = "User Not found!";
			echo json_encode($response);	    
		}
	}
	else{
		$response['success'] = 0;
		$response['message'] = "Invalid Username or password";
		echo json_encode($response);	
	} 
 ?> 