package com.onion.backend.common.filter;

import static com.onion.backend.common.OnionConstant.AUTHENTICATION_HEADER;
import static com.onion.backend.common.OnionConstant.TOKEN_BEARER;

import com.onion.backend.common.jwt.JwtProvider;
import com.onion.backend.config.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        return PatternMatchUtils.simpleMatch(SecurityConfig.WHITE_LIST.toArray(new String[0]), requestURI);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader(AUTHENTICATION_HEADER);

        if (!StringUtils.hasText(authorization) || !authorization.startsWith(TOKEN_BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtProvider.resolveToken(authorization);

        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("token not found");
        }

        if (jwtProvider.validateToken(token)) {

            Authentication authentication = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
