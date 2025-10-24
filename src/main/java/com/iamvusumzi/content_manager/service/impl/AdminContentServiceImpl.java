package com.iamvusumzi.content_manager.service.impl;

import com.iamvusumzi.content_manager.dto.ContentRequest;
import com.iamvusumzi.content_manager.model.Content;
import com.iamvusumzi.content_manager.model.User;
import com.iamvusumzi.content_manager.repository.ContentRepository;
import com.iamvusumzi.content_manager.repository.UserRepository;
import com.iamvusumzi.content_manager.service.ContentService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("adminContentService")
public class AdminContentServiceImpl extends BaseContentService implements ContentService {

    public AdminContentServiceImpl(ContentRepository contentRepository, UserRepository userRepository) {
        super(contentRepository, userRepository);
    }

    @Override
    public Content createContent(String username, ContentRequest request) {
        return createNewContent(username, request);
    }

    @Override
    public List<Content> getMyContents(String username) {
        return findContentByAuthor(username);
    }

    public List<Content> getAllContents() {
        return contentRepository.findAll();
    }

    @Override
    public Content getContentById(Integer contentId, String username) {
        return contentRepository.findById(contentId)
                .orElseThrow(()-> new RuntimeException("Content not found"));
    }

    @Override
    public Content updateContent(String username, Integer contentId, ContentRequest request) {
        return updateContentCommon(username, contentId, request);
    }

    @Override
    public void deleteContent(String username, Integer contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(()-> new RuntimeException("Content not found"));

        contentRepository.delete(content);
    }
}
