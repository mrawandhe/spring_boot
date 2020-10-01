package com.logs.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {
	
	private String userName;
	private String password;
	private String email;
	private String mobile;
	
	@Override
	public String toString() {
		return "UserInfo [userName=" + userName + ", password=" + password + ", email=" + email + ", mobile=" + mobile
				+ "]";
	}
	
}
