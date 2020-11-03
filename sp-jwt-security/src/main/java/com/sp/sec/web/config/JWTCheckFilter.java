package com.sp.sec.web.config;

import com.sp.sec.user.domain.User;
import com.sp.sec.user.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTCheckFilter extends BasicAuthenticationFilter {

    private final UserService userService;
    private final JWTUtil jwtUtil;

    public JWTCheckFilter(AuthenticationManager authenticationManager, UserService userService, JWTUtil jwtUtil) {
        super(authenticationManager);
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException
    {
        String token = request.getHeader(JWTUtil.AUTH_HEADER);
        if(token == null || !token.startsWith(JWTUtil.BEARER)){
            chain.doFilter(request, response);
            return;
        }
        VerifyResult result = jwtUtil.verify(token.substring(JWTUtil.BEARER.length()));
        if(result.isResult()){
            // TODO : user cacher ....
            User user = userService.findUser(result.getUserId()).get();
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
            );
        }
        chain.doFilter(request, response);
    }
}
