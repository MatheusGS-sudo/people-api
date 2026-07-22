package com.prova.Quipux.dto;

public record NationalityResponse(
        Long pessoaId,
        String nome,
        String codigoPais,
        String nacionalidade,
        Double probabilidade
) {
}
