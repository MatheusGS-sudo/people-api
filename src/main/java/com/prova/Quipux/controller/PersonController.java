package com.prova.Quipux.controller;

import com.prova.Quipux.dto.NationalityResponse;
import com.prova.Quipux.dto.PersonRequest;
import com.prova.Quipux.dto.PersonResponse;
import com.prova.Quipux.service.NationalityService;
import com.prova.Quipux.service.PersonService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    private final NationalityService nationalityService;

    @PostMapping("/registrarName")
    @ResponseStatus(HttpStatus.CREATED)
    public PersonResponse register(
            @Valid
            @RequestBody
            PersonRequest request
    ) {
        return personService.register(request);
    }
    @GetMapping("/list")
    public List<PersonResponse> list(

            @RequestParam(defaultValue = "id")
            @Pattern(
                    regexp = "id|nome|sobrenome|email",
                    message = "ordenarPor deve ser: id, nome, sobrenome ou email"
            )
            String ordenarPor
    ) {

        return personService.listAll(ordenarPor);
    }
    @GetMapping("/list/{id}")
    public PersonResponse findById(

            @PathVariable
            @Positive(
                    message = "O ID deve ser maior que zero"
            )
            Long id

    ) {

        return personService.findById(id);
    }
    @DeleteMapping("/list/{id}")
    public ResponseEntity<Void> delete(

            @PathVariable
            @Positive(
                    message = "O ID deve ser maior que zero"
            )
            Long id
    ) {

        personService.deleteById(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping(
            "/findNacionalityByPerson/{id}"
    )
    public NationalityResponse findNationality(

            @PathVariable
            @Positive(
                    message = "O ID deve ser maior que zero"
            )
            Long id
    ) {
        return nationalityService.findByPersonId(id);
    }
}
