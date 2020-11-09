package com.sp.sec.web.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JWTUtil {

    public static final String AUTH_HEADER = "Authentication";
    public static final String REFRESH_HEADER = "refresh-token";
    public static final String BEARER = "Bearer ";

    private Algorithm AL;
    public static enum TokenType {
        access,
        refresh
    }

    SpJwtProperties properties;

    public SpJwtProperties getProperties() {
        return properties;
    }

    public JWTUtil(SpJwtProperties properties){
        this.properties = properties;
        this.AL = Algorithm.HMAC512(properties.getSecret());
    }

    public String generate(String userId){
        return generate(userId, TokenType.access);
    }

    public String generate(String userId, TokenType type){
        return JWT.create().withSubject(userId)
                .withClaim("exp", Instant.now().getEpochSecond()+ getLifeTime(type))
                .sign(AL);
    }

    private long getLifeTime(TokenType type) {
        switch(type){
            case refresh:
                return this.properties.getTokenRefreshTime();
            case access:
            default:
                return this.properties.getTokenLifeTime();
        }
    }

    public VerifyResult verify(String token){
        try{
            DecodedJWT decode = JWT.require(AL).build().verify(token);
            return VerifyResult.builder().userId(decode.getSubject()).result(true).build();
        }catch(JWTVerificationException ex){
            DecodedJWT decode = JWT.decode(token);
            return VerifyResult.builder().userId(decode.getSubject()).result(false).build();
        }
    }

}
