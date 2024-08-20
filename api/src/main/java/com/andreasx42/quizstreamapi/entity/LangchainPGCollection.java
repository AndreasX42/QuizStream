package com.andreasx42.quizstreamapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "langchain_pg_collection",
        uniqueConstraints = {
                @UniqueConstraint(name = "langchain_pg_collection_name_key", columnNames = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class LangchainPGCollection {

    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "json")
    private String cmetadata;
}
