package com.iamvusumzi.content_manager.service.content.impl;
import com.iamvusumzi.content_manager.dto.ContentRequest;
import com.iamvusumzi.content_manager.model.Content;
import com.iamvusumzi.content_manager.model.Status;
import com.iamvusumzi.content_manager.model.User;
import com.iamvusumzi.content_manager.repository.ContentRepository;
import com.iamvusumzi.content_manager.repository.UserRepository;
import com.iamvusumzi.content_manager.service.content.ContentService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userContentService")
public class UserContentServiceImpl extends BaseContentService implements ContentService {

    public UserContentServiceImpl(ContentRepository contentRepository, UserRepository userRepository) {
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

    @Override
    public List<Content> getAllContents(){
        return contentRepository.findByStatusOrderByDateCreatedDesc(Status.PUBLISHED);
    }

    @Override
    public Content getContentById(Integer contentId, String username) {
        return getContent(username, contentId);
    }

    @Override
    public Content updateContent(String username, Integer contentId, ContentRequest request) {
        return updateContentCommon(username,contentId, request);
    }

    @Override
    public void deleteContent(String username, Integer contentId) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("User not found"));
        Content content = contentRepository.findById(contentId)
                .orElseThrow(()-> new RuntimeException("Content not found"));
        boolean isAuthor = content.getAuthor().getId().equals(currentUser.getId());

        if(!isAuthor) throw new AccessDeniedException("You are not authorized to delete this content");

        contentRepository.delete(content);
    }
}
