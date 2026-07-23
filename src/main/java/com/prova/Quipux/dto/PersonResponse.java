package com.prova.quipux.dto;

public record PersonResponse(
        Long id,
        String documento,
        String nome,
        String sobrenome,
        String email
){}
