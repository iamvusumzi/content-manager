package com.iamvusumzi.content_manager.controller;

import com.iamvusumzi.content_manager.dto.ContentRequest;
import com.iamvusumzi.content_manager.model.Content;
import com.iamvusumzi.content_manager.model.Status;
import com.iamvusumzi.content_manager.security.JwtAuthenticationFilter;
import com.iamvusumzi.content_manager.service.ContentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContentController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ContentService contentService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void getAllContents_shouldReturnList() throws Exception {
        Content content = new Content("Test title", "Test desc", Status.DRAFT);
        content.setId(1);
        content.setDateCreated(LocalDateTime.now());

        when(contentService.getAllContent()).thenReturn(List.of(content));

        mockMvc.perform(get("/api/contents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test title"));
    }

    @Test
    void createContent_shouldReturnCreated() throws Exception {
        ContentRequest request = new ContentRequest();
        request.setTitle("New Post");
        request.setDesc("New Desc");
        request.setStatus("DRAFT");

        Content saved = new Content("New Post", "New Desc", Status.DRAFT);
        saved.setId(1);
        saved.setDateCreated(LocalDateTime.now());

        when(contentService.createContent(any(ContentRequest.class))).thenReturn(saved);

        mockMvc.perform(post("/api/contents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Post"));
    }

    @Test
    void getContentById_shouldReturnContent() throws Exception {
        Content content = new Content("Single Post", "Desc", Status.PUBLISHED);
        content.setId(2);
        content.setDateCreated(LocalDateTime.now());

        when(contentService.getContentById(2)).thenReturn(Optional.of(content));

        mockMvc.perform(get("/api/contents/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Single Post"));
    }

    @Test
    void updateContent_shouldReturnUpdated() throws Exception {
        ContentRequest request = new ContentRequest();
        request.setTitle("Updated Title");
        request.setDesc("Updated Desc");
        request.setStatus("PUBLISHED");

        Content updated = new Content("Updated Title", "Updated Desc", Status.PUBLISHED);
        updated.setId(3);
        updated.setDateCreated(LocalDateTime.now());
        updated.setDateUpdated(LocalDateTime.now());

        when(contentService.updateContent(eq(3), any(ContentRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/contents/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void deleteContent_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/contents/4"))
                .andExpect(status().isNoContent());
    }
}
