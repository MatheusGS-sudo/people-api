package com.prova.Quipux.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "people",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_pessoa_documento",
                        columnNames = "documento"
                ),
                @UniqueConstraint(
                        name = "uk_people_email",
                        columnNames = "email"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            length = 60
    )
    private String nome;
    @Column(
            nullable = false,
            length = 60
    )
    private String sobrenome;
    @Column(
            nullable = false,
            length = 11
    )
    private String documento;
    @Column(
            nullable = false,
            length = 100
    )
    private String email;
}
