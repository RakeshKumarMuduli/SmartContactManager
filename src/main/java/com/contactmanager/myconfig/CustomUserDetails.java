package com.contactmanager.myconfig;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.contactmanager.Entity.User;

public class CustomUserDetails implements UserDetails{
	
	private User user;

	public CustomUserDetails(User u) {
		super();
		this.user = u;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		SimpleGrantedAuthority simple=new SimpleGrantedAuthority(user.getRole());
		return List.of(simple);
	}

	@Override
	public String getPassword() {
		
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		
		return user.getEmail();
	}

}

