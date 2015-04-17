<?php 
	// array for JSON response
	$response = array();
	if(isset($_GET['lockID']) && isset($_GET['comments'])){
		//echo "yes";
		$lockid = $_GET['lockID'];
		$comments = $_GET['comments'];
		// Connects to your Database 
		// include db connect class
		require_once __DIR__ . '/db_parameter/db_connect.php';
		// connecting to db
		$db = new DB_CONNECT();
		$query = mysql_query("UPDATE ACN_LOCK_INFO SET COMMENTS = '$comments' WHERE BINARY LOCKID = '$lockid'") 
		or die(mysql_error()); 
		// check if row inserted or not
		// check if row updated or not
	    if ($query){
			$response['success'] = 1;
			$response['message'] = "Record updated successfully!";
			mysql_query("COMMIT");
			echo json_encode($response);
	    }
	    else {
			$response['success'] = 0;
			$response['message'] = "Lock Not found!";
			mysql_query("ROLLBACK");
			echo json_encode($response);	    
		}
	}
	else{
		$response['success'] = 0;
		$response['message'] = "Invalid Username or password";
		echo json_encode($response);	
	} 
 ?> 