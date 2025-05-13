package com.onion.backend.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onion.backend.common.domain.ApiErrorResponse;
import com.onion.backend.common.exception.CustomAccessDeniedException;
import com.onion.backend.common.exception.CustomAuthenticationException;
import com.onion.backend.config.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        return PatternMatchUtils.simpleMatch(SecurityConfig.WHITE_LIST.toArray(new String[0]), requestURI);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomAuthenticationException customAuthenticationException) {
            String error = customAuthenticationException.getError();
            String message = customAuthenticationException.getMessage();

            setResponse(error, message, HttpStatus.FORBIDDEN, response);
        } catch (CustomAccessDeniedException customAccessDeniedException) {
            String error = customAccessDeniedException.getError();
            String message = customAccessDeniedException.getMessage();

            setResponse(error, message, HttpStatus.UNAUTHORIZED, response);
        } catch (Exception e) {
            setResponse(e.getMessage(), "unhandled exception", HttpStatus.UNAUTHORIZED, response);
        }
    }

    private void setResponse(String error, String message, HttpStatus status, HttpServletResponse response)
            throws IOException {

        ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(error, message, status.toString());

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String apiErrorResponseString = objectMapper.writeValueAsString(apiErrorResponse);
        response.getWriter().write(apiErrorResponseString);
    }
}
