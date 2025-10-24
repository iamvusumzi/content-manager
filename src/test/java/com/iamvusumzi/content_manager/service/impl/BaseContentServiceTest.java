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
 * Unit tests for BaseContentService.
 * Uses a concrete subclass (TestableContentService) to test protected methods.
 */
@ExtendWith(MockitoExtension.class)
class BaseContentServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private UserRepository userRepository;

    private BaseContentService baseContentService;

    private User user;
    private Content content;

    @BeforeEach
    void setUp() {
        baseContentService = new TestableContentService(contentRepository, userRepository);

        user = new User();
        user.setId(1);
        user.setUsername("vusumzi");

        content = new Content();
        content.setId(10);
        content.setTitle("Existing");
        content.setAuthor(user);
        content.setStatus(Status.DRAFT);
        content.setDateCreated(LocalDateTime.now());
    }

    // ----------------------------
    // createNewContent()
    // ----------------------------
    @Test
    void shouldCreateNewContentSuccessfully() {
        ContentRequest request = new ContentRequest();
        request.setTitle("My Article");
        request.setDesc("Description");
        request.setStatus("DRAFT");

        when(userRepository.findByUsername("vusumzi")).thenReturn(Optional.of(user));
        when(contentRepository.save(any(Content.class))).thenAnswer(inv -> inv.getArgument(0));

        Content created = baseContentService.createNewContent("vusumzi", request);

        assertThat(created.getAuthor()).isEqualTo(user);
        assertThat(created.getTitle()).isEqualTo("My Article");
        assertThat(created.getStatus()).isEqualTo(Status.DRAFT);
        verify(contentRepository).save(any(Content.class));
    }

    @Test
    void shouldThrowWhenUserNotFound_OnCreate() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        ContentRequest req = new ContentRequest();
        req.setTitle("Title");
        req.setStatus("DRAFT");

        assertThatThrownBy(() -> baseContentService.createNewContent("unknown", req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    // ----------------------------
    // findContentByAuthor()
    // ----------------------------
    @Test
    void shouldFindContentsByAuthor() {
        when(userRepository.findByUsername("vusumzi")).thenReturn(Optional.of(user));
        when(contentRepository.findByAuthorIdOrderByDateCreatedDesc(user.getId()))
                .thenReturn(List.of(content));

        List<Content> result = baseContentService.findContentByAuthor("vusumzi");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Existing");
        verify(contentRepository).findByAuthorIdOrderByDateCreatedDesc(user.getId());
    }

    @Test
    void shouldThrowWhenUserNotFound_OnFindByAuthor() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> baseContentService.findContentByAuthor("ghost"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    // ----------------------------
    // updateContentCommon()
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

        Content updated = baseContentService.updateContentCommon("vusumzi", content.getId(), req);

        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getStatus()).isEqualTo(Status.PUBLISHED);
        verify(contentRepository).save(content);
    }

    @Test
    void shouldThrowWhenNotAuthor_OnUpdate() {
        User anotherUser = new User();
        anotherUser.setId(2);
        anotherUser.setUsername("someoneelse");
        content.setAuthor(anotherUser);

        when(userRepository.findByUsername("vusumzi")).thenReturn(Optional.of(user));
        when(contentRepository.findById(content.getId())).thenReturn(Optional.of(content));

        ContentRequest req = new ContentRequest();
        req.setTitle("Try Update");

        assertThatThrownBy(() -> baseContentService.updateContentCommon("vusumzi", content.getId(), req))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("not authorized");
    }

    @Test
    void shouldThrowWhenContentNotFound_OnUpdate() {
        when(userRepository.findByUsername("vusumzi")).thenReturn(Optional.of(user));
        when(contentRepository.findById(99)).thenReturn(Optional.empty());

        ContentRequest req = new ContentRequest();
        req.setTitle("Update");

        assertThatThrownBy(() -> baseContentService.updateContentCommon("vusumzi", 99, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Content not found");
    }

    // ----------------------------
    // Helper subclass to expose protected methods
    // ----------------------------
    static class TestableContentService extends BaseContentService {
        protected TestableContentService(ContentRepository contentRepository, UserRepository userRepository) {
            super(contentRepository, userRepository);
        }
    }
}
