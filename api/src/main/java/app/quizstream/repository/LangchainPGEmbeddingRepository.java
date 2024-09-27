package app.quizstream.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import app.quizstream.entity.embedding.LangchainPGEmbedding;

public interface LangchainPGEmbeddingRepository extends JpaRepository<LangchainPGEmbedding, String> {

}
