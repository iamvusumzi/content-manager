package com.iamvusumzi.content_manager.service.impl;

import com.iamvusumzi.content_manager.dto.ContentRequest;
import com.iamvusumzi.content_manager.model.Content;
import com.iamvusumzi.content_manager.model.Status;
import com.iamvusumzi.content_manager.model.User;
import com.iamvusumzi.content_manager.repository.ContentRepository;
import com.iamvusumzi.content_manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for AdminContentServiceImpl — admin-level operations.
 * Admins can create their own content, update only their own,
 * but can view or delete any content.
 */
@ExtendWith(MockitoExtension.class)
class AdminContentServiceImplTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminContentServiceImpl adminContentService;

    private User admin;
    private Content content;

    @BeforeEach
    void setUp() {
        admin = new User();
        admin.setId(1);
        admin.setUsername("admin");

        User author = new User();
        author.setId(2);
        author.setUsername("vusumzi");

        content = new Content();
        content.setId(10);
        content.setTitle("Sample Content");
        content.setDesc("Test");
        content.setStatus(Status.DRAFT);
        content.setAuthor(author);
        content.setDateCreated(LocalDateTime.now());
    }

    // ----------------------------
    // createContent()
    // ----------------------------
    @Test
    void shouldCreateContentSuccessfully() {
        ContentRequest request = new ContentRequest();
        request.setTitle("Admin Post");
        request.setDesc("Admin description");
        request.setStatus("PUBLISHED");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(contentRepository.save(any(Content.class))).thenAnswer(inv -> inv.getArgument(0));

        Content created = adminContentService.createContent("admin", request);

        assertThat(created.getTitle()).isEqualTo("Admin Post");
        assertThat(created.getAuthor()).isEqualTo(admin);
        verify(contentRepository).save(any(Content.class));
    }

    @Test
    void shouldThrowWhenUserNotFound_OnCreate() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        ContentRequest req = new ContentRequest();
        req.setTitle("Title");
        req.setStatus("DRAFT");

        assertThatThrownBy(() -> adminContentService.createContent("ghost", req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    // ----------------------------
    // getMyContents()
    // ----------------------------
    @Test
    void shouldReturnMyContents() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(contentRepository.findByAuthorIdOrderByDateCreatedDesc(admin.getId()))
                .thenReturn(List.of(content));

        List<Content> results = adminContentService.getMyContents("admin");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Sample Content");
        verify(contentRepository).findByAuthorIdOrderByDateCreatedDesc(admin.getId());
    }

    // ----------------------------
    // getAllContents()
    // ----------------------------
    @Test
    void shouldReturnAllContents() {
        when(contentRepository.findAll()).thenReturn(List.of(content));

        List<Content> results = adminContentService.getAllContents();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Sample Content");
        verify(contentRepository).findAll();
    }

    // ----------------------------
    // getContentById()
    // ----------------------------
    @Test
    void shouldReturnContentById() {
        when(contentRepository.findById(10)).thenReturn(Optional.of(content));

        Content result = adminContentService.getContentById(10, "admin");

        assertThat(result).isEqualTo(content);
    }

    @Test
    void shouldThrowWhenContentNotFound_OnGetById() {
        when(contentRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminContentService.getContentById(99, "admin"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Content not found");
    }

    // ----------------------------
    // updateContent()
    // ----------------------------
    @Test
    void shouldUpdateOwnContentSuccessfully() {
        // content owned by admin
        content.setAuthor(admin);

        ContentRequest req = new ContentRequest();
        req.setTitle("Updated Title");
        req.setDesc("Updated Desc");
        req.setStatus("PUBLISHED");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(contentRepository.findById(content.getId())).thenReturn(Optional.of(content));
        when(contentRepository.save(any(Content.class))).thenAnswer(inv -> inv.getArgument(0));

        Content updated = adminContentService.updateContent("admin", content.getId(), req);

        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getStatus()).isEqualTo(Status.PUBLISHED);
        verify(contentRepository).save(content);
    }

    @Test
    void shouldThrowAccessDeniedWhenUpdatingNotOwnedContent() {
        // content belongs to someone else
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(contentRepository.findById(content.getId())).thenReturn(Optional.of(content));

        ContentRequest req = new ContentRequest();
        req.setTitle("Attempted Update");

        assertThatThrownBy(() -> adminContentService.updateContent("admin", content.getId(), req))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("not authorized");
    }

    // ----------------------------
    // deleteContent()
    // ----------------------------
    @Test
    void shouldDeleteAnyContent() {
        when(contentRepository.findById(10)).thenReturn(Optional.of(content));

        adminContentService.deleteContent("admin", 10);

        verify(contentRepository).delete(content);
    }

    @Test
    void shouldThrowWhenContentNotFound_OnDelete() {
        when(contentRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminContentService.deleteContent("admin", 99))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Content not found");
    }
}
