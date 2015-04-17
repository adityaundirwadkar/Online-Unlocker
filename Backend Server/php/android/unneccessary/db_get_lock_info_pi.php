<?php 
// array for JSON response
$response = array();
if(isset($_GET['username']) && isset($_GET['lockid'])){
	//echo "yes";
	$username = $_GET['username'];
	$lockid = $_GET['lockid'];
	// Connects to your Database 
	 mysql_connect("localhost", "root", "root") or die(mysql_error()); 
	 mysql_select_db("acnproject") or die(mysql_error()); 
	 $query = mysql_query("SELECT A.LOCKNAME,A.STATUS, A.COMMENTS FROM ACN_LOCK_INFO as A, ACN_USER_INFO as B WHERE (BINARY A.USERNAME='$username') and (BINARY a.lockname= '$lockid') and a.USERNAME = b.USERNAME order by 1") 
	 or die(mysql_error()); 
	 // check if row inserted or not
	    if (!empty($query) && (mysql_num_rows($query) >= 1)) {
			$response["dblockinfo"] = array();
			while($result = mysql_fetch_array($query)){
					$dblockinfo = array();
					//$dblockinfo['username'] = $result['USERNAME'];
					$dblockinfo['lockname'] = $result['LOCKNAME'];
					$dblockinfo['status'] = $result['STATUS'];
					$dblockinfo['comments'] = $result['COMMENTS'];
					array_push($response["dblockinfo"], $dblockinfo);
			}
			$response['success'] = 1;
			//echo json_encode($response);
			echo (int) $dblockinfo['status'];
	    }
	    else {
			$response['success'] = 0;
			$response['message'] = "Lock Not found!";
			echo json_encode($response);	    
		}
}
else{
		$response['success'] = 0;
		$response['message'] = "Invalid Username or lockid";
		echo json_encode($response);	
} 
 ?> 