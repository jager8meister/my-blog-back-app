package com.myblogbackapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "post_text", columnDefinition = "TEXT")
    private String text;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @Column(name = "likes_count")
    private int likesCount = 0;

    @Column(name = "comments_count")
    private int commentsCount = 0;

    @JsonIgnore
    @Lob
    @Column(name = "image_data", columnDefinition = "BYTEA")
    private byte[] imageData;

    @JsonIgnore
    @Column(name = "image_content_type")
    private String imageContentType;

    @JsonIgnore
    @Column(name = "image_filename")
    private String imageFilename;
}
