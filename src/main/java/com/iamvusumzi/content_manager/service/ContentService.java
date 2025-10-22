package com.iamvusumzi.content_manager.service;

import com.iamvusumzi.content_manager.dto.ContentRequest;
import com.iamvusumzi.content_manager.model.Content;
import com.iamvusumzi.content_manager.model.Status;
import com.iamvusumzi.content_manager.model.User;
import com.iamvusumzi.content_manager.repository.ContentRepository;
import com.iamvusumzi.content_manager.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class ContentService {

    private final ContentRepository contentRepository;

    private final UserRepository  userRepository;

    public ContentService(ContentRepository contentRepository, UserRepository userRepository) {
        this.contentRepository = contentRepository;
        this.userRepository = userRepository;
    }

    public Content createContent(String username, ContentRequest request) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("Username not found: "+ username));

        Content content = new Content();
        content.setTitle(request.getTitle());
        content.setDesc(request.getDesc());
        content.setStatus(Status.valueOf(request.getStatus().toUpperCase()));
        content.setDateCreated(LocalDateTime.now());
        author.addContent(content);

        return contentRepository.save(content);
    }

    public Content updateContent(String username, Integer id, ContentRequest request) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found: "+ username));

        Content existing = contentRepository.findById(id).orElseThrow(()-> new RuntimeException("Content not found with id: "+ id));

        boolean isAuthor = existing.getAuthor().getId().equals(author.getId());

        if (!isAuthor) {
            throw new AccessDeniedException("You are not allowed to update this content.");
        }

        existing.setTitle(request.getTitle());
        existing.setDesc(request.getDesc());
        existing.setStatus(Status.valueOf(request.getStatus().toUpperCase()));
        existing.setDateUpdated(LocalDateTime.now());

        return contentRepository.save(existing);
    }

    public List<Content> getPublichedContents() {
        return contentRepository.findByStatusOrderByDateCreatedDesc(Status.PUBLISHED);
    }

    public Content getContentById(Integer id, String username) {
        Content content = contentRepository.findById(id).orElseThrow(()-> new RuntimeException("Content not found with id: "+ id));
        if (content.getStatus().equals(Status.PUBLISHED)) {
            return content;
        }

        User author = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found: "+ username));

        boolean isAuthor = content.getAuthor().getId().equals(author.getId());

        if (!isAuthor) {
            throw new AccessDeniedException("You are not allowed to view this content.");
        }

        return content;
    }

    public List<Content> getMyContents(String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found: "+ username));
        return  contentRepository.findByAuthorIdOrderByDateCreatedDesc(author.getId());
    }

    public void deleteContentById(String username, Integer id) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found: "+ username));

        Content existing = contentRepository.findById(id).orElseThrow(()-> new RuntimeException("Content not found with id: "+ id));

        boolean isAuthor = existing.getAuthor().getId().equals(author.getId());

        if (!isAuthor) {
            throw new AccessDeniedException("You are not allowed to delete this content.");
        }
        author.removeContent(existing);
    }

}

