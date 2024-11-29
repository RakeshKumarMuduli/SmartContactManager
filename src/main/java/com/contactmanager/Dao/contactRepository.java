package com.contactmanager.Dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contactmanager.Entity.Contact;

public interface contactRepository extends JpaRepository<Contact, Integer> {

	@Query("from Contact as c where c.user.id= :userId")
	//here we use page for pagination
	//pageable has two argument i.e. current page and total page
	public Page<Contact> findByUserId(@Param("userId") int id,Pageable page);
}
