<?php 
	// array for JSON response
	$response = array();
	if(isset($_POST['username']) && isset($_POST['password'])){
		//echo "yes";
		$username = $_POST['username'];
		$password = $_POST['password'];
		// include db connect class
		require_once __DIR__ . '/db_parameter/db_connect.php';
		// connecting to db
		$db = new DB_CONNECT();

		$query = mysql_query("SELECT USERNAME,CREATED_AT FROM ACN_USER_INFO WHERE BINARY USERNAME='$username' and BINARY PASSWORD='$password'") 
		or die(mysql_error()); 
		// check if row inserted or not
		if (!empty($query) && (mysql_num_rows($query) == 1)) {
			$result = mysql_fetch_array($query); 

			$dbuserinfo = array();
			$dbuserinfo['username'] = $result['USERNAME'];		
			$dbuserinfo['created_at'] = $result['CREATED_AT'];

			$response['success'] = 1;
			$response["dbuserinfo"] = array();
			array_push($response["dbuserinfo"], $dbuserinfo);

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
		$response['message'] = "Incorrect Username or Password";
		echo json_encode($response);
	} 
 ?> 