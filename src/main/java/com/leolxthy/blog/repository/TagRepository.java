package com.leolxthy.blog.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TagRepository {


    private final JdbcTemplate jdbcTemplate;

    public TagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. 分页查询 + 搜索 (匹配通用模板的 data.list 和 data.total)
    public Map<String, Object> findPage(int page, int size, String keyword) {
        int offset = (page - 1) * size;
        String baseSql = "FROM blog_tag WHERE name LIKE ?";
        String searchKey = "%" + (keyword == null ? "" : keyword) + "%";

        // 获取总数
        Long total = jdbcTemplate.queryForObject("SELECT COUNT(*) " + baseSql, Long.class, searchKey);

        // 分页获取数据 (Oracle 12c+ 语法)
        String dataSql = "SELECT id, name FROM blog_tag WHERE name LIKE ? " +
                "ORDER BY id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(dataSql, searchKey, offset, size);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return result;
    }

    // 2. 新增标签
    public int insert(String name) {
        return jdbcTemplate.update("INSERT INTO blog_tag (name) VALUES (?)", name);
    }

    // 3. 修改标签
    public int update(Long id, String name) {
        return jdbcTemplate.update("UPDATE blog_tag SET name = ? WHERE id = ?", name, id);
    }

    // 4. 删除标签 (注意：会触发中间表 BLOG_ARTICLE_TAG 的外键约束)
    public int delete(Long id) {
        return jdbcTemplate.update("DELETE FROM blog_tag WHERE id = ?", id);
    }
}