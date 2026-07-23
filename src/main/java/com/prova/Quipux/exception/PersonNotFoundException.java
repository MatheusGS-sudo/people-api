package com.prova.quipux.exception;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(Long id) {
        super("Pessoa nao encontrada para o ID: " +id);
    }
}
