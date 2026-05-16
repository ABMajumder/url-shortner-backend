package com.url.shortner.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String originalUrl;
    private String shortUrl;
    private int clickcount = 0;
    private LocalDateTime createdDate;

    /*create many to one relationship between UrlMapping and User */
    @ManyToOne
    @JoinColumn(name = "user_id") // foreign key of the db
    private User user;

    /*create one to many relationship between UrlMapping and clickEvent */
    @OneToMany(mappedBy = "urlMapping")
    private List<ClickEvent> clickEvents;
}
