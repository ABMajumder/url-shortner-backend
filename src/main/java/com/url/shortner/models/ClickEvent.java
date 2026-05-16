package com.url.shortner.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ClickEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  id;
    private LocalDateTime clickDate;

    /* create many to one relationship between clickEvent and urlMapping */
    @ManyToOne
    @JoinColumn(name = "url_mapping_id")  //foreign key of the db
    private UrlMapping urlMapping;
}
