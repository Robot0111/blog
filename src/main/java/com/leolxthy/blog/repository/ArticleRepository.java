package com.leolxthy.blog.repository;

import com.leolxthy.blog.Entity.BlogArticle;
import com.leolxthy.blog.Utils.PinyinUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class ArticleRepository {

    private final JdbcTemplate jdbcTemplate;

    public ArticleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Long saveArticle(String contentMd, String contentHtml, Integer categoryId, List<Integer> tagIds) {
        // --- 1. 自动解析标题 ---
        String title = extractTitle(contentMd);

        // --- 2. 自动生成摘要 ---
        String summary = extractSummary(contentMd);

        // --- 3. 自动生成 SubUrl (例如：my-first-blog-171123456) ---
        String subUrl = PinyinUtils.toSlug(title); // 转换为拼音 slug

        // --- 4. 执行数据库插入 ---
        String sql = "INSERT INTO blog_article (" +
                "title, summary, sub_url, content_md, content_html, category_id, status" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"});
            ps.setString(1, title);
            ps.setString(2, summary);
            ps.setString(3, subUrl);
            ps.setString(4, contentMd);
            ps.setString(5, contentHtml);
            ps.setObject(6, categoryId);
            ps.setInt(7, 1); // 默认发布状态
            return ps;
        }, keyHolder);

        Long articleId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        // --- 5. 关联标签 ---
        if (tagIds != null && !tagIds.isEmpty()) {
            String tagSql = "INSERT INTO blog_article_tag (article_id, tag_id) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(tagSql, tagIds.stream()
                    .map(tagId -> new Object[]{articleId, tagId}).toList());
        }
        return articleId;
    }

    // 正则提取标题
    private String extractTitle(String md) {
        Pattern p = Pattern.compile("^###\\s+(.*)$", Pattern.MULTILINE);
        Matcher m = p.matcher(md);
        return m.find() ? m.group(1).trim() : "未命名文章_" + LocalDate.now();
    }

    // 提取第一段作为摘要
    private String extractSummary(String md) {
        // 移除 #, **, > 等 Markdown 符号
        String plain = md.replaceAll("(?m)^#+\\s+", "").replaceAll("[\\*`>]", "").trim();
        String firstLine = plain.split("\\n")[0];
        return firstLine.length() > 150 ? firstLine.substring(0, 150) : firstLine;
    }
}

//public List<Map<String, Object>> searchArticles(String keyword) {
//    // CONTAINS 是 Oracle 全文检索专有语法
//    String sql = "SELECT id, title, summary FROM blog_article " +
//            "WHERE CONTAINS(content_md, ?) > 0 AND is_deleted = 0";
//    return jdbcTemplate.queryForList(sql, keyword);
//}