package app.quizstream.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import app.quizstream.entity.collection.LangchainPGCollection;

import java.util.Optional;
import java.util.UUID;

public interface LangchainPGCollectionRepository extends JpaRepository<LangchainPGCollection, UUID> {

    Optional<LangchainPGCollection> findByName(String quizName);


}
