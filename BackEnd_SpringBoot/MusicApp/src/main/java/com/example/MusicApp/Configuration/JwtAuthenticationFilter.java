package com.example.MusicApp.Configuration;

import com.example.MusicApp.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
/*
Là một bộ lọc bảo mật (security filter) trong Spring Security,
có nhiệm vụ xác thực JWT (JSON Web Token) từ các yêu cầu HTTP đến
và thiết lập thông tin xác thực vào SecurityContext của Spring Security.
Đây là một cách để bảo vệ API và xác thực người dùng bằng token.
*/
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
                                    throws ServletException, IOException {
        // Lấy token từ header Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Kiểm tra header có hợp lệ không
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Nếu không có token, tiếp tục chuỗi filter
            filterChain.doFilter(request, response);
            return;
        }

        // Trích xuất JWT từ request
        jwt = getJwtFromRequest(request);

        // Kiểm tra tính hợp lệ của JWT
        // Nếu không hợp lệ, tiếp tục chuỗi filter
        if (!jwtService.validateAccessToken(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lấy tên người dùng từ token
        username = jwtService.extractUsername(jwt);

        // Kiểm tra nếu người dùng đã được xác thực chưa trong SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Tải thông tin người dùng từ database
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Kiểm tra token có hợp lệ với người dùng này không
            if (jwtService.validateTokenForUser(jwt, userDetails)) {
                // Tạo đối tượng xác thực cho người dùng
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                // Thiết lập chi tiết thông tin xác thực
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Lưu thông tin xác thực vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Tiếp tục với các filter khác
        filterChain.doFilter(request, response);
    }

    // Phương thức trích xuất JWT từ header
    private String getJwtFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        // Lấy token từ "Bearer <token>"
        return authHeader.substring(7);  // Cắt bỏ "Bearer "
    }

}
