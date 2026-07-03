package com.dp.plat.system.security;

import com.dp.plat.common.constant.CommonConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

/**
 * JWT authentication filter that extracts the token from the Authorization header,
 * validates it, and sets the security context with the authenticated user.
 *
 * <p>Authorities (role-based permissions) are loaded from the database via
 * {@link UserAuthorityService} (with a short TTL cache) and the token is
 * rejected when it has been blacklisted by {@link TokenBlacklistService}.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserAuthorityService userAuthorityService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            String jti = jwtTokenProvider.getJtiFromToken(token);
            if (jti != null && tokenBlacklistService.isBlacklisted(jti)) {
                log.warn("Blacklisted JWT token rejected, jti={}", jti);
                filterChain.doFilter(request, response);
                return;
            }
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            String username = jwtTokenProvider.getUsernameFromToken(token);
            if (userId != null && username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                Collection<GrantedAuthority> authorities = userAuthorityService.loadAuthorities(username);
                UserDetails userDetails = User.builder()
                        .username(username)
                        .password("")
                        .authorities(authorities)
                        .build();
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(CommonConstants.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(CommonConstants.TOKEN_PREFIX)) {
            return bearerToken.substring(CommonConstants.TOKEN_PREFIX.length());
        }
        return null;
    }
}
