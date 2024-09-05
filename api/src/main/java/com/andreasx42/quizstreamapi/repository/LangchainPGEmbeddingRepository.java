package com.andreasx42.quizstreamapi.repository;

import com.andreasx42.quizstreamapi.entity.embedding.LangchainPGEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LangchainPGEmbeddingRepository extends JpaRepository<LangchainPGEmbedding, String> {

}
