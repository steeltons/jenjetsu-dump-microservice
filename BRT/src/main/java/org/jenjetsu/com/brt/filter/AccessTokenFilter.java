package org.jenjetsu.com.brt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jenjetsu.com.core.service.JwtParser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class AccessTokenFilter extends OncePerRequestFilter {

    private final JwtParser jwtParser;

    public AccessTokenFilter(JwtParser jwtParser) {
        this.jwtParser = jwtParser;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> token = parseAccessTokenFromRequest(request);
        if(token.isPresent() && jwtParser.validateAccessToken(token.get())) {
            String role = jwtParser.getRoleFromAccessToken(token.get());
            UserDetails details = User.builder()
                    .username(jwtParser.getUsernameFromAccessToken(token.get()))
                    .roles(role)
                    .password(token.get())
                    .build();
            UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(details, null, Arrays.asList(new SimpleGrantedAuthority(role)));
            SecurityContextHolder.getContext().setAuthentication(upat);
        }
        filterChain.doFilter(request, response);
    }

    private Optional<String> parseAccessTokenFromRequest(HttpServletRequest req) {
        String authHeader = req.getHeader(jwtParser.HEADER_STRING);
        if(StringUtils.hasText(authHeader) && authHeader.startsWith(jwtParser.TOKEN_PREFIX)) {
            return Optional.of(authHeader.replace(jwtParser.TOKEN_PREFIX, ""));
        }
        return Optional.empty();
    }
}
