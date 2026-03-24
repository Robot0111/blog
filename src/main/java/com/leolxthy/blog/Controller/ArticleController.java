package com.leolxthy.blog.Controller;

import com.leolxthy.blog.Entity.BlogArticle;
import com.leolxthy.blog.repository.ArticleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleRepository articleRepository;

    public ArticleController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveArticle(@RequestBody BlogArticle article) {
        try {
            long saved = articleRepository.saveArticle(
                     article.getContentMd()
                    ,article.getContentHtml()
                    ,article.getCategoryId()
                    ,article.getTagIds()
            );
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("数据库存入失败: " + e.getMessage());
        }
    }
}
