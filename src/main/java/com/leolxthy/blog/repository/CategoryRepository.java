package com.leolxthy.blog.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CategoryRepository {


    private final JdbcTemplate jdbcTemplate;

    public CategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. 分页查询 + 搜索
    public Map<String, Object> findPage(int page, int size, String keyword) {
        int offset = (page - 1) * size;
        String baseSql = "FROM blog_category WHERE name LIKE ?";
        String searchKey = "%" + (keyword == null ? "" : keyword) + "%";

        // 查总数
        Long total = jdbcTemplate.queryForObject("SELECT COUNT(*) " + baseSql, Long.class, searchKey);

        // 查数据 (Oracle 12c+ 语法)
        String dataSql = "SELECT id, name, description, create_time " + baseSql +
                " ORDER BY id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(dataSql, searchKey, offset, size);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return result;
    }

    // 2. 新增
    public int insert(String name, String desc) {
        return jdbcTemplate.update("INSERT INTO blog_category (name, description) VALUES (?, ?)", name, desc);
    }

    // 3. 修改
    public int update(Long id, String name, String desc) {
        return jdbcTemplate.update("UPDATE blog_category SET name = ?, description = ? WHERE id = ?", name, desc, id);
    }

    // 4. 删除 (注意：如果分类下有文章，Oracle 会报外键错误)
    public int delete(Long id) {
        return jdbcTemplate.update("DELETE FROM blog_category WHERE id = ?", id);
    }
}
