<?php 
	// array for JSON response
	$response = array();
	if(isset($_GET['lockID']) && isset($_GET['comments']) && isset($_GET['home_longitude']) && isset($_GET['home_latitude'])){
		//echo "yes";
		$lockid = $_GET['lockID'];
		$comments = $_GET['comments'];
		$home_longitude = $_GET['home_longitude'];
		$home_latitude = $_GET['home_latitude'];
		// Connects to your Database 
		// include db connect class
		require_once __DIR__ . '/db_parameter/db_connect.php';
		// connecting to db
		$db = new DB_CONNECT();
		$acn_lock_info = mysql_query("UPDATE ACN_LOCK_INFO SET COMMENTS = '$comments' WHERE BINARY LOCKID = '$lockid'") 
		or die(mysql_error()); 
		
		//Code to check whether the PI's location and user's new location has a different co-ordinates
		$lat = $home_latitude;  // latitude of centre of bounding circle in degrees
		$lon = $home_longitude;  // longitude of centre of bounding circle in degrees
		$R = 6371;  // earth's mean radius, km  
		$rad = 3;	//user's location should be at least 3km away from the pi's original location.
		// first-cut bounding box (in degrees)
		$maxLat = $lat + rad2deg($rad/$R);
		$minLat = $lat - rad2deg($rad/$R);
		// compensate for degrees longitude getting smaller with increasing latitude
		$maxLon = $lon + rad2deg($rad/$R/cos(deg2rad($lat)));
		$minLon = $lon - rad2deg($rad/$R/cos(deg2rad($lat)));
		$newLat = deg2rad($lat);
		$newLong = deg2rad($lon);
		$query = mysql_query("
				Select LOCKID, USERNAME , HOME_LATITUDE, HOME_LONGITUDE, 
				   acos(sin($newLat)*sin(radians(HOME_LATITUDE)) + cos($newLat)*cos(radians(HOME_LATITUDE))*cos(radians(HOME_LONGITUDE)-$newLong)) * $R As D
			From (
			  Select LOCKID, Username, HOME_LATITUDE, HOME_LONGITUDE
			  From ACN_LOCK_LOCATION_INFO
			  Where LOCKID = '$lockid' AND SOURCE='P' AND HOME_LATITUDE Between $minLat And $maxLat
				And HOME_LONGITUDE Between $minLon And $maxLon
			  ) As FirstCut 
			Where acos(sin($newLat)*sin(radians(HOME_LATITUDE)) + cos($newLat)*cos(radians(HOME_LATITUDE))*cos(radians(HOME_LONGITUDE)-$newLong)) * $R <= $rad
			Order by D") 
		or die(mysql_error()); 
		//echo "<br>", mysql_num_rows($query);
		//If query returns 0 rows that means the user is at least 5km away from PI's location so we can add user's location as well.
		if(mysql_num_rows($query) < 1){
			$acn_lock_location_info = mysql_query("UPDATE ACN_LOCK_LOCATION_INFO SET HOME_LONGITUDE = $home_longitude ,HOME_LATITUDE = $home_latitude WHERE BINARY LOCKID = '$lockid' AND SOURCE='M'") 
			or die(mysql_error()); 
			// check if row updated or not
			if ($acn_lock_info){
				$response['success'] = 1;
				$response['message'] = "Current Location added to authorized location!";
				mysql_query("COMMIT");
				echo json_encode($response);
			}else {
				$response['success'] = 0;
				$response['message'] = "Current Location can not be added to authorized location! Please try again!";
				mysql_query("ROLLBACK");
				echo json_encode($response);	    
			}
		}
		else{
			$response['success'] = 0;
			$response['message'] = "Similar location found! Please try again!";
			echo json_encode($response);	
		}
	}else{
			$response['success'] = 0;
			$response['message'] = "Invalid Lock ID";
			echo json_encode($response);	
		}

 ?> 