package edu.utdallas.acngroup12.main;

public class LockInfo {
	    public boolean status;
	    public String comments;
	    public String lockID;
	    
	    public LockInfo(){
	        super();
	    }
	   
	    public LockInfo(boolean status, String lockID, String comments) {
	        super();
	        this.status = status;
	        this.comments = comments;
	        this.lockID = lockID;
	    }
	}