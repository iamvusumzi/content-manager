package com.iamvusumzi.content_manager.controller;

import com.iamvusumzi.content_manager.dto.ContentRequest;
import com.iamvusumzi.content_manager.dto.ContentResponse;
import com.iamvusumzi.content_manager.model.Content;
import com.iamvusumzi.content_manager.service.ContentService;
import com.iamvusumzi.content_manager.service.impl.AdminContentServiceImpl;
import com.iamvusumzi.content_manager.service.impl.UserContentServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/contents")
public class ContentController {

    private final UserContentServiceImpl userContentService;
    private final AdminContentServiceImpl  adminContentService;

    public ContentController(@Qualifier("userContentService") ContentService userContentService,
                             @Qualifier("adminContentService") ContentService adminContentService) {
        this.userContentService = ( UserContentServiceImpl ) userContentService;
        this.adminContentService = (AdminContentServiceImpl) adminContentService;
    }

    @PostMapping
    public ResponseEntity<ContentResponse> createContent(@Valid @RequestBody ContentRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        ContentService service = getContentService();

        Content createdContent = service.createContent(username, request);
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

        ContentService service = getContentService();

        Content updatedContent = service.updateContent(username, id, request);
        return ResponseEntity.ok(mapToResponse(updatedContent));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Integer id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        ContentService service = getContentService();
        service.deleteContent(username, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ContentResponse>> getAllContents() {

        ContentService service = getContentService();

        List<Content> contents = service.getAllContents();
        List<ContentResponse> response = contents.stream()
                .map(this::mapToResponse)
                .toList();
        if(response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ContentResponse>> getMyContents() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        ContentService service = getContentService();

        List<Content> contents = service.getMyContents(username);
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

        ContentService service = getContentService();

        Content content = service.getContentById(id, username);
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

    private ContentService getContentService() {
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        if (role.equals("ROLE_ADMIN")) {
            return adminContentService;
        }
        return userContentService;
    }

}
