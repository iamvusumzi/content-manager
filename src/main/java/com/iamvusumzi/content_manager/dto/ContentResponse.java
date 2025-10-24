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

    public Integer getId() { return id; }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDesc() { return desc; }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getStatus() { return Status; }
    public void setStatus(String status) {
        Status = status;
    }
    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }
    public LocalDateTime getDateUpdated() { return dateUpdated; }
    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

}
