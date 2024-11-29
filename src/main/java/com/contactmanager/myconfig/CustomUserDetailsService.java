package com.contactmanager.myconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.contactmanager.Dao.userRepository;
import com.contactmanager.Entity.User;

public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private userRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
	  User user = repository.getUserByUserName(username);
	  
		if(user==null) {
			throw new UsernameNotFoundException("user not exists");
		}
		
		CustomUserDetails cud= new CustomUserDetails(user);
		
		return cud;
	}

}
