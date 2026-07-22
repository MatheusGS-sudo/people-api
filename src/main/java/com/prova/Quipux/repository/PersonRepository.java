package com.prova.Quipux.repository;

import com.prova.Quipux.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
    boolean existsByDocumento(String documento);
    boolean existsByEmailIgnoreCase(String email);
}
