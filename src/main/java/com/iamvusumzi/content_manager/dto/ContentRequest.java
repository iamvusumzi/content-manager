package com.iamvusumzi.content_manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContentRequest {
    @NotBlank
    @Size(min = 3, message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    private String desc;
    private String status;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getTitle() {
        return title;
    }
    public String getDesc() {
        return desc;
    }
    public String getStatus() {
        return status;
    }
}
