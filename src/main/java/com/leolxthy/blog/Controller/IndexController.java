package com.leolxthy.blog.Controller;

import com.leolxthy.blog.repository.ArticleRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class IndexController {

    private final ArticleRepository articleRepository;

    public IndexController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @GetMapping("/")
    public String index(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 5; // 每页显示条数
        // 获取分页数据
        Map<String, Object> result = articleRepository.findPage(page, pageSize, null);

        model.addAttribute("articles", result.get("list"));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil(((Number) result.get("total")).doubleValue() / pageSize));

        return "index/home";
    }
    @GetMapping("/index/{id}")
    public String articleDetail(@PathVariable Long id, Model model) {
        // 复用你之前写的 findById 方法
        Map<String, Object> article = articleRepository.findById(id);

        if (article == null) {
            return "error/404"; // 如果文章不存在
        }

        // 增加阅读量记录（可选）
       // articleRepository.incrementViews(id);

        model.addAttribute("article", article);
        return "index/detail"; // 对应新的 HTML 模板
    }
}
