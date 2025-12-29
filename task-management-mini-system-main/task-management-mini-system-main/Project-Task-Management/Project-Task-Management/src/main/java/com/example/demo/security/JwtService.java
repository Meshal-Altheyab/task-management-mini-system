package com.example.demo.security;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
@Service
public class JwtService {

    private final SecretKey key; // مفتاح التوقيع السري	
    private final long jwtExpirationMs; // مدة صلاحية التوكن بالميلي	
    
    //ياخذ القيم من ملف الاعدادات 	
    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long jwtExpirationMs) {

        this.key = Keys.hmacShaKeyFor(secret.getBytes()); //
        this.jwtExpirationMs = jwtExpirationMs;
    }

    // إنشاء التوكن
    public String generateToken(UserDetails userDetails) {

        Date now = new Date();// الوقت الحالي	
        Date expiry = new Date(now.getTime() + jwtExpirationMs); // وقت الانتهاء	

        return Jwts.builder()
                .setSubject(userDetails.getUsername())  //نحط اسم اليوزير  
                .setIssuedAt(now)    // متى انصنع التوكن               
                .setExpiration(expiry)   //متى ينتهي               
                .signWith(key, SignatureAlgorithm.HS256) // نوقع التوكن بالمفتاح السري وخوارزمية التوقيع	
                .compact(); // يرجّع التوكن كسلسلة نصية طويلة                     
    }

    //استخراج معلومة معينة من التوكن، هنا نريد اسم اليوزر 
    public String extractUsername(String token) {
    	
        return extractClaim(token, Claims::getSubject);
    }

    // التحقق هل التوكن صحيح 
    public boolean isTokenValid(String token, UserDetails userDetails) {

        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);//هل التوكن منتهي و هل اليوزر الى داخل التوكن يطابق المستخدم الحالي 	
    }

    
    
    // التحقق هل التوكن منتهي		

    private boolean isTokenExpired(String token) {

        Date expiration = extractClaim(token, Claims::getExpiration);// استخراج تاريخ الانتهاء من التوكن	
        return expiration.before(new Date());// هل تاريخ الانتهاء قبل التاريخ الحالي	
    }

    	// استخراج معلومة معينة من التوكن	
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) { // دالة تأخذ التوكن ودالة اخرى لاستخراج معلومة معينة	

        final Claims claims = extractAllClaims(token);// استخراج كل المعلومات من التوكن	
        return claimsResolver.apply(claims); // تطبيق الدالة المستقبلة على المعلومات المستخرجة	
    }
    
    // استخراج كل المعلومات من التوكن	
    private Claims extractAllClaims(String token) {  

        return Jwts.parserBuilder()
                .setSigningKey(key)      
                .build()
                .parseClaimsJws(token)   
                .getBody();// يرجّع جسم التوكن الذي يحتوي على كل المعلومات	
    }
}