<?php 
	// array for JSON response
	$response = array();
	if(isset($_POST['lockid'])){
		//echo "yes";
		$lockid = $_POST['lockid'];
		// include db connect class
		require_once __DIR__ . '/db_parameter/db_connect.php';
		// connecting to db
		$db = new DB_CONNECT();

		$query = mysql_query("SELECT A.COMMENTS AS COMMENTS,B.SOURCE AS SOURCE, ISNULL(B.HOME_LONGITUDE) AS HOME_LONGITUDE, ISNULL(B.HOME_LATITUDE) AS HOME_LATITUDE FROM ACN_LOCK_INFO AS A, ACN_LOCK_LOCATION_INFO AS B WHERE A.LOCKID = B.LOCKID AND B.SOURCE = 'M' AND A.LOCKID = '$lockid'") 
		or die(mysql_error()); 
		// check if row inserted or not
		if (!empty($query) && (mysql_num_rows($query) == 1)) {
			$result = mysql_fetch_array($query); 

			$dblockinfo = array();
			$dblockinfo['comments'] = $result['COMMENTS'];
			$dblockinfo['source'] = $result['SOURCE'];
			$dblockinfo['home_longitude'] = $result['HOME_LONGITUDE'];
			$dblockinfo['home_latitude'] = $result['HOME_LATITUDE'];

			$response['success'] = 1;
			$response["dblockinfo"] = array();
			array_push($response["dblockinfo"], $dblockinfo);

			echo json_encode($response);
		}
		else {
			$response['success'] = 0;
			$response['message'] = "Error";
			echo json_encode($response);	    
		}
	}
	else{
		$response['success'] = 0;
		$response['message'] = "Incorrect LOCK ID";
		echo json_encode($response);
	} 
 ?> 