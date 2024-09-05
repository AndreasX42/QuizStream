package com.andreasx42.quizstreamapi.repository;

import com.andreasx42.quizstreamapi.entity.collection.LangchainPGCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LangchainPGCollectionRepository extends JpaRepository<LangchainPGCollection, UUID> {

    Optional<LangchainPGCollection> findByName(String quizName);


}
