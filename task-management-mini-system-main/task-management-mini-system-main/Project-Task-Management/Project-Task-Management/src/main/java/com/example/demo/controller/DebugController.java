package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {

    @GetMapping("/debug/me")
    public String me(org.springframework.security.core.Authentication auth) {

        if (auth == null) {
            System.out.println("Authentication is NULL");
            return "no auth";
        }

        System.out.println("Username = " + auth.getName());
        auth.getAuthorities()
            .forEach(a -> System.out.println("Authority = '" + a.getAuthority() + "'"));

        return "ok";
    }
}
