package com.iamvusumzi.content_manager.repository;

import com.iamvusumzi.content_manager.model.Content;
import com.iamvusumzi.content_manager.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Integer> {
    List<Content> findByStatusOrderByDateCreatedDesc(Status status);
    List<Content> findByAuthorIdOrderByDateCreatedDesc(Integer authorId);
}
