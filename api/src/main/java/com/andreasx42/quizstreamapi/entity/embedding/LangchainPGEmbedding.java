package com.andreasx42.quizstreamapi.entity.embedding;

import com.andreasx42.quizstreamapi.entity.collection.LangchainPGCollection;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.List;
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

    @Type(JsonType.class)
    @Column(name = "embedding", columnDefinition = "vector", insertable = false, updatable = false)
    private List<Double> embedding;

    @Column(name = "document")
    private String document;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb", updatable = false, insertable = false)
    private EmbeddingMetadata cmetadata;

    @ManyToOne
    @JoinColumn(name = "collection_id", insertable = false, updatable = false)
    private LangchainPGCollection collection;

}