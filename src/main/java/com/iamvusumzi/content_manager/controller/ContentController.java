package com.iamvusumzi.content_manager.controller;

import com.iamvusumzi.content_manager.dto.ContentRequest;
import com.iamvusumzi.content_manager.dto.ContentResponse;
import com.iamvusumzi.content_manager.model.Content;
import com.iamvusumzi.content_manager.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
        Content createdContent = contentService.createContent(request);
        ContentResponse response = mapToResponse(createdContent);
        return ResponseEntity
                .created(URI.create("/api/contents/" + createdContent.getId()))
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContentResponse> updateContent(
            @PathVariable Integer id,
            @Valid @RequestBody ContentRequest request) {
        Content updatedContent = contentService.updateContent(id, request);
        return ResponseEntity.ok(mapToResponse(updatedContent));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Integer id) {
        contentService.deleteContentById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ContentResponse>> getAllContent() {
        List<ContentResponse> responses = contentService.getAllContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        if(responses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ContentResponse> getContentById(@PathVariable Integer id) {
        return contentService.getContentById(id)
                .map(content -> ResponseEntity.ok(mapToResponse(content)))
                .orElse(ResponseEntity.notFound().build());
    }

    private ContentResponse mapToResponse(Content content) {
        ContentResponse dto = new ContentResponse();
        dto.setId(content.getId());
        dto.setTitle(content.getTitle());
        dto.setDesc(content.getDesc());
        dto.setStatus(content.getStatus().name());
        dto.setDateCreated(content.getDateCreated());
        dto.setDateUpdated(content.getDateUpdated());
        return dto;
    }

}
