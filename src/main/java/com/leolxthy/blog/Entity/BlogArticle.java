package com.leolxthy.blog.Entity;

import lombok.Data;

import java.util.List;

@Data
public class BlogArticle {
    private Long id;
    private String coverImage;
    private String contentMd;
    private String contentHtml;
    private Integer categoryId;
    private Integer status;
    private List<Integer> tagIds; // 用于多对多关联
}
