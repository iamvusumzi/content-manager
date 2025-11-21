package com.iamvusumzi.content_manager.service.content;
import com.iamvusumzi.content_manager.model.Content;
import com.iamvusumzi.content_manager.dto.ContentRequest;
import java.util.List;

public interface ContentService {

    Content createContent(String username, ContentRequest request);
    List<Content> getMyContents(String username);
    List<Content> getAllContents();
    Content getContentById(Integer contentId, String username);
    Content updateContent(String username, Integer contentId, ContentRequest request);
    void deleteContent(String username, Integer contentId);
}
