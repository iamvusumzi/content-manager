package com.iamvusumzi.content_manager.dto;

import java.time.LocalDateTime;

public class ContentResponse {
    private Integer id;
    private String title;
    private String desc;
    private String Status;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;

    public Integer getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }
    public String getStatus() {
        return Status;
    }
    public LocalDateTime getDateCreated() {
        return dateCreated;
    }
    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

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
}
