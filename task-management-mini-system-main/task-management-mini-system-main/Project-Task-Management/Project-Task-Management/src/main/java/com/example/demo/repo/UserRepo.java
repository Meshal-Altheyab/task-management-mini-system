package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.User;

@Repository 
public interface   UserRepo extends JpaRepository<User, Integer> { // اليوزر هو نوع الكيان و الاينتيجر هو نوع المفتاح الرئيسي للكيان	
	
	User findByUsername(String username); // للبحث عن يوزر بناء على اسم المستخدم	
	
	boolean existsByUsername(String username); // للتحقق من وجود يوزر باسم مستخدم معين	
	boolean existsByEmail(String email); // للتحقق من وجود يوزر ببريد الكتروني معين	
	

}
