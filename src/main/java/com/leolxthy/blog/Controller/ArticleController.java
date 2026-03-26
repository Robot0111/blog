package com.leolxthy.blog.Controller;

import com.leolxthy.blog.Entity.BlogArticle;
import com.leolxthy.blog.repository.ArticleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
                     article.getId()
                    ,article.getContentMd()
                    ,article.getContentHtml()
                    ,article.getCategoryId()
                    ,article.getTagIds()
            );
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("数据库存入失败: " + e.getMessage());
        }
    }
    // 分页获取文章列表
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(articleRepository.findPage(page, size, keyword));
    }

    // 删除文章（逻辑删除）
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        int result = articleRepository.delete(id);
        return result > 0 ? ResponseEntity.ok("删除成功") : ResponseEntity.status(500).body("删除失败");
    }

    // 快速切换状态接口
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> toggleStatus(@PathVariable Long id, @RequestParam int status) {
        int result = articleRepository.updateStatus(id, status);
        return result > 0 ? ResponseEntity.ok("状态更新成功") : ResponseEntity.status(500).body("状态更新失败！");
    }
    //根据id查询文章
    @GetMapping("/{id}")
    public ResponseEntity<?> getArticle(@PathVariable Long id) {
        Map<String, Object> article = articleRepository.findById(id);
        if (article == null) {
            return ResponseEntity.status(404).body("文章不存在");
        }
        return ResponseEntity.ok(article);
    }

}