package com.iamvusumzi.content_manager.service.impl;

import com.iamvusumzi.content_manager.dto.ContentRequest;
import com.iamvusumzi.content_manager.model.Content;
import com.iamvusumzi.content_manager.model.Status;
import com.iamvusumzi.content_manager.model.User;
import com.iamvusumzi.content_manager.repository.ContentRepository;
import com.iamvusumzi.content_manager.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public abstract class BaseContentService {
    protected final ContentRepository contentRepository;
    protected final UserRepository userRepository;

    protected BaseContentService(ContentRepository contentRepository, UserRepository userRepository) {
        this.contentRepository = contentRepository;
        this.userRepository = userRepository;
    }

    protected Content createNewContent(String username, ContentRequest request) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) throw new RuntimeException("User not found: " + username);

        User author = user.get();

        Content content = new Content();
        content.setTitle(request.getTitle());
        content.setDesc(request.getDesc());
        content.setStatus(Status.valueOf(request.getStatus().toUpperCase()));
        content.setDateCreated(LocalDateTime.now());
        content.setAuthor(author);

        return contentRepository.save(content);
    }

    protected List<Content> findContentByAuthor(String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return contentRepository.findByAuthorIdOrderByDateCreatedDesc(author.getId());
    }

    protected Content updateContentCommon(String username, Integer contentId, ContentRequest request) {

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Content existing = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));
        boolean isAuthor = existing.getAuthor().getId().equals(currentUser.getId());

        if (!isAuthor) throw new AccessDeniedException("You are not authorized to update this content");

        if (request.getTitle() != null) {
            existing.setTitle(request.getTitle());
        }
        if (request.getDesc() != null) {
            existing.setDesc(request.getDesc());
        }
        if (request.getStatus() != null) {
            existing.setStatus(Status.valueOf(request.getStatus().toUpperCase()));
        }

        existing.setDateUpdated(LocalDateTime.now());

        return contentRepository.save(existing);
    }

    protected Content getContent(String username, Integer contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(()-> new RuntimeException("Content not found"));
        if(content.getStatus().equals(Status.PUBLISHED)) return content;

        User author = userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("User not found"));
        boolean isAuthor = content.getAuthor().getId().equals(author.getId());

        if(!isAuthor) throw new AccessDeniedException("You are not authorized to view this content");
        return content;
    }
}
