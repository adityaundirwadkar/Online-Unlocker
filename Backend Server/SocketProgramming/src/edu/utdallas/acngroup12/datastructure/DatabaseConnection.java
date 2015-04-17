package edu.utdallas.acngroup12.datastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;


public class DatabaseConnection{
	//MySQL DB parameters
	private Connection dbconnection;	
	private String dburl;
	private String dbName;
	private String dbDriver;
	private String dbuserName; 
	private String dbpassword;
	private boolean dbConnectivityFlag;
	
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;
	private String dbQuery;
		
	public DatabaseConnection() {
		// TODO Auto-generated constructor stub
		//Initialize MySQL DB related parameters.
		this.dbconnection = null;
		this.dburl = "jdbc:mysql://localhost:3306/";
		this.dbName = "acnproject";
		this.dbuserName = "acnproject";
		this.dbpassword = "1234";
		this.dbDriver = "com.mysql.jdbc.Driver";
		this.setDbConnectivityFlag(false);
		this.preparedStatement = null;
		this.resultSet = null;
		this.dbQuery = null;
		
		try {
			//This will load the MySQL driver, each DB has its own driver
	        Class.forName(this.dbDriver);
	        // setup the connection with the DB.
	        this.dbconnection = DriverManager.getConnection(this.dburl + this.dbName, this.dbuserName, this.dbpassword);
	        if(this.dbconnection != null){
	        	System.out.println("Ready to fire query!");
	        	this.setDbConnectivityFlag(true);
	        }
	        else {
	        	System.out.print("Unable to connect to DB");
	        }	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeDatabaseConnection(){
		 try {
			 this.setDbConnectivityFlag(false);
			 this.dbconnection.close();
			 this.preparedStatement.close();
			 this.resultSet.close();
			 this.dbQuery =null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	 
	}

	/**
	 * @return the dbConnectivityFlag
	 */
	public boolean getDbConnectivityFlag() {
		return dbConnectivityFlag;
	}

	/**
	 * @param dbConnectivityFlag the dbConnectivityFlag to set
	 */
	public void setDbConnectivityFlag(boolean dbConnectivityFlag) {
		this.dbConnectivityFlag = dbConnectivityFlag;
	}
	
	
	
	public HashMap<String, String> getPIOnlineStatus(String lockID){
		HashMap<String, String> piOnlineStatus = new HashMap<String, String>();
		//String connectionCurrentStatus = "No Data Found!";
		this.dbQuery = "SELECT LOCKID, UPPER(IS_ONLINE) AS IS_ONLINE "
						+ "FROM ACN_LOCK_INFO "
						+ "WHERE (BINARY LOCKID = ? )";
		try {
			this.preparedStatement = this.dbconnection.prepareStatement(this.dbQuery);
			this.preparedStatement.setString(1, lockID);
			//Execute the query
			this.resultSet = this.preparedStatement.executeQuery();
			if (!this.resultSet.isBeforeFirst() ) {    
				System.out.println("No data");
				piOnlineStatus.put("LOCKID", "LOCK NOT FOUND!");
				piOnlineStatus.put("IS_ONLINE", "LOCK NOT FOUND!");
				return piOnlineStatus;
			}
			else{
				while(this.resultSet.next()){
					piOnlineStatus.put("LOCKID", this.resultSet.getString("LOCKID"));
					piOnlineStatus.put("IS_ONLINE", this.resultSet.getString("IS_ONLINE"));					
				}
				return piOnlineStatus;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			piOnlineStatus.put("LOCKID", "LOCK NOT FOUND!");
			piOnlineStatus.put("IS_ONLINE", "LOCK NOT FOUND!");
			return piOnlineStatus;
		}
	}
	
	//Set IS_ONLINE of all locks as the argument! 
	public int setPIOnlineStatus(String newStatus){
		int updateStatus = -1;
		this.dbQuery = "UPDATE ACN_LOCK_INFO SET "
						+ "IS_ONLINE = ? ";
		try {
			this.preparedStatement = this.dbconnection.prepareStatement(this.dbQuery);
			this.preparedStatement.setString(1, newStatus);			
			//Execute the query
			updateStatus = this.preparedStatement.executeUpdate();
			return updateStatus;			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return updateStatus;
		} catch (NullPointerException npe) {
			// TODO Auto-generated catch block
			npe.printStackTrace();
			return updateStatus;
		}
	}	
	
	public int setPIOnlineStatus(HashMap<String, String> newPIOnlineStatus){
		int updateStatus = -1;
		this.dbQuery = "UPDATE ACN_LOCK_INFO SET "
						+ "IS_ONLINE = ? "
						+ "WHERE (BINARY LOCKID = ? )";
		try {
			this.preparedStatement = this.dbconnection.prepareStatement(this.dbQuery);
			this.preparedStatement.setString(1, newPIOnlineStatus.get("IS_ONLINE"));
			this.preparedStatement.setString(2, newPIOnlineStatus.get("LOCKID"));			
			//Execute the query
			updateStatus = this.preparedStatement.executeUpdate();
			return updateStatus;			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return updateStatus;
		} catch (NullPointerException npe) {
			// TODO Auto-generated catch block
			npe.printStackTrace();
			return updateStatus;
		}
	}	
	
	//Select lockStatusType hard or soft!
	public HashMap<String, String> getLockStatus(String lockID){
		HashMap<String, String> lockStatus = new HashMap<String, String>();
		this.dbQuery = "SELECT LOCKID, UPPER(SOFT_LOCK_STATUS) AS SOFT_LOCK_STATUS, UPPER(HARD_LOCK_STATUS) AS HARD_LOCK_STATUS, UPPER(IS_ONLINE) AS IS_ONLINE, STATUS_CHANGED "
						+ "FROM ACN_LOCK_INFO "
						+ "WHERE (BINARY LOCKID = ? )";
		
		try {
			this.preparedStatement = this.dbconnection.prepareStatement(this.dbQuery);
			this.preparedStatement.setString(1, lockID);
			//Execute the query
			this.resultSet = this.preparedStatement.executeQuery();
			if (!this.resultSet.isBeforeFirst() ) {    
				System.out.println("No data");
				lockStatus.put("LOCKID", "LOCK NOT FOUND!");
				lockStatus.put("SOFT_LOCK_STATUS", "LOCK NOT FOUND!");
				lockStatus.put("HARD_LOCK_STATUS", "LOCK NOT FOUND!");
				lockStatus.put("STATUS_CHANGED", "LOCK NOT FOUND!");
				return lockStatus;
			}
			else{
				while(this.resultSet.next()){
					lockStatus.put("LOCKID", this.resultSet.getString("LOCKID"));
					lockStatus.put("SOFT_LOCK_STATUS", this.resultSet.getString("SOFT_LOCK_STATUS"));
					lockStatus.put("HARD_LOCK_STATUS", this.resultSet.getString("HARD_LOCK_STATUS"));
					lockStatus.put("STATUS_CHANGED", this.resultSet.getString("STATUS_CHANGED"));
				}
				return lockStatus;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			lockStatus.put("LOCKID", "LOCK NOT FOUND!");
			lockStatus.put("SOFT_LOCK_STATUS", "LOCK NOT FOUND!");
			lockStatus.put("HARD_LOCK_STATUS", "LOCK NOT FOUND!");
			lockStatus.put("STATUS_CHANGED", "LOCK NOT FOUND!");
			return lockStatus;
		}
	}
	
	//Select lockStatusType hard or soft!
	public int setLockStatus(HashMap<String, String> newLockStatus){
		int updateStatus = -1; 
		this.dbQuery = "UPDATE ACN_LOCK_INFO SET "
						+ "SOFT_LOCK_STATUS = UPPER(?), HARD_LOCK_STATUS = UPPER(?), STATUS_CHANGED = ? "
						+ "WHERE (BINARY LOCKID = ? )";		
		try {
			this.preparedStatement = this.dbconnection.prepareStatement(this.dbQuery);
			this.preparedStatement.setString(1, newLockStatus.get("SOFT_LOCK_STATUS"));
			this.preparedStatement.setString(2, newLockStatus.get("HARD_LOCK_STATUS"));
			this.preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));			
			this.preparedStatement.setString(4, newLockStatus.get("LOCKID"));
			updateStatus = this.preparedStatement.executeUpdate();
			return updateStatus;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return updateStatus;
		} catch (NullPointerException npe) {
			// TODO Auto-generated catch block
			npe.printStackTrace();
			return updateStatus;
		}
	}
	
	public int setPICoordinates(String lockID, double longitude, double latitude){
		int updateStatus = -1; 
		this.dbQuery = "UPDATE ACN_LOCK_LOCATION_INFO SET "
						+ "	HOME_LONGITUDE = "+longitude+", HOME_LATITUDE = "+latitude
						+ " WHERE (BINARY LOCKID = ? ) AND SOURCE = 'P'";		
		try {
			this.preparedStatement = this.dbconnection.prepareStatement(this.dbQuery);		
			this.preparedStatement.setString(1, lockID);
			updateStatus = this.preparedStatement.executeUpdate();
			return updateStatus;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return updateStatus;
		} catch (NullPointerException npe) {
			// TODO Auto-generated catch block
			npe.printStackTrace();
			return updateStatus;
		}
	}
	
	public double convertFeetToMeters(double feet)
	{
	  //function converts Feet to Meters.
	      double toMeters;
	      toMeters = feet/3.2808;  // official conversion rate of Meters to Feet
	      return toMeters;
	} 
	
	public long convertDoubleToLong(double d){
		  String str = Double.toString(d);
		  return Long.parseLong(str.replace(".", ""));
		}
	
	public HashMap<String, String> getLockProximity(double mLongitude, double mLatitude, String lockID, double radius){
		HashMap<String, String> lockProximity = new HashMap<String, String>();
		int earthRadius = 6371;  // earth's mean radius, KM 
		double mProximity = (convertFeetToMeters(radius))/1000;	// User's proximity in KM converted from feet
		// first-cut bounding box (in degrees)
		double maxLat = mLatitude + Math.toDegrees(mProximity/earthRadius);
		double minLat = mLatitude - Math.toDegrees(mProximity/earthRadius);
		// compensate for degrees longitude getting smaller with increasing latitude
		double maxLon = mLongitude + Math.toDegrees(mProximity/earthRadius/Math.cos(Math.toRadians(mLatitude)));
		double minLon = mLongitude - Math.toDegrees(mProximity/earthRadius/Math.cos(Math.toRadians(mLatitude)));
		
		double newLat = Math.toRadians(mLatitude);
		double newLong = Math.toRadians(mLongitude);	
		
		this.dbQuery = "Select LOCKID, USERNAME , HOME_LATITUDE, HOME_LONGITUDE, "
				   +"acos(sin("+newLat+")*sin(radians(HOME_LATITUDE)) + cos("+newLat+")*cos(radians(HOME_LATITUDE))*cos(radians(HOME_LONGITUDE)-("+newLong+"))) * "+earthRadius+" As D "
				   +"From ( "
				   +"Select LOCKID, Username, HOME_LATITUDE, HOME_LONGITUDE "
				   +"From ACN_LOCK_LOCATION_INFO "
				   +"Where LOCKID = ? AND HOME_LATITUDE Between "+minLat+" And "+maxLat+" "
				   +"And HOME_LONGITUDE Between "+minLon+" And "+maxLon+" "
				   +") As FirstCut  "
				   +"Where acos(sin("+newLat+")*sin(radians(HOME_LATITUDE)) + cos("+newLat+")*cos(radians(HOME_LATITUDE))*cos(radians(HOME_LONGITUDE)-("+newLong+"))) * "+earthRadius+" <= "+mProximity+" "
				   +"Order by D";
		try {
			this.preparedStatement = this.dbconnection.prepareStatement(this.dbQuery);
			this.preparedStatement.setString(1, lockID);
			//Execute the query
			this.resultSet = this.preparedStatement.executeQuery();
			if (!this.resultSet.isBeforeFirst() ) {    
				System.out.println("No data");
				lockProximity.put("LOCKID", "LOCK NOT FOUND!");
				lockProximity.put("CAN_UNLOCK", "NO");	
				return lockProximity;
			}
			else{
				while(this.resultSet.next()){
					lockProximity.put("LOCKID",  this.resultSet.getString("LOCKID"));
					lockProximity.put("CAN_UNLOCK", "YES");					
				}
				return lockProximity;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			lockProximity.put("LOCKID", "LOCK NOT FOUND!");
			lockProximity.put("CAN_UNLOCK", "NO");	
			return lockProximity;
		}		
	}
	
	
	public static void main (String[] args){
		DatabaseConnection databaseConnection = new DatabaseConnection();
		/*System.out.println("Status of lock_2's connection: " +databaseConnection.getPIOnlineStatus("LOCK_2"));
		//System.out.println("Status of lock_3's connection: " +databaseConnection.setConnectionUsability("LOCK_3", "Y"));
		
		HashMap<String, String> newMap = databaseConnection.getLockStatus("LOCK_2");
		System.out.println("Status of lock_1 : " +newMap);		
		newMap.put("LOCKID", "LOCK_2");
		newMap.put("SOFT_LOCK_STATUS", "UNLOCKED");
		newMap.put("IS_ONLINE", "NO");
		databaseConnection.setLockStatus(newMap);		
		databaseConnection.setPIOnlineStatus(newMap);		
		System.out.println("Status of lock_2 : " +databaseConnection.getLockStatus("LOCK_4"));*/
		
		HashMap<String, String> newMap = databaseConnection.getLockProximity(-96.7475, 32.986944, "LOCK_3", 10);
		newMap.toString();
		databaseConnection.closeDatabaseConnection();
	}
}
