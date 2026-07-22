package com.prova.Quipux.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/check")
    public Map<String, String> checkAuthentication(
        Authentication authentication
    ) {

        return Map.of(
            "username", authentication.getName(),
            "message", "Autenticação realizada com sucesso"
        );
    }
}
