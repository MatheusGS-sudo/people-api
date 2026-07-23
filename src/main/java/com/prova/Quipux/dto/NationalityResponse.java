package com.prova.quipux.dto;

public record NationalityResponse(
        Long pessoaId,
        String nome,
        String codigoPais,
        String nacionalidade,
        Double probabilidade
) {
}
