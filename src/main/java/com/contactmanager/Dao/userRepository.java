package com.contactmanager.Dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.contactmanager.Entity.User;

@Component
public interface userRepository extends CrudRepository<User, Integer>{

	@Query("select  u from User u where u.email= :email")
	public User getUserByUserName(@Param("email")String email);
}
