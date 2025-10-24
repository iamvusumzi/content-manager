package com.iamvusumzi.content_manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iamvusumzi.content_manager.dto.ContentRequest;
import com.iamvusumzi.content_manager.model.Content;
import com.iamvusumzi.content_manager.model.Status;
import com.iamvusumzi.content_manager.model.User;
import com.iamvusumzi.content_manager.security.JwtUtil;
import com.iamvusumzi.content_manager.service.impl.AdminContentServiceImpl;
import com.iamvusumzi.content_manager.service.impl.UserContentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContentController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "userContentService")
    private UserContentServiceImpl userContentService;

    @MockBean(name = "adminContentService")
    private AdminContentServiceImpl adminContentService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Content content;

    @BeforeEach
    void setUp() {
        User author = new User();
        author.setId(1);
        author.setUsername("vusumzi");

        content = new Content();
        content.setId(10);
        content.setTitle("Spring Testing");
        content.setDesc("Sample content");
        content.setStatus(Status.PUBLISHED);
        content.setAuthor(author);
        content.setDateCreated(LocalDateTime.now());
    }

    private void setAuth(String username, String role) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null,
                        List.of(new SimpleGrantedAuthority(role)))
        );
    }

    @Test
    void shouldCreateContentAsUser() throws Exception {
        setAuth("vusumzi", "ROLE_USER");

        ContentRequest request = new ContentRequest();
        request.setTitle("User Post");
        request.setDesc("Test content");
        request.setStatus("DRAFT");

        when(userContentService.createContent(anyString(), any(ContentRequest.class)))
                .thenReturn(content);

        mockMvc.perform(post("/api/contents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Spring Testing"))
                .andExpect(jsonPath("$.author").value("vusumzi"));
    }

    @Test
    void shouldCreateContentAsAdmin() throws Exception {
        setAuth("admin", "ROLE_ADMIN");

        ContentRequest request = new ContentRequest();
        request.setTitle("Admin Post");
        request.setDesc("Admin Description");
        request.setStatus("PUBLISHED");

        when(adminContentService.createContent(anyString(), any(ContentRequest.class)))
                .thenReturn(content);

        mockMvc.perform(post("/api/contents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Spring Testing"))
                .andExpect(jsonPath("$.author").value("vusumzi"));
    }

    @Test
    void shouldGetAllContentsAsUser() throws Exception {
        setAuth("vusumzi", "ROLE_USER");
        when(userContentService.getAllContents()).thenReturn(List.of(content));

        mockMvc.perform(get("/api/contents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Spring Testing"))
                .andExpect(jsonPath("$[0].author").value("vusumzi"));
    }

    @Test
    void shouldGetAllContentsAsAdmin() throws Exception {
        setAuth("admin", "ROLE_ADMIN");
        when(adminContentService.getAllContents()).thenReturn(List.of(content));

        mockMvc.perform(get("/api/contents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Spring Testing"));
    }

    @Test
    void shouldReturnNoContentWhenListEmpty() throws Exception {
        setAuth("vusumzi", "ROLE_USER");
        when(userContentService.getAllContents()).thenReturn(List.of());

        mockMvc.perform(get("/api/contents"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetMyContentsAsUser() throws Exception {
        setAuth("vusumzi", "ROLE_USER");

        when(userContentService.getMyContents("vusumzi")).thenReturn(List.of(content));

        mockMvc.perform(get("/api/contents/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Spring Testing"))
                .andExpect(jsonPath("$[0].author").value("vusumzi"));
    }

    @Test
    void shouldReturnNoContentWhenMyListEmpty() throws Exception {
        setAuth("vusumzi", "ROLE_USER");

        when(userContentService.getMyContents("vusumzi")).thenReturn(List.of());

        mockMvc.perform(get("/api/contents/my"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetContentByIdAsUser() throws Exception {
        setAuth("vusumzi", "ROLE_USER");
        when(userContentService.getContentById(eq(10), anyString())).thenReturn(content);

        mockMvc.perform(get("/api/contents/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring Testing"));
    }

    @Test
    void shouldGetContentByIdAsAdmin() throws Exception {
        setAuth("admin", "ROLE_ADMIN");
        when(adminContentService.getContentById(eq(10), anyString())).thenReturn(content);

        mockMvc.perform(get("/api/contents/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring Testing"));
    }

    @Test
    void shouldDeleteContentAsUser() throws Exception {
        setAuth("vusumzi", "ROLE_USER");
        Mockito.doNothing().when(userContentService).deleteContent("vusumzi", 10);

        mockMvc.perform(delete("/api/contents/10"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldUpdateContentAsUser() throws Exception {
        setAuth("vusumzi", "ROLE_USER");
        ContentRequest req = new ContentRequest();
        req.setTitle("Updated Title");
        req.setDesc(content.getDesc());
        req.setStatus(String.valueOf(content.getStatus()));

        when(userContentService.updateContent(eq("vusumzi"), eq(10), any(ContentRequest.class)))
                .thenReturn(content);

        mockMvc.perform(put("/api/contents/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring Testing"));
    }
}

