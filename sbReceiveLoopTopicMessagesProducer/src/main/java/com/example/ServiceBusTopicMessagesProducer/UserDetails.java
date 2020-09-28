package com.example.ServiceBusTopicMessagesProducer;

import java.nio.charset.Charset;

public class UserDetails {

	private String UserName;
	private String UAddress;
	private String Ustate;

	public UserDetails() {
		
	}
	public UserDetails(String userName, String uAddress, String ustate) {
		
		this.UserName = userName;
		this.UAddress = uAddress;
		this.Ustate = ustate;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getUAddress() {
		return UAddress;
	}

	public void setUAddress(String uAddress) {
		UAddress = uAddress;
	}

	public String getUstate() {
		return Ustate;
	}

	public void setUstate(String ustate) {
		Ustate = ustate;
	}

	public String getBytes(Charset utf8) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String toString() {
		return "UserDetails [UserName=" + UserName + ", UAddress=" + UAddress + ", Ustate=" + Ustate + "]";
	}
	
	

}
