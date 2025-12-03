package com.example.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.repo.UserRepo;
import com.example.demo.security.UserPrincipal;

//إذا عطيتك اليوزر، جيب لي معلومات هذا المستخدم من قاعدة البيانات
@Service
public class MyUserDetailsService implements UserDetailsService { 

	@Autowired	
	private UserRepo userRepo; // حقن مستودع اليوزر	
	
	// تحميل تفاصيل اليوزر بناء على اسم المستخدم	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userRepo.findByUsername(username);
		
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		return new UserPrincipal(user);
		
	}
	
	
	
}
