package com.iamvusumzi.content_manager.service.content.impl;

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
 * Tests for UserContentServiceImpl — user-level content management behavior.
 */
@ExtendWith(MockitoExtension.class)
class UserContentServiceImplTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserContentServiceImpl userContentService;

    private User user;
    private Content content;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("vusumzi");

        content = new Content();
        content.setId(10);
        content.setTitle("Draft Post");
        content.setStatus(Status.DRAFT);
        content.setAuthor(user);
        content.setDateCreated(LocalDateTime.now());
    }

    // ----------------------------
    // createContent()
    // ----------------------------
    @Test
    void shouldCreateContentSuccessfully() {
        ContentRequest req = new ContentRequest();
        req.setTitle("My Article");
        req.setDesc("Description");
        req.setStatus("DRAFT");

        when(userRepository.findByUsername("vusumzi")).thenReturn(Optional.of(user));
        when(contentRepository.save(any(Content.class))).thenAnswer(inv -> inv.getArgument(0));

        Content created = userContentService.createContent("vusumzi", req);

        assertThat(created.getTitle()).isEqualTo("My Article");
        assertThat(created.getAuthor()).isEqualTo(user);
        verify(contentRepository).save(any(Content.class));
    }

    // ----------------------------
    // getMyContents()
    // ----------------------------
    @Test
    void shouldReturnMyContents() {
        when(userRepository.findByUsername("vusumzi")).thenReturn(Optional.of(user));
        when(contentRepository.findByAuthorIdOrderByDateCreatedDesc(user.getId()))
                .thenReturn(List.of(content));

        List<Content> results = userContentService.getMyContents("vusumzi");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Draft Post");
        verify(contentRepository).findByAuthorIdOrderByDateCreatedDesc(user.getId());
    }

    // ----------------------------
    // getAllContents()
    // ----------------------------
    @Test
    void shouldReturnAllPublishedContents() {
        Content published = new Content();
        published.setId(2);
        published.setStatus(Status.PUBLISHED);

        when(contentRepository.findByStatusOrderByDateCreatedDesc(Status.PUBLISHED))
                .thenReturn(List.of(published));

        List<Content> results = userContentService.getAllContents();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo(Status.PUBLISHED);
        verify(contentRepository).findByStatusOrderByDateCreatedDesc(Status.PUBLISHED);
    }

    // ----------------------------
    // getContentById()
    // ----------------------------
    @Test
    void shouldReturnPublishedContentRegardlessOfAuthor() {
        content.setStatus(Status.PUBLISHED);
        when(contentRepository.findById(10)).thenReturn(Optional.of(content));

        Content result = userContentService.getContentById(10, "someoneElse");

        assertThat(result).isEqualTo(content);
    }

    @Test
    void shouldReturnDraftIfAuthor() {
        when(contentRepository.findById(10)).thenReturn(Optional.of(content));
        when(userRepository.findByUsername("vusumzi")).thenReturn(Optional.of(user));

        Content result = userContentService.getContentById(10, "vusumzi");

        assertThat(result).isEqualTo(content);
    }

    @Test
    void shouldThrowAccessDeniedIfNotAuthorAndNotPublished() {
        User another = new User();
        another.setId(2);
        another.setUsername("someoneElse");
        content.setAuthor(another);
        content.setStatus(Status.DRAFT);

        when(contentRepository.findById(10)).thenReturn(Optional.of(content));
        when(userRepository.findByUsername("vusumzi")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userContentService.getContentById(10, "vusumzi"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("not authorized");
    }

    @Test
    void shouldThrowWhenContentNotFound_OnGetById() {
        when(contentRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userContentService.getContentById(99, "vusumzi"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Content not found");
    }

    // ----------------------------
    // updateContent()
    // ----------------------------
    @Test
    void shouldUpdateContentSuccessfully() {
        ContentRequest req = new ContentRequest();
        req.setTitle("Updated Title");
        req.setDesc("Updated Desc");
        req.setStatus("PUBLISHED");

        when(userRepository.findByUsername("vusumzi")).thenReturn(Optional.of(user));
        when(contentRepository.findById(content.getId())).thenReturn(Optional.of(content));
        when(contentRepository.save(any(Content.class))).thenAnswer(inv -> inv.getArgument(0));

        Content updated = userContentService.updateContent("vusumzi", content.getId(), req);

        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getStatus()).isEqualTo(Status.PUBLISHED);
    }

    // ----------------------------
    // deleteContent()
    // ----------------------------
    @Test
    void shouldDeleteOwnContent() {
        when(userRepository.findByUsername("vusumzi")).thenReturn(Optional.of(user));
        when(contentRepository.findById(10)).thenReturn(Optional.of(content));

        userContentService.deleteContent("vusumzi", 10);

        verify(contentRepository).delete(content);
    }

    @Test
    void shouldThrowAccessDeniedWhenDeletingSomeoneElsesContent() {
        User another = new User();
        another.setId(2);
        content.setAuthor(another);

        when(userRepository.findByUsername("vusumzi")).thenReturn(Optional.of(user));
        when(contentRepository.findById(10)).thenReturn(Optional.of(content));

        assertThatThrownBy(() -> userContentService.deleteContent("vusumzi", 10))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("not authorized");
    }

    @Test
    void shouldThrowWhenContentNotFound_OnDelete() {
        when(userRepository.findByUsername("vusumzi")).thenReturn(Optional.of(user));
        when(contentRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userContentService.deleteContent("vusumzi", 99))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Content not found");
    }
}
