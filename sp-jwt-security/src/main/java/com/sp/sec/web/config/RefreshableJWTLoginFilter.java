package com.sp.sec.web.config;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.sec.user.domain.User;
import com.sp.sec.user.service.UserService;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RefreshableJWTLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public RefreshableJWTLoginFilter(AuthenticationManager authenticationManager, UserService userService, JWTUtil jwtUtil, ObjectMapper objectMapper){
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        setFilterProcessesUrl("/login");
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UserLogin userLogin = objectMapper.readValue(request.getInputStream(), UserLogin.class);

        // id password login
        if(userLogin.getType().equals(UserLogin.Type.login)){
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userLogin.getUsername(), userLogin.getPassword(), null
            );
            return authenticationManager.authenticate(authToken);
        }else if(userLogin.getType().equals(UserLogin.Type.refresh)){
            // refresh token
            if(StringUtils.isEmpty(userLogin.getRefreshToken()))
                throw new IllegalArgumentException("리프레쉬 토큰이 필요함. : "+userLogin.getRefreshToken());

            VerifyResult result = jwtUtil.verify(userLogin.getRefreshToken());
            if(result.isResult()){
                User user = userService.findUser(result.getUserId()).orElseThrow(() -> new UsernameNotFoundException("알 수 없는 사용자 : " + result.getUserId()));
                return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            }else{
                throw new TokenExpiredException("리프레쉬 토큰 만료");
            }
        }else{
            throw new IllegalArgumentException("알 수 없는 타입 : "+userLogin.getType());
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException
    {
        User user = (User)authResult.getPrincipal();
        response.addHeader(JWTUtil.AUTH_HEADER, JWTUtil.BEARER+jwtUtil.generate(user.getUserId(),  JWTUtil.TokenType.access));
        response.addHeader(JWTUtil.REFRESH_HEADER, jwtUtil.generate(user.getUserId(),  JWTUtil.TokenType.refresh));
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException
    {
        System.out.println(failed.getMessage());
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
