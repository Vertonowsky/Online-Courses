package com.example.vertonowsky.chapter;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ChapterRepository extends CrudRepository<Chapter, Integer> {

    @Query("Select c FROM Chapter c LEFT JOIN FETCH c.topics WHERE c.id = :id")
    Optional<Chapter> findById(Integer id);

    @Query("Select c FROM Chapter c LEFT JOIN FETCH c.topics AS t WHERE t.id IN (:topicId)")
    Optional<Chapter> findByTopicId(Integer topicId);

    void delete(Chapter chapter);

}
