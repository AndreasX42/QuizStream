package com.andreasx42.quizstreamapi.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "langchain_pg_embedding")
@Getter
@Setter
@NoArgsConstructor
public class LangchainPGEmbedding {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "collection_id")
    private UUID collectionId;

    @Column(name = "embedding", columnDefinition = "vector", updatable = false)
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 1)
    @Transient
    private float[] embedding;

    @Column(name = "document")
    private String document;

    @Column(columnDefinition = "json", updatable = false)
    private String cmetadata;

    @ManyToOne
    @JoinColumn(name = "collection_id", insertable = false, updatable = false)
    private LangchainPGCollection collection;

}