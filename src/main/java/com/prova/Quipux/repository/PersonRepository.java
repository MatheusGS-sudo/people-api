package com.prova.quipux.repository;

import com.prova.quipux.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
    boolean existsByDocumento(String documento);
    boolean existsByEmailIgnoreCase(String email);
}
