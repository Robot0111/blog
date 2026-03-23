package com.leolxthy.blog.config;

import com.leolxthy.blog.repository.ModernJdbcTokenRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;
@Configuration
@EnableWebSecurity
public class Security {

    private final DataSource dataSource;

    private final JdbcTemplate jdbcTemplate;

    // 注入数据源
    public Security (DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home", "/login", "/error", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/admin")
                        .permitAll()
                )
                // --- 新增记住我配置 ---
                .rememberMe(remember -> remember
                        .tokenRepository(tokenRepository()) // 设置持久化令牌仓库
                        .tokenValiditySeconds(60 * 60 * 24 * 7) // 设置记住 7 天
                        .userDetailsService(userDetailsService(dataSource)) // 记住我功能需要它来重新加载用户信息
                        .key("leolxthy_secret_key") // 设置一个固定的私钥，防止重启后 Cookie 失效
                        .rememberMeParameter("remember-me") // 对应前端 input 的 name
                )
                // ---------------------
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .deleteCookies("remember-me") // 注销时顺便删掉记住我的 Cookie
                        .permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        // 使用我们自定义的、不依赖过时基类的实现
        return new ModernJdbcTokenRepository(jdbcTemplate);
    }
    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
