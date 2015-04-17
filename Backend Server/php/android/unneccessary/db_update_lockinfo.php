<?php 
// array for JSON response
$response = array();
if(isset($_GET['lockid']) && isset($_GET['newlockstatus'])){
	//echo "yes";
	//$username = $_GET['username'];
	$lockid = $_GET['lockid'];
	$newlockstatus = $_GET['newlockstatus'];
	// Connects to your Database 
	 mysql_connect("localhost", "root", "root") or die(mysql_error()); 
	 mysql_select_db("acnproject") or die(mysql_error()); 
	 $query = mysql_query("UPDATE ACN_LOCK_INFO AS A SET A.STATUS='$newlockstatus' WHERE A.LOCKNAME = '$lockid'")
	 or die(mysql_error()); 
	 // check if row updated or not
	    if ($query){
			$response['success'] = 1;
			$response['message'] = "Record updated successfully!";
			echo json_encode($response);
	    }
	    else {
			$response['success'] = 0;
			$response['message'] = "Lock Not found!";
			echo json_encode($response);	    
		}
}
else{
		$response['success'] = 0;
		$response['message'] = "Invalid lockname";
		echo json_encode($response);	
} 
 ?> 