package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.LoginRequest;
import com.example.demo.model.RegisterRequest;
import com.example.demo.model.User;
import com.example.demo.repo.UserRepo;
import com.example.demo.security.JwtService;

@Service
public class RegistrationService {
	
	//عشان نتعامل مع جدول
    @Autowired
    private UserRepo userRepo;
    // لتشفير كلمة المرور
    @Autowired
    private PasswordEncoder passwordEncoder;
    // للمصادقة
    @Autowired
    private AuthenticationManager authManager;
	// عشان نولّد JWT
    @Autowired
    private JwtService jwtService;

    // تسجيل مستخدم جديد
    public void register(RegisterRequest request) {
    	// التحقق من وجود اسم المستخدم أو البريد الإلكتروني مسبقًا	
        if (userRepo.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // إنشاء كائن مستخدم جديد وتعبئته بالبيانات	
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        
        
        // تشفير كلمة المرور قبل حفظها في قاعدة البيانات	
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);
        
        // تعيين الدور الافتراضي للمستخدم الجديد	
        user.setRole("USER");
        
        
        userRepo.save(user);
    }

    public String login(LoginRequest request) {
    	//نخلق كائن يمثل بيانات الدخول اللي جايه من المستخدم
        UsernamePasswordAuthenticationToken authInputToken = 
                new UsernamePasswordAuthenticationToken( 
                        request.getUsername(),
                        request.getPassword()
                );
        
        var auth = authManager.authenticate(authInputToken);
        
        UserDetails userDetails = (UserDetails) auth.getPrincipal(); // يرجع كائن UserDetails
        // 
        String token = jwtService.generateToken(userDetails); 

        return token;
    }
}
