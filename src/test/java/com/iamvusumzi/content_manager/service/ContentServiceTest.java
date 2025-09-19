package com.iamvusumzi.content_manager.service;

import com.iamvusumzi.content_manager.dto.ContentRequest;
import com.iamvusumzi.content_manager.model.Content;
import com.iamvusumzi.content_manager.model.Status;
import com.iamvusumzi.content_manager.repository.ContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ContentServiceTest {
    private ContentRepository contentRepository;
    private ContentService contentService;

    @BeforeEach
    void setUp() {
        contentRepository = mock(ContentRepository.class);
        contentService = new ContentService(contentRepository);
    }

    @Test
    void createContent_success() {
        ContentRequest request = new ContentRequest();
        request.setTitle("Test Title");
        request.setDesc("Test Desc");
        request.setStatus("DRAFT");

        when(contentRepository.save(any(Content.class))).thenAnswer(i -> i.getArgument(0));

        Content saved = contentService.createContent(request);

        assertThat(saved.getTitle()).isEqualTo("Test Title");
        assertThat(saved.getStatus()).isEqualTo(Status.DRAFT);
        assertThat(saved.getDateCreated()).isNotNull();
    }

    @Test
    void updateContent_success() {
        Content existing = new Content("Old title", "Old desc", Status.DRAFT);
        existing.setId(1);

        when(contentRepository.findById(1)).thenReturn(Optional.of(existing));
        when(contentRepository.save(any(Content.class))).thenAnswer(i -> i.getArgument(0));

        ContentRequest request = new ContentRequest();
        request.setTitle("New title");
        request.setDesc("New desc");
        request.setStatus("PUBLISHED");

        Content updated = contentService.updateContent(1, request);

        assertThat(updated.getTitle()).isEqualTo("New title");
        assertThat(updated.getStatus()).isEqualTo(Status.PUBLISHED);
        assertThat(updated.getDateUpdated()).isNotNull();

    }
    @Test
    void updateContent_shouldThrowIfNotFound() {
        when(contentRepository.findById(99)).thenReturn(Optional.empty());

        ContentRequest request = new ContentRequest();
        request.setTitle("Updated");
        request.setDesc("Updated desc");
        request.setStatus("PUBLISHED");

        assertThrows(RuntimeException.class, () ->
                contentService.updateContent(99, request));
    }
}
