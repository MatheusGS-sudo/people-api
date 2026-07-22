package com.prova.Quipux.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PersonRequest (
    @NotBlank(message = "O documento é obrigatório")
    @Pattern(
            regexp = "\\d{11}",
            message = "O documento deve possuir até 11 números"
    )
    String documento,
    @NotBlank(message = "O nome é obrigatório")
    @Size(
            min = 2,
            max = 60,
            message = "O nome deve possuir entre 2 a 6 caracteres"

    )
    @Pattern(
            regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ ]+$",
            message = "O nome possui caracteres inválidos"
    )
    String nome,
    @NotBlank(message = "O sobrenome é obrigatório")
    @Size(
            min = 2,
            max = 60,
            message = "O sobrenome deve possuir entre 2 e 60 caracteres"
    )
    @Size(
            min = 2,
            max = 60,
            message = "O sobrenome deve possuir entre 2 e 60 caracteres"
    )
    String sobrenome,
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Informe um e-mail válido")
    @Size(
            max = 150,
            message = "O e-mail deve possuir no máximo 150 caracteres"
    )
    String email
){}
