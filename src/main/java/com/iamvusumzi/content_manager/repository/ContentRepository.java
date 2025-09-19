package com.iamvusumzi.content_manager.repository;

import com.iamvusumzi.content_manager.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Integer> {
}
