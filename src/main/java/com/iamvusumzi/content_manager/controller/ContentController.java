package com.iamvusumzi.content_manager.controller;

import com.iamvusumzi.content_manager.dto.ContentRequest;
import com.iamvusumzi.content_manager.dto.ContentResponse;
import com.iamvusumzi.content_manager.model.Content;
import com.iamvusumzi.content_manager.service.impl.UserContentServiceImpl;
import jakarta.validation.Valid;
import org.jboss.logging.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/contents")
public class ContentController {

    private final UserContentServiceImpl userContentService;


    public ContentController(UserContentServiceImpl userContentService) {
        this.userContentService = userContentService;
    }

    @PostMapping
    public ResponseEntity<ContentResponse> createContent(@Valid @RequestBody ContentRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Content createdContent = userContentService.createContent(username, request);
        ContentResponse response = mapToResponse(createdContent);
        return ResponseEntity
                .created(URI.create("/api/contents/" + createdContent.getId()))
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContentResponse> updateContent(
            @PathVariable Integer id,
            @Valid @RequestBody ContentRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Content updatedContent = userContentService.updateContent(username, id, request);
        return ResponseEntity.ok(mapToResponse(updatedContent));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Integer id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userContentService.deleteContent(username, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ContentResponse>> getAllPublishedContents() {
        List<Content> contents = userContentService.getAllPublishedContents();
        List<ContentResponse> response = contents.stream()
                .map(this::mapToResponse)
                .toList();
        if(response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ContentResponse>> getMyContents() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Content> contents = userContentService.getMyContents(username);
        List<ContentResponse> response = contents.stream()
                .map(this::mapToResponse)
                .toList();
        if(response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentResponse> getContentById(@PathVariable Integer id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Content content = userContentService.getContentById(id, username);

        ContentResponse response = mapToResponse(content);

        return ResponseEntity.ok(response);
    }

    private ContentResponse mapToResponse(Content content) {
        ContentResponse dto = new ContentResponse();
        dto.setId(content.getId());
        dto.setTitle(content.getTitle());
        dto.setDesc(content.getDesc());
        dto.setStatus(content.getStatus().name());
        dto.setDateCreated(content.getDateCreated());
        dto.setDateUpdated(content.getDateUpdated());
        dto.setAuthor(content.getAuthor().getUsername());
        return dto;
    }

}
