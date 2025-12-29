package com.example.demo.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.demo.model.User;



public class UserPrincipal implements UserDetails {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	
	
	private User user;
	
	
	public UserPrincipal(User user) {
		this.user = user;
	}

	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

	    String role = user.getRole();

	    if (role == null) {
	        return Collections.emptyList();
	    }

	    // نشيل أي مسافات
	    role = role.trim();

	    // نخزن في الداتابيس مثلا ADMIN أو USER
	    // وهنا نضيف ROLE_ عشان Spring يحبها كذا
	    if (!role.startsWith("ROLE_")) {
	        role = "ROLE_" + role;
	    }

	    return Collections.singletonList(
	            new SimpleGrantedAuthority(role)
	    );
	}
	
	
	//يرجع كلمة المرور من كائن اليوزر
	@Override
	public String getPassword() {
		return user.getPassword();
	}
	//يرجع اسم المستخدم من كائن اليوزر	
	@Override
	public String getUsername() {
		return user.getUsername();
	}
	//يرجع كائن اليوزر الكامل	
    public User getUser() {
        return user;
    }
	
    
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	
	
	
	
}
