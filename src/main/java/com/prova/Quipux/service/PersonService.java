package com.prova.Quipux.service;

import com.prova.Quipux.dto.PersonRequest;
import com.prova.Quipux.dto.PersonResponse;
import com.prova.Quipux.entity.Person;
import com.prova.Quipux.exception.DuplicateResourceException;
import com.prova.Quipux.exception.PersonNotFoundException;
import com.prova.Quipux.repository.PersonRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    @Transactional
    public PersonResponse register(PersonRequest request) {

        String documento = request.documento().trim();

        String email = request
                .email()
                .trim()
                .toLowerCase();

        if (personRepository.existsByDocumento(documento)) {
            throw new DuplicateResourceException(
                    "Já existe uma pessoa com esse documento"
            );
        }

        if (personRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException(
                    "Já existe uma pessoa com esse e-mail"
            );
        }

        Person person = Person.builder()
                .documento(documento)
                .nome(request.nome().trim())
                .sobrenome(request.sobrenome().trim())
                .email(email)
                .build();

        Person savedPerson = personRepository.save(person);

        return toResponse(savedPerson);
    }

    @Transactional(readOnly = true)
    public List<PersonResponse> listAll(String ordenarPor) {

        Sort sort = Sort.by(
                Sort.Direction.ASC,
                ordenarPor
        );

        return personRepository
                .findAll(sort)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PersonResponse findById(Long id) {
        Person person = findEntityById(id);

        return toResponse(person);
    }

    @Transactional(readOnly = true)
    public Person findEntityById(Long id) {

        return personRepository
                .findById(id)
                .orElseThrow(
                        () -> new PersonNotFoundException(id)
                );
    }

    @Transactional
    public void deleteById(Long id) {

        Person person = findEntityById(id);

        personRepository.delete(person);
    }

    private PersonResponse toResponse(Person person) {

        return new PersonResponse(
                person.getId(),
                person.getDocumento(),
                person.getNome(),
                person.getSobrenome(),
                person.getEmail()
        );
    }
}