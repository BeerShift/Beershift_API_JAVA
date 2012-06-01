package org.sample.servlet.service;

public class LoginService {

	public boolean authencticate (String userID, String password)
	{

		if( password == null || password.trim() =="")
			return false;
		
		return true;
	}
	
}
