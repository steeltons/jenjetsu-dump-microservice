package org.jenjetsu.com.crm.config;

import org.jenjetsu.com.core.entity.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/auth/**").permitAll()
                .anyRequest().permitAll();
        System.out.println("Wac created");
        return http.build();
    }

    /**
     * <h2>In memory user database</h2>
     * Creates UserDetailsService with one manager
     * @return UserDetailsService
     */
    @Bean("managerService")
    public UserDetailsService users() {
        UserDetails user = User.builder()
                .username("admin")
                .password("123321")
                .roles(Role.MANAGER.toString())
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
