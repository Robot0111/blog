package com.leolxthy.blog.Controller;

import com.leolxthy.blog.repository.TagRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tags")
public class TagApiController {

    private final TagRepository tagRepository;

    public TagApiController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    // 分页查询
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(tagRepository.findPage(page, size, keyword));
    }

    // 保存 (新增或修改)
    @PostMapping
    public ResponseEntity<String> save(@RequestBody Map<String, Object> payload) {
        String name = (String) payload.get("name");
        Object idObj = payload.get("id");

        try {
            if (idObj == null || idObj.toString().isEmpty()) {
                tagRepository.insert(name);
            } else {
                Long id = Long.parseLong(idObj.toString());
                tagRepository.update(id, name);
            }
            return ResponseEntity.ok("标签保存成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("操作失败：" + e.getMessage());
        }
    }

    // 删除
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            tagRepository.delete(id);
            return ResponseEntity.ok("删除成功");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(400).body("删除失败：该标签已被文章引用，请先解除关联。");
        }
    }
}
