<?php 
	// array for JSON response
	$response = array();
	if(isset($_POST['username']) && isset($_POST['password'])){
		//echo "yes";
		$username = $_POST['username'];
		$password = $_POST['password'];
		// Connects to your Database 
		//mysql_connect("localhost", "root", "root") or die(mysql_error()); 
		//mysql_select_db("acnproject") or die(mysql_error()); 

		// include db connect class
		require_once __DIR__ . '/db_parameter/db_connect.php';
		// connecting to db
		$db = new DB_CONNECT();
		$query = mysql_query("SELECT A.LOCKID,A.SOFT_LOCK_STATUS AS STATUS, A.IS_ONLINE AS IS_ONLINE, A.COMMENTS FROM ACN_LOCK_INFO AS A, ACN_USER_INFO AS B WHERE (BINARY A.USERNAME='$username') AND (BINARY B.PASSWORD= '$password') AND A.USERNAME = B.USERNAME ORDER BY 1") 
		or die(mysql_error()); 
		// check if row inserted or not
		if (!empty($query) && (mysql_num_rows($query) >= 1)) {
			$response["dblockinfo"] = array();
			while($result = mysql_fetch_array($query)){
				$dblockinfo = array();
				//$dblockinfo['username'] = $result['USERNAME'];
				$dblockinfo['lockid'] = $result['LOCKID'];
				$dblockinfo['status'] = $result['STATUS'];
				$dblockinfo['is_online'] = $result['IS_ONLINE'];
				$dblockinfo['comments'] = $result['COMMENTS'];
				array_push($response["dblockinfo"], $dblockinfo);
			}
			$response['success'] = 1;
			echo json_encode($response);
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