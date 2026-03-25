package com.leolxthy.blog.Controller;

import com.leolxthy.blog.repository.CategoryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // 1. 获取分页列表 (GET /api/categories?page=1&keyword=xxx)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        Map<String, Object> data = categoryRepository.findPage(page, size, keyword);
        return ResponseEntity.ok(data);
    }

    // 2. 保存分类 (POST /api/categories)
    // 根据是否传 ID 来判断是新增还是更新
    @PostMapping
    public ResponseEntity<String> save(@RequestBody Map<String, Object> payload) {
        String name = (String) payload.get("name");
        String desc = (String) payload.get("description");
        Object idObj = payload.get("id");

        try {
            if (idObj == null || idObj.toString().isEmpty()) {
                categoryRepository.insert(name, desc);
            } else {
                Long id = Long.parseLong(idObj.toString());
                categoryRepository.update(id, name, desc);
            }
            return ResponseEntity.ok("保存成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("保存失败：" + e.getMessage());
        }
    }

    // 3. 删除分类 (DELETE /api/categories/5)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            categoryRepository.delete(id);
            return ResponseEntity.ok("删除成功");
        } catch (DataIntegrityViolationException e) {
            // 如果 Oracle 报外键约束错误 (ORA-02292)
            return ResponseEntity.status(400).body("删除失败：该分类下已有文章，请先移动或删除相关文章。");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("服务器错误：" + e.getMessage());
        }
    }
}
