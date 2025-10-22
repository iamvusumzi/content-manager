package com.iamvusumzi.content_manager.dto;

import java.time.LocalDateTime;

public class ContentResponse {
    private Integer id;
    private String title;
    private String desc;
    private String Status;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private String author;

    public void setId(Integer id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public void setStatus(String status) {
        Status = status;
    }
    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }
    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
    public void setAuthor(String author) { this.author = author; }

}
