package com.iamvusumzi.content_manager.service;

import com.iamvusumzi.content_manager.dto.ContentRequest;
import com.iamvusumzi.content_manager.model.Content;
import com.iamvusumzi.content_manager.model.Status;
import com.iamvusumzi.content_manager.repository.ContentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class ContentService {

    private final ContentRepository contentRepository;

    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public Content createContent(ContentRequest request) {
        Content content = new Content();
        content.setTitle(request.getTitle());
        content.setDesc(request.getDesc());
        content.setStatus(Status.valueOf(request.getStatus().toUpperCase()));
        content.setDateCreated(LocalDateTime.now());
        return contentRepository.save(content);
    }

    public Content updateContent(Integer id, ContentRequest request) {
        return contentRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(request.getTitle());
                    existing.setDesc(request.getDesc());
                    existing.setStatus(Status.valueOf(request.getStatus().toUpperCase()));
                    existing.setDateUpdated(LocalDateTime.now());
                    return contentRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Content not found with id " + id));
    }

    public List<Content> getAllContent() {
        return contentRepository.findAll();
    }

    public Optional<Content> getContentById(Integer id) {
        return contentRepository.findById(id);
    }

    public void deleteContentById(Integer id) {
        contentRepository.deleteById(id);
    }

}

