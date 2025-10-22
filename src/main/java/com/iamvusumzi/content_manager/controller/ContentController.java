package com.iamvusumzi.content_manager.controller;

import com.iamvusumzi.content_manager.dto.ContentRequest;
import com.iamvusumzi.content_manager.dto.ContentResponse;
import com.iamvusumzi.content_manager.model.Content;
import com.iamvusumzi.content_manager.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/contents")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @PostMapping
    public ResponseEntity<ContentResponse> createContent(@Valid @RequestBody ContentRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Content createdContent = contentService.createContent(username, request);
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
        Content updatedContent = contentService.updateContent(username, id, request);
        return ResponseEntity.ok(mapToResponse(updatedContent));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Integer id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        contentService.deleteContentById(username, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ContentResponse>> getAllPublishedContents() {
        List<Content> contents = contentService.getPublichedContents();
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
        List<Content> contents = contentService.getMyContents(username);
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
        Content content = contentService.getContentById(id, username);

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
