package com.sp.sec.web.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Instant;

public class JWTUtil {

    public static final String AUTH_HEADER = "Authentication";
    public static final String BEARER = "Bearer ";

    private Algorithm AL;
    private long lifeTime;

    SpJwtProperties properties;

    public JWTUtil(SpJwtProperties properties){
        this.properties = properties;
        this.AL = Algorithm.HMAC512(properties.getSecret());
        this.lifeTime = properties.getTokenLifeTime();
    }

    public String generate(String userId){
        return JWT.create().withSubject(userId)
                .withClaim("exp", Instant.now().getEpochSecond()+lifeTime)
                .sign(AL);
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
