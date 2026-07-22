package com.dp.plat.system.security;

import com.dp.plat.common.constant.CommonConstants;
import com.dp.plat.framework.common.enums.UserTypeEnum;
import com.dp.plat.framework.security.core.LoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT authentication filter that extracts the token from the Authorization header,
 * validates it, and sets the security context with the authenticated user.
 *
 * <p>适配 yudao 框架：principal 使用 {@link LoginUser}（取代 Spring Security 默认
 * {@code UserDetails}），使 {@link com.dp.plat.framework.security.core.util.SecurityFrameworkUtils#getLoginUserId()}
 * 可直接获取当前用户编号。Authorities（角色权限）仍由 {@link UserAuthorityService} 加载，
 * 以兼容 {@code @PreAuthorize("hasAuthority('xxx')")} 注解校验。Token 黑名单由
 * {@link TokenBlacklistService} 处理。</p>
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
                // 加载角色权限（兼容 @PreAuthorize hasAuthority 注解）
                Collection<GrantedAuthority> authorities = userAuthorityService.loadAuthorities(username);
                // 构建 yudao LoginUser 作为 principal
                LoginUser loginUser = new LoginUser();
                loginUser.setId(userId);
                loginUser.setUserType(UserTypeEnum.ADMIN.getValue());
                Map<String, String> info = new HashMap<>();
                info.put(LoginUser.INFO_KEY_NICKNAME, username);
                loginUser.setInfo(info);
                // 设置认证信息：principal=LoginUser, authorities 来自角色权限
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(loginUser, null, authorities);
                Map<String, Object> details = new HashMap<>();
                details.put("webDetails", new WebAuthenticationDetailsSource().buildDetails(request));
                details.put("username", username);
                authentication.setDetails(details);
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

