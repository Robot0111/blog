package com.leolxthy.blog.repository;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class ModernJdbcTokenRepository implements PersistentTokenRepository {

    private final JdbcTemplate jdbcTemplate;

    public ModernJdbcTokenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        jdbcTemplate.update("insert into persistent_logins (username, series, token, last_used) values(?,?,?,?)",
                token.getUsername(), token.getSeries(), token.getTokenValue(), token.getDate());
    }

    @Override
    public void updateToken(@NonNull String series, @NonNull String tokenValue, @NonNull Date lastUsed) {
        jdbcTemplate.update("update persistent_logins set token = ?, last_used = ? where series = ?",
                tokenValue, lastUsed, series);
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(@NonNull String seriesId) {
        String sql = "select username, series, token, last_used from persistent_logins where series = ?";

        List<PersistentRememberMeToken> tokens = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new PersistentRememberMeToken(
                        rs.getString("username"),
                        rs.getString("series"),
                        rs.getString("token"),
                        rs.getTimestamp("last_used")),
                seriesId);

        // 如果列表为空，返回 null 是符合 Spring Security 接口约定的
        return tokens.isEmpty() ? null : tokens.getFirst();
    }

    @Override
    public void removeUserTokens(@NonNull String username) {
        jdbcTemplate.update("delete from persistent_logins where username = ?", username);
    }
}