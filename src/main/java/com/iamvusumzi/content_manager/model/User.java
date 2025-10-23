package com.iamvusumzi.content_manager.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Content> contents;

    public User() {
        contents = new ArrayList<>();
    }
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.contents = new ArrayList<>();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public List<Content> getContents() { return contents; }

    public  void addContent(Content content) {
        content.setAuthor(this);
        contents.add(content);
    }

    public void removeContent(Content content) {
        content.setAuthor(null);
        contents.remove(content);
    }
}
